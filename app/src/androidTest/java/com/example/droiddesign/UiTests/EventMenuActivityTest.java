package com.example.droiddesign.UiTests;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import androidx.test.core.app.ActivityScenario;

import com.example.droiddesign.model.SharedPreferenceHelper;
import com.example.droiddesign.view.Everybody.EventMenuActivity;
import com.example.droiddesign.view.Everybody.FirebaseServiceUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({FirebaseAuth.class, FirebaseFirestore.class, FirebaseServiceUtils.class})
public class EventMenuActivityTest {

	@Mock
	private FirebaseFirestore mockFirestore;
	@Mock
	private FirebaseAuth mockFirebaseAuth;
	@Mock
	private FirebaseUser mockFirebaseUser;
	@Mock
	private SharedPreferenceHelper mockPrefsHelper;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		// Mock static methods of FirebaseAuth and FirebaseFirestore
		mockStatic(FirebaseAuth.class);
		mockStatic(FirebaseFirestore.class);

		when(FirebaseAuth.getInstance()).thenReturn(mockFirebaseAuth);
		when(FirebaseFirestore.getInstance()).thenReturn(mockFirestore);

		when(mockFirebaseAuth.getCurrentUser()).thenReturn(mockFirebaseUser);
		when(mockFirebaseUser.getUid()).thenReturn("testUserId");

		when(mockPrefsHelper.getUserId()).thenReturn("testUserId");
		when(mockPrefsHelper.getRole()).thenReturn("Organizer");

		// Initialize FirebaseServiceUtils with mocks (if FirebaseServiceUtils uses static fields/methods, prepare it with PowerMockito)
		FirebaseServiceUtils.initialize(mockFirestore, mockFirebaseAuth);
	}

	@Test
	public void testFetchUserRole() {
		ActivityScenario.launch(EventMenuActivity.class).onActivity(activity -> {
			// Set the SharedPreferences helper to the mock
			activity.prefsHelper = mockPrefsHelper;

			// Call the method that triggers Firebase usage
			activity.fetchUserRole();

			// Verify that Firestore was accessed as expected
			verify(mockFirestore).collection("Users").document("testUserId").get();
		});
	}

//		onView(withId(R.id.button_menu)).perform(click( ));

//	@Before
//	public void setup() {
//		MockitoAnnotations.initMocks(this);
//
//		// Mock FirebaseAuth to return a mock user
//		when(mockFirebaseAuth.getCurrentUser()).thenReturn(mockFirebaseUser);
//		when(mockFirebaseUser.getUid()).thenReturn("testUserId");
//
//		// Mock FirebaseFirestore
//		Mockito.mockStatic(FirebaseFirestore.class);
//		when(FirebaseFirestore.getInstance()).thenReturn(mockFirestore);
//		when(mockFirestore.collection(anyString()).document(anyString())).thenReturn(mockDocumentReference);
//		when(mockDocumentReference.get()).thenReturn(mockDocumentSnapshotTask);
//		when(mockDocumentSnapshotTask.isSuccessful()).thenReturn(true);
//		when(mockDocumentSnapshotTask.getResult()).thenReturn(mockDocumentSnapshot);
//		when(mockDocumentSnapshot.exists()).thenReturn(true);
//		when(mockDocumentSnapshot.getString("role")).thenReturn("Organizer");
//		when(mockDocumentSnapshot.getString("email")).thenReturn("organizer@example.com");
//
//		// Launch activity after setting up mocks
//		activityRule.launchActivity(null);
//	}

//	@Test
//	public void testEventRecyclerViewIsDisplayed() {
//		onView(withId(R.id.events_recycler_view)).check(matches(isDisplayed()));
//	}
//
//	@Test
//	public void testNavigationMenuIsDisplayed() {
//		onView(withId(R.id.navigation_menu)).check(matches(isDisplayed()));
//	}

	// More tests can be added to verify other UI components and interactions

//	@Test
//	public void testEventRecyclerViewIsDisplayed() {
//		Espresso.onView(withId(R.id.events_recycler_view))
//				.check(ViewAssertions.matches(isDisplayed()));
//	}
//
//	@Test
//	public void testNavigationMenuIsDisplayed() {
//		Espresso.onView(withId(R.id.navigation_menu))
//				.check(ViewAssertions.matches(isDisplayed()));
//	}
//
//	@Test
//	public void testMenuButtonIsDisplayed() {
//		Espresso.onView(withId(R.id.button_menu))
//				.check(ViewAssertions.matches(isDisplayed()));
//	}
//
//	@Test
//	public void testQuickScanFabIsDisplayed() {
//		Espresso.onView(withId(R.id.fab_quick_scan))
//				.check(ViewAssertions.matches(isDisplayed()));
//	}
//
//	@Test
//	public void testAddEventFabIsDisplayed() {
//		Espresso.onView(withId(R.id.fab_add_event))
//				.check(ViewAssertions.matches(isDisplayed()));
//	}
//
//	@Test
//	public void testAdminCardIsDisplayed() {
//		// This test assumes that the admin card should be visible.
//		// If the visibility of the admin card depends on the user's role,
//		// you might need to mock the role or adjust the test accordingly.
//		Espresso.onView(withId(R.id.admin_card))
//				.check(ViewAssertions.matches(isDisplayed()));
//	}
}
