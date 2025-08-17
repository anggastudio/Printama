package com.anggastudio.printama.ui

import android.Manifest
import android.bluetooth.BluetoothDevice
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.anggastudio.printama.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import java.util.*

@RunWith(AndroidJUnit4::class)
class DeviceListFragmentInstrumentedTest {

    @get:Rule
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.BLUETOOTH,
        Manifest.permission.BLUETOOTH_ADMIN,
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.BLUETOOTH_SCAN
    )

    @Test
    fun fragmentWithNoDevices_showsEmptyState() {
        val scenario = launchFragmentInContainer<DeviceListFragment>(themeResId = androidx.appcompat.R.style.Theme_AppCompat)

        scenario.onFragment { fragment ->
            fragment.setDeviceList(emptySet())
        }

        onView(withId(R.id.tv_empty_state)).check(matches(isDisplayed()))
        onView(withId(R.id.rv_device_list)).check(matches(withEffectiveVisibility(Visibility.GONE)))
        onView(withId(R.id.btn_save_printer)).check(matches(isDisplayed()))
    }

    @Test
    fun fragmentWithDevices_showsListAndButtons() {
        val device1 = Mockito.mock(BluetoothDevice::class.java)
        val device2 = Mockito.mock(BluetoothDevice::class.java)
        val set: Set<BluetoothDevice> = LinkedHashSet(listOf(device1, device2))

        val scenario = launchFragmentInContainer<DeviceListFragment>(themeResId = androidx.appcompat.R.style.Theme_AppCompat)

        scenario.onFragment { fragment ->
            fragment.setDeviceList(set)
        }

        onView(withId(R.id.rv_device_list)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_test_printer)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_save_printer)).check(matches(isDisplayed()))
    }
}