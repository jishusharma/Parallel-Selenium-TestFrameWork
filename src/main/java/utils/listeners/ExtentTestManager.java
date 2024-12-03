package utils.listeners;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class ExtentTestManager {
    private static final Logger LOGGER = LogManager.getLogger(ExtentTestManager.class);
    private static final ConcurrentHashMap<Long, ExtentTest> extentTestMap = new ConcurrentHashMap<>();
    private static final ExtentReports extent = ExtentManager.getInstance();

    private ExtentTestManager() {
        // Private constructor to prevent instantiation
    }

    public static synchronized ExtentTest getTest() {
        return Optional.ofNullable(extentTestMap.get(Thread.currentThread().getId()))
                .orElseGet(() -> {
                    LOGGER.warn("No ExtentTest instance found for the current thread. Creating a new one.");
                    return startTest("Unnamed Test");
                });
    }

    public static synchronized void endTest() {
        Optional.ofNullable(extentTestMap.remove(Thread.currentThread().getId()))
                .ifPresent(test -> {
                    extent.flush();
                    LOGGER.info("Ended test: {}", test.getModel().getName());
                });
    }

    public static synchronized ExtentTest startTest(String testName) {
        ExtentTest test = extent.createTest(testName);
        extentTestMap.put(Thread.currentThread().getId(), test);
        LOGGER.info("Started test: {}", testName);
        return test;
    }
}