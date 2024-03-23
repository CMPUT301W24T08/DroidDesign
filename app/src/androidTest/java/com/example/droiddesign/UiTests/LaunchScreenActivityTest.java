package com.example.droiddesign.UiTests;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.droiddesign.R;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class LaunchScreenActivityTest {

    @Test
    public void testSkipButton() {
        onView(withId(R.id.skip_button)).perform(click());
        // Add checks to ensure the new activity is opened, if any
    }

    @Test
    public void testSignUp() {
        onView(withId(R.id.button_sign_up)).perform(click());
        // Add checks for the Sign Up dialog
    }

    @Test
    public void testLoginWithEmail() {
        onView(withId(R.id.continue_with_email)).perform(click());
        // Fill in the email and password fields and submit
    }

    @Test
    public void testContinueWithGoogle() {
        onView(withId(R.id.button_continue_with_google)).perform(click());
    }
}