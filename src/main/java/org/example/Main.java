package org.example;

import org.example.service.NordPoolDailyScraper;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        WebDriver _globalDriver = new ChromeDriver();
        _globalDriver.manage().window().maximize();
        _globalDriver.manage().timeouts().implicitlyWait(2000, TimeUnit.MILLISECONDS);

        NordPoolDailyScraper scrapper = new NordPoolDailyScraper(_globalDriver);
        scrapper.scrapeData();

    }
}