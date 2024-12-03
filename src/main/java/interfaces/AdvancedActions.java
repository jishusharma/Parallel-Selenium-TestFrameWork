package interfaces;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public interface AdvancedActions {
    void hoverOverElement(WebElement element);
    void hoverOverElement(By locator);
    void doubleClick(WebElement element);
    void doubleClick(By locator);
    void dragAndDrop(WebElement source, WebElement target);
    void dragAndDrop(By source, By target);
}
