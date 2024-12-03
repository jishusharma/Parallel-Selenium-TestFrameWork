package tests;

import org.opencv.core.Core;
import org.testng.annotations.Test;
import org.testng.Assert;
import pages.DemoQAMainPage;
import base.BaseTest;
import ai.AIUtil;
import ai.OpenCVUtil;
import org.openqa.selenium.WebElement;

public class AiCompareImagesTest extends BaseTest {

    private final AIUtil aiUtil;

    public AiCompareImagesTest() {
        this.aiUtil = OpenCVUtil.getInstance();
    }

    @Test(description = "Verify logo using AI image comparison")
    public void verifyLogoUsingAI() {
        DemoQAMainPage mainPage = new DemoQAMainPage(threadLocalDriver::get);
        WebElement logoElement = mainPage.getLogoElement();

        String expectedLogoPath = "src/test/java/resources/ai_images/expected/Toolsqa.jpg";
        String actualLogoPath = "src/test/java/resources/ai_images/actual/Toolsqa.jpg";

        aiUtil.captureElementScreenshot(logoElement, actualLogoPath);

        boolean isLogoMatching = aiUtil.compareImages(expectedLogoPath, actualLogoPath, 0.95);

        Assert.assertTrue(isLogoMatching, "Logo does not match the expected image");
    }

    @Test(description = "Test testOpenCVLoaded ")
    public void testOpenCVLoaded() {
        Assert.assertNotNull(Core.VERSION, "OpenCV is not loaded");
        System.out.println("OpenCV Version: " + Core.VERSION);
    }
    // Add more AI-enhanced tests here
}
