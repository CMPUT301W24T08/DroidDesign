package com.example.droiddesign.UnitTests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.droiddesign.model.SharedPreferenceHelper;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class SharedPreferenceHelperTest {

    private static final String TEST_USER_ID = "testUserId";
    private static final String TEST_ROLE = "testRole";
    private static final String TEST_EMAIL = "testEmail@example.com";

    @Mock
    Context mockContext;
    @Mock
    SharedPreferences mockSharedPreferences;
    @Mock
    SharedPreferences.Editor mockEditor;

    private SharedPreferenceHelper sharedPreferenceHelper;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        when(mockContext.getSharedPreferences(anyString(), anyInt())).thenReturn(mockSharedPreferences);
        when(mockSharedPreferences.edit()).thenReturn(mockEditor);
        when(mockEditor.putString(anyString(), anyString())).thenReturn(mockEditor);

        sharedPreferenceHelper = new SharedPreferenceHelper(mockContext);
    }

    @Test
    public void saveUserProfile_savesDataCorrectly() {
        sharedPreferenceHelper.saveUserProfile(TEST_USER_ID, TEST_ROLE, TEST_EMAIL);

        verify(mockEditor).putString(sharedPreferenceHelper.getUserId(), TEST_USER_ID);
        verify(mockEditor).putString(sharedPreferenceHelper.getRole(), TEST_ROLE);
        verify(mockEditor).apply();
    }

    @Test
    public void getUserId_returnsCorrectUserId() {
        when(mockSharedPreferences.getString(sharedPreferenceHelper.getUserId(), null)).thenReturn(TEST_USER_ID);

        assertEquals(TEST_USER_ID, sharedPreferenceHelper.getUserId());
    }

    @Test
    public void getRole_returnsCorrectRole() {
        when(mockSharedPreferences.getString(sharedPreferenceHelper.getRole(), null)).thenReturn(TEST_ROLE);

        assertEquals(TEST_ROLE, sharedPreferenceHelper.getRole());
    }

    @Test
    public void isFirstTimeUser_returnsTrueIfUserIdIsNull() {
        when(mockSharedPreferences.getString(sharedPreferenceHelper.getUserId(), null)).thenReturn(null);

        assertTrue(sharedPreferenceHelper.isFirstTimeUser());
    }

    @Test
    public void isFirstTimeUser_returnsFalseIfUserIdIsNotNull() {
        sharedPreferenceHelper.saveUserProfile(null,TEST_ROLE);

        assertTrue(sharedPreferenceHelper.isFirstTimeUser());
        sharedPreferenceHelper.saveUserProfile(TEST_USER_ID,TEST_ROLE);

        assertTrue(sharedPreferenceHelper.isFirstTimeUser());
    }

    @Test
    public void clearPreferences_clearsAllData() {
        sharedPreferenceHelper.clearPreferences();

        verify(mockEditor).clear();
        verify(mockEditor).apply();
    }
}

