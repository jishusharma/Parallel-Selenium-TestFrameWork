package interfaces;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public interface ElementActions {
    void click(WebElement element);
    void click(By locator);
    void sendKeys(WebElement element, String text);
    void sendKeys(By locator, String text);
    String getText(WebElement element);
    String getText(By locator);
    boolean isDisplayed(WebElement element);
    boolean isDisplayed(By locator);
    boolean isEnabled(WebElement element);
    boolean isEnabled(By locator);
    boolean isSelected(WebElement element);
    boolean isSelected(By locator);
    WebElement findElement(By locator);
}
