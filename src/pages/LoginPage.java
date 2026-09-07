package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class LoginPage {
    private WebDriver driver;
    private WebDriverWait wait;

    private By emailInput = By.xpath("/html/body/div/div/div/main/div/div[1]/div/div[3]/form/div[1]/div[1]/div/div[1]/div/input");
    private By passInput = By.xpath("/html/body/div/div/div/main/div/div[1]/div/div[3]/form/div[1]/div[2]/div/div[1]/div/input");
    private By loginBtn = By.xpath("//button[contains(.,'Đăng nhập') or contains(.,'Login')]");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, 20);
    }

    public void login(String email, String password) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@type='text' or contains(@placeholder,'Email')]")));
        driver.findElement(emailInput).sendKeys(email);
        driver.findElement(passInput).sendKeys(password);
        driver.findElement(loginBtn).click();
    }
}