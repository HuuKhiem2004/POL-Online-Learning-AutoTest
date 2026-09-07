package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class DashboardPage {
    private WebDriver driver;
    private WebDriverWait wait;

    private By menuHocVien = By.xpath("/html/body/div/div/div/nav/div[1]/div[2]/a[2]");
    private By menuKhoaHoc = By.xpath("//*[@id='app']/div/nav/div[1]/div[2]/a[3]");
    private By menuKhoaHocBackup = By.xpath("//a[contains(.,'Quản lý khóa học')]");

    public DashboardPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, 20);
    }

    public void goToQuanLyHocVien() {
        wait.until(ExpectedConditions.elementToBeClickable(menuHocVien)).click();
    }

    public void goToQuanLyKhoaHoc() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(menuKhoaHoc)).click();
        } catch (Exception e) {
            driver.findElement(menuKhoaHocBackup).click();
        }
    }
}