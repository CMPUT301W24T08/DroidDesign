package com.example.droiddesign.UnitTests;


//package com.example.droiddesign;

//package com.example.droiddesign;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import androidx.core.content.ContextCompat;
import com.example.droiddesign.controller.LocationController;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.tasks.Task;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GeoLocationTest {

    @Mock
    private Activity mockActivity;
    @Mock
    private FusedLocationProviderClient mockFusedLocationProviderClient;
    @Mock
    private Task<Void> mockTask;

    private LocationController locationController;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        locationController = new LocationController(mockActivity);
        locationController.fusedLocationClient = mockFusedLocationProviderClient; // Directly inject mock FusedLocationProviderClient

        // Correctly mock the requestLocationUpdates method to return the mock Task
        when(mockFusedLocationProviderClient.requestLocationUpdates(any(), any(), any())).thenReturn(mockTask);

        // Resolve the ambiguity by specifying the argument type explicitly for removeLocationUpdates
        when(mockFusedLocationProviderClient.removeLocationUpdates(any(LocationCallback.class))).thenReturn(mockTask);
    }

    @Test
    public void startLocationUpdates_PermissionsGranted() {
        try (MockedStatic<ContextCompat> mockedStatic = Mockito.mockStatic(ContextCompat.class)) {
            mockedStatic.when(() -> ContextCompat.checkSelfPermission(eq(mockActivity), eq(Manifest.permission.ACCESS_FINE_LOCATION)))
                    .thenReturn(PackageManager.PERMISSION_GRANTED);

            locationController.startLocationUpdates();
            verify(mockFusedLocationProviderClient).requestLocationUpdates(any(), any(LocationCallback.class), any());
        }
    }

    // Additional test methods can be implemented here to cover more scenarios
}
