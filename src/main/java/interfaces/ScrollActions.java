package interfaces;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public interface ScrollActions {
    void scrollIntoView(WebElement element);
    void scrollIntoView(By locator);
}
