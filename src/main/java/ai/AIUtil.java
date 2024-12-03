package ai;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public interface AIUtil {
    boolean compareImages(String expectedImagePath, String actualImagePath, double threshold);
    void captureElementScreenshot(WebElement element, String outputPath);
    void captureFullPageScreenshot(WebDriver driver, String outputPath);
}