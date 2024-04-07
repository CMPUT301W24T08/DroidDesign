package com.example.droiddesign.UiTests;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.Manifest;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import com.example.droiddesign.R;
import com.example.droiddesign.view.Everybody.LaunchScreenActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ViewEventAnnouncementTest {

	@Rule
	public ActivityScenarioRule<LaunchScreenActivity> intentsTestRule =
			new ActivityScenarioRule<>(LaunchScreenActivity.class);
	@Rule
	public GrantPermissionRule grantPermissionRule = GrantPermissionRule.grant(Manifest.permission.POST_NOTIFICATIONS);

	@Before
	public void setUp() {

	}

	@Test
	public void ViewAnnouncementTest() {
		if (isButtonEnterDisplayed()) {
			onView(withId(R.id.button_enter)).perform(click( ));
			onView(withId(R.
					id.skip_account_creation)).perform(click( ));
			onView(withId(R.id.attendee_button)).check(matches(isDisplayed( )));
			onView(withId(R.id.attendee_button)).perform(click( ));
			while (!isActivityDisplayed( )) {
				try {
					Thread.sleep(3000); // Sleep for 1 second
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		}
		onView(withId(R.id.button_menu)).perform(click( ));
		// Check if browse_events button is displayed
		onView(withId(R.id.browse_events)).check(matches(isDisplayed()));
		onView(withId(R.id.browse_events)).perform(click( ));
		// Check if the events are displayed
		onView(withId(R.id.events_recycler_view)).check(matches(isDisplayed()));
		// Wait for events RecyclerView to be populated and perform a click on the first item
		onView(withId(R.id.events_recycler_view)).check(matches(isDisplayed()))
				.perform(actionOnItemAtPosition(0, click()));
		onView(withId(R.id.sign_up_button)).check(matches(isDisplayed()));
		onView(withId(R.id.button_menu)).perform(click( ));
		onView(withId(R.id.announcement_menu)).perform(click( ));
		// confirms by checking the check announcement button is not there as an attendee
		// Confirm that the Check Announcement button is not visible or gone
		Espresso.onView(ViewMatchers.withId(R.id.send_button))
				.check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
		onView(withId(R.id.organizer_message_recyclerview)).check(matches(isDisplayed())); // TODO: if organizer sent sth, see the chat card
	}

	private boolean isButtonEnterDisplayed() {
		try {
			onView(withId(R.id.button_enter)).check(matches(isDisplayed()));
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	private boolean isActivityDisplayed() {
		try {
			onView(withId(R.id.activity_event_menu)).check(matches(isDisplayed()));
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}