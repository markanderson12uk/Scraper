/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.cedar.screenscraper;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

/**
 * Class to run JUnit tests
 * @author Mark
 */
public class TestRunner {

    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(Scraper.class);
        for (Failure failure : result.getFailures()) {
            System.out.println(failure.toString());
        }

    }

}
