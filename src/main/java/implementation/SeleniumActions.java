package implementation;

import interfaces.*;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.AdaptiveWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

public class SeleniumActions implements ElementActions, NavigationActions, ScrollActions, AdvancedActions, WaitActions,
        WindowTabActions {
    private static volatile SeleniumActions instance;
    private final Supplier<WebDriver> driverSupplier;
    private final WebDriverWait wait;

    SeleniumActions(Supplier<WebDriver> driverSupplier) {
        this.driverSupplier = driverSupplier;
        this.wait = new WebDriverWait(driverSupplier.get(), Duration.ofSeconds(10));
    }

    public static SeleniumActions getInstance(Supplier<WebDriver> driverSupplier) {
        if (instance == null) {
            synchronized (SeleniumActions.class) {
                if (instance == null) {
                    instance = new SeleniumActions(driverSupplier);
                }
            }
        }
        return instance;
    }

    public WebDriver getDriver() {
        return driverSupplier.get();
    }

    // ElementActions implementation
    @Override
    public void click(WebElement element) {
        try {
            WebElement clickableElement = AdaptiveWait.waitForElementClickable(getDriver(), element);
            clickableElement.click();
        } catch (ElementClickInterceptedException e) {
            ((JavascriptExecutor) getDriver()).executeScript("arguments[0].click();", element);
        } catch (StaleElementReferenceException e) {
            WebElement freshElement = AdaptiveWait.waitForElementClickable(getDriver(), element);
            freshElement.click();
        }
    }

    @Override
    public void click(By locator) {
        WebElement element = findElement(locator);
        click(element);
    }

    @Override
    public void sendKeys(WebElement element, String text) {
        try {
            wait.until(ExpectedConditions.visibilityOf(element));
            element.clear();
            element.sendKeys(text);
        } catch (StaleElementReferenceException e) {
            WebElement freshElement = wait.until(ExpectedConditions.refreshed(ExpectedConditions.visibilityOf(element)));
            freshElement.clear();
            freshElement.sendKeys(text);
        }
    }

    @Override
    public void sendKeys(By locator, String text) {
        WebElement element = findElement(locator);
        sendKeys(element, text);
    }

    @Override
    public String getText(WebElement element) {
        return element.getText();
    }

    @Override
    public String getText(By locator) {
        return getText(findElement(locator));
    }

    @Override
    public boolean isDisplayed(WebElement element) {
        return element.isDisplayed();
    }

    @Override
    public boolean isDisplayed(By locator) {
        return isDisplayed(findElement(locator));
    }

    @Override
    public boolean isEnabled(WebElement element) {
        return element.isEnabled();
    }

    @Override
    public boolean isEnabled(By locator) {
        return isEnabled(findElement(locator));
    }

    @Override
    public boolean isSelected(WebElement element) {
        return element.isSelected();
    }

    @Override
    public boolean isSelected(By locator) {
        return isSelected(findElement(locator));
    }

    @Override
    public WebElement findElement(By locator) {
        return getDriver().findElement(locator);
    }

    // NavigationActions implementation
    @Override
    public void navigateToUrl(String url) {
        getDriver().get(url);
    }

    // ScrollActions implementation
    @Override
    public void scrollIntoView(WebElement element) {
        try {
            ((JavascriptExecutor) getDriver()).executeScript("arguments[0].scrollIntoView(true);", element);
        } catch (Exception e) {
            // Log the exception
        }
    }

    @Override
    public void scrollIntoView(By locator) {
        scrollIntoView(findElement(locator));
    }

    // AdvancedActions implementation
    @Override
    public void hoverOverElement(WebElement element) {
        new Actions(getDriver()).moveToElement(element).perform();
    }

    @Override
    public void hoverOverElement(By locator) {
        hoverOverElement(findElement(locator));
    }

    @Override
    public void doubleClick(WebElement element) {
        new Actions(getDriver()).doubleClick(element).perform();
    }

    @Override
    public void doubleClick(By locator) {
        doubleClick(findElement(locator));
    }

    @Override
    public void dragAndDrop(WebElement source, WebElement target) {
        new Actions(getDriver()).dragAndDrop(source, target).perform();
    }

    @Override
    public void dragAndDrop(By source, By target) {
        dragAndDrop(findElement(source), findElement(target));
    }

    // WaitActions implementation
    @Override
    public void waitForElementVisible(By locator, int timeoutInSeconds) {
        new WebDriverWait(getDriver(), Duration.ofSeconds(timeoutInSeconds))
                .until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    @Override
    public void waitForElementVisible(By locator) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    @Override
    public void waitForElementVisible(WebElement element) {
        wait.until(ExpectedConditions.visibilityOf(element));
    }

    @Override
    public void waitForElements(ExpectedCondition<List<WebElement>> condition) {
        wait.until(condition);
    }

    @Override
    public void waitForElement(ExpectedCondition<WebElement> condition) {
        wait.until(condition);
    }

    @Override
    public void waitForElementClickable(By locator, int timeoutInSeconds) {
        new WebDriverWait(getDriver(), Duration.ofSeconds(timeoutInSeconds))
                .until(ExpectedConditions.elementToBeClickable(locator));
    }

    @Override
    public WebElement fluentWait(By locator, int timeoutInSeconds, int pollingTimeInMillis) {
        return createFluentWait(timeoutInSeconds, pollingTimeInMillis)
                .until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    @Override
    public <V> V fluentWait(Function<WebDriver, V> condition, int timeoutInSeconds, int pollingTimeInMillis) {
        return createFluentWait(timeoutInSeconds, pollingTimeInMillis)
                .until(condition);
    }

    private FluentWait<WebDriver> createFluentWait(int timeoutInSeconds, int pollingTimeInMillis) {
        return new FluentWait<>(getDriver())
                .withTimeout(Duration.ofSeconds(timeoutInSeconds))
                .pollingEvery(Duration.ofMillis(pollingTimeInMillis))
                .ignoring(NoSuchElementException.class, StaleElementReferenceException.class);
    }

    // WindowTabActions implementation
    @Override
    public void switchToWindow(String windowHandle) {
        getDriver().switchTo().window(windowHandle);
    }

    @Override
    public void switchToWindowByTitle(String title) {
        for (String handle : getDriver().getWindowHandles()) {
            getDriver().switchTo().window(handle);
            if (getDriver().getTitle().contains(title)) {
                return;
            }
        }
        throw new NoSuchWindowException("No window found with title: " + title);
    }

    @Override
    public void switchToWindowByUrl(String url) {
        for (String handle : getDriver().getWindowHandles()) {
            getDriver().switchTo().window(handle);
            if (getDriver().getCurrentUrl().contains(url)) {
                return;
            }
        }
        throw new NoSuchWindowException("No window found with URL containing: " + url);
    }

    @Override
    public void switchToNewWindow() {
        String currentHandle = getDriver().getWindowHandle();
        Set<String> handles = getDriver().getWindowHandles();
        handles.remove(currentHandle);
        getDriver().switchTo().window(handles.iterator().next());
    }

    @Override
    public void closeCurrentWindowOrTab() {
        getDriver().close();
    }

    @Override
    public void switchToParentWindow() {
        getDriver().switchTo().defaultContent();
    }

    @Override
    public void switchToFrame(int index) {
        getDriver().switchTo().frame(index);
    }

    @Override
    public void switchToFrame(String nameOrId) {
        getDriver().switchTo().frame(nameOrId);
    }

    @Override
    public void switchToDefaultContent() {
        getDriver().switchTo().defaultContent();
    }

    @Override
    public Set<String> getWindowHandles() {
        return getDriver().getWindowHandles();
    }

    @Override
    public String getCurrentWindowHandle() {
        return getDriver().getWindowHandle();
    }

    @Override
    public void createNewTab() {
        getDriver().switchTo().newWindow(WindowType.TAB);
    }

    @Override
    public void switchToTab(int index) {
        ArrayList<String> tabs = new ArrayList<>(getDriver().getWindowHandles());
        if (index >= 0 && index < tabs.size()) {
            getDriver().switchTo().window(tabs.get(index));
        } else {
            throw new IndexOutOfBoundsException("Invalid tab index: " + index);
        }
    }

    @Override
    public int getNumberOfWindows() {
        return getDriver().getWindowHandles().size();
    }

    @Override
    public void maximizeWindow() {
        getDriver().manage().window().maximize();
    }

    @Override
    public void minimizeWindow() {
        getDriver().manage().window().minimize();
    }

    @Override
    public void setWindowSize(int width, int height) {
        getDriver().manage().window().setSize(new Dimension(width, height));
    }
}
