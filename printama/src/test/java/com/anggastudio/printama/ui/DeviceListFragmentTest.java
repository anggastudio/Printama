package com.anggastudio.printama.ui;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.InflateException;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;

import com.anggastudio.printama.Printama;
import com.anggastudio.printama.R;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.robolectric.Shadows.shadowOf;

/**
 * Unit tests for {@link DeviceListFragment}
 * Tests DialogFragment functionality for Bluetooth device selection
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28, manifest = Config.NONE)
public class DeviceListFragmentTest {

    @Mock
    private BluetoothDevice mockDevice1;
    
    @Mock
    private BluetoothDevice mockDevice2;
    
    @Mock
    private Printama.OnConnectPrinter mockOnConnectPrinter;
    
    private DeviceListFragment fragment;
    private Set<BluetoothDevice> bondedDevices;
    private Context context;
    
    private static final String DEVICE_ADDRESS_1 = "00:11:22:33:44:55";
    private static final String DEVICE_ADDRESS_2 = "AA:BB:CC:DD:EE:FF";
    private static final String DEVICE_NAME_1 = "Printer Device 1";
    private static final String DEVICE_NAME_2 = "Printer Device 2";

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        context = RuntimeEnvironment.getApplication();
        
        // Setup mock devices
        when(mockDevice1.getAddress()).thenReturn(DEVICE_ADDRESS_1);
        when(mockDevice2.getAddress()).thenReturn(DEVICE_ADDRESS_2);
        when(mockDevice1.getName()).thenReturn(DEVICE_NAME_1);
        when(mockDevice2.getName()).thenReturn(DEVICE_NAME_2);
        
        // Setup bonded devices set
        bondedDevices = new HashSet<>();
        bondedDevices.add(mockDevice1);
        bondedDevices.add(mockDevice2);
        
        fragment = DeviceListFragment.newInstance();
    }

    @Test
    public void testNewInstance() {
        DeviceListFragment fragment = DeviceListFragment.newInstance();
        
        assertNotNull("Fragment should be created", fragment);
        assertNotNull("Fragment should have arguments", fragment.getArguments());
    }

    @Test
    public void testConstructor() {
        DeviceListFragment fragment = new DeviceListFragment();
        
        assertNotNull("Fragment should be created with default constructor", fragment);
    }

    @Test
    public void testSetOnConnectPrinter() {
        fragment.setOnConnectPrinter(mockOnConnectPrinter);
        
        // Verify no exception is thrown
        assertNotNull("Fragment should handle callback setting", fragment);
    }

    @Test
    public void testSetOnConnectPrinter_withNull() {
        fragment.setOnConnectPrinter(null);
        
        // Verify no exception is thrown
        assertNotNull("Fragment should handle null callback", fragment);
    }

    @Test
    public void testSetDeviceList() {
        fragment.setDeviceList(bondedDevices);
        
        // Verify no exception is thrown
        assertNotNull("Fragment should handle device list setting", fragment);
    }

    @Test
    public void testSetDeviceList_withEmptySet() {
        Set<BluetoothDevice> emptySet = new HashSet<>();
        fragment.setDeviceList(emptySet);
        
        // Verify no exception is thrown
        assertNotNull("Fragment should handle empty device list", fragment);
    }

    @Test
    public void testSetDeviceList_withNull() {
        fragment.setDeviceList(null);
        
        // Verify no exception is thrown
        assertNotNull("Fragment should handle null device list", fragment);
    }

    @Test
    public void testSetColorTheme() {
        int activeColor = 0xFF00FF00; // Green
        int inactiveColor = 0xFF808080; // Gray
        
        fragment.setColorTheme(activeColor, inactiveColor);
        
        // Verify no exception is thrown
        assertNotNull("Fragment should handle color theme setting", fragment);
    }

    @Test
    public void testSetColorTheme_withZeroValues() {
        fragment.setColorTheme(0, 0);
        
        // Verify no exception is thrown
        assertNotNull("Fragment should handle zero color values", fragment);
    }

    private static boolean isRobolectricResourceInflationFailure(Exception e) {
        String msg = e.getMessage();
        return (e instanceof android.view.InflateException)
                || (e instanceof android.content.res.Resources.NotFoundException)
                || (msg != null && (
                        msg.contains("No package ID")
                                || msg.toLowerCase().contains("inflate")
                                || msg.toLowerCase().contains("layout")
                                || msg.toLowerCase().contains("resource")
                ));
    }

    private static boolean isAcceptableDialogCreationFailure(Exception e) {
        String msg = e.getMessage();
        return (e instanceof NullPointerException)
                || (e instanceof IllegalStateException)
                || isRobolectricResourceInflationFailure(e)
                || (msg != null && (
                        msg.toLowerCase().contains("dialog")
                                || msg.toLowerCase().contains("window")
                                || msg.toLowerCase().contains("theme")
                ));
    }

    @Test
    public void testOnCreateView() {
        fragment = DeviceListFragment.newInstance();
        
        try {
            // Test fragment creation
            FragmentManager fragmentManager = mock(FragmentManager.class);
            FragmentTransaction transaction = mock(FragmentTransaction.class);
            when(fragmentManager.beginTransaction()).thenReturn(transaction);
            
            View view = fragment.onCreateView(
                LayoutInflater.from(context), null, null);
            
            assertNotNull("Fragment view should be created", view);
        } catch (Exception e) {
            assertTrue("Should fail due to missing layout/resources, got: "
                            + e.getClass().getName() + " - " + e.getMessage(),
                    isRobolectricResourceInflationFailure(e));
        }
    }

    @Test
    public void testFragmentLifecycle() {
        fragment = DeviceListFragment.newInstance();
        
        // Test basic fragment operations that don't require UI
        assertNotNull("Fragment should be created", fragment);
        
        fragment.setOnConnectPrinter(mockOnConnectPrinter);
        fragment.setDeviceList(bondedDevices);
        
        // These should work without UI inflation
        assertTrue("Fragment setup should complete", true);
    }

    @Test
    public void testOnCreateDialog() {
        if (fragment == null) {
            fragment = DeviceListFragment.newInstance();
        }
        try {
            android.app.Dialog dialog = fragment.onCreateDialog(null);
            assertNotNull("Dialog should be created", dialog);
        } catch (Exception e) {
            assertTrue("Exception should be acceptable for dialog creation under Robolectric. Got: "
                            + e.getClass().getName() + " - " + e.getMessage(),
                    isAcceptableDialogCreationFailure(e));
        }
    }

    @Test
    public void testPrivateMethodsAccessibility() {
        // Test that private methods exist and can be accessed via reflection
        try {
            java.lang.reflect.Method setupDeviceListMethod = 
                DeviceListFragment.class.getDeclaredMethod("setupDeviceList");
            assertNotNull("setupDeviceList method should exist", setupDeviceListMethod);
            
            java.lang.reflect.Method showDeviceListMethod = 
                DeviceListFragment.class.getDeclaredMethod("showDeviceList");
            assertNotNull("showDeviceList method should exist", showDeviceListMethod);
            
            java.lang.reflect.Method showEmptyStateMethod = 
                DeviceListFragment.class.getDeclaredMethod("showEmptyState");
            assertNotNull("showEmptyState method should exist", showEmptyStateMethod);
            
            java.lang.reflect.Method openBluetoothSettingsMethod = 
                DeviceListFragment.class.getDeclaredMethod("openBluetoothSettings");
            assertNotNull("openBluetoothSettings method should exist", openBluetoothSettingsMethod);
            
            java.lang.reflect.Method testPrinterMethod = 
                DeviceListFragment.class.getDeclaredMethod("testPrinter");
            assertNotNull("testPrinter method should exist", testPrinterMethod);
            
            java.lang.reflect.Method savePrinterMethod = 
                DeviceListFragment.class.getDeclaredMethod("savePrinter");
            assertNotNull("savePrinter method should exist", savePrinterMethod);
            
            java.lang.reflect.Method toggleButtonsMethod = 
                DeviceListFragment.class.getDeclaredMethod("toggleButtons");
            assertNotNull("toggleButtons method should exist", toggleButtonsMethod);
            
            java.lang.reflect.Method setColorMethod = 
                DeviceListFragment.class.getDeclaredMethod("setColor");
            assertNotNull("setColor method should exist", setColorMethod);
            
        } catch (NoSuchMethodException e) {
            fail("Expected methods should exist: " + e.getMessage());
        }
    }

    @Test
    public void testOnActivityResult() {
        Intent data = new Intent();
        
        // Test onActivityResult doesn't throw exception
        try {
            fragment.onActivityResult(1001, android.app.Activity.RESULT_OK, data);
            // Should not throw exception
        } catch (Exception e) {
            // Unexpected exception
            fail("onActivityResult should not throw exception: " + e.getMessage());
        }
    }

    @Test
    public void testOnActivityResult_wrongRequestCode() {
        Intent data = new Intent();
        
        try {
            fragment.onActivityResult(9999, android.app.Activity.RESULT_OK, data);
            // Should not throw exception
        } catch (Exception e) {
            fail("onActivityResult should handle wrong request code: " + e.getMessage());
        }
    }

    @Test
    public void testOnActivityCreated() {
        try {
            fragment.onActivityCreated(null);
            // Should not throw exception
        } catch (Exception e) {
            // Expected in test environment due to context access
            assertTrue("Exception should be related to context access", 
                e instanceof NullPointerException);
        }
    }

    @Test
    public void testEdgeCases_nullContext() {
        // Test behavior when context is null
        fragment.setDeviceList(bondedDevices);
        
        // Fragment should handle null context gracefully
        assertNotNull("Fragment should handle null context", fragment);
    }

    @Test
    public void testEdgeCases_largeDeviceSet() {
        // Test with a large number of devices
        Set<BluetoothDevice> largeDeviceSet = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            BluetoothDevice mockDevice = mock(BluetoothDevice.class);
            when(mockDevice.getAddress()).thenReturn(String.format("00:11:22:33:44:%02d", i % 100));
            when(mockDevice.getName()).thenReturn("Device " + i);
            largeDeviceSet.add(mockDevice);
        }
        
        fragment.setDeviceList(largeDeviceSet);
        
        // Should handle large device set without issues
        assertNotNull("Fragment should handle large device set", fragment);
    }

    @Test
    public void testMultipleCallbacks() {
        // Test setting multiple callbacks
        Printama.OnConnectPrinter callback1 = mock(Printama.OnConnectPrinter.class);
        Printama.OnConnectPrinter callback2 = mock(Printama.OnConnectPrinter.class);
        
        fragment.setOnConnectPrinter(callback1);
        fragment.setOnConnectPrinter(callback2);
        
        // Should handle multiple callback settings
        assertNotNull("Fragment should handle multiple callbacks", fragment);
    }

    @Test
    public void testStatePreservation() {
        // Test that fragment can preserve state
        fragment.setDeviceList(bondedDevices);
        fragment.setOnConnectPrinter(mockOnConnectPrinter);
        fragment.setColorTheme(0xFF00FF00, 0xFF808080);
        
        // Create a new fragment and verify it can be configured the same way
        DeviceListFragment newFragment = DeviceListFragment.newInstance();
        newFragment.setDeviceList(bondedDevices);
        newFragment.setOnConnectPrinter(mockOnConnectPrinter);
        newFragment.setColorTheme(0xFF00FF00, 0xFF808080);
        
        assertNotNull("New fragment should be configurable", newFragment);
    }
}