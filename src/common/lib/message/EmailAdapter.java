package common.lib.message;

import com.example.layoutdesign.base.ActivityBase;

import android.content.Intent;
import android.net.Uri;

public class EmailAdapter extends ActivityBase {
	
	public void sendMail(String[] emailAddresses, String[] carbonCompies, String subject,
			String message)
	{
		Intent emailIntent = new Intent(Intent.ACTION_SEND);
		emailIntent.setData(Uri.parse("mailto:"));
		emailIntent.putExtra(Intent.EXTRA_EMAIL, emailAddresses);
		emailIntent.putExtra(Intent.EXTRA_CC, carbonCompies);
		emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
		emailIntent.putExtra(Intent.EXTRA_TEXT, message);
		emailIntent.setType("message/rfc822");
		startActivity(Intent.createChooser(emailIntent, "Email"));
	}
}
