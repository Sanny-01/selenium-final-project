import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

public class FinalProject {
    WebDriver driver;
    String s;

    public String email() {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("mmss");
        LocalTime time = LocalTime.now();
        s = format.format(time);
        return s;
    }

    @BeforeTest
    @Parameters("browser")
    public void setup(String browser) {
        if (browser.equalsIgnoreCase("Chrome")) {
            WebDriverManager.chromedriver().setup();
            driver = new ChromeDriver();
            driver.manage().window().maximize();
        } else if (browser.equalsIgnoreCase("Firefox")) {
            WebDriverManager.firefoxdriver().setup();
            driver = new FirefoxDriver();
            driver.manage().window().maximize();
        }
    }

    @Test
    public void Buy() {

        driver.get("http://tutorialsninja.com/demo/");

        WebDriverWait wait = new WebDriverWait(driver, 7);
        WebElement click;
        JavascriptExecutor js = (JavascriptExecutor) driver;
        Actions action = new Actions(driver);

        // Registering a user
        driver.findElement(By.className("dropdown")).click();
        driver.findElement(By.xpath("//ul[@class = 'dropdown-menu dropdown-menu-right']/child::li[1]")).click();
        driver.findElement(By.id("input-firstname")).sendKeys("Name");
        driver.findElement(By.name("lastname")).sendKeys("Last");
        String num = email();
        String email = num + "email@gmail.com";
        driver.findElement(By.xpath("//input[@placeholder = 'E-Mail']")).sendKeys(email);
        driver.findElement(By.xpath("//input[contains(@id,'telephone')]")).sendKeys("55133231");
        driver.findElement(By.cssSelector("input[name^='pass']")).sendKeys("Pass");
        String password = "Pass";
        driver.findElement(By.cssSelector("input#input-confirm")).sendKeys("Pass");
        driver.findElement(By.cssSelector("input[name = 'newsletter'][value = '1']")).click();
        driver.findElement(By.xpath("//input[@type = 'checkbox' and @name = 'agree']")).click();
        driver.findElement(By.xpath("//input[@value = 'Continue']")).click();

        // if email is already registered it will log in
        try {
            driver.findElement(By.xpath("//i[@class = 'fa fa-exclamation-circle']")).isDisplayed();
            wait.until(ExpectedConditions.elementToBeClickable((By.xpath("//div[@class = 'list-group']/a[1]"))));
            driver.findElement(By.xpath("//div[@class = 'list-group']/a[1]")).click();
            driver.findElement(By.id("input-email")).sendKeys(email);
            driver.findElement(By.id("input-password")).sendKeys(password);
            driver.findElement(By.xpath("//input[@value = 'Login']")).click();
        } catch (NoSuchElementException e) {
            System.out.println("New user");
        }

        // moving to Desktops, selecting show all desktops & choosing mp3
        WebElement element = driver.findElement(By.xpath("//ul[@class = 'nav navbar-nav']/li[1]"));
        js.executeScript("window.scrollTo(0,-document.body.scrollHeight)");
        action.moveToElement(element).perform();
        driver.findElement(By.cssSelector("a.see-all")).click();
        driver.findElement(By.cssSelector("a.list-group-item:last-child")).click();

        // moving to ipod shuffle and hovering on it to check tooltip
        WebElement hover = driver.findElement(By.xpath("//img[@alt = 'iPod Shuffle']"));
        js.executeScript("arguments[0].scrollIntoView(true);", hover);
        action.moveToElement(hover).build().perform();
        WebElement tooltip = driver.findElement(By.xpath("//img[@title = 'iPod Shuffle']"));
        String actual = tooltip.getAttribute("title");
        String expected = "iPod Shuffle";
        Assert.assertEquals(actual, expected);

        // clicking on ipod shuffle's link,clicking first picture & move on another picture until 5 of 5 text is present
        driver.findElement(By.xpath("//div[@class = 'product-layout product-grid col-lg-4 col-md-4 col-sm-6 col-xs-12'][3]/div/div/a")).click();
        click = driver.findElement(By.xpath("//a[starts-with(@title ,'iPod')]"));
        wait.until(ExpectedConditions.elementToBeClickable(click));
        click.click();
        WebElement move = driver.findElement(By.xpath("//button[@title = 'Previous (Left arrow key)']//following::button"));
        WebElement counter = driver.findElement(By.xpath("//div[@class = 'mfp-bottom-bar']//div[@class = 'mfp-counter']"));
        while (!js.executeScript("return arguments[0].innerHTML", counter).equals("5 of 5")) {
            move.click();
        }
        click = driver.findElement(By.xpath("//button[@title = 'Close (Esc)']"));
        wait.until(ExpectedConditions.elementToBeClickable(click));
        click.click();

        // writing a review
        driver.findElement(By.xpath("//ul[@class = 'nav nav-tabs']/li/a[text() = 'Reviews (0)']")).click();
        driver.findElement(By.id("input-name")).clear();
        driver.findElement(By.id("input-name")).sendKeys("User1");
        driver.findElement(By.id("input-review")).sendKeys("This is a good ipod i can recommend it ");
        driver.findElement(By.xpath("//input[@type = 'radio' ]//following::input[4]")).click();
        click = driver.findElement(By.cssSelector("button#button-review"));
        js.executeScript("arguments[0].click();", click);

        // adding to cart , checking it was added & going to checkout
        element = driver.findElement(By.xpath("//button[text() = 'Add to Cart']"));
        element.click();
        wait.until(ExpectedConditions.elementToBeClickable(element));
        actual = driver.findElement(By.id("cart-total")).getText();
        Assert.assertNotEquals(actual, "0 item(s) - $0.00");
        wait.until(ExpectedConditions.elementToBeClickable((By.xpath("//span[@id = 'cart-total']/parent::button"))));
        driver.findElement(By.xpath("//span[@id = 'cart-total']//parent::button")).click();
        wait.until(ExpectedConditions.elementToBeClickable((By.xpath("//p[@class = 'text-right']/a[last()]"))));
        driver.findElement(By.xpath("//p[@class = 'text-right']/a[last()]")).click();


        // Fill Billing details
        try {
            // checking if user already has a billing address
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@name = 'payment_address']")));
            driver.findElement(By.xpath("//input[@name = 'payment_address']")).click();
            wait.until(ExpectedConditions.elementToBeClickable((By.id("button-payment-address"))));
            driver.findElement(By.id("button-payment-address")).click();
        } catch (Exception e) {
            wait.until(ExpectedConditions.visibilityOfAllElements(driver.findElements(By.id("collapse-payment-address"))));
            driver.findElement(By.name("firstname")).sendKeys("User1");
            js.executeScript("document.getElementById('input-payment-lastname').value = 'Surname1';");
            driver.findElement(By.xpath("//input[@class = 'form-control' and @name = 'address_1']")).sendKeys("User1Address");
            driver.findElement(By.cssSelector("input[name = 'city']")).sendKeys("Tbilisi");
            driver.findElement(By.cssSelector("input[id $= 'postcode']")).sendKeys("122");
            driver.findElement(By.cssSelector("select.form-control[name = 'country_id']")).click();
            element = driver.findElement(By.xpath("//select[@id = 'input-payment-country']/option[text() = 'Georgia']"));
            driver.findElement(By.cssSelector("select.form-control[name = 'country_id']")).click();
            wait.until(ExpectedConditions.elementToBeClickable(element));
            element.click();
            driver.findElement(By.name("zone_id")).click();
            wait.until(ExpectedConditions.visibilityOfElementLocated((By.xpath("//select[@id = 'input-payment-zone']/option[text() = 'Tbilisi']"))));
            driver.findElement(By.xpath("//select[@id = 'input-payment-zone']/option[text() = 'Tbilisi']")).click();
            driver.findElement(By.xpath("//input[@value = 'Continue']")).click();
        }

        //Delivery details and Methods
        wait.until(ExpectedConditions.elementToBeClickable(By.id("button-shipping-address")));
        driver.findElement(By.id("button-shipping-address")).click();
        WebElement scroll = driver.findElement(By.id("button-shipping-address"));
        js.executeScript("arguments[0].scrollIntoView(true);", scroll);
        wait.until(ExpectedConditions.elementToBeClickable(By.id("button-shipping-method")));
        driver.findElement(By.id("button-shipping-method")).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@name = 'agree']")));
        driver.findElement(By.xpath("//input[@name = 'agree']")).click();
        driver.findElement(By.id("button-payment-method")).click();

        // checking Sub-Total, Flat Shipping Rate and Total amount & confirming order
        wait.until(ExpectedConditions.elementToBeClickable(By.id("button-confirm")));
        int row = 3;
        for (int i = 1; i <= row; i++) {
            element = driver.findElement(By.xpath("//div[@class = 'table-responsive']/table/tfoot/tr[" + i + "]/td[1]"));
            if (element.getText().equals("Sub-Total:")) {
                actual = driver.findElement(By.xpath("//div[@class = 'table-responsive']/table/tfoot/tr[" + i + "]/td[2]")).getText();
                Assert.assertEquals(actual, "$100.00");
            } else if (element.getText().equals("Flat Shipping Rate:")) {
                actual = driver.findElement(By.xpath("//div[@class = 'table-responsive']/table/tfoot/tr[" + i + "]/td[2]")).getText();
                Assert.assertEquals(actual, "$5.00");
            } else if (element.getText().equals("Total:")) {
                actual = driver.findElement(By.xpath("//div[@class = 'table-responsive']/table/tfoot/tr[" + i + "]/td[2]")).getText();
                Assert.assertEquals(actual, "$105.00");
            }
        }
        driver.findElement(By.id("button-confirm")).click();

        // Going to history, checking status and date
        wait.until(ExpectedConditions.elementToBeClickable(By.linkText("history")));
        driver.findElement(By.linkText("history")).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@class = 'pull-right']/a")));
        List<WebElement> rows = driver.findElements(By.tagName("tr"));
        List<WebElement> cols = rows.get(1).findElements(By.tagName("td"));
        int col = cols.size();

        for (int i = 1; i < col; i++) {
            element = driver.findElement(By.xpath("//div[@class = 'table-responsive']/table/thead/tr/td[" + i + "]"));
            if (element.getText().equals("Status")) {
                actual = driver.findElement(By.xpath("//div[@class = 'table-responsive']/table/tbody/tr/td[" + i + "]")).getText();
                Assert.assertEquals(actual, "Pending");
            } else if (element.getText().equals("Date Added")) {
                actual = driver.findElement(By.xpath("//div[@class = 'table-responsive']/table/tbody/tr/td[" + i + "]")).getText();
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                Date currentDate = new Date();
                String date = dateFormat.format(currentDate);
                Assert.assertEquals(actual, date);
            }
        }
    }
}
