package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.io.File; 

public class QuanLyKhoaHocPage {
    private WebDriver driver;
	private By btn_KhoaHoc = By.xpath("//a[contains(@href, '/quan-tri-vien/khoa-hoc')]");
	private By btn_ThemMoi = By.xpath("//a[contains(@href, '/quan-tri-vien/khoa-hoc/them-moi')]");
	private By btn_ThemHocVien = By.xpath("//button[contains(., 'THÊM HỌC VIÊN') or contains(., 'Thêm học viên')]");
	private By tb_HV1 = By.xpath("//input[@name='email_or_id_student']");
	private By tb_HV2 = By.xpath("//input[@name='email_or_id_student']");
	private By btn_XacNhanThem = By.xpath("//button[normalize-space()='Thêm' or normalize-space()='THÊM']");
	private By btn_AnhBiaKhoaHoc = By.xpath("//div[contains(@class, 'v-file-input')]//input[@type='file']");
	private By tb_TenKhoaHoc = By.xpath("//label[contains(text(), 'Tên khoá học') or contains(text(), 'Tên khóa học')]/following-sibling::input");
	private By tb_MoTaVeKhoaHoc = By.xpath("//label[contains(text(), 'Mô tả')]/following-sibling::textarea");
	private By btn_ThemMoi_KhoaHoc = By.xpath("//button[@type='submit' and (contains(., 'THÊM MỚI') or contains(., 'Thêm mới'))]");
	private By btn_OK_ThanhCong = By.xpath("//button[contains(@class, 'swal2-confirm') and text()='OK']");
	private By tab_NoiDungMonHoc = By.xpath("//div[@role='tab' and (contains(., 'Nội dung') or contains(., 'NỘI DUNG'))]");
	private By btn_ThemChuongHoc = By.xpath("//button[contains(., 'THÊM CHƯƠNG HỌC') or contains(., 'Thêm chương học')]");
	private By btn_LuuNoiDung = By.xpath("//button[contains(., 'LƯU NỘI DUNG MÔN HỌC') or contains(., 'Lưu nội dung môn học')]");
	
    public QuanLyKhoaHocPage(WebDriver driver) {
        this.driver = driver;
    }

    public void KhoaHoc(String inHV1, String inHV2, String inAnhBiaKhoaHoc, String inTenKhoaHoc, String inMoTaVeKhoaHoc) {
    	System.out.println("Bắt đầu tab Quản Lý Khóa Học");
    	
		WebDriverWait wait = new WebDriverWait(driver, 10);
		wait.until(ExpectedConditions.elementToBeClickable(btn_KhoaHoc)).click();
		wait.until(ExpectedConditions.elementToBeClickable(btn_ThemMoi)).click();
		
		System.out.println("Đang bắt đầu thêm học viên...");
		wait.until(ExpectedConditions.elementToBeClickable(btn_ThemHocVien)).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(tb_HV1)).sendKeys(inHV1);
		wait.until(ExpectedConditions.elementToBeClickable(btn_XacNhanThem)).click();
		
		wait.until(ExpectedConditions.elementToBeClickable(btn_ThemHocVien)).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(tb_HV2)).sendKeys(inHV2);
		wait.until(ExpectedConditions.elementToBeClickable(btn_XacNhanThem)).click();
		
		System.out.println("Đang thêm ảnh bìa khóa học...");
        File fileAnhBia = new File("data/" + inAnhBiaKhoaHoc);
		driver.findElement(btn_AnhBiaKhoaHoc).sendKeys(fileAnhBia.getAbsolutePath());
		
		System.out.println("Đang thêm tên và mô tả khóa học...");
		wait.until(ExpectedConditions.visibilityOfElementLocated(tb_TenKhoaHoc)).sendKeys(inTenKhoaHoc);
		wait.until(ExpectedConditions.visibilityOfElementLocated(tb_MoTaVeKhoaHoc)).sendKeys(inMoTaVeKhoaHoc);
		
		wait.until(ExpectedConditions.elementToBeClickable(btn_ThemMoi_KhoaHoc)).click();
		wait.until(ExpectedConditions.elementToBeClickable(btn_OK_ThanhCong)).click();
		System.out.println("Thêm mới khóa học thành công.");
	}

    public void vaoChiTietKhoaHoc(String tenKhoaHoc) {
        WebDriverWait wait = new WebDriverWait(driver, 10);
        try { Thread.sleep(2000); } catch (InterruptedException e) {}
        By link_KhoaHoc = By.xpath("//a[contains(text(), '" + tenKhoaHoc + "')]");
        wait.until(ExpectedConditions.elementToBeClickable(link_KhoaHoc)).click();
    }
    
    public void nhapNoiDungMonHoc(String[] dsTieuDeChuong, String[] dsMoTaChuong, 
                                  String[] dsTieuDeBai, String[] dsMoTaBai,
                                  String[] dsLinkVideo, String[] dsPathFile) {
        WebDriverWait wait = new WebDriverWait(driver, 15);
        System.out.println("Bấm vào tab nội dung môn học...");
        wait.until(ExpectedConditions.elementToBeClickable(tab_NoiDungMonHoc)).click();
        
        for (int i = 0; i < 2; i++) {
            wait.until(ExpectedConditions.elementToBeClickable(btn_ThemChuongHoc)).click();
            try { Thread.sleep(500); } catch (InterruptedException e) {}
        }

        System.out.println("Đang tự động thiết lập Chương và Bài học...");
        for (int i = 1; i <= 2; i++) {
            By btn_MoChuong = By.xpath("//button[contains(@class, 'v-expansion-panel-header') and contains(., 'Chương " + i + "')]");
            wait.until(ExpectedConditions.elementToBeClickable(btn_MoChuong)).click();
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//label[contains(text(), 'Tiêu đề chương')]/following-sibling::input)[" + i + "]"))).sendKeys(dsTieuDeChuong[i-1]);
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//label[contains(text(), 'Mô tả về chương học')]/following-sibling::textarea)[" + i + "]"))).sendKeys(dsMoTaChuong[i-1]);

            By btn_ThemBaiHoc = By.xpath("(//button[contains(translate(., 'T', 't'), 'thêm bài học')])[" + i + "]");
            for (int j = 0; j < 2; j++) {
                wait.until(ExpectedConditions.elementToBeClickable(btn_ThemBaiHoc)).click();
                try { Thread.sleep(500); } catch (InterruptedException e) {}
            }

            for (int j = 1; j <= 2; j++) {
                int viTriO = (i - 1) * 2 + j;
                By btn_MoBai = By.xpath("(//button[contains(@class, 'v-expansion-panel-header') and contains(., 'Chương " + i + "')]/following-sibling::div//button[contains(., 'Bài số " + j + "')])[1]");
                wait.until(ExpectedConditions.elementToBeClickable(btn_MoBai)).click();
                try { Thread.sleep(500); } catch (InterruptedException e) {}

                wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//label[contains(text(), 'Tiêu đề bài học')]/following-sibling::input)[" + viTriO + "]"))).sendKeys(dsTieuDeBai[viTriO - 1]);
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//label[contains(text(), 'Mô tả về bài học')]/following-sibling::textarea)[" + viTriO + "]"))).sendKeys(dsMoTaBai[viTriO - 1]);

                By btn_TaiLieu = By.xpath("(//button[contains(translate(., 'T', 't'), 'thêm tài liệu học')])[" + viTriO + "]");
                wait.until(ExpectedConditions.elementToBeClickable(btn_TaiLieu)).click();
                try { Thread.sleep(1000); } catch (InterruptedException e) {}

                WebElement radioVideo = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(@class, 'v-dialog--active')]//label[contains(text(), 'Xem video')]")));
                ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", radioVideo);
                try { Thread.sleep(500); } catch (InterruptedException e) {}

                wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class, 'v-dialog--active')]//label[contains(text(), 'Tên tài liệu')]/following-sibling::input"))).sendKeys("Clip bài " + viTriO);
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class, 'v-dialog--active')]//label[contains(text(), 'Video youtube url')]/following-sibling::input"))).sendKeys(dsLinkVideo[viTriO - 1]);
                wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[contains(@class, 'v-dialog--active')]//button[contains(translate(., 'T', 't'), 'thêm')]"))).click();
                wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("v-overlay__scrim")));

                wait.until(ExpectedConditions.elementToBeClickable(btn_TaiLieu)).click();
                try { Thread.sleep(1000); } catch (InterruptedException e) {}

                org.openqa.selenium.WebElement radioFile = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(@class, 'v-dialog--active')]//label[contains(text(), 'File tải về')]")));
                ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", radioFile);

                wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class, 'v-dialog--active')]//label[contains(text(), 'Tên tài liệu')]/following-sibling::input"))).sendKeys("Tài liệu bài " + viTriO);
                
                File docFile = new File("data/" + dsPathFile[viTriO - 1]);
                driver.findElement(By.xpath("//div[contains(@class, 'v-dialog--active')]//input[@type='file']")).sendKeys(docFile.getAbsolutePath());
                wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[contains(@class, 'v-dialog--active')]//button[contains(translate(., 'T', 't'), 'thêm')]"))).click();
                
                wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("v-overlay__scrim")));
            }
        }
        wait.until(ExpectedConditions.elementToBeClickable(btn_LuuNoiDung)).click();
        wait.until(ExpectedConditions.elementToBeClickable(btn_OK_ThanhCong)).click();
        System.out.println("Lưu thành công nội dung môn học.");
    }
    
    public void themDienDanThaoLuan(String tenDienDan, String moTaDienDan, String pathAnhBia) {
    	System.out.println("Chuyển sang tab diễn đàn thảo luận...");
        try { Thread.sleep(2000); } catch (InterruptedException e) {}

        org.openqa.selenium.JavascriptExecutor js = (org.openqa.selenium.JavascriptExecutor) driver;
        String scriptClickTab = "var tabs = document.querySelectorAll('.v-tab');" +
                                "for(var i=0; i<tabs.length; i++){" +
                                "  if(tabs[i].innerText.includes('DIỄN ĐÀN THẢO LUẬN')){" +
                                "    tabs[i].click(); break; } }";
        js.executeScript(scriptClickTab);
        
        try { Thread.sleep(1000); } catch (InterruptedException e) {}
        WebDriverWait wait = new WebDriverWait(driver, 15);
        
        WebElement btnThem = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[contains(., 'Thêm diễn đàn mới')]")));
        js.executeScript("arguments[0].click();", btnThem);
        
        try { Thread.sleep(1000); } catch (InterruptedException e) {}
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@name='name_conversation']"))).sendKeys(tenDienDan);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//textarea[@name='des_conversation']"))).sendKeys(moTaDienDan);
        
        File fileAnhDD = new File("data/" + pathAnhBia);
        driver.findElement(By.xpath("//div[contains(@class, 'v-file-input')]//input[@type='file']")).sendKeys(fileAnhDD.getAbsolutePath());

        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[contains(@class, 'v-card__actions')]//button[contains(., 'Thêm')]"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(@class, 'swal2-confirm') and text()='OK']"))).click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("swal2-container")));
    }
    
    public void themVideoConference(String tenVideo, String moTaVideo, String linkMeeting) {
    	System.out.println("Chuyển sang tab video conference...");
        WebDriverWait wait = new WebDriverWait(driver, 10); 
        org.openqa.selenium.JavascriptExecutor js = (org.openqa.selenium.JavascriptExecutor) driver;

        String scriptClickTab = "var tabs = document.querySelectorAll('.v-tab');" +
                                "for(var i=0; i<tabs.length; i++){" +
                                "  if(tabs[i].innerText.includes('VIDEO CONFERENCE')){" +
                                "    tabs[i].click(); break; } }";
        js.executeScript(scriptClickTab);
        try { Thread.sleep(2000); } catch (InterruptedException e) {}
        
        try {
            WebElement btnMo = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[contains(., 'THÊM VIDEO CONFERENCE MỚI')]")));
            js.executeScript("arguments[0].click();", btnMo);
            Thread.sleep(1000); 
        } catch (Exception e) {
            js.executeScript("document.querySelectorAll('button').forEach(b => { if(b.innerText.includes('THÊM VIDEO CONFERENCE MỚI')) b.click(); });");
        }

        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class, 'v-dialog--active')]//label[contains(text(), 'Thêm video conference mới')]/following-sibling::input"))).sendKeys(tenVideo);
            driver.findElement(By.xpath("//div[contains(@class, 'v-dialog--active')]//textarea[contains(@name, 'des_video_conference')]")).sendKeys(moTaVideo);
            driver.findElement(By.xpath("//div[contains(@class, 'v-dialog--active')]//input[contains(@name, 'google_meeting_url')]")).sendKeys(linkMeeting);

            WebElement btnThem = driver.findElement(By.xpath("//div[contains(@class, 'v-dialog--active')]//button[contains(., 'THÊM') or contains(., 'Thêm')]"));
            js.executeScript("arguments[0].click();", btnThem);
        } catch (Exception e) {
            System.out.println("Lỗi điền form video: " + e.getMessage());
        }

        try {
            WebElement btnOK = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(@class, 'swal2-confirm') and text()='OK']")));
            js.executeScript("arguments[0].click();", btnOK);
        } catch (Exception e) {}

        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("swal2-container")));
        System.out.println("Hoàn tất quy trình tạo khóa học!");
    }
    
    public void backtoKhoaHoc() {
    	WebDriverWait wait = new WebDriverWait(driver, 10);
    	wait.until(ExpectedConditions.elementToBeClickable(btn_KhoaHoc)).click();
    }
}