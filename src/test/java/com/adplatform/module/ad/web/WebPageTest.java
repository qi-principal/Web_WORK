package com.adplatform.module.ad.web;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 前端页面测试类
 *
 * @author andrew
 * @date 2023-12-19
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class WebPageTest {

    @LocalServerPort
    private int port;

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    public void setUp() {
        // 设置ChromeDriver
        WebDriverManager.chromedriver().setup();
        
        // 配置Chrome选项
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // 无头模式，不显示浏览器窗口
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        
        // 初始化ChromeDriver
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    /**
     * 测试广告列表页面
     */
    @Test
    public void testAdvertisementListPage() {
        // 访问广告列表页面
        driver.get("http://localhost:" + port + "/advertisement/list.html");

        // 等待页面加载完成
        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("el-table")));

        // 测试新建广告
        WebElement addButton = driver.findElement(By.xpath("//button[contains(text(),'新建广告')]"));
        addButton.click();

        // 等待对话框显示
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("el-dialog")));

        // 填写表单
        driver.findElement(By.xpath("//input[@placeholder='请输入标题']"))
              .sendKeys("测试广告");
        driver.findElement(By.xpath("//textarea[@placeholder='请输入描述']"))
              .sendKeys("这是一个测试广告");
        
        // 选择广告类型
        driver.findElement(By.xpath("//input[@placeholder='请选择类型']")).click();
        wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//span[contains(text(),'图片广告')]"))).click();

        // 输入预算
        driver.findElement(By.xpath("//label[contains(text(),'总预算')]/following-sibling::div//input"))
              .sendKeys("1000");
        driver.findElement(By.xpath("//label[contains(text(),'日预算')]/following-sibling::div//input"))
              .sendKeys("100");

        // 选择投放时间
        driver.findElement(By.xpath("//input[@placeholder='开始时间']")).click();
        driver.findElement(By.xpath("//button[contains(@class,'el-picker-panel__link-btn')][2]")).click();

        // 保存
        driver.findElement(By.xpath("//span[contains(text(),'确 定')]")).click();

        // 等待保存成功提示
        wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("//p[contains(text(),'保存成功')]")));

        // 验证列表中是否有新建的广告
        assertTrue(driver.findElements(By.xpath("//div[contains(text(),'测试广告')]")).size() > 0);
    }

    /**
     * 测试素材管理页面
     */
    @Test
    public void testMaterialListPage() {
        // 访问素材管理页面
        driver.get("http://localhost:" + port + "/material/list.html");

        // 等待页面加载完成
        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("material-list")));

        // 测试上传素材
        WebElement uploadButton = driver.findElement(By.xpath("//button[contains(text(),'上传素材')]"));
        assertTrue(uploadButton.isDisplayed());

        // 选择素材类型
        driver.findElement(By.xpath("//input[@placeholder='请选择素材类型']")).click();
        wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//span[contains(text(),'图片')]"))).click();

        // 上传文件（这里只验证上传按钮存在，实际文件上传需要另外处理）
        WebElement fileInput = driver.findElement(By.xpath("//input[@type='file']"));
        assertTrue(fileInput.isEnabled());

        // 验证素材列表显示
        assertTrue(driver.findElement(By.className("material-list")).isDisplayed());

        // 验证分页组件显示
        assertTrue(driver.findElement(By.className("el-pagination")).isDisplayed());
    }

    /**
     * 测试广告状态流转
     */
    @Test
    public void testAdvertisementStatusFlow() {
        // 访问广告列表页面
        driver.get("http://localhost:" + port + "/advertisement/list.html");

        // 等待页面加载完成
        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("el-table")));

        // 找到第一个草稿状态的广告
        WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//button[contains(text(),'提交审核')]")));
        
        // 提交审核
        submitButton.click();
        wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("//p[contains(text(),'提交成功')]")));

        // 验证状态已更新
        assertTrue(driver.findElements(By.xpath("//div[contains(text(),'待审核')]")).size() > 0);
    }

    /**
     * 测试素材预览功能
     */
    @Test
    public void testMaterialPreview() {
        // 访问素材管理页面
        driver.get("http://localhost:" + port + "/material/list.html");

        // 等待页面加载完成
        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("material-list")));

        // 点击第一个图片素材
        WebElement firstImage = wait.until(ExpectedConditions.elementToBeClickable(
            By.className("material-image")));
        firstImage.click();

        // 验证预览对话框显示
        assertTrue(driver.findElement(By.className("el-dialog")).isDisplayed());

        // 关闭预览
        driver.findElement(By.className("el-dialog__close")).click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("el-dialog")));
    }
} 