package base;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.*;
import utils.GenericUtil;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class BaseTest {
    protected static final ThreadLocal<WebDriver> threadLocalDriver = new ThreadLocal<>();
    protected static final String TARGET = "target";
    protected static Properties config;
    protected static final String CONFIG_PROP = "config.properties";
    protected static final Logger LOGGER = LogManager.getLogger(BaseTest.class);
    private static WebDriverPool webDriverPool;
    private static final AtomicInteger driverCounter = new AtomicInteger(0);
    private static final ReentrantLock setupLock = new ReentrantLock();
    private static final ReentrantLock teardownLock = new ReentrantLock();

    @BeforeSuite(alwaysRun = true)
    public void setUpSuite() {
        config = GenericUtil.getPropertiesFile(CONFIG_PROP);
        webDriverPool = new WebDriverPool(CustomThreadSafeDriver.DriverType.CHROME, 10, 5);
    }

    @BeforeMethod(alwaysRun = true)
    public void setUp() throws Exception {
        setupLock.lock();
        try {
            LOGGER.info("Setting up test method");
            WebDriver driver = webDriverPool.borrowDriver();
            threadLocalDriver.set(driver);
            LOGGER.debug("DRIVER NAME = " + getDriverName());
        } finally {
            setupLock.unlock();
        }

        WebDriver driver = getDriver();
        driver.get(config.getProperty("DemoQAMainPageUrl"));
        driver.manage().window().maximize();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        WebDriver driver = threadLocalDriver.get();
        if (driver != null) {
            teardownLock.lock();
            try {
                webDriverPool.returnDriver(driver);
            } finally {
                teardownLock.unlock();
            }
        }
        threadLocalDriver.remove();
    }

    @AfterSuite(alwaysRun = true)
    public void tearDownSuite() {
        if (webDriverPool != null) {
            webDriverPool.close();
        }
    }

    protected WebDriver getDriver() {
        return threadLocalDriver.get();
    }

    public String getDriverName() {
        return getDriver().getClass().getSimpleName() + "-" + Thread.currentThread().getId();
    }

    public static void incrementDriverCounter() {
        driverCounter.incrementAndGet();
    }
}