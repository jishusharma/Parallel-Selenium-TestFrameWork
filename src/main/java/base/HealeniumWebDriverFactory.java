package base;

import com.epam.healenium.SelfHealingDriver;
import org.openqa.selenium.WebDriver;

public class HealeniumWebDriverFactory {
    private HealeniumWebDriverFactory() {
        // Private constructor to prevent instantiation
    }

    public static WebDriver wrapDriver(WebDriver delegate) {
        return SelfHealingDriver.create(delegate);
    }
}