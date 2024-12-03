package base;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedCondition;
import implementation.SeleniumActions;
import com.aventstack.extentreports.Status;
import utils.listeners.ExtentTestManager;

import java.time.Duration;
import java.util.function.Supplier;

public abstract class BasePage {
    protected final Supplier<WebDriver> driverSupplier;
    protected final SeleniumActions actions;

    // Default timeout for page load
    private static final Duration DEFAULT_PAGE_LOAD_TIMEOUT = Duration.ofSeconds(30);
    private static final Duration DEFAULT_AJAX_TIMEOUT = Duration.ofSeconds(10);

    public BasePage(Supplier<WebDriver> driverSupplier) {
        this.driverSupplier = driverSupplier;
        this.actions = SeleniumActions.getInstance(driverSupplier);
        initializePage();
    }

    private void initializePage() {
        try {
            PageFactory.initElements(getDriver(), this);
            waitForPageLoad();
            logInitialization(true);
        } catch (Exception e) {
            logInitialization(false);
            throw e;
        }
    }

    private void logInitialization(boolean success) {
        Status status = success ? Status.INFO : Status.ERROR;
        String message = success
                ? String.format("Initialized %s with SmartElementService", this.getClass().getSimpleName())
                : String.format("Failed to initialize %s", this.getClass().getSimpleName());
        ExtentTestManager.getTest().log(status, message);
    }

    protected WebDriver getDriver() {
        return driverSupplier.get();
    }

    // Default page loading mechanism
    protected void waitForPageLoad() {
        try {
            // Wait for document ready state
            waitForDocumentReady();

            // Wait for AJAX calls to complete
            waitForAjaxCalls();

            // Wait for page-specific element
            waitForPageSpecificElement();

            // Additional custom wait conditions for the specific page
            waitForAdditionalConditions();

            ExtentTestManager.getTest().log(Status.PASS,
                    String.format("%s loaded successfully", this.getClass().getSimpleName()));
        } catch (Exception e) {
            ExtentTestManager.getTest().log(Status.FAIL,
                    String.format("Page load failed for %s: %s",
                            this.getClass().getSimpleName(), e.getMessage()));
            throw e;
        }
    }

    private void waitForDocumentReady() {
        ExpectedCondition<Boolean> documentReady = driver -> {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            return js.executeScript("return document.readyState").equals("complete");
        };
        actions.fluentWait(documentReady,
                (int) DEFAULT_PAGE_LOAD_TIMEOUT.getSeconds(), 500);
    }

    private void waitForAjaxCalls() {
        ExpectedCondition<Boolean> ajaxComplete = driver -> {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            // jQuery AJAX calls check
            boolean jQueryDone = (Boolean) js.executeScript(
                    "return jQuery.active == 0 || typeof jQuery === 'undefined'");
            // Angular HTTP calls check
            boolean angularDone = (Boolean) js.executeScript(
                    "return typeof angular === 'undefined' || " +
                            "angular.element(document).injector() === undefined || " +
                            "angular.element(document).injector().get('$http').pendingRequests.length === 0");
            // Fetch API calls check (if possible to track)
            boolean fetchDone = (Boolean) js.executeScript(
                    "return window.fetchCallsInProgress === undefined || " +
                            "window.fetchCallsInProgress === 0");

            return jQueryDone && angularDone && fetchDone;
        };

        try {
            actions.fluentWait(ajaxComplete,
                    (int) DEFAULT_AJAX_TIMEOUT.getSeconds(), 500);
        } catch (Exception e) {
            // Log warning but don't fail - some pages might not use AJAX
            ExtentTestManager.getTest().log(Status.WARNING,
                    "AJAX wait completed with potential pending requests");
        }
    }

    private void waitForPageSpecificElement() {
        By uniqueElement = getUniqueElement();
        if (uniqueElement != null) {
            actions.waitForElementVisible(uniqueElement,
                    (int) DEFAULT_PAGE_LOAD_TIMEOUT.getSeconds());
        }
    }

    // Can be overridden by specific pages to add custom wait conditions
    protected void waitForAdditionalConditions() {
        // Default implementation does nothing
        // Page classes can override this to add specific wait conditions
    }

    // Core page information methods
    public String getPageTitle() {
        return getDriver().getTitle();
    }

    public String getCurrentUrl() {
        return getDriver().getCurrentUrl();
    }

    // Page state verification
    public boolean isPageLoaded() {
        try {
            waitForPageLoad();
            return true;
        } catch (Exception e) {
            ExtentTestManager.getTest().log(Status.WARNING,
                    "Page load check failed: " + e.getMessage());
            return false;
        }
    }

    // Navigation with built-in waits
    protected void navigateToPage(String url) {
        try {
            actions.navigateToUrl(url);
            waitForPageLoad();
            ExtentTestManager.getTest().log(Status.PASS,
                    "Successfully navigated to: " + url);
        } catch (Exception e) {
            ExtentTestManager.getTest().log(Status.FAIL,
                    "Navigation failed: " + e.getMessage());
            throw e;
        }
    }

    // Abstract method to be implemented by each page class
    protected abstract By getUniqueElement();
}