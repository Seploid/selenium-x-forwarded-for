import org.apache.http.HeaderElement;
import org.apache.http.message.BasicHeaderElement;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.seploid.blog.x_forwarded_for.ui.driver.BrowserType;
import org.seploid.blog.x_forwarded_for.ui.driver.DeviceType;
import org.seploid.blog.x_forwarded_for.ui.driver.DriverManager;

import java.util.ArrayList;
import java.util.List;

public class TestGeoIp {

    WebDriver driver;

    @Test
    public void testFirefox() {
//        preparing
        String expectedResult = "81.133.75.58";
        List<HeaderElement> headerElements = new ArrayList<HeaderElement>();
        headerElements.add(new BasicHeaderElement("X-Forwarded-For", expectedResult));
//        opening geo ip
        driver = DriverManager.getWebDriverWithIP("localhost", "4723", BrowserType.FIREFOX, DeviceType.NEXUS5_ANDROID4, headerElements);
        driver.get("http://ru.smart-ip.net/geoip");
        String actualResult = driver.findElement(By.id("hostname")).getAttribute("value");
        Assert.assertEquals("Incorrect!", expectedResult, actualResult);
    }

    @Test
    public void testChrome() {
//        preparing
        String expectedResult = "81.133.75.58";
        List<HeaderElement> headerElements = new ArrayList<HeaderElement>();
        headerElements.add(new BasicHeaderElement("X-Forwarded-For", expectedResult));
//        opening geo ip
        driver = DriverManager.getWebDriverWithIP("localhost", "4723", BrowserType.CHROME, DeviceType.NEXUS5_ANDROID4, headerElements);
        driver.get("http://ru.smart-ip.net/geoip");
        String actualResult = driver.findElement(By.id("hostname")).getAttribute("value");
        Assert.assertEquals("Incorrect!", expectedResult, actualResult);
    }

    @After
    public void tearDown() {
        driver.close();
        driver.quit();
    }
}
