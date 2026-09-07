package tests;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.testng.Assert;
import org.testng.annotations.*;
import pages.LoginPage;
import pages.QuanLyBaiTapPage;
import java.io.FileReader;


import org.apache.poi.xwpf.usermodel.*;
import java.io.File;
import java.io.FileOutputStream;

public class QuanLyBaiTapTest {

    WebDriver driver;
    QuanLyBaiTapPage quanLyBaiTapPage;
    String jsonFilePath = System.getProperty("user.dir") + "/data/bai_tap.json"; 
    String wordFilePath = System.getProperty("user.dir") + "/data/Report_BaiTap.docx";

    @BeforeTest
    public void setup() throws InterruptedException {
        System.setProperty("webdriver.edge.driver", "C:\\edgedriver.exe"); 
        driver = new EdgeDriver();
        driver.manage().window().maximize();

        LoginPage loginPage = new LoginPage(driver);
        quanLyBaiTapPage = new QuanLyBaiTapPage(driver);

        driver.get("https://elearning.plt.pro.vn/dang-nhap");
        loginPage.login("test.pltsolutions@gmail.com", "plt@intern_051224");
        Thread.sleep(3000); 
    }

    @BeforeMethod
    public void prepare() {
        quanLyBaiTapPage.goToThemBaiTap();
    }

    @DataProvider(name = "baiTapData")
    public Object[][] getTestData() throws Exception {
        JSONParser parser = new JSONParser();
        JSONArray dataList = (JSONArray) parser.parse(new FileReader(jsonFilePath));
        Object[][] data = new Object[dataList.size()][1];
        for (int i = 0; i < dataList.size(); i++) {
            data[i][0] = dataList.get(i);
        }
        return data;
    }

    @Test(dataProvider = "baiTapData")
    public void testThemBaiTap(JSONObject data) throws InterruptedException {
        // 1. Thực thi tạo bài tập
        quanLyBaiTapPage.themBaiTap(data);
        
        // 2. Lấy thông báo (Bên trong hàm này đã tự động chờ upload và NHẤN NÚT OK)
        String actMsg = quanLyBaiTapPage.getMessage(); 
        
        // 3. Lấy dữ liệu mong đợi từ file JSON
        String expMsg = (String) data.get("expectedMessage");
        String tenBaiTap = (String) data.get("tenBaiTap");
        
        // 4. Xuất Report ra file Word ngay lập tức (Giống y hệt cấu trúc bảng mẫu của bro)
        System.out.println("Đang xuất Report ra file Word...");
        xuatReportRaWord(wordFilePath, tenBaiTap, expMsg, actMsg);
        
        // 5. So sánh để báo cáo Pass/Fail cho hệ thống TestNG
        Assert.assertTrue(actMsg != null && actMsg.contains(expMsg), 
            "Sai thông báo! Mong đợi: " + expMsg + " nhưng thực tế thấy: " + actMsg);
    }

    // Tự động tắt trình duyệt sau khi test xong
    @AfterTest
    public void teardown() {
        if(driver != null) {
            System.out.println("Kết thúc bài test. Đang đóng trình duyệt...");
            driver.quit();
        }
    }

    public static void xuatReportRaWord(String duongDanFile, String tenBaiTap, String expMsg, String actMsg) {
        try (XWPFDocument document = new XWPFDocument()) {

            XWPFParagraph title = document.createParagraph();
            title.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun titleRun = title.createRun();
            titleRun.setText("BÁO CÁO KẾT QUẢ KIỂM THỬ - THÊM BÀI TẬP");
            titleRun.setBold(true);
            titleRun.setFontSize(16);
            titleRun.setColor("003366");

            document.createParagraph().createRun().setText("Tên Bài tập thực thi: " + tenBaiTap);
            document.createParagraph().createRun().setText(" ");


            XWPFTable table = document.createTable();
            table.setWidth(10000);


            XWPFTableRow header = table.getRow(0);
            header.getCell(0).setText("Hạng mục kiểm tra");
            header.addNewTableCell().setText("Kết quả Mong đợi (Expected)");
            header.addNewTableCell().setText("Kết quả Thực tế (Actual)");
            header.addNewTableCell().setText("Trạng thái");

            XWPFTableRow row1 = table.createRow();
            row1.getCell(0).setText("Thông báo hệ thống"); 
            row1.getCell(1).setText(expMsg); 
            
   
            String actMsgClean = actMsg != null ? actMsg.replace("\nOK", "").replace("\nOk", "") : "Không thấy thông báo";
            row1.getCell(2).setText(actMsgClean); 
            // Xử lý Trạng thái (PASS / FAIL)
            boolean isPass = actMsg != null && actMsg.contains(expMsg);
            XWPFRun statusRun = row1.getCell(3).addParagraph().createRun();
            statusRun.setText(isPass ? "PASS" : "FAIL");
            statusRun.setBold(true);
            statusRun.setColor(isPass ? "00B050" : "FF0000"); // Màu xanh nếu Pass, Đỏ nếu Fail

            // Ghi file ra ổ đĩa
            FileOutputStream out = new FileOutputStream(new File(duongDanFile));
            document.write(out);
            out.close();

            System.out.println(">>> Đã xuất báo cáo Word (Expected vs Actual) tại: " + duongDanFile);

        } catch (Exception e) {
            System.out.println("Lỗi khi tạo file Word: " + e.getMessage());
            System.out.println("Lưu ý: Đảm bảo thư viện Apache POI (poi-ooxml) đã được thêm vào pom.xml / referenced libraries!");
        }
    }
}