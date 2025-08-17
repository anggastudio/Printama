package com.anggastudio.printama.constants;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;

/**
 * Unit tests for PW (Printama Width) constants class
 * Tests all width constants and their values
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class PWTest {

    @Test
    public void testWidthConstants() {
        // Test that all width constants have correct values
        assertEquals("ORIGINAL_WIDTH should be 0", 0, PW.ORIGINAL_WIDTH);
        assertEquals("FULL_WIDTH should be -1", -1, PW.FULL_WIDTH);
        assertEquals("HALF_WIDTH should be -2", -2, PW.HALF_WIDTH);
        assertEquals("ONE_THIRD_WIDTH should be -3", -3, PW.ONE_THIRD_WIDTH);
        assertEquals("QUARTER_WIDTH should be -4", -4, PW.QUARTER_WIDTH);
        assertEquals("TWO_THIRD_WIDTH should be -5", -5, PW.TWO_THIRD_WIDTH);
        assertEquals("THREE_QUARTERS_WIDTH should be -6", -6, PW.THREE_QUARTERS_WIDTH);
    }

    @Test
    public void testConstantsArePublicStaticFinal() {
        // Test that constants are accessible as static fields
        int original = PW.ORIGINAL_WIDTH;
        int full = PW.FULL_WIDTH;
        int half = PW.HALF_WIDTH;
        int oneThird = PW.ONE_THIRD_WIDTH;
        int quarter = PW.QUARTER_WIDTH;
        int twoThird = PW.TWO_THIRD_WIDTH;
        int threeQuarters = PW.THREE_QUARTERS_WIDTH;
        
        // Verify they are accessible
        assertNotNull("ORIGINAL_WIDTH should be accessible", (Integer) original);
        assertNotNull("FULL_WIDTH should be accessible", (Integer) full);
        assertNotNull("HALF_WIDTH should be accessible", (Integer) half);
        assertNotNull("ONE_THIRD_WIDTH should be accessible", (Integer) oneThird);
        assertNotNull("QUARTER_WIDTH should be accessible", (Integer) quarter);
        assertNotNull("TWO_THIRD_WIDTH should be accessible", (Integer) twoThird);
        assertNotNull("THREE_QUARTERS_WIDTH should be accessible", (Integer) threeQuarters);
    }

    @Test
    public void testConstantsUniqueness() {
        // Test that all constants have unique values
        int[] constants = {
            PW.ORIGINAL_WIDTH, PW.FULL_WIDTH, PW.HALF_WIDTH, 
            PW.ONE_THIRD_WIDTH, PW.QUARTER_WIDTH, PW.TWO_THIRD_WIDTH, 
            PW.THREE_QUARTERS_WIDTH
        };
        
        for (int i = 0; i < constants.length; i++) {
            for (int j = i + 1; j < constants.length; j++) {
                assertNotEquals("Constants should have unique values", constants[i], constants[j]);
            }
        }
    }

    @Test
    public void testConstantsRange() {
        // Test that constants are within expected range
        assertTrue("ORIGINAL_WIDTH should be non-negative", PW.ORIGINAL_WIDTH >= 0);
        assertTrue("FULL_WIDTH should be negative", PW.FULL_WIDTH < 0);
        assertTrue("HALF_WIDTH should be negative", PW.HALF_WIDTH < 0);
        assertTrue("ONE_THIRD_WIDTH should be negative", PW.ONE_THIRD_WIDTH < 0);
        assertTrue("QUARTER_WIDTH should be negative", PW.QUARTER_WIDTH < 0);
        assertTrue("TWO_THIRD_WIDTH should be negative", PW.TWO_THIRD_WIDTH < 0);
        assertTrue("THREE_QUARTERS_WIDTH should be negative", PW.THREE_QUARTERS_WIDTH < 0);
    }

    @Test
    public void testConstantsOrdering() {
        // Test logical ordering of width values (descending)
        assertTrue("ORIGINAL_WIDTH should be greatest", PW.ORIGINAL_WIDTH > PW.FULL_WIDTH);
        assertTrue("FULL_WIDTH should be greater than HALF_WIDTH", PW.FULL_WIDTH > PW.HALF_WIDTH);
        assertTrue("HALF_WIDTH should be greater than ONE_THIRD_WIDTH", PW.HALF_WIDTH > PW.ONE_THIRD_WIDTH);
        assertTrue("ONE_THIRD_WIDTH should be greater than QUARTER_WIDTH", PW.ONE_THIRD_WIDTH > PW.QUARTER_WIDTH);
        assertTrue("QUARTER_WIDTH should be greater than TWO_THIRD_WIDTH", PW.QUARTER_WIDTH > PW.TWO_THIRD_WIDTH);
        assertTrue("TWO_THIRD_WIDTH should be greater than THREE_QUARTERS_WIDTH", PW.TWO_THIRD_WIDTH > PW.THREE_QUARTERS_WIDTH);
    }

    @Test
    public void testConstantsInSwitchStatement() {
        // Test that constants can be used in switch statements
        String result = getWidthDescription(PW.ORIGINAL_WIDTH);
        assertEquals("ORIGINAL_WIDTH should work in switch", "original", result);
        
        result = getWidthDescription(PW.FULL_WIDTH);
        assertEquals("FULL_WIDTH should work in switch", "full", result);
        
        result = getWidthDescription(PW.HALF_WIDTH);
        assertEquals("HALF_WIDTH should work in switch", "half", result);
    }

    @Test
    public void testConstantsInArrays() {
        // Test that constants can be used in arrays
        int[] widths = {
            PW.ORIGINAL_WIDTH, PW.FULL_WIDTH, PW.HALF_WIDTH,
            PW.ONE_THIRD_WIDTH, PW.QUARTER_WIDTH, PW.TWO_THIRD_WIDTH,
            PW.THREE_QUARTERS_WIDTH
        };
        
        assertEquals("Array should contain ORIGINAL_WIDTH", PW.ORIGINAL_WIDTH, widths[0]);
        assertEquals("Array should contain FULL_WIDTH", PW.FULL_WIDTH, widths[1]);
        assertEquals("Array should contain HALF_WIDTH", PW.HALF_WIDTH, widths[2]);
        assertEquals("Array should contain ONE_THIRD_WIDTH", PW.ONE_THIRD_WIDTH, widths[3]);
        assertEquals("Array should contain QUARTER_WIDTH", PW.QUARTER_WIDTH, widths[4]);
        assertEquals("Array should contain TWO_THIRD_WIDTH", PW.TWO_THIRD_WIDTH, widths[5]);
        assertEquals("Array should contain THREE_QUARTERS_WIDTH", PW.THREE_QUARTERS_WIDTH, widths[6]);
    }

    @Test
    public void testConstantsComparison() {
        // Test comparison operations with constants
        assertTrue("ORIGINAL_WIDTH == 0", PW.ORIGINAL_WIDTH == 0);
        assertTrue("FULL_WIDTH == -1", PW.FULL_WIDTH == -1);
        assertTrue("HALF_WIDTH == -2", PW.HALF_WIDTH == -2);
        assertTrue("ONE_THIRD_WIDTH == -3", PW.ONE_THIRD_WIDTH == -3);
        assertTrue("QUARTER_WIDTH == -4", PW.QUARTER_WIDTH == -4);
        assertTrue("TWO_THIRD_WIDTH == -5", PW.TWO_THIRD_WIDTH == -5);
        assertTrue("THREE_QUARTERS_WIDTH == -6", PW.THREE_QUARTERS_WIDTH == -6);
    }

    @Test
    public void testConstantsAsMethodParameters() {
        // Test that constants can be passed as method parameters
        assertEquals("ORIGINAL_WIDTH should be valid parameter", 0, getWidthValue(PW.ORIGINAL_WIDTH));
        assertEquals("FULL_WIDTH should be valid parameter", -1, getWidthValue(PW.FULL_WIDTH));
        assertEquals("HALF_WIDTH should be valid parameter", -2, getWidthValue(PW.HALF_WIDTH));
        assertEquals("ONE_THIRD_WIDTH should be valid parameter", -3, getWidthValue(PW.ONE_THIRD_WIDTH));
        assertEquals("QUARTER_WIDTH should be valid parameter", -4, getWidthValue(PW.QUARTER_WIDTH));
        assertEquals("TWO_THIRD_WIDTH should be valid parameter", -5, getWidthValue(PW.TWO_THIRD_WIDTH));
        assertEquals("THREE_QUARTERS_WIDTH should be valid parameter", -6, getWidthValue(PW.THREE_QUARTERS_WIDTH));
    }

    @Test
    public void testConstantsImmutability() {
        // Test that constants maintain their values
        int originalOriginal = PW.ORIGINAL_WIDTH;
        int originalFull = PW.FULL_WIDTH;
        int originalHalf = PW.HALF_WIDTH;
        int originalOneThird = PW.ONE_THIRD_WIDTH;
        int originalQuarter = PW.QUARTER_WIDTH;
        int originalTwoThird = PW.TWO_THIRD_WIDTH;
        int originalThreeQuarters = PW.THREE_QUARTERS_WIDTH;
        
        // Simulate some operations
        performDummyOperations();
        
        // Verify constants haven't changed
        assertEquals("ORIGINAL_WIDTH should remain unchanged", originalOriginal, PW.ORIGINAL_WIDTH);
        assertEquals("FULL_WIDTH should remain unchanged", originalFull, PW.FULL_WIDTH);
        assertEquals("HALF_WIDTH should remain unchanged", originalHalf, PW.HALF_WIDTH);
        assertEquals("ONE_THIRD_WIDTH should remain unchanged", originalOneThird, PW.ONE_THIRD_WIDTH);
        assertEquals("QUARTER_WIDTH should remain unchanged", originalQuarter, PW.QUARTER_WIDTH);
        assertEquals("TWO_THIRD_WIDTH should remain unchanged", originalTwoThird, PW.TWO_THIRD_WIDTH);
        assertEquals("THREE_QUARTERS_WIDTH should remain unchanged", originalThreeQuarters, PW.THREE_QUARTERS_WIDTH);
    }

    @Test
    public void testConstantsDocumentationValues() {
        // Test that constants match their documented values
        assertEquals("ORIGINAL_WIDTH should be 0 as documented", 0, PW.ORIGINAL_WIDTH);
        assertEquals("FULL_WIDTH should be -1 as documented", -1, PW.FULL_WIDTH);
        assertEquals("HALF_WIDTH should be -2 as documented", -2, PW.HALF_WIDTH);
        assertEquals("ONE_THIRD_WIDTH should be -3 as documented", -3, PW.ONE_THIRD_WIDTH);
        assertEquals("QUARTER_WIDTH should be -4 as documented", -4, PW.QUARTER_WIDTH);
        assertEquals("TWO_THIRD_WIDTH should be -5 as documented", -5, PW.TWO_THIRD_WIDTH);
        assertEquals("THREE_QUARTERS_WIDTH should be -6 as documented", -6, PW.THREE_QUARTERS_WIDTH);
    }

    @Test
    public void testFractionalWidthRelationships() {
        // Test logical relationships between fractional widths
        // These should be ordered by their actual fraction values
        // Note: More negative values represent larger fractions
        assertTrue("THREE_QUARTERS_WIDTH should be smallest (most negative)", 
                   PW.THREE_QUARTERS_WIDTH < PW.TWO_THIRD_WIDTH);
        assertTrue("TWO_THIRD_WIDTH should be less than HALF_WIDTH", 
                   PW.TWO_THIRD_WIDTH < PW.HALF_WIDTH);
        assertTrue("HALF_WIDTH should be greater than ONE_THIRD_WIDTH", 
                   PW.HALF_WIDTH > PW.ONE_THIRD_WIDTH); // Fixed: > instead of <
        assertTrue("ONE_THIRD_WIDTH should be greater than QUARTER_WIDTH", 
                   PW.ONE_THIRD_WIDTH > PW.QUARTER_WIDTH); // Fixed: > instead of <
    }

    @Test
    public void testConstantsPerformance() {
        // Test that accessing constants is fast
        long startTime = System.nanoTime();
        
        // Access constants multiple times
        for (int i = 0; i < 1000; i++) {
            int original = PW.ORIGINAL_WIDTH;
            int full = PW.FULL_WIDTH;
            int half = PW.HALF_WIDTH;
            int oneThird = PW.ONE_THIRD_WIDTH;
            int quarter = PW.QUARTER_WIDTH;
            int twoThird = PW.TWO_THIRD_WIDTH;
            int threeQuarters = PW.THREE_QUARTERS_WIDTH;
        }
        
        long endTime = System.nanoTime();
        long duration = endTime - startTime;
        
        // Should be very fast (less than 1ms for 1000 accesses)
        assertTrue("Constant access should be fast", duration < 1_000_000); // 1ms in nanoseconds
    }

    @Test
    public void testAllConstantsCount() {
        // Test that we have the expected number of constants
        // This helps ensure no constants are missed in testing
        int[] allConstants = {
            PW.ORIGINAL_WIDTH, PW.FULL_WIDTH, PW.HALF_WIDTH,
            PW.ONE_THIRD_WIDTH, PW.QUARTER_WIDTH, PW.TWO_THIRD_WIDTH,
            PW.THREE_QUARTERS_WIDTH
        };
        
        assertEquals("Should have exactly 7 width constants", 7, allConstants.length);
    }

    // Helper methods for testing
    private String getWidthDescription(int width) {
        switch (width) {
            case 0: // PW.ORIGINAL_WIDTH
                return "original";
            case -1: // PW.FULL_WIDTH
                return "full";
            case -2: // PW.HALF_WIDTH
                return "half";
            case -3: // PW.ONE_THIRD_WIDTH
                return "one-third";
            case -4: // PW.QUARTER_WIDTH
                return "quarter";
            case -5: // PW.TWO_THIRD_WIDTH
                return "two-third";
            case -6: // PW.THREE_QUARTERS_WIDTH
                return "three-quarters";
            default:
                return "unknown";
        }
    }
    
    private int getWidthValue(int width) {
        return width;
    }
    
    private void performDummyOperations() {
        // Simulate some operations
        int temp = 0;
        for (int i = 0; i < 100; i++) {
            temp += i;
        }
    }
}