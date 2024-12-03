package base;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.openqa.selenium.WebDriver;

import java.util.HashSet;
import java.util.Set;

public class WebDriverPool {
    private final GenericObjectPool<WebDriver> pool;
    private final Set<WebDriver> borrowedDrivers = new HashSet<>();

    public WebDriverPool(CustomThreadSafeDriver.DriverType driverType, int maxTotal, int maxIdle) {
        pool = new GenericObjectPool<>(new WebDriverFactory(driverType));
        pool.setMaxTotal(maxTotal);
        pool.setMaxIdle(maxIdle);
    }

    public synchronized WebDriver borrowDriver() throws Exception {
        WebDriver driver = pool.borrowObject();
        borrowedDrivers.add(driver);
        return driver;
    }

    public synchronized void returnDriver(WebDriver driver) {
        if (borrowedDrivers.remove(driver)) {
            pool.returnObject(driver);
        }
    }

    public synchronized void close() {
        for (WebDriver driver : borrowedDrivers) {
            try {
                pool.invalidateObject(driver);
            } catch (Exception e) {
                // Log the exception
            }
        }
        borrowedDrivers.clear();
        pool.close();
    }

    private static class WebDriverFactory extends BasePooledObjectFactory<WebDriver> {
        private final CustomThreadSafeDriver.DriverType driverType;

        public WebDriverFactory(CustomThreadSafeDriver.DriverType driverType) {
            this.driverType = driverType;
        }

        @Override
        public WebDriver create() {
            return CustomThreadSafeDriver.getThreadSafeWebDriver(driverType);
        }

        @Override
        public PooledObject<WebDriver> wrap(WebDriver driver) {
            return new DefaultPooledObject<>(driver);
        }

        @Override
        public void destroyObject(PooledObject<WebDriver> p) {
            p.getObject().quit();
        }
    }
}