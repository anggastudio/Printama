package com.anggastudio.printama.ui

import android.Manifest
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.anggastudio.printama.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ChoosePrinterActivityInstrumentedTest {

    @get:Rule
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.BLUETOOTH,
        Manifest.permission.BLUETOOTH_ADMIN,
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.BLUETOOTH_SCAN
    )

    @Test
    fun activityLaunches_displaysExpectedViews() {
        ActivityScenario.launch(ChoosePrinterActivity::class.java).use {
            onView(withId(R.id.rv_device_list)).check(matches(isDisplayed()))
            onView(withId(R.id.btn_test_printer)).check(matches(isDisplayed()))
            onView(withId(R.id.btn_save_printer)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun buttons_areClickable() {
        ActivityScenario.launch(ChoosePrinterActivity::class.java).use {
            onView(withId(R.id.btn_test_printer)).check(matches(isClickable()))
            onView(withId(R.id.btn_save_printer)).check(matches(isClickable()))
        }
    }
}