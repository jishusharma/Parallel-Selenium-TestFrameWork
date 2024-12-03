package interfaces;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;

import java.util.List;
import java.util.function.Function;

public interface WaitActions {
    void waitForElementVisible(By locator, int timeoutInSeconds);
    void waitForElementVisible(By locator);
    void waitForElementVisible(WebElement element);
    void waitForElements(ExpectedCondition<List<WebElement>> condition);
    void waitForElement(ExpectedCondition<WebElement> condition);
    void waitForElementClickable(By locator, int timeoutInSeconds);
    WebElement fluentWait(By locator, int timeoutInSeconds, int pollingTimeInMillis);
    <V> V fluentWait(Function<WebDriver, V> condition, int timeoutInSeconds, int pollingTimeInMillis);
}
