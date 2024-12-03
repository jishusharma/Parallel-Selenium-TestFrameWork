package ai;

import org.apache.commons.io.FileUtils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;

import java.io.File;
import java.io.IOException;

public class OpenCVUtil implements AIUtil {
    private static final Logger LOGGER = LogManager.getLogger(OpenCVUtil.class);
    private static OpenCVUtil instance;

    static {
        try {
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        } catch (UnsatisfiedLinkError e) {
            String libraryPath = System.getProperty("java.library.path");
            System.err.println("Native library path: " + libraryPath);
            System.err.println("Trying to load library: " + Core.NATIVE_LIBRARY_NAME);
            e.printStackTrace();
            System.exit(1);
        }
    }

    private OpenCVUtil() {
        // Private constructor to prevent instantiation
    }

    public static synchronized OpenCVUtil getInstance() {
        if (instance == null) {
            instance = new OpenCVUtil();
        }
        return instance;
    }

    @Override
    public boolean compareImages(String expectedImagePath, String actualImagePath, double threshold) {
        Mat expectedImage = Imgcodecs.imread(expectedImagePath);
        Mat actualImage = Imgcodecs.imread(actualImagePath);

        if (expectedImage.empty() || actualImage.empty()) {
            LOGGER.error("Failed to read images");
            return false;
        }

        Mat diff = new Mat();
        Core.absdiff(expectedImage, actualImage, diff);

        Mat grayDiff = new Mat();
        Imgproc.cvtColor(diff, grayDiff, Imgproc.COLOR_BGR2GRAY);

        Mat binaryDiff = new Mat();
        Imgproc.threshold(grayDiff, binaryDiff, 30, 255, Imgproc.THRESH_BINARY);

        int totalPixels = binaryDiff.rows() * binaryDiff.cols();
        int diffPixels = Core.countNonZero(binaryDiff);

        double similarity = 1.0 - (double) diffPixels / totalPixels;

        LOGGER.info("Image similarity: {}", similarity);

        return similarity >= threshold;
    }

    @Override
    public void captureElementScreenshot(WebElement element, String outputPath) {
        File screenshot = ((TakesScreenshot) element).getScreenshotAs(OutputType.FILE);
        try {
            FileUtils.copyFile(screenshot, new File(outputPath));
            LOGGER.info("Element screenshot saved to: {}", outputPath);
        } catch (IOException e) {
            LOGGER.error("Failed to save element screenshot", e);
        }
    }

    @Override
    public void captureFullPageScreenshot(WebDriver driver, String outputPath) {
        // Scroll to the bottom of the page
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollTo(0, document.body.scrollHeight)");

        // Get the total height of the page
        Long totalHeight = (Long) js.executeScript("return document.body.scrollHeight");

        // Set the viewport size
        js.executeScript("window.resizeTo(1366, " + totalHeight + ")");

        // Take the screenshot
        File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        try {
            FileUtils.copyFile(screenshot, new File(outputPath));
            LOGGER.info("Full page screenshot saved to: {}", outputPath);
        } catch (IOException e) {
            LOGGER.error("Failed to save full page screenshot", e);
        }

        // Reset the viewport size
        js.executeScript("window.resizeTo(1366, 768)");
    }

    // Add more AI-enhanced image processing methods as needed
}
