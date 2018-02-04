package com.example.tarikh.myapplication;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class AboutAppTest {
    @Rule
    public ActivityTestRule<AboutApp> aboutAppActivityRule = new ActivityTestRule<AboutApp>(
            AboutApp.class
    );

    @Test
    public void assertThatFirstTextViewIsDisplayed() {
        AboutApp aboutApp = aboutAppActivityRule.getActivity();
        View tv1 = aboutApp.findViewById(R.id.textView);
        assertNotNull(tv1);
    }

    @Test
    public void assertThatSecondTextViewIsDisplayed() {
        AboutApp aboutApp = aboutAppActivityRule.getActivity();
        View tv2 = aboutApp.findViewById(R.id.textView2);
        assertNotNull(tv2);
    }

    @Test
    public void assertThatImageViewIsDisplayed() {
        AboutApp aboutApp = aboutAppActivityRule.getActivity();
        View imageView = aboutApp.findViewById(R.id.imageView);
        assertNotNull(imageView);
    }
}
