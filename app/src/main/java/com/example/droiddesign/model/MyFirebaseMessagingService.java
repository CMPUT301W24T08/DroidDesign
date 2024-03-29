package com.example.droiddesign.model;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.example.droiddesign.R;
import com.example.droiddesign.view.LaunchScreenActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
	private static final String TAG = "FCM Service";

	@Override
	public void onMessageReceived(RemoteMessage remoteMessage) {
		// Not getting messages here? See why this may be: https://goo.gl/39bRNJ
//		Log.d(TAG, "From: " + remoteMessage.getFrom());

		// Check if message contains a notification payload.
//		if (remoteMessage.getNotification() != null) {
//			Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
//		}

		// Also if you intend on generating your own notifications as a result of a received FCM
		// message, here is where that should be initiated. See sendNotification method below.
		sendNotification(remoteMessage.getFrom(), remoteMessage.getNotification().getBody());
		sendNotification(remoteMessage.getNotification().getBody());
	}

	private void sendNotification(String from, String body) {
		new Handler(Looper.getMainLooper()).post(new Runnable(){
			@Override
			public void run(){
				Toast.makeText(MyFirebaseMessagingService.this.getApplicationContext(),from+" -> "+body, Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void sendNotification(String messageBody) {
		Intent intent = new Intent(this, LaunchScreenActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
				PendingIntent.FLAG_IMMUTABLE);

		String channelId = "fcm_default_channel";
		Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		NotificationCompat.Builder notificationBuilder =
				new NotificationCompat.Builder(this, channelId)
						.setSmallIcon(R.mipmap.conclavelogo_round)
						.setContentTitle("New Event Message")
						.setContentText(messageBody)
						.setAutoCancel(true)
						.setSound(defaultSoundUri)
						.setContentIntent(pendingIntent);

		NotificationManager notificationManager =
				(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		// Since android Oreo notification channel is needed.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			NotificationChannel channel = new NotificationChannel(channelId,
					"Channel human readable title",
					NotificationManager.IMPORTANCE_DEFAULT);
			notificationManager.createNotificationChannel(channel);
		}

		notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
	}


}
