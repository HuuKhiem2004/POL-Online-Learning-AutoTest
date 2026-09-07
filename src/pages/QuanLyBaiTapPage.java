package pages;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.util.List;

public class QuanLyBaiTapPage {
    private WebDriver driver;
    private WebDriverWait wait;
    private Actions actions;
    private JavascriptExecutor js;

    private By inputTenBaiTap = By.xpath("//label[contains(text(),'Tên bài tập')]/following::input[1]");
    private By btnLuuBaiTap = By.xpath("//button[@type='submit']");
    
    private By btnThemCauHoi = By.xpath("//button[contains(normalize-space(),'Thêm câu hỏi') or contains(normalize-space(),'THÊM CÂU HỎI')]");
    private By btnDropdown = By.xpath("(//button[contains(normalize-space(),'THÊM CÂU HỎI')]/following-sibling::button)[1] | (//i[contains(@class, 'mdi-menu-down')]/ancestor::button)[1]");
    private By menuAmThanh = By.xpath("//div[@role='menuitem']//div[contains(text(),'Âm thanh')]");
    private By menuHinhAnh = By.xpath("//div[@role='menuitem']//div[contains(text(),'Hình ảnh')]");
    private By menuVideo = By.xpath("//div[@role='menuitem']//div[contains(text(),'Video')]");
    private By menuTuLuan = By.xpath("//div[@role='menuitem']//div[contains(text(),'Tự luận')]");

    public QuanLyBaiTapPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, 15);
        this.actions = new Actions(driver);
        this.js = (JavascriptExecutor) driver;
    }

    public void goToThemBaiTap() {
        driver.get("https://elearning.plt.pro.vn/quan-tri-vien/bai-tap/them-moi");
    }

    public void themBaiTap(JSONObject baiTapData) throws InterruptedException {
        String tenBaiTap = (String) baiTapData.get("tenBaiTap");
        String duongDanAnh = (String) baiTapData.get("duongDanAnh");
        JSONArray danhSachCauHoi = (JSONArray) baiTapData.get("danhSachCauHoi");

        nhapThongTinCoBan(tenBaiTap, duongDanAnh);
        taoGiaoDienCacCauHoi(danhSachCauHoi);
        nhapNoiDungTatCaCauHoi(danhSachCauHoi);
        luuBaiTap();
    }

    private void nhapThongTinCoBan(String tenBaiTap, String duongDanAnh) throws InterruptedException {
        WebElement txtTenBaiTap = wait.until(ExpectedConditions.elementToBeClickable(inputTenBaiTap));
        js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", txtTenBaiTap);
        txtTenBaiTap.click(); 
        
        txtTenBaiTap.sendKeys(Keys.CONTROL + "a");
        txtTenBaiTap.sendKeys(Keys.BACK_SPACE);
        txtTenBaiTap.sendKeys(tenBaiTap != null && !tenBaiTap.isEmpty() ? tenBaiTap : " ");
        if (tenBaiTap == null || tenBaiTap.isEmpty()) txtTenBaiTap.sendKeys(Keys.BACK_SPACE);
        Thread.sleep(500);

        if(duongDanAnh != null && !duongDanAnh.isEmpty()){
            try {
                File file = new File("data/" + duongDanAnh);
                wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@type='file']")))
                    .sendKeys(file.getAbsolutePath());
                Thread.sleep(1000);
            } catch (Exception e) {}
        }
    }

    private void taoGiaoDienCacCauHoi(JSONArray danhSachCauHoi) throws InterruptedException {
        for (int k = 0; k < danhSachCauHoi.size(); k++) {
            JSONObject cauHoi = (JSONObject) danhSachCauHoi.get(k);
            String loai = (String) cauHoi.get("loaiCauHoi");

            if (loai == null || loai.equals("MacDinh")) {
                WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(btnThemCauHoi));
                js.executeScript("arguments[0].scrollIntoView(true);", btn);
                js.executeScript("arguments[0].click();", btn);
            } else {
                WebElement drop = wait.until(ExpectedConditions.presenceOfElementLocated(btnDropdown));
                js.executeScript("arguments[0].scrollIntoView(true);", drop);
                js.executeScript("arguments[0].click();", drop);
                Thread.sleep(500); 
                
                WebElement menuItem = null;
                if (loai.equals("AmThanh")) menuItem = wait.until(ExpectedConditions.presenceOfElementLocated(menuAmThanh));
                else if (loai.equals("HinhAnh")) menuItem = wait.until(ExpectedConditions.presenceOfElementLocated(menuHinhAnh));
                else if (loai.equals("Video")) menuItem = wait.until(ExpectedConditions.presenceOfElementLocated(menuVideo));
                else if (loai.equals("TuLuan")) menuItem = wait.until(ExpectedConditions.presenceOfElementLocated(menuTuLuan));
                
                js.executeScript("arguments[0].click();", menuItem);
            }
            Thread.sleep(1000); 
        }
    }

    private void nhapNoiDungTatCaCauHoi(JSONArray danhSachCauHoi) throws InterruptedException {
        for (int i = 0; i < danhSachCauHoi.size(); i++) {
            JSONObject cauHoi = (JSONObject) danhSachCauHoi.get(i);
            String loai = (String) cauHoi.get("loaiCauHoi");
            if (loai == null) loai = "MacDinh";
            String noiDungCauHoi = (String) cauHoi.get("noiDungCauHoi");
            
            String xpathH3 = String.format("(//h3[contains(@class, 'title-panel-header')])[%d]", i + 1);
            WebElement h3Title = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpathH3)));
            WebElement headerBtn = h3Title.findElement(By.xpath("./parent::button"));
            
            if (!headerBtn.getAttribute("class").contains("--active")) {
                js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", headerBtn);
                js.executeScript("arguments[0].click();", headerBtn);
                Thread.sleep(1000); 
            }

            String xpathPanel = xpathH3 + "/ancestor::div[contains(@class, 'v-expansion-panel')][1]";

            if (loai.equals("TuLuan")) {
                dienTextTheoLabel(xpathPanel, "Nội dung câu hỏi", noiDungCauHoi);
                dienTextTheoLabel(xpathPanel, "Số lượng ký tự cho phép trả lời", String.valueOf(cauHoi.get("gioiHanKyTu")));

            } else if (loai.equals("Video")) {
                dienTextTheoLabel(xpathPanel, "ID video trên youtube.com", (String) cauHoi.get("idVideo"));
                dienTextTheoLabel(xpathPanel, "Nội dung câu hỏi", noiDungCauHoi);
                truotTabDienDapAn(cauHoi);

            } else if (loai.equals("AmThanh") || loai.equals("HinhAnh")) {
                
                String labelText = loai.equals("AmThanh") ? "File âm thanh" : "File hình ảnh";
                String exactFileInputXpath = xpathPanel + "//label[contains(text(), '" + labelText + "')]/following::input[@type='file'][1]";

                WebElement fileInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(exactFileInputXpath)));
                
                js.executeScript("arguments[0].style.display = 'block'; arguments[0].style.visibility = 'visible';", fileInput);
                Thread.sleep(500);

                File fileMedia = new File("data/" + cauHoi.get("fileMedia"));
                fileInput.sendKeys(fileMedia.getAbsolutePath());
                Thread.sleep(1500); 
                
                dienTextTheoLabel(xpathPanel, "Nội dung câu hỏi", noiDungCauHoi);
                truotTabDienDapAn(cauHoi);

            } else { 
                dienTextTheoLabel(xpathPanel, "Nội dung câu hỏi", noiDungCauHoi);
                truotTabDienDapAn(cauHoi);
            }
        }
    }

    private void dienTextTheoLabel(String xpathPanel, String labelText, String text) throws InterruptedException {
        String xpathInput = xpathPanel + "//label[contains(text(), '" + labelText + "')]/following::*[self::input or self::textarea][1]";
        
        WebElement inputEl = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpathInput)));
        js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", inputEl);
        inputEl.click();
        
        inputEl.sendKeys(Keys.CONTROL + "a");
        inputEl.sendKeys(Keys.BACK_SPACE);
        inputEl.sendKeys(text);
        Thread.sleep(300);
    }

    private void truotTabDienDapAn(JSONObject cauHoi) throws InterruptedException {
        JSONArray danhSachDapAn = (JSONArray) cauHoi.get("danhSachDapAn");
        for (int j = 0; j < danhSachDapAn.size(); j++) {
            JSONObject dapAn = (JSONObject) danhSachDapAn.get(j);
            String noiDungDapAn = (String) dapAn.get("noiDung");
            boolean isCorrect = (boolean) dapAn.get("laDapAnDung");

            actions.sendKeys(Keys.TAB).perform(); Thread.sleep(200); 
            if (isCorrect) actions.sendKeys(Keys.SPACE).perform();   
            
            actions.sendKeys(Keys.TAB).perform(); Thread.sleep(200); 
            actions.sendKeys(noiDungDapAn).perform();

            actions.sendKeys(Keys.TAB).perform(); Thread.sleep(200); 
        }
    }

    private void luuBaiTap() throws InterruptedException {
        WebElement nutLuu = wait.until(ExpectedConditions.presenceOfElementLocated(btnLuuBaiTap));
        js.executeScript("arguments[0].scrollIntoView(true);", nutLuu);
        js.executeScript("arguments[0].click();", nutLuu);
        System.out.println("Đã bấm nút Lưu. Chờ hệ thống tải lên...");
    }

    // =========================================================================
    // HÀM LẤY THÔNG BÁO BẢN QUÉT ĐỘNG - CHỐNG LỖI ẨN POPUP
    // =========================================================================
    public String getMessage() {
        try {
            System.out.println("Đang đợi popup thông báo xuất hiện...");
            WebDriverWait waitLong = new WebDriverWait(driver, 60);

            // 1. CHỜ XỬ LÝ UPLOAD: Kiểm tra xem có bảng "Đang tải" nào hiện không
            try {
                wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[contains(text(), 'Đang tải') or contains(text(), 'Uploading')]")));
                System.out.println("Hệ thống báo Đang tải lên... Xin vui lòng đợi.");
                waitLong.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//*[contains(text(), 'Đang tải') or contains(text(), 'Uploading')]")));
                Thread.sleep(1500); // Chờ 1.5s cho bảng thông báo thực sự mọc ra
            } catch (Exception e) {
                // Bỏ qua nếu quá trình upload diễn ra quá nhanh không kịp bắt chữ "Đang tải"
            }

            // 2. VÉT LƯỚI TÌM POPUP HIỂN THỊ
            System.out.println("Đang đọc nội dung bảng thông báo...");
            WebElement activePopup = null;
            long endTime = System.currentTimeMillis() + 15000; // Đợi tối đa 15s
            
            while (System.currentTimeMillis() < endTime) {
                // Tìm tất cả các bảng SweetAlert hoặc Snackbar có trong DOM
                List<WebElement> popups = driver.findElements(By.xpath("//div[contains(@class, 'swal2-popup')] | //div[contains(@class, 'v-snack__wrapper')]"));
                for (WebElement p : popups) {
                    if (p.isDisplayed()) { // Chọn đích danh thằng nào đang thực sự hiện trên màn hình
                        activePopup = p;
                        break;
                    }
                }
                if (activePopup != null) break;
                Thread.sleep(500); // Ngủ nửa giây rồi quét lại nếu chưa thấy
            }

            if (activePopup == null) {
                return "Không thấy thông báo";
            }

            // 3. LẤY NỘI DUNG VÀ CLICK OK
            String msg = activePopup.getText();
            if (msg == null || msg.trim().isEmpty()) {
                msg = (String) js.executeScript("return arguments[0].innerText;", activePopup);
            }
            System.out.println("Nội dung bắt được: " + msg.replace("\n", " "));

            try {
                System.out.println("Đang tìm nút OK của bảng hiện tại để đóng...");
                // Tìm nút OK nằm TỪ BÊN TRONG cái bảng đang hiển thị
                WebElement btnClose = activePopup.findElement(By.xpath(".//button[contains(@class, 'swal2-confirm') or contains(translate(., 'ok', 'OK'), 'OK') or contains(., 'Đồng ý')]"));
                js.executeScript("arguments[0].click();", btnClose);
                System.out.println(">>> Đã dùng Javascript ép click nút OK thành công.");
                
                WebDriverWait waitShort = new WebDriverWait(driver, 5);
                waitShort.until(ExpectedConditions.invisibilityOf(activePopup));
                System.out.println("Popup đã được đóng hoàn toàn.");
            } catch (Exception e) {
                System.out.println("Cảnh báo: Lỗi click chuột. Đang thử dùng phím Enter...");
                try {
                    actions.sendKeys(Keys.ENTER).perform();
                } catch (Exception ex) {}
            }
            
            return msg; 
        } catch (Exception e) {
            System.out.println("Lỗi quá trình lấy thông báo: " + e.getMessage());
            return "Không thấy thông báo";
        }
    }
}