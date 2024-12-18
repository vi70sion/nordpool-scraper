package org.example.service;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.sql.Connection;
import java.sql.DriverManager;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NordPoolDailyScraper {
    private WebDriver _globalDriver;
    private final String PATH_URL = "https://data.nordpoolgroup.com/auction/day-ahead/prices?deliveryDate=latest&currency=EUR&aggregation=DailyAggregate&deliveryAreas=";
    public NordPoolDailyScraper(WebDriver _globalDriver) {
        this._globalDriver = _globalDriver;
    }

    public void scrapeData() {
        try {
            String area = ConfigLoader.getProperty("area");
            _globalDriver.get(PATH_URL + area);
            Thread.sleep(2000);
            WebDriverWait wait = new WebDriverWait(_globalDriver, Duration.ofSeconds(10));
            WebElement rejectCookiesButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[text()='Disallow and close']")));
            rejectCookiesButton.click();

            Actions actions = new Actions(_globalDriver);
            WebElement scrollableContainer = _globalDriver.findElement(By.className("dx-scrollable-container"));
            WebElement scrollableContent = _globalDriver.findElement(By.cssSelector(".dx-scrollable-content"));
            JavascriptExecutor jsExecutor = (JavascriptExecutor) _globalDriver;

            WebElement rowDown = _globalDriver.findElement(By.cssSelector(".dx-row")); // Viena eilutė
            int rowHeight = rowDown.getSize().getHeight();
            int scrollAmount = rowHeight * 20;
            jsExecutor.executeScript("window.scrollBy(0, arguments[0]);", scrollAmount);

            Map<String, String> dataMap = new HashMap<>();
            boolean notEnd = true;

            while (notEnd) {
                List<WebElement> rows = _globalDriver.findElements(By.cssSelector("tr.dx-row.dx-data-row"));
                int rowsSize = rows.size()/2;

                for(int i = 0; i < rowsSize; i++) {
                    String key = rows.get(i + rowsSize).findElement(By.cssSelector("td[aria-colindex='1']")).getText();
                    String value = rows.get(i).findElement(By.cssSelector("td[aria-colindex='2']")).getText();
                    //todo patikrinti ar kuris nera null
                    dataMap.put(key, value);
                    System.out.println(key + " " + value);
                    if (key.equals("2024-01-01")) notEnd = false;
                }

                jsExecutor.executeScript("arguments[0].scrollBy(0, 350);", scrollableContainer);
                Thread.sleep(100);

            }

            dataMap.remove(null);

            String url = ConfigLoader.getProperty("url");
            String user = ConfigLoader.getProperty("username");
            String password = ConfigLoader.getProperty("password");
            Connection connection = DriverManager.getConnection(url, user, password);

            NordPoolPriceRepository priceRepository =new NordPoolPriceRepository();
            for(Map.Entry<String, String> entry : dataMap.entrySet()){
                priceRepository.insertData(connection, entry.getKey(), area, entry.getValue());
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            _globalDriver.quit();
        }
    }



}
