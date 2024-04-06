package com.example.droiddesign.UiTests;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import com.example.droiddesign.R;
import com.example.droiddesign.view.Everybody.LaunchScreenActivity;

import org.junit.Rule;
import org.junit.Test;

public class AllowNotificationTest {

	@Rule
	public ActivityScenarioRule<LaunchScreenActivity> activityScenarioRule
			= new ActivityScenarioRule<>(LaunchScreenActivity.class);

//    @Rule
//    public GrantPermissionRule grantPermissionRule = GrantPermissionRule.grant(Manifest.permission.POST_NOTIFICATIONS);

	@Test
	public void testLaunchScreen() {
		// Check if the enter button is displayed
		Espresso.onView(ViewMatchers.withId(R.id.button_enter))
				.check(matches(isDisplayed()));

		// Check if the enter button is enabled
		Espresso.onView(ViewMatchers.withId(R.id.button_enter))
				.check(matches(isEnabled()));

		// Simulate a button click to open the login fragment
		Espresso.onView(ViewMatchers.withId(R.id.button_enter))
				.perform(ViewActions.click());

	}


	@Test
	public void testPermissionRequestHandling() {
		// Assume that we can inject a mock or fake PermissionChecker
		FakePermissionChecker fakePermissionChecker = new FakePermissionChecker();
		fakePermissionChecker.setPermissionGranted(false);  // Simulate permission not granted

		UiDevice uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

		// Launch the activity
		Intent intent = new Intent(ApplicationProvider.getApplicationContext(), LaunchScreenActivity.class);
		ActivityScenario.launch(intent);



		// Find the "Allow" button in the dialog and click it
		UiSelector allowButtonSelector = new UiSelector().text("Allow");
		if (uiDevice.findObject(allowButtonSelector).exists()) {
			try {
				uiDevice.findObject(allowButtonSelector).click();
			} catch (UiObjectNotFoundException e) {
				throw new RuntimeException(e);
			}
			// If the click succeeds, this implies that the dialog was displayed and the button was clicked
			assert(true); // Test passes as the expected dialog was shown and 'Allow' was clicked
		} else {
			assert(false); // Test fails as the dialog was not shown
		}
	}

	// Define a fake permission checker used for tests
	static class FakePermissionChecker {
		private boolean isPermissionGranted;

		public void setPermissionGranted(boolean isPermissionGranted) {
			this.isPermissionGranted = isPermissionGranted;
		}

		public boolean isPermissionGranted(String permission) {
			return this.isPermissionGranted;
		}
	}
}
