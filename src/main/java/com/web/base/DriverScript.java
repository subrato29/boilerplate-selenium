package com.web.base;

import java.io.File;
import java.io.IOException;
import java.util.TreeMap;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.asserts.SoftAssert;

import com.web.accelerator.TestWeb;
import com.web.reports.ReportUtil;
import com.web.support.WebDriverFactory;
import com.web.support.Xls_Reader;
import com.web.utilities.Constants;
import com.web.utilities.Util;
import com.web.utilities.Zip;

public class DriverScript {

    public static String TEST_DATA_PATH = System.getProperty("user.dir") + "/src/main/java/com/mobile/data";
    public static Xls_Reader xls = null, xlsController = new Xls_Reader(TEST_DATA_PATH + File.separator + "testcontroller.xlsx");
    public static WebDriver webDriver = null;
    public static RemoteWebDriver mobileDriver = null;
    public static int rowNum = 2, rowNumController = 2;
    public static int rowNumExecutableTC = 2;
    public static int count = 0;
    public static String testCaseName;
    public static String testCaseId, testType;
    public static SoftAssert softAssert;
    public static int errorCount = 0;
    public static DesiredCapabilities capabilities = null;
    public static boolean continueRun = false;
    public static boolean logout = false;
    public static int maxTimeOut = 0;
    public static boolean isWebStarted = false;
    public static String BROWSER = null, baseUrl = null, testEnv = null, screenName = null;

    static TreeMap < Integer, String > executableTCIndex = new TreeMap < Integer, String > ();
    public static int getRowNumForExecutableTestCases() {
        while (rowNumExecutableTC <= xlsController.getRowCount(Constants.TEST_DATA)) {
            if (xlsController.getCellData(Constants.TEST_DATA, Constants.TEST_CASE_RUNMODE, rowNumExecutableTC).toUpperCase().equals(Constants.TEST_CASE_RUNMODE_YES)) {
                count++;
            }
            rowNumExecutableTC++;
        }
        rowNumExecutableTC = 2;
        return count;
    }

    public static final int countOfExecutableTestCases = getRowNumForExecutableTestCases();


    public static int getMaxTimeOut() {
        int maxTimeOut;
        try {
            maxTimeOut = Integer.parseInt(Util.getProperty("MaxTimeOut"));
        } catch (Throwable t) {
            maxTimeOut = 2;
        }
        return maxTimeOut;
    }

    public static String getTestDataSheetName() {
        String testDataSheet = xlsController.getCellData(Constants.TEST_DATA, Constants.TEST_DATA_SHEET_NAME, rowNum);
        return testDataSheet;
    }


    @BeforeMethod
    public void beforeMethod() {

    }

    @AfterMethod
    public void afterMethod() {
        TestWeb.quit();
        ReportUtil.test = null;
    }


    @AfterClass()
    public void afterClass() throws IOException {
        TestWeb.quit();
        Util.openHTMLReport();
        Zip.zipFile();
    }


    @BeforeClass()
    public void init() throws IOException {

    }

    /**
     * 
     * @param tcId
     * @param app =>
     * @return
     */
    public static boolean isTestCaseRunnable(String tcId) {
        boolean isTestCaseRunnable = false;
        try {
            BROWSER = Util.getProperty("Browser");
        } catch (IOException e) {}
        isWebStarted = false;
        continueRun = false;
        rowNumController = xlsController.getCellRowNum(Constants.TEST_DATA, Constants.TEST_CASE_ID, tcId);
        rowNum = rowNumController;
        testCaseId = tcId;
        testCaseName = xlsController.getCellData(Constants.TEST_DATA, Constants.TEST_CASE_NAME, rowNum);
        if (xlsController.getCellData(Constants.TEST_DATA, Constants.TEST_CASE_RUNMODE, rowNum).equalsIgnoreCase(Constants.TEST_CASE_RUNMODE_YES)) {
            TestWeb.quit();
            isWebStarted = true;
            webDriver = null;
            if (webDriver == null) {
                try {
                    testEnv = Util.getProperty("test_environment").toUpperCase();
                } catch (IOException e) {}
                getUrl();
                isTestCaseRunnable = true;
                xls = new Xls_Reader(TEST_DATA_PATH + File.separator + getTestDataSheetName() + ".xlsx");
                initializeTest();
            }
        } else {
            webDriver = null;
            isTestCaseRunnable = false;
        }
        return isTestCaseRunnable;
    }

    public static void getUrl() {
        if (testEnv.toUpperCase().equals("PROD01")) {
            baseUrl = "https://www.website.com";
        } else if (testEnv.toUpperCase().equals("PROD02")) {
            baseUrl = "https://www.website.com";
        }
    }

    public static void initializeTest() {
        continueRun = true;
        try {
            if (Util.getGlobalProperty("ExecutionPlatform").toUpperCase().equals("JENKINS")) {
                System.setProperty("webdriver.chrome.silentOutput", "true");
                System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "/Drivers/chromedriver.exe");
                ChromeOptions opt = new ChromeOptions();
                capabilities = new DesiredCapabilities();
                capabilities.setCapability("chromeOptions", opt);
                capabilities.setBrowserName("chrome");
                opt.addArguments("--headless", "--no-sandbox", "--disable-gpu", "--window-size=1920,1200", "--ignore-certificate-errors");
                webDriver = new ChromeDriver(opt);
                TestWeb.deleteCookie();
                webDriver.get(baseUrl);
                System.out.println("WebDriver is instantiated..........");
            } else {
                try {
                    if (Util.getProperty("HeadlessBrowser").toUpperCase().equals("YES")) {
                        System.setProperty("webdriver.chrome.silentOutput", "true");
                        System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "/Drivers/chromedriver.exe");
                        ChromeOptions opt = new ChromeOptions();
                        opt.addArguments("--headless", "--disable-gpu", "--window-size=1920,1200", "--ignore-certificate-errors");
                        webDriver = new ChromeDriver(opt);
                        webDriver.get(baseUrl);
                        System.out.println("WebDriver is instantiated..........");
                    } else {
                        WebDriverFactory.initialize();
                        System.out.println("WebDriver is instantiated..........");
                    }
                } catch (IOException e) {
                    continueRun = false;
                    ReportUtil.markFailed("WebDriver has not been instantiated.");
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            continueRun = false;
            ReportUtil.markFailed("WebDriver has not been instantiated.");
            e.printStackTrace();
        }
    }

}