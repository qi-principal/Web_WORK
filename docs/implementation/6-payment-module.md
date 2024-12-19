# 第六阶段：支付模块实现

## 1. 模块结构

```
src/main/java/com/adplatform/module/payment/
├── controller/
│   ├── RechargeController.java
│   └── TransactionController.java
├── service/
│   ├── RechargeService.java
│   ├── RechargeServiceImpl.java
│   ├── TransactionService.java
│   └── TransactionServiceImpl.java
├── repository/
│   ├── RechargeOrderRepository.java
│   └── TransactionRepository.java
├── entity/
│   ├── RechargeOrder.java
│   └── Transaction.java
├── dto/
│   ├── RechargeOrderDTO.java
│   ├── TransactionDTO.java
│   └── RechargeRequest.java
├── enums/
│   ├── PayType.java
│   ├── PayStatus.java
│   └── TransactionType.java
└── integration/
    ├── alipay/
    │   ├── AlipayConfig.java
    │   └── AlipayService.java
    └── wechat/
        ├── WechatPayConfig.java
        └── WechatPayService.java
```

## 2. 核心代码实现

### 2.1 充值订单实体
```java
@Data
@Entity
@Table(name = "recharge_order")
public class RechargeOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String orderNo;
    private Long userId;
    private BigDecimal amount;
    private Integer payType;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime payTime;
    
    @PrePersist
    public void prePersist() {
        this.orderNo = generateOrderNo();
        this.createTime = LocalDateTime.now();
    }
    
    private String generateOrderNo() {
        return "R" + System.currentTimeMillis() + 
               String.format("%04d", ThreadLocalRandom.current().nextInt(10000));
    }
}
```

### 2.2 支付宝集成服务
```java
@Service
public class AlipayService {
    @Autowired
    private AlipayClient alipayClient;
    
    @Autowired
    private AlipayConfig alipayConfig;
    
    public String createPayment(RechargeOrder order) {
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setReturnUrl(alipayConfig.getReturnUrl());
        request.setNotifyUrl(alipayConfig.getNotifyUrl());
        
        Map<String, Object> bizContent = new HashMap<>();
        bizContent.put("out_trade_no", order.getOrderNo());
        bizContent.put("total_amount", order.getAmount());
        bizContent.put("subject", "账户充值");
        bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");
        
        request.setBizContent(new ObjectMapper().writeValueAsString(bizContent));
        
        AlipayTradePagePayResponse response = alipayClient.pageExecute(request);
        if (response.isSuccess()) {
            return response.getBody();
        }
        throw new ApiException("创建支付宝订单失败");
    }
    
    public boolean verifyNotify(Map<String, String> params) {
        return AlipaySignature.rsaCheckV1(
            params, 
            alipayConfig.getAlipayPublicKey(), 
            alipayConfig.getCharset(), 
            alipayConfig.getSignType()
        );
    }
}
```

### 2.3 充值服务实现
```java
@Service
@Transactional
public class RechargeServiceImpl implements RechargeService {
    @Autowired
    private RechargeOrderRepository orderRepository;
    
    @Autowired
    private TransactionService transactionService;
    
    @Autowired
    private AlipayService alipayService;
    
    @Autowired
    private WechatPayService wechatPayService;
    
    @Override
    public RechargeOrderDTO createOrder(RechargeRequest request) {
        // 创建充值订单
        RechargeOrder order = new RechargeOrder();
        order.setUserId(request.getUserId());
        order.setAmount(request.getAmount());
        order.setPayType(request.getPayType());
        order.setStatus(PayStatus.PENDING.getCode());
        
        order = orderRepository.save(order);
        
        // 创建支付
        String payUrl;
        if (order.getPayType() == PayType.ALIPAY.getCode()) {
            payUrl = alipayService.createPayment(order);
        } else if (order.getPayType() == PayType.WECHAT.getCode()) {
            payUrl = wechatPayService.createPayment(order);
        } else {
            throw new ApiException("不支持的支付方式");
        }
        
        RechargeOrderDTO dto = convertToDTO(order);
        dto.setPayUrl(payUrl);
        return dto;
    }
    
    @Override
    public void handlePaymentCallback(String orderNo, Integer payType, 
                                    String tradeNo, BigDecimal amount) {
        RechargeOrder order = orderRepository.findByOrderNo(orderNo)
            .orElseThrow(() -> new ApiException("订单不存在"));
            
        // 验证订单状态
        if (order.getStatus() != PayStatus.PENDING.getCode()) {
            return;
        }
        
        // 验证金额
        if (order.getAmount().compareTo(amount) != 0) {
            throw new ApiException("支付金额不匹配");
        }
        
        // 更新订单状态
        order.setStatus(PayStatus.SUCCESS.getCode());
        order.setPayTime(LocalDateTime.now());
        orderRepository.save(order);
        
        // 创建交易记录
        transactionService.createTransaction(
            order.getUserId(),
            TransactionType.RECHARGE,
            order.getAmount(),
            "充值-" + orderNo
        );
    }
}
```

## 3. 接口说明

### 3.1 充值接口
- POST `/api/v1/recharge/create`: 创建充值订单
- GET `/api/v1/recharge/{orderNo}`: 获取充值订单
- POST `/api/v1/recharge/notify/alipay`: 支付宝回调
- POST `/api/v1/recharge/notify/wechat`: 微信支付回调

### 3.2 交易接口
- GET `/api/v1/transactions`: 获取交易记录
- GET `/api/v1/transactions/{id}`: 获取交易详情

## 4. 支付流程

### 4.1 充值流程
1. 用户发起充值请求
2. 系统创建充值订单
3. 调用支付接口获取支付链接
4. 用户完成支付
5. 支付平台回调通知
6. 系统更新订单状态
7. 增加用户余额

### 4.2 消费流程
1. 广告投放消费
2. 系统检查用户余额
3. 创建消费交易记录
4. 扣减用户余额

## 5. 测试用例

### 5.1 充值测试
```java
@Test
public void testRecharge() {
    // 创建充值订单
    RechargeRequest request = new RechargeRequest();
    request.setUserId(1L);
    request.setAmount(new BigDecimal("100"));
    request.setPayType(PayType.ALIPAY.getCode());
    
    RechargeOrderDTO order = rechargeService.createOrder(request);
    assertNotNull(order);
    assertNotNull(order.getPayUrl());
    assertEquals(PayStatus.PENDING.getCode(), order.getStatus());
    
    // 模拟支付回调
    rechargeService.handlePaymentCallback(
        order.getOrderNo(),
        PayType.ALIPAY.getCode(),
        "2023112822001123456789",
        new BigDecimal("100")
    );
    
    // 验证订单状态
    RechargeOrder updatedOrder = orderRepository.findByOrderNo(order.getOrderNo()).get();
    assertEquals(PayStatus.SUCCESS.getCode(), updatedOrder.getStatus());
    assertNotNull(updatedOrder.getPayTime());
}
```

### 5.2 交易记录测试
```java
@Test
public void testTransaction() {
    // 创建交易记录
    TransactionDTO transaction = transactionService.createTransaction(
        1L,
        TransactionType.RECHARGE,
        new BigDecimal("100"),
        "测试充值"
    );
    
    assertNotNull(transaction);
    assertEquals(TransactionType.RECHARGE.getCode(), transaction.getType());
    assertEquals(new BigDecimal("100"), transaction.getAmount());
    
    // 查询交易记录
    List<TransactionDTO> transactions = transactionService
        .getUserTransactions(1L, PageRequest.of(0, 10));
    assertFalse(transactions.isEmpty());
}
``` 