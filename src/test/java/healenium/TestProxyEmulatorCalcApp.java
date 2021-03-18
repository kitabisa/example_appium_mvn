package healenium; /**
 * Healenium-appium Copyright (C) 2019 EPAM
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *        http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.browserup.bup.BrowserUpProxy;
import com.browserup.bup.BrowserUpProxyServer;
import com.browserup.bup.client.ClientUtil;
import com.browserup.bup.proxy.CaptureType;
import com.browserup.harreader.model.Har;
import com.browserup.harreader.model.HarEntry;
import com.epam.healenium.appium.DriverWrapper;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.remote.MobileCapabilityType;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
public class TestProxyEmulatorCalcApp {

    private static AppiumDriver appiumDriver;
    private static BrowserUpProxy proxy;

    @SneakyThrows
    @BeforeAll
    public static void setUp() throws MalformedURLException {
        proxy = new BrowserUpProxyServer();
        proxy.start();
        //int proxyPort = proxy.getPort();

        //enable more detailed HAR capture, if desired (see CaptureType for the complete list)
        proxy.enableHarCaptureTypes(CaptureType.REQUEST_CONTENT, CaptureType.RESPONSE_CONTENT);

        //create a new HAR with the label "com.android.calculator2"
        proxy.newHar("com.android.calculator2");

        //get the Selenium proxy object
        Proxy seleniumProxy = ClientUtil.createSeleniumProxy(proxy);

        DesiredCapabilities dc = new DesiredCapabilities();
        dc.setCapability(CapabilityType.PROXY, seleniumProxy);

        dc.setCapability(MobileCapabilityType.DEVICE_NAME, "emulator-5554");
        dc.setCapability(MobileCapabilityType.PLATFORM_NAME, "android");
        dc.setCapability("appPackage", "com.android.calculator2");
        dc.setCapability("appActivity", ".Calculator");

        dc.setCapability("test_data:testResultOk:result", "testResultHealed:resul");
        dc.setCapability("test_data:testFindElementsOk:digit_7", "testFindElementsHealed:digit_77");

        //declare delegate driver
        appiumDriver = new AndroidDriver<AndroidElement>(new URL("http://127.0.0.1:4723/wd/hub"), dc);
        //adding healing support
        //appiumDriver = DriverWrapper.wrap(appiumDriver);
    }

    @SneakyThrows
    @Test
    public void testResultOk() {
        testAddOperation();
        assertEquals("61", appiumDriver.findElementById("result").getText());
        System.out.println("!!! " + proxy.getHar().getLog().getComment());
    }

    @SneakyThrows
    @Test
    public void testResultHealed() {
        testAddOperation();
        assertEquals("61", appiumDriver.findElementById("resul").getText());
    }

    @Test
    public void testFindElementsOk() {
        List<MobileElement> elements = appiumDriver.findElements(By.id("digit_7"));
        assertEquals("7", elements.get(0).getText(), "Digit 7");
    }

    @Test
    public void testFindElementsHealed() {
        List<MobileElement> elements = appiumDriver.findElements(By.id("digit_77"));
        assertEquals( "7", elements.get(0).getText(), "Healed digit 7");
    }

    private void testAddOperation() {
        MobileElement el1 = (MobileElement) appiumDriver.findElementById("digit_2");
        el1.click();
        MobileElement el2 = (MobileElement) appiumDriver.findElementById("digit_5");
        el2.click();
        MobileElement el3 = (MobileElement) appiumDriver.findElementByAccessibilityId("plus");
        el3.click();
        MobileElement el4 = (MobileElement) appiumDriver.findElementById("digit_3");
        el4.click();
        MobileElement el5 = (MobileElement) appiumDriver.findElementById("digit_6");
        el5.click();
        MobileElement el6 = (MobileElement) appiumDriver.findElementByAccessibilityId("equals");
        el6.click();
    }

    @AfterAll
    public static void tearDown() {
        if (appiumDriver != null) {
            appiumDriver.quit();
        }
    }
}
