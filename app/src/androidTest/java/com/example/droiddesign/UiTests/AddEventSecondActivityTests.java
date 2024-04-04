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
import com.example.droiddesign.view.AddEventActivity;
import com.example.droiddesign.view.AddEventSecondActivity;


import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class AddEventSecondActivityTests {
    @Rule
    public ActivityScenarioRule<AddEventSecondActivity> scenario = new ActivityScenarioRule<>(AddEventSecondActivity.class);

    @Test
    public void testAttendeeLimit(){
        onView(withId(R.id.input_number_max_attendees)).perform(typeText("10"),closeSoftKeyboard());
        onView(withId(R.id.input_number_max_attendees)).check(matches(isDisplayed()));
    }

    @Test
    public void testFinishAddingEvent(){
        onView(withId(R.id.finish_add_button)).perform(click());
        onView(withId(R.id.event_description)).check(matches(isDisplayed()));
    }




}