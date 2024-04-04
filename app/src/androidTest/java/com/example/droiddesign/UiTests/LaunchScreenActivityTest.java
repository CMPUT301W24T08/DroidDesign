package com.example.droiddesign.UiTests;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.droiddesign.R;
import com.example.droiddesign.view.AddEventActivity;
import com.example.droiddesign.view.LaunchScreenActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class LaunchScreenActivityTest {

    @Rule
    public ActivityScenarioRule<LaunchScreenActivity> scenario = new ActivityScenarioRule<>(LaunchScreenActivity.class);

    @Test
    public void testEntryButton(){
        onView(withId(R.id.button_enter)).perform(click());
        onView(withId(R.id.edit_email)).check(matches(isDisplayed()));
    }

    @Test
    public void testSkipNavigation(){
        onView(withId(R.id.button_enter)).perform(click());
        onView(withId(R.id.skip_account_creation)).perform(click());
        onView(withId(R.id.organizer_button)).check(matches(isDisplayed()));


    }
}