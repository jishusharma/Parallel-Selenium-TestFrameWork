package utils.listeners;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.nio.file.Paths;

public class ExtentManager {
    private static final Logger LOGGER = LogManager.getLogger(ExtentManager.class);
    private static final String REPORT_FILE_NAME = "ExecutionReport.html";
    private static final String REPORT_FILE_PATH = Paths.get(System.getProperty("user.dir"), "target", "extent-reports").toString();
    private static final String REPORT_FILE_LOCATION = Paths.get(REPORT_FILE_PATH, REPORT_FILE_NAME).toString();

    private static ExtentReports extent;

    private ExtentManager() {
        // Private constructor to prevent instantiation
    }

    public static synchronized ExtentReports getInstance() {
        if (extent == null) {
            extent = createInstance();
        }
        return extent;
    }

    private static ExtentReports createInstance() {
        String fileName = getReportPath();
        ExtentHtmlReporter htmlReporter = new ExtentHtmlReporter(fileName);
        configureHtmlReporter(htmlReporter);

        extent = new ExtentReports();
        extent.attachReporter(htmlReporter);
        setSystemInfo(extent);

        return extent;
    }

    private static void configureHtmlReporter(ExtentHtmlReporter htmlReporter) {
        htmlReporter.config().setTheme(Theme.DARK);
        htmlReporter.config().setDocumentTitle(REPORT_FILE_NAME);
        htmlReporter.config().setEncoding("utf-8");
        htmlReporter.config().setReportName(REPORT_FILE_NAME);
        htmlReporter.config().setTimeStampFormat("MMM dd, yyyy HH:mm:ss");
    }

    private static void setSystemInfo(ExtentReports extent) {
        extent.setSystemInfo("Environment", "Parallel Running Mode");
        extent.setSystemInfo("Application", "My Demo Automation Project");
        extent.setSystemInfo("ReportDir", new File(REPORT_FILE_PATH).getAbsolutePath());
    }

    private static String getReportPath() {
        File reportDir = new File(REPORT_FILE_PATH);
        if (!reportDir.exists() && !reportDir.mkdirs()) {
            LOGGER.error("Failed to create directory: {}", REPORT_FILE_PATH);
        }
        LOGGER.info("Report Path: {}", REPORT_FILE_LOCATION);
        return REPORT_FILE_LOCATION;
    }

    public static String getReportFilePath() {
        return REPORT_FILE_PATH;
    }
}
