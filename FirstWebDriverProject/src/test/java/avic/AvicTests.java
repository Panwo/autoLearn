package avic;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;


import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.openqa.selenium.By.xpath;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;


public class AvicTests {

    private WebDriver driver;

    @BeforeTest
    public void profileSetUp() {
        System.setProperty("webdriver.chrome.driver", "src\\main\\resources\\chromedriver.exe");
    }

    @BeforeMethod
    public void testsSetUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("https://avic.ua/");
    }

    @Test(priority = 1)
    public void checkSubmitWithoutParametersError() {
        driver.findElement(xpath("//a[contains(@class,'header-top__item lgreen-color js_addMessage_btn')]")).click();
        driver.findElement(xpath("//div[@class='tab-content shown']//button[@type='submit'][contains(text(),'Отправить сообщение')]")).click();
        assertEquals(driver.findElement(By.xpath("//div[contains(@class,'form-field input-field col-xs-6 error')]")).getAttribute("data-error"), "Обязательное поле");
    }

    @Test(priority = 2)
    public void checkAddTwoItemsToCart() throws InterruptedException {
        driver.findElement(xpath("//input [@class= 'search-query']")).sendKeys("power bank", Keys.ENTER);
        new WebDriverWait(driver, 100).until(ExpectedConditions.presenceOfElementLocated(xpath("//img[@alt='Внешний аккумулятор (Power Bank) PowerPlant PP-LA9305']")));
        Thread.sleep(800);
        driver.findElement(xpath("//img[@alt='Внешний аккумулятор (Power Bank) PowerPlant PP-LA9305']")).click();
        driver.findElement(xpath("//a[text() = 'Купить']")).click();

        new WebDriverWait(driver, 100).until(ExpectedConditions.presenceOfElementLocated(xpath("//span[@class = 'js_plus btn-count btn-count--plus ']")));
        driver.findElement(xpath("//span[@class = 'js_plus btn-count btn-count--plus ']")).click();

        Thread.sleep(800);

        String itemsCount =
                driver.findElement(xpath("//div[contains(@class,'header-bottom__cart')]//div[contains(@class,'cart_count')]")).getText();
        assertEquals(itemsCount, "2");

    }

    @Test(priority = 3)
    public void checkNumberOfElementsWithinSidebar() {
        String sideBarElements[] = driver.findElement(xpath("//div[@class='sidebar']")).getText().split("\n");
        assertEquals(sideBarElements.length, 13);
    }

    @Test(priority = 4)
    public void checkWrongQueryRequestResult() {
        driver.findElement(xpath("//input[@id='input_search']"))
                .sendKeys("webDriver -> ExpectedConditions.presenceOfElementLocated", Keys.ENTER);
        ExpectedConditions.visibilityOfAllElements();
        String expectedResponse = "Ничего не найдено";
        String actualResponse = driver.findElement(xpath("//p[@class='col-xs-12']")).getText();
        assertEquals(actualResponse, expectedResponse);
    }


    //--------------------------------------------------------------
    //--------------------------------------------------------------
    // Lecture tests
    @Test(priority = 1)
    public void checkThatUrlContainsSearchWord() {
        driver.findElement(xpath("//input[@id='input_search']")).sendKeys("iPhone 11");//вводим в поиск iPhone 11
        driver.findElement(xpath("//button[@class='button-reset search-btn']")).click();
        assertTrue(driver.getCurrentUrl().contains("query=iPhone"));//проверяем что урла содержит кверю
    }

    @Test(priority = 2)
    public void checkElementsAmountOnSearchPage() {
        driver.findElement(xpath("//input[@id='input_search']")).sendKeys("iPhone 11");//вводим в поиск iPhone 11
        driver.findElement(xpath("//button[@class='button-reset search-btn']")).click();
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);//неявное ожидание 30 сек
        List<WebElement> elementsList = driver.findElements(xpath("//div[@class='prod-cart__descr']"));//собрали элементы поиска в лист
        int actualElementsSize = elementsList.size();//узнали количество элементов в листе
        assertEquals(actualElementsSize, 12);//сравнили количество элементов актуальное с тем которое ожидаем
    }

    @Test(priority = 3)
    public void checkThatSearchResultsContainsSearchWord() {
        driver.findElement(xpath("//input[@id='input_search']")).sendKeys("iPhone 11");//вводим в поиск iPhone 11
        driver.findElement(xpath("//button[@class='button-reset search-btn']")).click();
        List<WebElement> elementList = driver.findElements(xpath("//div[@class='prod-cart__descr']"));//собрали элементы поиска в лист
        for (WebElement webElement : elementList) { //прошлись циклом и проверили что каждый элемент листа содержит текс iPhone 11
            assertTrue(webElement.getText().contains("iPhone 11"));
        }
    }

    @Test(priority = 4)
    public void checkAddToCart() {
        driver.findElement(xpath("//span[@class='sidebar-item']")).click();//каталог товаров
        driver.findElement(xpath("//ul[contains(@class,'sidebar-list')]//a[contains(@href, 'apple-store')]")).click();//Apple Store
        driver.findElement(xpath("//div[@class='brand-box__title']/a[contains(@href,'iphone')]")).click();//iphone
        new WebDriverWait(driver, 30).until(
                webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));//wait for page loading
        driver.findElement(xpath("//a[@class='prod-cart__buy'][contains(@data-ecomm-cart,'Slim Box White (MHDC3)')]")).click();//add to cart iphone
        WebDriverWait wait = new WebDriverWait(driver, 30);//ждем пока не отобразится попап с товаром добавленным в корзину
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("js_cart")));
        driver.findElement(xpath("//div[@class='btns-cart-holder']//a[contains(@class,'btn--orange')]")).click();//продолжить покупки
        String actualProductsCountInCart =
                driver.findElement(xpath("//div[contains(@class,'header-bottom__cart')]//div[contains(@class,'cart_count')]"))
                        .getText();//получили 1 которая в корзине (один продукт)
        assertEquals(actualProductsCountInCart, "1");
    }

    @AfterMethod
    public void tearDown() {
        driver.close();
    }
}
