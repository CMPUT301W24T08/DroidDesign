package com.example.droiddesign.UiTests;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.droiddesign.R;
import com.example.droiddesign.model.SharedPreferenceHelper;
import com.example.droiddesign.view.EventMenuActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;

@RunWith(AndroidJUnit4.class)
public class EventMenuActivityTests {
    @Rule
    public ActivityScenarioRule<EventMenuActivity> scenario = new ActivityScenarioRule<>(EventMenuActivity.class);

    private FirebaseAuth mockAuth;


    @Test
    public void testSideBarButton(){
        SharedPreferenceHelper mockPrefHelper = mock(SharedPreferenceHelper.class);
        when(mockPrefHelper.getRole()).thenReturn("attendee");

        // Launch the activity
        scenario.getScenario().onActivity(activity -> {
            try {
                // Use reflection to set the sharedPreferenceHelper field
                Field field = EventMenuActivity.class.getDeclaredField("sharedPreferenceHelper");
                field.setAccessible(true); // Make the field accessible
                field.set(activity, mockPrefHelper); // Set the mock SharedPreferenceHelper

                // Perform UI testing actions
                onView(withId(R.id.button_menu)).perform(click());
                onView(withId(R.id.browse_events)).check(matches(isDisplayed()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    @Test
    public void testBrowseEvents(){
        FirebaseUser mockUser = mock(FirebaseUser.class);
        when(mockUser.getUid()).thenReturn("mock");
        onView(withId(R.id.button_menu)).perform(click());
        onView(withId(R.id.browse_events)).perform(click());
        onView(withId(R.id.events_recycler_view)).check(matches(isDisplayed()));
    }


}