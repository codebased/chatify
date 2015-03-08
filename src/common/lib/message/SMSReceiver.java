package common.lib.message;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;


public class SMSReceiver extends BroadcastReceiver{

    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

	@Override
	public void onReceive(Context context, Intent intent) {

		if ( intent.getAction().equals(SMS_RECEIVED))
		{
			Bundle bundle = intent.getExtras();
			SmsMessage[] messages = null;
			StringBuilder sms = new StringBuilder();

			if ( bundle != null ){
				Object[] pdus = (Object[]) bundle.get("pdus");
				messages = new SmsMessage[pdus.length];
				for(int idx=0; idx<messages.length; idx++){
					messages[idx] = SmsMessage.createFromPdu( (byte[]) pdus[idx] );
					if ( idx == 0 ){
						sms.append(messages[idx].getOriginatingAddress());
						sms.append(":");
					}

					sms.append(messages[idx].getMessageBody().toString());
				}
			}



			Intent broadcastIntent = new Intent();
			broadcastIntent.setAction("SMS_RECEIVED_ACTION");
			broadcastIntent.putExtra("sms", sms.toString());
			context.sendBroadcast(broadcastIntent);
			
		}
	}
	
	

	public void abortBroadCast(){
		this.abortBroadcast();
	}
}