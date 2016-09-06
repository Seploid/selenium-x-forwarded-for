package org.seploid.blog.x_forwarded_for.ui.driver;

import org.apache.http.HeaderElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.seploid.blog.x_forwarded_for.utils.FileUtils;
import org.seploid.blog.x_forwarded_for.utils.ZipUtils;

import java.io.File;
import java.util.List;

public class CapabilityBuilder {

    private BrowserType browserType;

    private ChromeOptions chromeOptions = new ChromeOptions();

    private FirefoxProfile firefoxProfile  = new FirefoxProfile();

    private final String FF_MOD_HEADER_EXTENSION_PATH = "modify_headers-0.7.1.1-fx.xpi";
    private final String CHROME_HEADER_EXTENSION_PATH = "chrome_extension";
    private final String HEADER_JSON_NAME = "header.json";

    public CapabilityBuilder(BrowserType browserType) {
        this.browserType = browserType;
    }

    public void setHeaderElements(List<HeaderElement> headerElements) {
        switch (browserType) {
            case CHROME:
                // define path to resources
                String unpackedExtensionPath = FileUtils.getResourcePath(CHROME_HEADER_EXTENSION_PATH, true);
                // setting  headers for extension in unpackaged kind
                FileUtils.writeToJson(unpackedExtensionPath + File.separator + HEADER_JSON_NAME, headerElements);
                // packing prepared extension to ZIP with crx extension
                String crxExtensionPath = ZipUtils.packZipWithNameOfFolder(unpackedExtensionPath, "crx");
                // creating capability based on packed extension
                chromeOptions.addExtensions(new File(crxExtensionPath));

                break;
            case FIREFOX:
                firefoxProfile.addExtension(new File(FileUtils.getResourcePath(FF_MOD_HEADER_EXTENSION_PATH, false)));
                firefoxProfile.setPreference("extensions.modify_headers.currentVersion", "0.7.1.1-signed");
                firefoxProfile.setPreference("modifyheaders.headers.count", headerElements.size());
                for (int i = 0; i < headerElements.size(); i++) {
                    firefoxProfile.setPreference("modifyheaders.headers.action0", "Add");
                    firefoxProfile.setPreference("modifyheaders.headers.name" + i, headerElements.get(i).getName());
                    firefoxProfile.setPreference("modifyheaders.headers.value" + i, headerElements.get(i).getValue());
                    firefoxProfile.setPreference("modifyheaders.headers.enabled" + i, true);
                }
                firefoxProfile.setPreference("modifyheaders.config.active", true);
                firefoxProfile.setPreference("modifyheaders.config.alwaysOn", true);
                firefoxProfile.setPreference("modifyheaders.config.start", true);
                break;
        }
    }

    public void setUserAgent(String userAgent) {
        switch (browserType) {
            case CHROME:
                //for Chrome add appropriate option for launching
                chromeOptions.addArguments("--user-agent=" + userAgent);
                break;
            case FIREFOX:
                //for Firefox set appropriate preference
                firefoxProfile.setPreference("general.useragent.override", userAgent);
                break;
        }
    }

    public DesiredCapabilities build() {
        DesiredCapabilities capabilities;
        switch (browserType) {
            case CHROME:
                capabilities = DesiredCapabilities.chrome();
                capabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
                break;
            case FIREFOX:
                capabilities = DesiredCapabilities.firefox();
                capabilities.setCapability(FirefoxDriver.PROFILE, firefoxProfile);
                break;
            default:
                throw new RuntimeException("Unsupported browser time.");
        }
        return capabilities;
    }
}
