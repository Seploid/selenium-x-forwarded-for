package org.seploid.blog.x_forwarded_for.ui.driver;

import org.apache.http.HeaderElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.HttpCommandExecutor;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class DriverManager {

    private static final String URL = "http://%s:%s/wd/hub";

    public static WebDriver getWebDriverWithIP(String host, String port, BrowserType browserType, DeviceType deviceType, List<HeaderElement> headerElements) {
        CapabilityBuilder builder = new CapabilityBuilder(browserType);
        builder.setHeaderElements(headerElements);
        builder.setUserAgent(deviceType.getUserAgent());
        DesiredCapabilities capabilities = builder.build();
        try {
            HttpCommandExecutor executor = new HttpCommandExecutor(new URL(String.format(URL, host, port)));
            return new RemoteWebDriver(executor, capabilities);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
