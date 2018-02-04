package com.example.tarikh.myapplication;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ParkingBayMainTest {

    Instrumentation.ActivityMonitor monitor = getInstrumentation().addMonitor(AboutApp.class.getName(), null, false);

    @Rule
    public ActivityTestRule<ParkingBayMain> mainActivityTestRule = new ActivityTestRule<ParkingBayMain>(
            ParkingBayMain.class
    );

    @Test
    public void assertThatCorrectToolbarIsDisplayed() {
        ParkingBayMain mainActivity = mainActivityTestRule.getActivity();
        View toolbarView = mainActivity.findViewById(R.id.my_toolbar);
        assertNotNull(toolbarView);
    }

    @Test
    public void assertThatMapViewIsDisplayed() {
        ParkingBayMain mainActivity = mainActivityTestRule.getActivity();
        View map = mainActivity.findViewById(R.id.mapFrag);
        assertNotNull(map);
    }

    @Test
    public void assertThatAboutUsActivityStartsWhenHelpGuideButtonIsClicked() {
        ParkingBayMain mainActivity = mainActivityTestRule.getActivity();
        assertNotNull(mainActivity.findViewById(R.id.my_toolbar));

        onView(withId(R.id.aboutAppItemID)).perform(click());
        Activity secondActivity = getInstrumentation().waitForMonitorWithTimeout(monitor, 2000);
        assertNotNull(secondActivity);
        assertNotNull(secondActivity.findViewById(R.id.textView));

    }

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.example.tarikh.myapplication", appContext.getPackageName());
    }
}
