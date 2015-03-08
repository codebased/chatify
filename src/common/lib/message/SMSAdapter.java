package common.lib.message;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsManager;

public class SMSAdapter  {
	
	
	String SENT = "SMS_SENT";
	String DELIVERED = "SMS_DELIVERED";
	PendingIntent sentPI, deliveredPI;
	BroadcastReceiver smsSentReceiver, smsDeliveredReceiver;
	Activity viewBase = null;
	ISMSListener listener = null;
	IntentFilter intentFilter;
	
	public SMSAdapter(Activity v, ISMSListener listener){
		this.viewBase = v;
		this.listener = listener;
	}
	
	private BroadcastReceiver intentReceiver = new BroadcastReceiver() {
	
		@Override
		public void onReceive(Context context, Intent intent){
			String str = intent.getExtras().getString("sms");
		}
	};
	
	public void sendSMS(String phoneNumber, String message, boolean trackDelivery, boolean listenDelivery)
	{
		if ( trackDelivery ){
			this.sentPI = PendingIntent.getBroadcast(this.viewBase, 0, new Intent(SENT),0);
			this.deliveredPI = PendingIntent.getBroadcast(this.viewBase, 0, new Intent(this.DELIVERED), 0);
		}
		
		if ( listenDelivery){
			intentFilter = new IntentFilter();
			intentFilter.addAction("SMS_RECEIVED_ACTION");
			this.viewBase.registerReceiver(intentReceiver, intentFilter);
		}
		
		SmsManager sms = SmsManager.getDefault();
		sms.sendTextMessage(phoneNumber, null, message, null, null);
	}
	
	public void sendSMSDefault(String[] phoneNumbers, String message){
		
		Intent i = new Intent(android.content.Intent.ACTION_VIEW);
		StringBuilder builder = new StringBuilder();
		for(String s: phoneNumbers){
			builder.append(s + ";");
		}
		
		i.putExtra("address", builder.toString());
		i.putExtra("sms_body", message);
		i.setType("vnd.android-dir/mms-sms");
		this.viewBase.startActivity(i);
	}
	
	public void trackSent()
	{
		this.smsSentReceiver = new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context context, Intent intent) {
				if ( listener != null ) {
					listener.onSent(getResultCode());
				}
			}
		};
		
		this.viewBase.registerReceiver(this.smsSentReceiver, new IntentFilter(SENT));
	}
	
	
	public void untrackSent(){
		
		if ( this.intentReceiver != null ){
		this.viewBase.unregisterReceiver(this.intentReceiver);
		}
		
		if ( this.smsSentReceiver != null ){
		this.viewBase.unregisterReceiver(this.smsSentReceiver);
		}
	}
	
	public void trackDelivered (){
		this.smsDeliveredReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				if ( listener != null ){
					listener.onDelivered(getResultCode());
				}
			}
		};
		
		this.viewBase.registerReceiver(this.smsDeliveredReceiver, new IntentFilter(DELIVERED));
	}
	
	public void untrackDelivered(){
		if ( this.intentReceiver != null) {
			this.viewBase.unregisterReceiver(intentReceiver);
		}
		
		if (this.smsDeliveredReceiver != null ){
			this.viewBase.unregisterReceiver(this.smsDeliveredReceiver);
		}
	}
}