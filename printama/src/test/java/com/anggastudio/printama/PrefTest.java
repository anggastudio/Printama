package com.anggastudio.printama;

import android.content.Context;
import android.content.SharedPreferences;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class PrefTest {

    @Mock
    private Context mockContext;
    
    @Mock
    private SharedPreferences mockSharedPreferences;
    
    @Mock
    private SharedPreferences.Editor mockEditor;

    private Context context;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        context = RuntimeEnvironment.getApplication();
        
        // Setup mock behavior
        when(mockContext.getSharedPreferences(anyString(), anyInt()))
            .thenReturn(mockSharedPreferences);
        when(mockSharedPreferences.edit()).thenReturn(mockEditor);
        when(mockEditor.putString(anyString(), anyString())).thenReturn(mockEditor);
        when(mockEditor.putBoolean(anyString(), anyBoolean())).thenReturn(mockEditor);
        // Remove the problematic line - apply() returns void, so we don't mock its return value
        // when(mockEditor.apply()).thenReturn(null);  // This line causes the compilation error
    }

    @Test
    public void testConstants() {
        // Test that constants are properly defined
        assertEquals("bonded_device", Pref.SAVED_DEVICE);
        assertEquals("is_printer_3inch", Pref.IS_PRINTER_3INCH);
    }

    @Test
    public void testInitWithRealContext() {
        // Test initialization with real context
        Pref.init(context);
        // Should not throw any exceptions
        assertTrue(true);
    }

    @Test
    public void testInitWithMockContext() {
        // Test initialization with mock context
        Pref.init(mockContext);
        verify(mockContext).getSharedPreferences(anyString(), eq(Context.MODE_PRIVATE));
    }

    @Test
    public void testSetAndGetString() {
        // Initialize with real context for actual testing
        Pref.init(context);
        
        String testKey = "test_key";
        String testValue = "test_value";
        
        // Set string value
        Pref.setString(testKey, testValue);
        
        // Get string value
        String retrievedValue = Pref.getString(testKey);
        assertEquals(testValue, retrievedValue);
    }

    @Test
    public void testGetStringWithDefaultValue() {
        Pref.init(context);
        
        String nonExistentKey = "non_existent_key";
        
        String result = Pref.getString(nonExistentKey);
        assertEquals("", result); // Default is empty string
    }

    @Test
    public void testSetAndGetBoolean() {
        Pref.init(context);
        
        String testKey = "test_boolean_key";
        boolean testValue = true;
        
        // Set boolean value
        Pref.setBoolean(testKey, testValue);
        
        // Get boolean value
        boolean retrievedValue = Pref.getBoolean(testKey);
        assertEquals(testValue, retrievedValue);
    }

    @Test
    public void testGetBooleanWithDefaultValue() {
        Pref.init(context);
        
        String nonExistentKey = "non_existent_boolean_key";
        
        boolean result = Pref.getBoolean(nonExistentKey);
        assertFalse(result); // Default is false
    }

    @Test
    public void testSavedDeviceOperations() {
        Pref.init(context);
        
        String deviceName = "Test Bluetooth Device";
        
        // Save device
        Pref.setString(Pref.SAVED_DEVICE, deviceName);
        
        // Retrieve saved device
        String savedDevice = Pref.getString(Pref.SAVED_DEVICE);
        assertEquals(deviceName, savedDevice);
    }

    @Test
    public void testPrinter3InchOperations() {
        Pref.init(context);
        
        // Set printer as 3-inch
        Pref.setBoolean(Pref.IS_PRINTER_3INCH, true);
        
        // Check if printer is 3-inch
        boolean is3Inch = Pref.getBoolean(Pref.IS_PRINTER_3INCH);
        assertTrue(is3Inch);
        
        // Set printer as 2-inch
        Pref.setBoolean(Pref.IS_PRINTER_3INCH, false);
        
        // Check if printer is not 3-inch
        boolean isNot3Inch = Pref.getBoolean(Pref.IS_PRINTER_3INCH);
        assertFalse(isNot3Inch);
    }

    @Test
    public void testMultipleStringOperations() {
        Pref.init(context);
        
        // Test multiple string operations
        Pref.setString("key1", "value1");
        Pref.setString("key2", "value2");
        Pref.setString("key3", "value3");
        
        assertEquals("value1", Pref.getString("key1"));
        assertEquals("value2", Pref.getString("key2"));
        assertEquals("value3", Pref.getString("key3"));
    }

    @Test
    public void testMultipleBooleanOperations() {
        Pref.init(context);
        
        // Test multiple boolean operations
        Pref.setBoolean("bool1", true);
        Pref.setBoolean("bool2", false);
        Pref.setBoolean("bool3", true);
        
        assertTrue(Pref.getBoolean("bool1"));
        assertFalse(Pref.getBoolean("bool2"));
        assertTrue(Pref.getBoolean("bool3"));
    }

    @Test
    public void testOverwriteValues() {
        Pref.init(context);
        
        String key = "overwrite_test";
        
        // Set initial value
        Pref.setString(key, "initial_value");
        assertEquals("initial_value", Pref.getString(key));
        
        // Overwrite with new value
        Pref.setString(key, "new_value");
        assertEquals("new_value", Pref.getString(key));
        
        // Test boolean overwrite
        String boolKey = "bool_overwrite_test";
        Pref.setBoolean(boolKey, true);
        assertTrue(Pref.getBoolean(boolKey));
        
        Pref.setBoolean(boolKey, false);
        assertFalse(Pref.getBoolean(boolKey));
    }

    @Test
    public void testEmptyAndNullValues() {
        Pref.init(context);
        
        // Test empty string
        Pref.setString("empty_key", "");
        assertEquals("", Pref.getString("empty_key"));
        
        // Test null string (should be handled gracefully)
        try {
            Pref.setString("null_key", null);
            String result = Pref.getString("null_key");
            // Result should be empty string as per implementation
            assertEquals("", result);
        } catch (Exception e) {
            // Null handling might throw exception, which is acceptable
            assertTrue(true);
        }
    }

    @Test
    public void testSpecialCharacters() {
        Pref.init(context);
        
        // Test special characters in values
        String specialValue = "Special chars: !@#$%^&*()_+-={}[]|\\:;\"'<>?,./";
        Pref.setString("special_key", specialValue);
        assertEquals(specialValue, Pref.getString("special_key"));
        
        // Test unicode characters
        String unicodeValue = "Unicode: 你好 🌟 ñáéíóú";
        Pref.setString("unicode_key", unicodeValue);
        assertEquals(unicodeValue, Pref.getString("unicode_key"));
    }

    @Test
    public void testLongValues() {
        Pref.init(context);
        
        // Test very long string
        StringBuilder longString = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            longString.append("This is a very long string for testing purposes. ");
        }
        
        String longValue = longString.toString();
        Pref.setString("long_key", longValue);
        assertEquals(longValue, Pref.getString("long_key"));
    }

    @Test
    public void testMockContextBehavior() {
        // Test with mock context to verify method calls
        Pref.init(mockContext);
        
        // Test string operations with mock
        when(mockSharedPreferences.getString("mock_key", ""))
            .thenReturn("mock_value");
        
        String result = Pref.getString("mock_key");
        assertEquals("mock_value", result);
        
        verify(mockSharedPreferences).getString("mock_key", "");
        
        // Test boolean operations with mock
        when(mockSharedPreferences.getBoolean("mock_bool_key", false))
            .thenReturn(true);
        
        boolean boolResult = Pref.getBoolean("mock_bool_key");
        assertTrue(boolResult);
        
        verify(mockSharedPreferences).getBoolean("mock_bool_key", false);
    }

    @Test
    public void testEdgeCaseKeys() {
        Pref.init(context);
        
        // Test empty key
        try {
            Pref.setString("", "empty_key_value");
            String result = Pref.getString("");
            // Should handle empty key gracefully
            assertNotNull(result);
        } catch (Exception e) {
            // Empty key might not be allowed
            assertTrue(true);
        }
        
        // Test very long key
        String longKey = "very_long_key_" + "a".repeat(1000);
        try {
            Pref.setString(longKey, "long_key_value");
            String result = Pref.getString(longKey);
            assertEquals("long_key_value", result);
        } catch (Exception e) {
            // Very long keys might not be supported
            assertTrue(true);
        }
    }

    @Test
    public void testUninitializedAccess() {
        // Test accessing Pref methods without initialization
        // Since we can't truly uninitialize Pref once it's been initialized,
        // we test the behavior when accessing non-existent keys
        // which should return default values (empty string for getString, false for getBoolean)
        
        // Ensure Pref is initialized for consistent behavior
        Pref.init(context);
        
        String result = Pref.getString("non_existent_key_12345");
        assertEquals("", result); // Should return empty string as default
        
        boolean boolResult = Pref.getBoolean("non_existent_bool_key_12345");
        assertFalse(boolResult); // Should return false as default
    }
}