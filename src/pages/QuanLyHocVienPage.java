package pages;

import org.json.simple.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.util.List;

public class QuanLyHocVienPage {
    private WebDriver driver;
    private WebDriverWait wait;
    private JavascriptExecutor js;
    private Actions actions;

    // --- KHAI BÁO XPATH ---
    private By btnThemMoi = By.xpath("//button[.//i[contains(@class, 'mdi-plus')] and (contains(., 'Thêm mới') or contains(., 'THÊM MỚI'))]");
    
    // Giới hạn vùng tìm kiếm trong Form đang mở (v-dialog--active) để chống click nhầm
    private By inputHoTen = By.xpath("//div[contains(@class, 'v-dialog--active')]//label[contains(text(), 'Họ và tên')]/following::input[1]");
    private By inputEmail = By.xpath("//div[contains(@class, 'v-dialog--active')]//label[contains(text(), 'Email')]/following::input[1]");
    private By inputNgaySinh = By.xpath("//div[contains(@class, 'v-dialog--active')]//label[contains(text(), 'Ngày sinh')]/following::input[1]");
    private By inputDiaChi = By.xpath("//div[contains(@class, 'v-dialog--active')]//label[contains(text(), 'Địa chỉ')]/following::input[1]");
    private By inputMaSV = By.name("student_code"); 
    private By inputSoDT = By.xpath("//div[contains(@class, 'v-dialog--active')]//label[contains(text(), 'Số điện thoại')]/following::input[1]");
    
    private By btnLuuThem = By.xpath("//div[contains(@class, 'v-dialog--active')]//button[@type='submit' or contains(., 'LƯU') or contains(., 'Thêm')]");
    private By btnSubmitSua = By.xpath("//div[contains(@class, 'v-dialog--active')]//button[@type='submit' or contains(., 'LƯU')]");
    private By radioNu = By.xpath("//div[contains(@class, 'v-dialog--active')]//label[normalize-space()='Nữ']");
    
    private By searchInput = By.xpath("//input[@placeholder='Tìm kiếm' or contains(@placeholder, 'Search')] | //label[contains(text(),'Search') or contains(text(),'Tìm kiếm')]/following::input[1]");
    
    // Bắt popup thành công: Loại trừ Form nhập liệu, chỉ lấy bảng có nút OK
    private By popupMessage = By.xpath("//div[contains(@class, 'swal2-popup')] | //div[contains(@class, 'v-snack__wrapper')] | //div[contains(@class, 'v-dialog--active')]//div[contains(@class, 'v-card') and .//button[contains(translate(., 'ok', 'OK'), 'OK')]]");

    public QuanLyHocVienPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, 10);
        this.js = (JavascriptExecutor) driver;
        this.actions = new Actions(driver);
    }

    // --- HÀM 1: THÊM HỌC VIÊN ---
    public String themHocVien(JSONObject hocVien) throws InterruptedException {
        // 1. Chờ màn hình sạch rác (hết mờ) rồi bấm Thêm Mới
        try { wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("v-overlay__scrim"))); } catch (Exception e) {}
        WebElement btnThem = wait.until(ExpectedConditions.elementToBeClickable(btnThemMoi));
        js.executeScript("arguments[0].click();", btnThem);
        Thread.sleep(1500);

        // 2. Nhập liệu bằng JS (tránh lỗi form Vuetify kén chữ)
        nhapDuLieuBangJS(inputHoTen, (String)hocVien.get("hoTen"));
        nhapDuLieuBangJS(inputMaSV, (String)hocVien.get("maSV"));
        nhapDuLieuBangJS(inputEmail, (String)hocVien.get("email"));
        nhapDuLieuBangJS(inputSoDT, (String)hocVien.get("soDT"));
        nhapDuLieuBangJS(inputNgaySinh, (String)hocVien.get("ngaySinh"));
        nhapDuLieuBangJS(inputDiaChi, (String)hocVien.get("diaChi"));

        // 3. Ép click nút Lưu bằng JS
        WebElement btnLuu = wait.until(ExpectedConditions.presenceOfElementLocated(btnLuuThem));
        js.executeScript("arguments[0].click();", btnLuu);
        
        // 4. Bắt buộc đợi Form Thêm đóng lại hoàn toàn rồi mới lấy thông báo
        try { wait.until(ExpectedConditions.invisibilityOf(btnLuu)); } catch (Exception e){}
        Thread.sleep(500); 
        return getMessage();
    }

    // --- HÀM TÌM KIẾM DÙNG ROBOT ---
    public void search(String emailTarget, boolean isFirstTime) throws InterruptedException {
        try {
            // Lần đầu tiên: Dùng Selenium trỏ chuột vào ô Search để lấy focus
            if (isFirstTime) {
                WebElement box = wait.until(ExpectedConditions.presenceOfElementLocated(searchInput));
                js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", box);
                js.executeScript("arguments[0].click();", box); 
                box.click(); 
                Thread.sleep(1000);
            }

            Robot robot = new Robot();
            
            // Ctrl+A và Delete để làm sạch ô Search
            robot.keyPress(KeyEvent.VK_CONTROL); robot.keyPress(KeyEvent.VK_A);
            robot.keyRelease(KeyEvent.VK_A); robot.keyRelease(KeyEvent.VK_CONTROL);
            Thread.sleep(300);
            robot.keyPress(KeyEvent.VK_DELETE); robot.keyRelease(KeyEvent.VK_DELETE);
            Thread.sleep(500); 

            // Copy email vào Clipboard và Ctrl+V dán ra
            StringSelection stringSelection = new StringSelection(emailTarget);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
            robot.keyPress(KeyEvent.VK_CONTROL); robot.keyPress(KeyEvent.VK_V);
            robot.keyRelease(KeyEvent.VK_V); robot.keyRelease(KeyEvent.VK_CONTROL);
            Thread.sleep(300);
            
            // Enter và đợi bảng tự lọc
            robot.keyPress(KeyEvent.VK_ENTER); robot.keyRelease(KeyEvent.VK_ENTER);
            Thread.sleep(3000); 

        } catch (Exception e) {}
    }

    // --- HÀM 2: SỬA HỌC VIÊN ---
    public String suaHocVien(String emailTarget, boolean isFirstTime) throws InterruptedException {
        search(emailTarget, isFirstTime); 
        try {
            // Bấm nút Sửa (Cây bút - Nút số 1)
            String xpathNutSua = String.format("//td[contains(text(), '%s')]/parent::tr//button[1]", emailTarget);
            WebElement btnSua = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpathNutSua)));
            js.executeScript("arguments[0].click();", btnSua);
            Thread.sleep(1500); 

            // Chọn radio Nữ và Lưu
            wait.until(ExpectedConditions.elementToBeClickable(radioNu)).click();
            WebElement btnSubmit = wait.until(ExpectedConditions.presenceOfElementLocated(btnSubmitSua));
            js.executeScript("arguments[0].click();", btnSubmit);

            // Đợi Form Sửa đóng lại mới đọc thông báo
            try { wait.until(ExpectedConditions.invisibilityOf(btnSubmit)); } catch (Exception e){}
            Thread.sleep(500); 
            return getMessage();
        } catch (Exception e) {
            return "Không tìm thấy nút Sửa";
        }
    }

    // --- HÀM 3: XÓA HỌC VIÊN ---
    public String xoaHocVien(String emailTarget, boolean isFirstTime) throws InterruptedException {
        search(emailTarget, isFirstTime); 
        try {
            // Bấm nút Xóa (Thùng rác - Nút số 2)
            String xpathNutXoa = String.format("//td[contains(text(), '%s')]/parent::tr//button[2]", emailTarget);
            WebElement btnXoa = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpathNutXoa)));
            js.executeScript("arguments[0].click();", btnXoa);
            Thread.sleep(1500); 

            // Bấm xác nhận Xóa trên popup SweetAlert
            WebElement btnXacNhan = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(@class, 'v-dialog--active')]//button[contains(translate(., 'xóa', 'XÓA'), 'XÓA') or contains(translate(., 'xoá', 'XOÁ'), 'XOÁ')]")));
            js.executeScript("arguments[0].click();", btnXacNhan);

            // Đợi Popup xác nhận đóng lại
            try { wait.until(ExpectedConditions.invisibilityOf(btnXacNhan)); } catch (Exception e){}
            Thread.sleep(500);
            return getMessage();
        } catch (Exception e) {
            return "Không tìm thấy nút Xóa";
        }
    }

    // --- HÀM BƠM DỮ LIỆU JS (Trị lỗi Vue.js không nhận phím gõ) ---
    private void nhapDuLieuBangJS(By locator, String value) throws InterruptedException {
        if (value == null) return;
        try {
            WebElement el = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
            js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", el);
            js.executeScript("arguments[0].value = arguments[1];", el, value);
            // Bắn event để Vue.js cập nhật state
            js.executeScript("arguments[0].dispatchEvent(new Event('input', { bubbles: true }));", el);
            js.executeScript("arguments[0].dispatchEvent(new Event('change', { bubbles: true }));", el);
            // Gõ nháp 1 khoảng trắng rồi xóa để UI phản hồi
            el.sendKeys(" "); el.sendKeys(Keys.BACK_SPACE);
            Thread.sleep(300);
        } catch (Exception e) {}
    }

    // --- HÀM LẤY THÔNG BÁO VÀ ĐÓNG POPUP ---
    public String getMessage() {
        try {
            String msg = "Không thấy thông báo";
            WebElement activePopup = null;
            long endTime = System.currentTimeMillis() + 8000; 
            
            // 1. Quét tìm popup đang hiển thị
            while (System.currentTimeMillis() < endTime) {
                List<WebElement> popups = driver.findElements(popupMessage);
                for (WebElement p : popups) {
                    if (p.isDisplayed() && !p.getText().isEmpty()) {
                        activePopup = p; 
                        msg = p.getText(); // Lấy chữ lưu lại ngay
                        break;
                    }
                }
                if (activePopup != null) break;
                Thread.sleep(500);
            }

            // 2. Tìm nút OK để đóng popup đó lại
            if (activePopup != null) {
                try {
                    WebElement btnClose = activePopup.findElement(By.xpath(".//button[contains(@class, 'swal2-confirm') or contains(translate(., 'ok', 'OK'), 'OK') or contains(., 'Đồng ý')]"));
                    js.executeScript("arguments[0].click();", btnClose);
                    
                    // 3. Quan trọng: Đợi popup mờ đi hoàn toàn mới cho code chạy tiếp
                    WebDriverWait waitShort = new WebDriverWait(driver, 5);
                    waitShort.until(ExpectedConditions.invisibilityOf(activePopup));
                    Thread.sleep(1000); 

                } catch (Exception e) {
                    // Nếu kẹt, thử Enter dự phòng
                    actions.sendKeys(Keys.ENTER).perform();
                    Thread.sleep(1500);
                }
            }
            return msg;
        } catch (Exception e) {
            return "Lỗi lấy message";
        }
    }
}