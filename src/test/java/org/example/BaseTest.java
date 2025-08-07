package org.example;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static org.junit.jupiter.api.Assertions.*;

//If you go 'Run BaseTest' and certain tests couldn't be executed, please run tests separately!
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BaseTest {
    protected static WebDriver driver;
    protected static String baseUrl;
    protected static JavascriptExecutor js;

    @BeforeAll
    public static void setUp() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        driver = new ChromeDriver(options);
        js = (JavascriptExecutor) driver;
        baseUrl = "https://www.wizardpc.ba/";
    }


    @Test
    @Order(1)
    public void loginWithValidCredentials() throws InterruptedException {
        driver.get(baseUrl);
        driver.findElement(By.linkText("Logovanje")).click();
        assertEquals("https://www.wizardpc.ba/login.php", driver.getCurrentUrl());
        driver.findElement(By.id("username")).sendKeys("testibu");
        driver.findElement(By.id("pw")).sendKeys("burch2022");
        Thread.sleep(3000);
        driver.findElement(By.id("login")).click();
        assertTrue(true, "Test Burch");
        Thread.sleep(3000);
        assertEquals("https://www.wizardpc.ba/",driver.getCurrentUrl());
    }


    @Test
    @Order(2)
    public void loginWithInvalidCredentials() throws InterruptedException {
        driver.get(baseUrl);
        driver.findElement(By.linkText("Logovanje")).click();
        Thread.sleep(3000);
        driver.findElement(By.id("username")).sendKeys("invalidUser");
        driver.findElement(By.id("pw")).sendKeys("fail");
        driver.findElement(By.id("login")).click();
        Thread.sleep(3000);

        String alertMessage = driver.switchTo().alert().getText();
        assertEquals("Pogresan password ili username!", alertMessage);
        driver.switchTo().alert().accept();
    }


    @Test
    @Order(3)
    public void loginWithMissingCredentials() throws InterruptedException {
        driver.get(baseUrl);
        driver.findElement(By.linkText("Logovanje")).click();
        Thread.sleep(3000);
        driver.findElement(By.id("username")).sendKeys("invalidUser");
        driver.findElement(By.id("login")).click();
        Thread.sleep(3000);

        String alertMessage = driver.switchTo().alert().getText();
        assertEquals("Neko od polja je prazno!", alertMessage);
        driver.switchTo().alert().accept();
    }


    @ParameterizedTest
    @ValueSource(strings = {"laptop", "xyz123"})
    @Order(4)
    public void searchForProduct(String query) throws InterruptedException {
        driver.get(baseUrl);
        driver.findElement(By.id("searchfor")).sendKeys(query, Keys.ENTER);
        assertEquals("https://www.wizardpc.ba/search.php?searchfor=" + query, driver.getCurrentUrl());
        Thread.sleep(3000);
    }


    @ParameterizedTest
    @ValueSource(strings = {"laptop", "xyz123"})
    @Order(5)
    public void searchForProductSearchButton(String query) throws InterruptedException{
        driver.get(baseUrl);
        driver.findElement(By.xpath("/html/body/div[1]/header/div[2]/div[2]/div[2]/div[2]/div[1]/div/div[2]/div/div/div/div[1]")).click();
        assertEquals("https://www.wizardpc.ba/search.php?searchfor=" + query, driver.getCurrentUrl());
        Thread.sleep(3000);
    }


    @Test
    @Order(6)
    public void addProductToCartAndAssertThePriceOnScreen() throws InterruptedException {
        driver.get(baseUrl);
        WebElement totalPrice = driver.findElement(By.id("total_price"));
        assertEquals("0 KM",totalPrice.getText());
        driver.findElement(By.xpath("//span[@aid='12439']")).click();
        Thread.sleep(3000);
        assertEquals("49.00 KM",totalPrice.getText());
    }


    @Test
    @Order(7)
    public void addProductToCartAndAssertWeGoToCartScreenAndHaveRightProductInCart() throws InterruptedException {
        driver.get(baseUrl);
        driver.findElement(By.xpath("//span[@aid='12439']")).click();
        Thread.sleep(3000);
        driver.findElement(By.id("cart_block")).click();
        Thread.sleep(3000);
        assertEquals("https://www.wizardpc.ba/cart.php",driver.getCurrentUrl());
        WebElement productName = driver.findElement(By.cssSelector("table.table-hover tr:nth-of-type(2) td:nth-of-type(2) a"));
        assertEquals("Tripod za Mobitel LED Ring S-LINK SL-SF200",productName.getText());
        WebElement totalPrice = driver.findElement(By.xpath("//div[@class='row1 wos-total-cost']/div[contains(text(), 'Ukupno')]"));
        assertEquals("Ukupno: 49,00 KM",totalPrice.getText());
    }


    @Test
    @Order(8)
    public void testContinueShoppingAfterAddingTheProduct() throws InterruptedException {
        driver.get(baseUrl);
        driver.findElement(By.xpath("//span[@aid='12439']")).click();
        Thread.sleep(3000);
        driver.findElement(By.id("cart_block")).click();
        driver.findElement(By.linkText("Nastavi kupovinu")).click();
        Thread.sleep(3000);
        assertEquals("https://www.wizardpc.ba/",driver.getCurrentUrl());
    }


    @Test
    @Order(9)
    public void testThePriceAfterSelectingShippingRadio() throws InterruptedException {
        driver.get(baseUrl);
        driver.findElement(By.xpath("//span[@aid='12439']")).click();
        Thread.sleep(2000);
        driver.findElement(By.id("cart_block")).click();
        driver.findElement(By.xpath("//div[@class='row1 wos-total-cost']//input[@type='radio' and @value='1']")).click();
        WebElement totalPrice = driver.findElement(By.xpath("//div[@class='row1 wos-total-cost']/div[contains(text(), 'Ukupno')]"));
        assertEquals("Ukupno: 49,00 KM",totalPrice.getText(),"It Should be 49,00 KM");
    }


    @Test
    @Order(10)
    public void FromCartProceedToCheckout() throws InterruptedException {
        driver.get(baseUrl);
        driver.findElement(By.xpath("//span[@aid='12439']")).click();
        Thread.sleep(2000);
        driver.findElement(By.id("cart_block")).click();
        Thread.sleep(1000);
        driver.findElement(By.linkText("Završi kupovinu")).click();
        Thread.sleep(2000);
        assertEquals("https://www.wizardpc.ba/checkout.php",driver.getCurrentUrl());
    }


    @Test
    @Order(11)
    public void registrationWithValidDetails() throws InterruptedException {
        driver.get(baseUrl);
        driver.findElement(By.linkText("Registracija")).click();
        driver.findElement(By.id("usernameee")).sendKeys("newUser22");
        driver.findElement(By.id("mail")).sendKeys("user22@example.com");
        driver.findElement(By.id("pw1")).sendKeys("securePassword123");
        driver.findElement(By.id("pw2")).sendKeys("securePassword123");
        driver.findElement(By.id("i-agree-to-terms")).click();
        driver.findElement(By.id("ime")).sendKeys(("Test IBU"));
        driver.findElement(By.id("adresa")).sendKeys("Burch");
        driver.findElement(By.id("ptt")).sendKeys("71210");
        driver.findElement(By.id("grad")).sendKeys(("Sarajevo"));
        driver.findElement(By.id("tel")).sendKeys("+38761111222");
        Thread.sleep(2000);
        driver.findElement(By.id("registr")).click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.alertIsPresent());
        Alert alert = driver.switchTo().alert();
        String alertText = alert.getText();
        assertEquals("Uspjesno ste se registrovali!", alertText);
        alert.accept();
        assertEquals("https://www.wizardpc.ba/index.php",driver.getCurrentUrl());
        Thread.sleep(1000);
        assertTrue(driver.getPageSource().contains("usernameee"),
                "Our username should be displayed in top right same as when we log in");
        Thread.sleep(10000);
    }


    @Test
    @Order(12)
    public void registrationWithMissingFields() throws InterruptedException {
        driver.get(baseUrl);
        driver.findElement(By.linkText("Registracija")).click();
        driver.findElement(By.id("usernameee")).sendKeys("newUser");
        driver.findElement(By.id("mail")).sendKeys("user99@example.com");
        driver.findElement(By.id("pw1")).sendKeys("securePassword123");
        driver.findElement(By.id("pw2")).sendKeys("securePassword123");
        driver.findElement(By.id("i-agree-to-terms")).click();
        driver.findElement(By.id("registr")).click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.alertIsPresent());
        Alert alert = driver.switchTo().alert();
        String alertText = alert.getText();
        assertEquals("Neko od polja je prazno!!", alertText);
        Thread.sleep(5000);
        alert.accept();
    }


    @Test
    @Order(13)
    public void registrationWithInvalidEmail() throws InterruptedException {
        driver.get(baseUrl);
        driver.findElement(By.linkText("Registracija")).click();
        driver.findElement(By.id("usernameee")).sendKeys("newUserrrrrr");
        driver.findElement(By.id("mail")).sendKeys("invalidEmail");
        driver.findElement(By.id("pw1")).sendKeys("securePassword123");
        driver.findElement(By.id("pw2")).sendKeys("securePassword123");
        driver.findElement(By.id("i-agree-to-terms")).click();
        driver.findElement(By.id("ime")).sendKeys(("Test IBU"));
        driver.findElement(By.id("adresa")).sendKeys("Burch");
        driver.findElement(By.id("ptt")).sendKeys("71210");
        driver.findElement(By.id("grad")).sendKeys(("Sarajevo"));
        driver.findElement(By.id("tel")).sendKeys("+38761111222");
        driver.findElement(By.id("registr")).click();
        Thread.sleep(5000);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.alertIsPresent());
        Alert alert = driver.switchTo().alert();
        String alertText = alert.getText();
        assertEquals("E-mail nije validan!!", alertText);
        alert.accept();
    }


    @Test
    @Order(14)
    public void registrationWithExistingUsername() throws InterruptedException {
        driver.get(baseUrl);
        driver.findElement(By.linkText("Registracija")).click();
        driver.findElement(By.id("usernameee")).sendKeys("newUser8");
        driver.findElement(By.id("mail")).sendKeys("user8@example.com");
        driver.findElement(By.id("pw1")).sendKeys("securePassword123");
        driver.findElement(By.id("pw2")).sendKeys("securePassword123");
        driver.findElement(By.id("i-agree-to-terms")).click();
        driver.findElement(By.id("ime")).sendKeys(("Test IBU"));
        driver.findElement(By.id("adresa")).sendKeys("Burch");
        driver.findElement(By.id("ptt")).sendKeys("71210");
        driver.findElement(By.id("grad")).sendKeys(("Sarajevo"));
        driver.findElement(By.id("tel")).sendKeys("+38761111222");
        driver.findElement(By.id("registr")).click();
        Thread.sleep(5000);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.alertIsPresent());
        Alert alert = driver.switchTo().alert();
        String alertText = alert.getText();
        assertEquals("Username vec zauzet!", alertText);
        alert.accept();
    }


    @Test
    @Order(15)
    public void registrationWithExistingEmail() throws InterruptedException {
        driver.get(baseUrl);
        driver.findElement(By.linkText("Registracija")).click();
        driver.findElement(By.id("usernameee")).sendKeys("newUser92");
        driver.findElement(By.id("mail")).sendKeys("user8@example.com");
        driver.findElement(By.id("pw1")).sendKeys("securePassword123");
        driver.findElement(By.id("pw2")).sendKeys("securePassword123");
        driver.findElement(By.id("i-agree-to-terms")).click();
        driver.findElement(By.id("ime")).sendKeys(("Test IBU"));
        driver.findElement(By.id("adresa")).sendKeys("Burch");
        driver.findElement(By.id("ptt")).sendKeys("71210");
        driver.findElement(By.id("grad")).sendKeys(("Sarajevo"));
        driver.findElement(By.id("tel")).sendKeys("+38761111222");
        driver.findElement(By.id("registr")).click();
        Thread.sleep(5000);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.alertIsPresent());
        Alert alert = driver.switchTo().alert();
        String alertText = alert.getText();
        assertEquals("Email vec zauzet!", alertText,"Treba da izbaci alert da je mail vec zauzet");
        alert.accept();
    }


    @Test
    @Order(16)
    public void navigationToContactPage() throws InterruptedException {
        driver.get(baseUrl);
        driver.findElement(By.linkText("Kontakt")).click();
        Thread.sleep(2000);
        assertEquals("https://www.wizardpc.ba/contact.php", driver.getCurrentUrl());
    }


    @Test
    @Order(17)
    public void formValidationWithEmptyFields() throws InterruptedException {
        driver.get(baseUrl);
        driver.findElement(By.linkText("Kontakt")).click();
        driver.findElement(By.className("button")).click();
        Thread.sleep(2000);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement emailError = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[1]/div[1]/div[2]/div/div/div/div[2]/form/div[1]/div")));
        assertEquals("Polje mora biti popunjeno", emailError.getText());
        WebElement contentError = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("error-sadrzaj")));
        assertNotEquals("Polje mora biti popunjeno!", contentError.getText());
        Thread.sleep(2000);
    }


    @Test
    @Order(18)
    public void validateHttpsEnforcement() {
        driver.get(baseUrl);
        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.startsWith("https"), "HTTPS not enforced.");
    }


    @Test
    @Order(19)
    public void sessionTimeoutTest() throws InterruptedException {
        driver.get(baseUrl);
        driver.findElement(By.xpath("/html/body/div[1]/header/div[2]/div[1]/div[2]/div[2]/div/div/div[3]/ul/li[3]/a")).click();
        driver.findElement(By.id("username")).sendKeys("testibu");
        driver.findElement(By.id("pw")).sendKeys("burch2022");
        driver.findElement(By.id("login")).click();
        Thread.sleep(2000);

        driver.navigate().refresh();
        WebElement loginPage = driver.findElement(By.id("url"));
        assertFalse(loginPage.isDisplayed(), "Session did not timeout.");
    }


    @Test
    @Order(20)
    public void validateProductCategoryNavigationMobiteli() throws InterruptedException {
        driver.get(baseUrl);
        driver.findElement(By.linkText("Mobiteli")).click();
        Thread.sleep(2000);
        assertEquals("https://www.wizardpc.ba/search.php?SubCatID=16", driver.getCurrentUrl());
    }


    @Test
    @Order(21)
    public void validateProductCategoryNavigationVideoIgre() throws InterruptedException{
        driver.get(baseUrl);
        driver.findElement(By.linkText("Video Igre")).click();
        Thread.sleep(2000);
        assertEquals("https://www.wizardpc.ba/search.php?CatID=16", driver.getCurrentUrl());
    }


    @Test
    @Order(22)
    public void scrollToBottomAndAssertSocialInstagramLink() throws InterruptedException {
        driver.get(baseUrl);
        Thread.sleep(2000);
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
        Thread.sleep(2000);
        String originalWindow = driver.getWindowHandle();
        driver.findElement(By.xpath("//*[@id=\"footer\"]/footer/b/div/div/a[4]")).click();
        Thread.sleep(2000);
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.numberOfWindowsToBe(2));
        for (String windowHandle : driver.getWindowHandles()) {
            if (!windowHandle.equals(originalWindow)) {
                driver.switchTo().window(windowHandle);
                break;
            }
        }
        Thread.sleep(2000);
        String currentUrl = driver.getCurrentUrl();
        String regex = "https://www\\.instagram\\.com/wizard_pc/\\#?";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(currentUrl);
        assertTrue(matcher.matches(), "The URL does not match the expected pattern.");
        driver.close();
        driver.switchTo().window(originalWindow);
    }


    @Test
    @Order(23)
    public void scrollToBottomAndAssertSocialFacebookLink() throws InterruptedException {
        driver.get(baseUrl);
        Thread.sleep(2000);
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
        Thread.sleep(2000);
        String originalWindow = driver.getWindowHandle();
        driver.findElement(By.xpath("//*[@id=\"footer\"]/footer/b/div/div/a[1]")).click();
        Thread.sleep(2000);
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.numberOfWindowsToBe(2));
        for (String windowHandle : driver.getWindowHandles()) {
            if (!windowHandle.equals(originalWindow)) {
                driver.switchTo().window(windowHandle);
                break;
            }
        }
        Thread.sleep(2000);
        assertEquals("https://www.facebook.com/wizardpccomputershop/?ref=py_c&_rdc=1&_rdr#", driver.getCurrentUrl());
        driver.close();
        driver.switchTo().window(originalWindow);
    }


    @Test
    @Order(25)
    public void testHoverOverKonzoleAndClickPlayStation5() throws InterruptedException{
        driver.get(baseUrl);
        Thread.sleep(2000);
        WebElement konzole = driver.findElement(By.xpath("/html/body/div[1]/div/div[1]/div[1]/div/div[2]/div/div/ul/li[2]/a"));
        Actions actions = new Actions(driver);
        actions.moveToElement(konzole).perform();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement playStation5 = wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("PlayStation 5")));
        playStation5.click();
        Thread.sleep(2000);
        assertEquals("https://www.wizardpc.ba/search.php?SubCatID=131", driver.getCurrentUrl());
    }


    @Test
    @Order(26)
    public void testHoverOverBijelaTehnikaAndClickPrikaziSve() throws InterruptedException{
        driver.get(baseUrl);
        Thread.sleep(2000);
        WebElement bijelaTehnika = driver.findElement(By.xpath("/html/body/div[1]/div/div[1]/div[1]/div/div[2]/div/div/ul/li[7]/a"));
        Actions actions = new Actions(driver);
        actions.moveToElement(bijelaTehnika).perform();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement prikaziSve = wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Prikaži sve")));
        prikaziSve.click();
        Thread.sleep(2000);
        assertEquals("https://www.wizardpc.ba/search.php?CatID=34", driver.getCurrentUrl());
    }


    @Test
    @Order(27)
    public void testAddProductToCartPlusButton() throws InterruptedException{
        driver.get("https://www.wizardpc.ba/");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement product = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(),'Tripod za Mobitel LED Ring S-LINK SL-SF200')]")));
        product.click();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("q_up")));
        int clickCount = 3;
        for (int i = 0; i < clickCount; i++) {
            WebElement plusButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("q_up")));
            plusButton.click();
        }
        WebElement buyButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@value='Kupi']")));
        buyButton.click();
        driver.get("https://www.wizardpc.ba/cart.php");
        WebElement quantityInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//td[3]/input")));
        String actualQuantity = quantityInput.getAttribute("value");
        assertEquals(String.valueOf(clickCount + 1), actualQuantity);
    }


    @Test
    @Order(28)
    public void testAddProductToCartByManuallyEntering() throws InterruptedException{
        driver.get("https://www.wizardpc.ba/");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement product = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(),'Tripod za Mobitel LED Ring S-LINK SL-SF200')]")));
        product.click();
        Thread.sleep(2000);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("quantity_wanted")));
        WebElement field = driver.findElement(By.id("quantity_wanted"));
        field.clear();
        field.sendKeys("3");
        Thread.sleep(2000);
        WebElement buyButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@value='Kupi']")));
        buyButton.click();
        driver.get("https://www.wizardpc.ba/cart.php");
        Thread.sleep(2000);
        WebElement quantityInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//td[3]/input")));
        String actualQuantity = quantityInput.getAttribute("value");
        assertEquals("3", actualQuantity);
    }


    @Test
    @Order(29)
    public void testAddingProductToWishlist() throws InterruptedException {
        driver.get(baseUrl);
        driver.findElement(By.xpath("//*[@id=\"middle\"]/div/div/div[4]/div[1]/div/div[1]/div/a")).click();
        Thread.sleep(2000);
        driver.findElement(By.xpath("//*[@id=\"product\"]/div/div/div/div/div[2]/a[1]")).click();
        Thread.sleep(2000);
        driver.findElement(By.linkText("Lista želja")).click();
        Thread.sleep(2000);
        assertTrue(driver.getPageSource().contains("Tripod za Mobitel LED Ring S-LINK SL-SF200"));
        Thread.sleep(2000);
    }


    @AfterAll
    public static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}