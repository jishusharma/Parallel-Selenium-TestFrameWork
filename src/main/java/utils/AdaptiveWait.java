package utils;

import base.BaseTest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class AdaptiveWait {
    private static final long INITIAL_TIMEOUT = 1000; // 1 second
    private static final long MAX_TIMEOUT = 32001; // 32 seconds
    private static final long INCREASE_FACTOR = 2;
    protected static final Logger LOGGER = LogManager.getLogger(BaseTest.class);

    public static <T> T until(WebDriver driver, ExpectedCondition<T> condition) {
        long timeout = INITIAL_TIMEOUT;
        Exception lastException = null;

        while (timeout <= MAX_TIMEOUT) {
            try {
                return new WebDriverWait(driver, Duration.ofMillis(timeout)).until(condition);
            } catch (Exception e) {
                lastException = e;
                timeout *= INCREASE_FACTOR;
                LOGGER.debug("TIME OUT INCREASED TO: " + timeout);
            }
        }
        throw new RuntimeException("Condition not met within maximum timeout", lastException);
    }

    public static WebElement waitForElementClickable(WebDriver driver, WebElement element) {
        return until(driver, d -> {
            if (element.isDisplayed() && element.isEnabled()) {
                return element;
            }
            return null;
        });
    }

    // Add more adaptive wait methods as needed
}
