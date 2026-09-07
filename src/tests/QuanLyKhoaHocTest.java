package tests;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import pages.QuanLyKhoaHocPage; 
import java.io.FileReader;
import org.apache.poi.xwpf.usermodel.*;
import java.io.File;
import java.io.FileOutputStream;

public class QuanLyKhoaHocTest {
    public static void main(String[] args) {
        
        try {
            // 1. CHUẨN BỊ ĐƯỜNG DẪN TƯƠNG ĐỐI
            String projectPath = System.getProperty("user.dir");
            String jsonFilePath = projectPath + "/data/khoa_hoc.json";
            String wordFilePath = projectPath + "/data/Report_KhoaHoc.docx";

            // 2. ĐỌC FILE JSON BẰNG JSON-SIMPLE
            JSONParser jsonparser = new JSONParser();
            FileReader reader = new FileReader(jsonFilePath);
            Object obj = jsonparser.parse(reader);
            JSONObject data = (JSONObject) obj;

            // 3. CHUẨN BỊ DRIVER (Chỉnh lại cho máy bạn)
            System.setProperty("webdriver.chrome.driver", "C:\\chromedriver.exe");
            WebDriver driver = new ChromeDriver();
            driver.get("https://elearning.plt.pro.vn/dang-nhap");
            driver.manage().window().maximize();

            // 4. ĐĂNG NHẬP
            driver.findElement(By.xpath("//input[@type='text']")).sendKeys("test.pltsolutions@gmail.com");
            driver.findElement(By.xpath("//input[@type='password']")).sendKeys("plt@intern_051224");
            driver.findElement(By.xpath("//button[contains(., 'Đăng nhập')]")).click();

            QuanLyKhoaHocPage qlkh = new QuanLyKhoaHocPage(driver);

            // 5. CHẠY KỊCH BẢN VỚI DATA TỪ JSON
            
            // Tạo khóa học
            qlkh.KhoaHoc(
                    (String) data.get("emailHV1"),
                    (String) data.get("emailHV2"),
                    (String) data.get("anhBiaKhoaHoc"),
                    (String) data.get("tenKhoaHoc"),
                    (String) data.get("moTaKhoaHoc")
            );

            // =====================================================================
            // [BƯỚC MỚI CẬP NHẬT]: LẤY KẾT QUẢ Ở DANH SÁCH & BÊN TRONG RỒI IN WORD
            // =====================================================================
            System.out.println("Đang thu thập Tiêu đề và Mô tả ở màn hình danh sách...");
            
            // Khai báo WebDriverWait để phục kích phần tử
            WebDriverWait wait = new WebDriverWait(driver, 10);
            
            // Nghỉ 3 giây cho bảng danh sách load xong
            try { Thread.sleep(3000); } catch (InterruptedException e) {}

            String expTenKhoaHoc = (String) data.get("tenKhoaHoc");
            String expMoTa = (String) data.get("moTaKhoaHoc");
            String expEmail1 = (String) data.get("emailHV1");

            String actTenKhoaHoc = "Không tìm thấy";
            String actMoTa = "Không tìm thấy";
            String actEmail = "Không tìm thấy";

            // 1. LẤY DATA Ở MÀN HÌNH DANH SÁCH (Đã thêm giáp sắt WebDriverWait)
            try {
                // Tìm thẻ <a> chứa tên khóa học (Cột Tiêu đề)
                WebElement linkTieuDe = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[contains(normalize-space(), '" + expTenKhoaHoc + "')]")));
                actTenKhoaHoc = linkTieuDe.getText();

                // Tìm cột Mô tả (Thường nằm ngay sau cột Tiêu đề)
                WebElement cotMoTa = driver.findElement(By.xpath("//a[contains(normalize-space(), '" + expTenKhoaHoc + "')]/ancestor::td/following-sibling::td[1]"));
                actMoTa = cotMoTa.getText();
            } catch (Exception e) {
                System.out.println("Cảnh báo: Không tìm thấy khóa học ở màn hình danh sách! Có thể do XPath hoặc mạng lag.");
            }

            // 2. VÀO CHI TIẾT KHÓA HỌC (Để lấy Email và làm các bước tiếp theo)
            System.out.println("Đang vào chi tiết khóa học để lấy Email...");
            qlkh.vaoChiTietKhoaHoc(expTenKhoaHoc);

            // 3. LẤY EMAIL HỌC VIÊN Ở BÊN TRONG
            try {
                // Quét xem trong bảng có cái td nào chứa dấu @ không
                actEmail = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//table//tr//td[contains(text(), '@')]"))).getText();
            } catch (Exception e) {
                System.out.println("Cảnh báo: Không tìm thấy Email thực tế bên trong.");
            }

            // 4. XUẤT REPORT WORD SAU KHI ĐÃ GOM ĐỦ 3 MÓN
            xuatReportRaWord(wordFilePath, expTenKhoaHoc, actTenKhoaHoc, expMoTa, actMoTa, expEmail1, actEmail);
            // =====================================================================

            // Lấy các mảng dữ liệu để làm Nội dung môn học
            String[] dsTieuDeChuong = convertJsonArrayToStringArray((JSONArray) data.get("dsTieuDeChuong"));
            String[] dsMoTaChuong = convertJsonArrayToStringArray((JSONArray) data.get("dsMoTaChuong"));
            String[] dsTieuDeBai = convertJsonArrayToStringArray((JSONArray) data.get("dsTieuDeBai"));
            String[] dsMoTaBai = convertJsonArrayToStringArray((JSONArray) data.get("dsMoTaBai"));
            String[] dsLinkVideo = convertJsonArrayToStringArray((JSONArray) data.get("dsLinkVideo"));
            
            // Tạo mảng path file
            String fPath = (String) data.get("fPath");
            String[] dsPathFile = {fPath, fPath, fPath, fPath};

            // Nhập nội dung
            qlkh.nhapNoiDungMonHoc(dsTieuDeChuong, dsMoTaChuong, dsTieuDeBai, dsMoTaBai, dsLinkVideo, dsPathFile);
            
            // Thêm diễn đàn
            qlkh.themDienDanThaoLuan(
                (String) data.get("tenDienDan"), 
                (String) data.get("moTaDienDan"), 
                (String) data.get("anhBiaKhoaHoc")
            );

            // Thêm Video Conference
            qlkh.themVideoConference(
                (String) data.get("tenVideo"), 
                (String) data.get("moTaDienDan"), 
                (String) data.get("linkMeeting")
            );
            
            qlkh.backtoKhoaHoc();
            
            System.out.println("Hoàn tất Test Case.");
            driver.quit();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Hàm phụ trợ chuyển đổi mảng JSON
    public static String[] convertJsonArrayToStringArray(JSONArray jsonArray) {
        if (jsonArray == null) return new String[0]; 
        String[] array = new String[jsonArray.size()];
        for (int i = 0; i < jsonArray.size(); i++) {
            array[i] = (String) jsonArray.get(i);
        }
        return array;
    }
    
    // Hàm phụ trợ xuất Report ra Word
    public static void xuatReportRaWord(String duongDanFile, 
            String expTen, String actTen, 
            String expMoTa, String actMoTa, 
            String expEmail, String actEmail) {
            try (XWPFDocument document = new XWPFDocument()) {
                // 1. Ghi Tiêu đề Report
                XWPFParagraph title = document.createParagraph();
                title.setAlignment(ParagraphAlignment.CENTER);
                XWPFRun titleRun = title.createRun();
                titleRun.setText("BÁO CÁO KẾT QUẢ KIỂM THỬ TỰ ĐỘNG - TẠO KHÓA HỌC");
                titleRun.setBold(true);
                titleRun.setFontSize(16);
                titleRun.setColor("003366");

                // Khoảng trắng
                document.createParagraph().createRun().setText(" ");

                // 2. Tạo Bảng So Sánh (4 cột)
                XWPFTable table = document.createTable();
                table.setWidth(10000);

                // Header
                XWPFTableRow header = table.getRow(0);
                header.getCell(0).setText("Hạng mục kiểm tra");
                header.addNewTableCell().setText("Kết quả Mong đợi (Expected)");
                header.addNewTableCell().setText("Kết quả Thực tế (Actual)");
                header.addNewTableCell().setText("Trạng thái");

                // Dữ liệu Tên Khóa Học
                XWPFTableRow row1 = table.createRow();
                row1.getCell(0).setText("Tên Khóa Học");
                row1.getCell(1).setText(expTen);
                row1.getCell(2).setText(actTen != null ? actTen : "Null");
                row1.getCell(3).setText(expTen.equals(actTen) ? "PASS" : "FAIL");

                // Dữ liệu Mô Tả
                XWPFTableRow row2 = table.createRow();
                row2.getCell(0).setText("Mô Tả Khóa Học");
                row2.getCell(1).setText(expMoTa);
                row2.getCell(2).setText(actMoTa != null ? actMoTa : "Null");
                row2.getCell(3).setText(expMoTa.equals(actMoTa) ? "PASS" : "FAIL");

                // Dữ liệu Email Học Viên
                XWPFTableRow row3 = table.createRow();
                row3.getCell(0).setText("Email Học Viên");
                row3.getCell(1).setText(expEmail);
                row3.getCell(2).setText(actEmail != null ? actEmail : "Null");
                row3.getCell(3).setText(actEmail != null && actEmail.contains(expEmail) ? "PASS" : "FAIL");

                // 3. Lưu file ra ổ cứng
                FileOutputStream out = new FileOutputStream(new File(duongDanFile));
                document.write(out);
                out.close();

                System.out.println("Đã xuất Report Word thành công tại: " + duongDanFile);

            } catch (Exception e) {
                System.out.println("Lỗi khi xuất file Word: " + e.getMessage());
            }
    }
}