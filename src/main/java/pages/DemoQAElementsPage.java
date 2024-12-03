package pages;

import base.BasePage;
import implementation.SeleniumActions;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.Properties;
import java.util.function.Supplier;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.By;
import utils.GenericUtil;

public class DemoQAElementsPage extends BasePage {
    private final SeleniumActions actions;
    protected static Properties config;
    protected static final String CONFIG_PROP = "config.properties";

    static {
        config = GenericUtil.getPropertiesFile(CONFIG_PROP);
    }

    @FindBy(id = "item-0")
    private WebElement textBoxMenu;

    @FindBy(id = "item-1")
    private WebElement checkBoxMenu;

    @FindBy(id = "item-2")
    private WebElement radioButtonMenu;

    @FindBy(id = "item-3")
    private WebElement webTablesMenu;

    @FindBy(id = "userName")
    private WebElement fullNameInput;

    @FindBy(id = "userEmail")
    private WebElement emailInput;

    @FindBy(id = "currentAddress")
    private WebElement currentAddressInput;

    @FindBy(id = "permanentAddress")
    private WebElement permanentAddressInput;

    @FindBy(id = "submit")
    private WebElement submitButton;

    @FindBy(id = "output")
    private WebElement outputBox;

    @FindBy(css = ".rct-icon-expand-close")
    private WebElement expandAllButton;

    @FindBy(id = "addNewRecordButton")
    private WebElement addNewRecordButton;

    public DemoQAElementsPage(Supplier<WebDriver> driverSupplier) {
        super(driverSupplier);
        this.actions = SeleniumActions.getInstance(driverSupplier);
    }

    @Override
    public boolean isPageLoaded() {
        return false;
    }

    @Override
    protected By getUniqueElement() {
        return null;
    }

    /**
     * Gets the TextBox menu element for analysis.
     *
     * @return WebElement representing the TextBox menu
     */
    public WebElement getTextBoxMenu() {
        // Wait for element to be visible before returning
        actions.waitForElementVisible(textBoxMenu);
        return textBoxMenu;
    }

    /**
     * Gets the CheckBox menu element for analysis.
     *
     * @return WebElement representing the CheckBox menu
     */
    public WebElement getCheckBoxMenu() {
        actions.waitForElementVisible(checkBoxMenu);
        return checkBoxMenu;
    }

    /**
     * Gets the RadioButton menu element for analysis.
     *
     * @return WebElement representing the RadioButton menu
     */
    public WebElement getRadioButtonMenu() {
        actions.waitForElementVisible(radioButtonMenu);
        return radioButtonMenu;
    }

    /**
     * Gets the WebTables menu element for analysis.
     *
     * @return WebElement representing the WebTables menu
     */
    public WebElement getWebTablesMenu() {
        actions.waitForElementVisible(webTablesMenu);
        return webTablesMenu;
    }

    // Create a method to get any element by its current locator

    /**
     * Gets any element on the page using its current locator.
     * Useful for analyzing different locator strategies.
     *
     * @param locator The By locator for the element
     * @return WebElement found using the provided locator
     */
    public WebElement getElementByLocator(By locator, int timeout) {
        actions.waitForElementVisible(locator, timeout);
        return actions.getDriver().findElement(locator);
    }

    public void navigateToElementsPage() {
        actions.navigateToUrl(config.getProperty("DemoQAElementsPageUrl"));
    }

    public void navigateToTextBox() {
        actions.click(textBoxMenu);
    }

    public void navigateToCheckBox() {
        actions.click(checkBoxMenu);
    }

    public void navigateToRadioButton() {
        actions.click(radioButtonMenu);
    }

    public void navigateToWebTables() {
        actions.click(webTablesMenu);
    }

    public void fillTextBoxForm(String fullName, String email, String currentAddress, String permanentAddress) {
        actions.sendKeys(fullNameInput, fullName);
        actions.sendKeys(emailInput, email);
        actions.sendKeys(currentAddressInput, currentAddress);
        actions.sendKeys(permanentAddressInput, permanentAddress);
    }

    public void submitTextBoxForm() {
        actions.click(submitButton);
    }

    public boolean isOutputDisplayed() {
        return actions.isDisplayed(outputBox);
    }

    public String getOutputName() {
        return actions.getText(By.cssSelector("#output #name"));
    }

    public String getOutputEmail() {
        return actions.getText(By.cssSelector("#output #email"));
    }

    public String getOutputCurrentAddress() {
        return actions.getText(By.cssSelector("#output #currentAddress"));
    }

    public String getOutputPermanentAddress() {
        return actions.getText(By.cssSelector("#output #permanentAddress"));
    }

    public void expandAllCheckBoxes() {
        actions.click(expandAllButton);
    }

    public void selectCheckBox(String label) {
        WebElement checkbox = actions.getDriver().findElement(By.xpath("//span[text()='" + label + "']/ancestor::label"));
        actions.click(checkbox);
    }

    public boolean isCheckBoxSelected(String label) {
        WebElement checkbox = actions.getDriver().findElement(By.xpath("//span[text()='" + label + "']/ancestor::label//input"));
        return checkbox.isSelected();
    }

    public void selectRadioButton(String label) {
        WebElement radioButton = actions.getDriver().findElement(By.xpath("//label[text()='" + label + "']"));
        actions.click(radioButton);
    }

    public boolean isRadioButtonSelected(String label) {
        WebElement radioButton = actions.getDriver().findElement(By.xpath("//label[text()='" + label + "']/preceding-sibling::input"));
        return radioButton.isSelected();
    }

    public String getSelectedRadioButtonText() {
        return actions.getText(By.className("text-success"));
    }

    public void addNewRecord(String firstName, String lastName, String email, String age, String salary, String department) {
        actions.click(addNewRecordButton);
        actions.sendKeys(By.id("firstName"), firstName);
        actions.sendKeys(By.id("lastName"), lastName);
        actions.sendKeys(By.id("userEmail"), email);
        actions.sendKeys(By.id("age"), age);
        actions.sendKeys(By.id("salary"), salary);
        actions.sendKeys(By.id("department"), department);
        actions.click(By.id("submit"));
    }

    public boolean isRecordPresent(String firstName, String lastName) {
        return actions.isDisplayed(By.xpath("//div[@class='rt-tbody']//div[contains(text(), '" + firstName + "')]" +
                "/following-sibling::div[contains(text(), '" + lastName + "')]"));
    }
}
