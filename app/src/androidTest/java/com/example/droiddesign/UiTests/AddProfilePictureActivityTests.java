package com.example.droiddesign.UiTests;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import androidx.test.espresso.contrib.PickerActions;

import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.droiddesign.R;

import com.example.droiddesign.view.AddProfilePictureActivity;


import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class AddProfilePictureActivityTests {
    @Rule
    public ActivityScenarioRule<AddProfilePictureActivity> scenario = new ActivityScenarioRule<>(AddProfilePictureActivity.class);

    @Test
    public void testSaveButton() throws InterruptedException {
        onView(withId(R.id.save_button)).perform(click());
        Thread.sleep(5000);
        onView(withId(R.id.event_description)).check(matches(isDisplayed()));
    }

    @Test
    public void testBackButton(){
        onView(withId(R.id.button_back)).perform(click());
        onView(withId(R.id.event_description)).check(matches(isDisplayed()));
    }




}