package test;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class TestSuite {
    public static void main(String[] args) {
        // Run all test classes. You can add more test classes here as needed.
        Result result = JUnitCore.runClasses(
            PlayerManagerTest.class,
            GameManagerTest.class,
            InventoryTest.class,
            PotionManagerTest.class
        );
        
        // Print summary information
        System.out.println("==================================");
        System.out.println("TEST RESULTS SUMMARY:");
        System.out.println("Tests run: " + result.getRunCount());
        System.out.println("Tests failed: " + result.getFailureCount());
        System.out.println("Tests ignored: " + result.getIgnoreCount());
        System.out.println("Execution time: " + result.getRunTime() + " ms");
        
        // If there are failures, print detailed info for each
        if (!result.wasSuccessful()) {
            System.out.println("\nFAILURE DETAILS:");
            for (Failure failure : result.getFailures()) {
                System.out.println("----------------------------------");
                System.out.println("Test: " + failure.getTestHeader());
                System.out.println("Message: " + failure.getMessage());
                System.out.println("Trace:\n" + failure.getTrace());
            }
        } else {
            System.out.println("\nAll tests passed successfully!");
        }
        
        System.out.println("==================================");
    }
}
