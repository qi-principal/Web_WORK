# 第二阶段：用户模块实现

## 1. 模块结构

```
src/main/java/com/adplatform/module/user/
├── controller/
│   ├── UserController.java
│   └── AuthController.java
├── service/
│   ├── UserService.java
│   ├── UserServiceImpl.java
│   └── UserDetailsServiceImpl.java
├── repository/
│   └── UserRepository.java
├── entity/
│   ├── User.java
│   ├── Role.java
│   └── UserRole.java
├── dto/
│   ├── UserDTO.java
│   ├── LoginRequest.java
│   ├── RegisterRequest.java
│   └── LoginResponse.java
└── security/
    ├── JwtTokenProvider.java
    ├── JwtAuthenticationFilter.java
    └── SecurityConfig.java
```

## 2. 核心代码实现

### 2.1 实体类
```java
@Data
@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String username;
    private String password;
    private String email;
    private String phone;
    private Integer userType;
    private Integer status;
    private BigDecimal balance;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_role",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();
}
```

### 2.2 JWT工具类
```java
@Component
public class JwtTokenProvider {
    @Value("${jwt.secret}")
    private String jwtSecret;
    
    @Value("${jwt.expiration}")
    private int jwtExpiration;
    
    public String generateToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration * 1000L);
        
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }
    
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }
    
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
```

### 2.3 认证过滤器
```java
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtTokenProvider tokenProvider;
    
    @Autowired
    private UserDetailsService userDetailsService;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain filterChain) 
            throws ServletException, IOException {
        try {
            String jwt = getJwtFromRequest(request);
            
            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                String username = tokenProvider.getUsernameFromToken(jwt);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                
                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            log.error("认证异常", e);
        }
        
        filterChain.doFilter(request, response);
    }
    
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
```

### 2.4 控制器
```java
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtTokenProvider tokenProvider;
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getUsername(), 
                request.getPassword()
            )
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = tokenProvider.generateToken(authentication);
        
        return Result.success(new LoginResponse(token));
    }
    
    @PostMapping("/register")
    public Result<UserDTO> register(@RequestBody RegisterRequest request) {
        return Result.success(userService.register(request));
    }
}
```

## 3. 安全配置

### 3.1 Spring Security配置
```java
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserDetailsService userDetailsService;
    
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .cors().and().csrf().disable()
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
                .antMatchers("/api/v1/auth/**").permitAll()
                .anyRequest().authenticated();
        
        http.addFilterBefore(jwtAuthenticationFilter, 
                            UsernamePasswordAuthenticationFilter.class);
    }
    
    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder());
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

## 4. 接口说明

### 4.1 认证接口
- POST `/api/v1/auth/login`: 用户登录
- POST `/api/v1/auth/register`: 用户注册

### 4.2 用户接口
- GET `/api/v1/users/me`: 获取当前用户信息
- PUT `/api/v1/users/me`: 更新当前用户信息
- GET `/api/v1/users/{id}`: 获取指定用户信息
- PUT `/api/v1/users/{id}/status`: 更新用户状态

## 5. 测试用例

### 5.1 注册测试
```java
@Test
public void testRegister() {
    RegisterRequest request = new RegisterRequest();
    request.setUsername("test");
    request.setPassword("test123");
    request.setEmail("test@example.com");
    request.setUserType(1);
    
    UserDTO result = userService.register(request);
    assertNotNull(result);
    assertEquals("test", result.getUsername());
}
```

### 5.2 登录测试
```java
@Test
public void testLogin() {
    LoginRequest request = new LoginRequest();
    request.setUsername("test");
    request.setPassword("test123");
    
    LoginResponse response = authService.login(request);
    assertNotNull(response);
    assertNotNull(response.getToken());
}
``` 