package tests;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver; 
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import org.apache.poi.xwpf.usermodel.*;

import pages.DashboardPage;
import pages.LoginPage;
import pages.QuanLyHocVienPage;

public class QuanLyHocVienTest {

    WebDriver driver;
    JSONArray dataList;
    String jsonFilePath = System.getProperty("user.dir") + "/data/hoc_vien.json";
    LoginPage loginPage;
    DashboardPage dashboardPage;
    QuanLyHocVienPage quanLyHocVienPage;

    @BeforeTest
    public void setup() throws Exception {
        JSONParser parser = new JSONParser();
        dataList = (JSONArray) parser.parse(new FileReader(jsonFilePath));

        System.setProperty("webdriver.chrome.driver", "C:\\chromedriver.exe");
        driver = new ChromeDriver();
        
        driver.get("https://elearning.plt.pro.vn/dang-nhap");
        driver.manage().window().maximize();

        loginPage = new LoginPage(driver);
        dashboardPage = new DashboardPage(driver);
        quanLyHocVienPage = new QuanLyHocVienPage(driver);
        
        loginPage.login("test.pltsolutions@gmail.com", "plt@intern_051224");
        Thread.sleep(2000);

        dashboardPage.goToQuanLyHocVien();
        Thread.sleep(1000);
    }

    // --- TEST 1: THÊM HỌC VIÊN ---
    @Test(priority = 1, enabled = true)
    public void Them_Moi_Hoc_Vien() throws Exception {
        XWPFDocument document = taoTrangBiaWord("BÁO CÁO TEST: THÊM HỌC VIÊN");
        XWPFTable table = taoBangReport(document);
        boolean allPass = true; 

        for (Object o : dataList) {
            JSONObject hocVien = (JSONObject) o;
            String email = (String) hocVien.get("email");
            String expMsg = (String) hocVien.get("expectedMessage");

            String actMsg = quanLyHocVienPage.themHocVien(hocVien);
            ghiDongVaoReport(table, email, expMsg, actMsg);
            
            if (actMsg == null || !actMsg.toLowerCase().contains(expMsg.toLowerCase())) {
                allPass = false;
            }
        }
        luuFileWord(document, "/data/Report_ThemHocVien.docx");
        Assert.assertTrue(allPass, "Có lỗi ở bước Thêm. Check file Word!");
    }

    // --- TEST 2: SỬA HỌC VIÊN ---
    @Test(priority = 2, enabled = true)
    public void Sua_Hang_Loat() throws Exception {
        XWPFDocument document = taoTrangBiaWord("BÁO CÁO TEST: CẬP NHẬT HỌC VIÊN");
        XWPFTable table = taoBangReport(document);
        boolean allPass = true;
        
        // Bật cờ cho người đầu tiên đi tìm ô Search
        boolean isFirstTime = true;

        for (Object o : dataList) {
            JSONObject hv = (JSONObject) o;
            String emailTarget = (String) hv.get("email");
            String expMsg = "thành công"; 

            String actMsg = quanLyHocVienPage.suaHocVien(emailTarget, isFirstTime);
            ghiDongVaoReport(table, emailTarget, expMsg, actMsg);
            
            if (actMsg == null || !actMsg.toLowerCase().contains(expMsg.toLowerCase())) {
                allPass = false;
            }
            // Người thứ 2 trở đi tận dụng con trỏ tự động, không tìm lại nữa
            isFirstTime = false; 
        }
        luuFileWord(document, "/data/Report_SuaHocVien.docx");
        Assert.assertTrue(allPass, "Có lỗi ở bước Sửa. Check file Word!");
    }

    // --- TEST 3: XÓA HỌC VIÊN ---
    @Test(priority = 3, enabled = true)
    public void Xoa_Hang_Loat() throws Exception {
        XWPFDocument document = taoTrangBiaWord("BÁO CÁO TEST: XÓA HỌC VIÊN");
        XWPFTable table = taoBangReport(document);
        boolean allPass = true;

        // Trả lại logic: Bật cờ cho người đầu tiên y chang vòng Sửa
        boolean isFirstTime = true;

        for (Object o : dataList) {
            JSONObject hv = (JSONObject) o;
            String emailTarget = (String) hv.get("email");
            String expMsg = "thành công"; 

            // Truyền cờ isFirstTime linh hoạt, không fix cứng 'true' nữa
            String actMsg = quanLyHocVienPage.xoaHocVien(emailTarget, isFirstTime);
            ghiDongVaoReport(table, emailTarget, expMsg, actMsg);
            
            if (actMsg == null || !actMsg.toLowerCase().contains(expMsg.toLowerCase())) {
                allPass = false;
            }
            
            // Xóa xong người đầu tiên, tắt cờ đi để Robot tự múa Ctrl+A Delete
            isFirstTime = false;
        }
        luuFileWord(document, "/data/Report_XoaHocVien.docx");
        Assert.assertTrue(allPass, "Có lỗi ở bước Xóa. Check file Word!");
    }

    @AfterTest
    public void afterTest() {
        if(driver != null) {
            driver.quit();
        }
    }

    // --- CÁC HÀM XUẤT REPORT (GIỮ NGUYÊN) ---
    private XWPFDocument taoTrangBiaWord(String tieuDe) {
        XWPFDocument document = new XWPFDocument();
        XWPFParagraph title = document.createParagraph();
        title.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun titleRun = title.createRun();
        titleRun.setText(tieuDe);
        titleRun.setBold(true);
        titleRun.setFontSize(16);
        titleRun.setColor("003366");
        document.createParagraph().createRun().setText(" "); 
        return document;
    }

    private XWPFTable taoBangReport(XWPFDocument document) {
        XWPFTable table = document.createTable();
        table.setWidth(10000);
        XWPFTableRow header = table.getRow(0);
        header.getCell(0).setText("Email Test");
        header.addNewTableCell().setText("Expected Result");
        header.addNewTableCell().setText("Actual Result");
        header.addNewTableCell().setText("Status");
        return table;
    }

    private void ghiDongVaoReport(XWPFTable table, String itemTest, String expMsg, String actMsg) {
        XWPFTableRow row = table.createRow();
        row.getCell(0).setText(itemTest);
        row.getCell(1).setText(expMsg != null ? expMsg : "N/A");
        
        String cleanActMsg = actMsg != null ? actMsg.replace("\nOK", "").replace("\nOk", "") : "Không thấy thông báo";
        row.getCell(2).setText(cleanActMsg);
        
        boolean isPass = actMsg != null && expMsg != null && actMsg.toLowerCase().contains(expMsg.toLowerCase());
        XWPFRun statusRun = row.getCell(3).addParagraph().createRun();
        statusRun.setText(isPass ? "PASS" : "FAIL");
        statusRun.setBold(true);
        statusRun.setColor(isPass ? "00B050" : "FF0000"); 
    }

    private void luuFileWord(XWPFDocument document, String relativePath) {
        try {
            String fullPath = System.getProperty("user.dir") + relativePath;
            FileOutputStream out = new FileOutputStream(new File(fullPath));
            document.write(out);
            out.close();
        } catch (Exception e) {}
    }
}