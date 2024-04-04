package com.example.droiddesign.UiTests;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import androidx.test.espresso.contrib.PickerActions;
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


import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class AddEventActivityTests {
    @Rule
    public ActivityScenarioRule<AddEventActivity> scenario = new ActivityScenarioRule<>(AddEventActivity.class);

    @Test
    public void testCancelButton(){
        onView(withId(R.id.button_cancel)).perform(click());
        onView(withId(R.id.fab_quick_scan)).check(matches(isDisplayed()));
    }

    @Test
    public void testBrowseEventsNavigation(){
        onView(withId(R.id.button_start_date)).perform(click());
        onView(withId(R.id.browse_events)).perform(click());
        onView(withId(R.id.events_recycler_view)).check(matches(isDisplayed()));
    }

    @Test
    public void testDatePicker() {

            onView(withId(R.id.button_start_date)).perform(click());

            onView(withClassName(Matchers.equalTo(DatePicker.class.getName())))
                    .perform(PickerActions.setDate(2023, 3, 27)); // Set the desired date


            onView(withText(android.R.string.ok)).perform(click());

            onView(withId(R.id.button_start_date)).check(matches(withText("27-03-2023"))); // Adjust the expected date format as needed

        }
    @Test
    public void testTimePicker() {
        onView(withId(R.id.button_start_time)).perform(click());
        onView(withClassName(Matchers.equalTo(TimePicker.class.getName()))).perform(PickerActions.setTime(10,20));
        onView(withText(android.R.string.ok)).perform(click());
        onView(withId(R.id.button_start_time)).check(matches(withText("10:20")));
    }

    @Test
    public void testNextPageNav(){
        onView(withId(R.id.fab_next_page)).perform(click());
        onView(withId(R.id.button_upload_poster)).check(matches(isDisplayed()));
    }





}