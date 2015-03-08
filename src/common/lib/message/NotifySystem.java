package common.lib.message;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;

public class NotifySystem {

	public static void Notification(Activity currentActivity, String title, String subject, String longText){

		NotifySystem.Notification(currentActivity, title, subject, longText,0);
	}

	public static void Notification(Activity currentActivity, String title, String subject, String longText, long uniqueID)
	{
		NotifySystem.Notification(currentActivity, null, title, subject, longText, uniqueID);
	}

	public static void Notification(Activity currentActivity, String targetActivityName, 
			String title, String subject, String longText, long uniqueID)
	{
		android.app.Notification n = null;
		if ( targetActivityName == null )
		{
			n = new android.app.Notification.Builder(currentActivity)
			.setContentTitle(title)
			.setContentText(subject)
			.setStyle(new android.app.Notification.BigTextStyle().bigText(longText))
			.setSmallIcon(android.R.drawable.ic_dialog_info)
			.setAutoCancel(true).build();
		}

		else
		{

			try {
				Intent intent= new Intent(currentActivity, Class.forName(targetActivityName));

				PendingIntent pIntent = PendingIntent.getActivity(currentActivity, 0, intent, 0);
				n = new android.app.Notification.Builder(currentActivity)
				.setContentTitle(title)
				.setContentText(subject)
				.setStyle(new android.app.Notification.BigTextStyle().bigText(longText))
				.setSmallIcon(android.R.drawable.ic_dialog_info)
				.setAutoCancel(true)
				.setContentIntent(pIntent).build();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}


		if ( n != null ){
			NotificationManager notificationManager = (NotificationManager) 
					currentActivity.getSystemService(currentActivity.NOTIFICATION_SERVICE);

			notificationManager.notify(0, n);
		}
	}
}
