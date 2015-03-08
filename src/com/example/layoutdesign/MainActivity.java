package com.example.layoutdesign;

import com.example.layoutdesign.DataSource.PreferenceDataSource;
import com.example.layoutdesign.Model.HttpAction;
import com.example.layoutdesign.Model.HttpActionOptions;
import com.example.layoutdesign.activity.HomeActivity;
import com.example.layoutdesign.base.ActivityBase;
import com.example.layoutdesign.base.Base;
import com.example.layoutdesign.interfaceport.ISmackOperationResult;
import com.example.layoutdesign.interfaceport.SmackActionOptions;
import com.google.android.gcm.GCMRegistrar;

import common.lib.http.RegisterPhoneResponse;
import common.lib.http.AuthenticationResponse;
import common.lib.http.HttpRequestParams;
import common.lib.http.HttpResponseParams;
import common.lib.http.IOperationExecution;
import common.lib.http.JSONDataAdapter;
import common.lib.http.IdentifyPhoneResponse;
import common.lib.http.UnRegisterPhoneResponse;
import common.lib.message.SMSAdapter;
import common.lib.misc.SecurityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Base
	implements IOperationExecution, ISmackOperationResult
{
	private String phoneNumber;
	private String authenticationCode;
	
	private Spinner countrySpinner;

	AsyncTask<Void, Void, Void> gsmRegisterationTask;

	public String getAuthenticationCode() {
		return authenticationCode;
	}

	public void setAuthenticationCode(String authenticationCode) {
		this.authenticationCode = authenticationCode;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		if ( ApplicationMaster.isInternetAvailable() ) {

			if ( ApplicationMaster.hasValidChatServerConnection() ){
				// if it is already connected then probably it is a good idea to authenticate the phone number.
				this.authenticatePhone();
			}
			else {
				// connect with the server.
				ApplicationMaster.connectChatServer(this);
			}
		}
		else {
			this.showAlert("Error", R.string.InternetNotAvailable);
		}
	}

	/*
	 * The verify button is present in the authentication UI. 
	 * Thus we would need to catch the event and identify the phone.
	 * This action will send a code to the user.
	 */
	public void onVerifyClick(View v) {

		String number = ((EditText) this.findViewById(R.id.phoneNumber)).getText().toString();

		if(number.length() > 0 ){
			this.phoneNumber = number;
			this.identifyPhone(number);
		}
	}

	/*
	 * This is the final call where the verification code is checked against the stored ID 
	 * in the database.
	 */
	public void onVerifyAuthenticationCodeClick(View v){
		String verificationCode = ((EditText) this.findViewById(R.id.verificationCode)).getText().toString();
		if ( verificationCode.length() > 0 )
		{
			this.registerPhone(this.phoneNumber, verificationCode);
		}
	}

	/*
	 * This method is being called when the resent button is pressed.
	 */
	public void onResetVerifyClick(View v) {
		this.identifyPhone(this.phoneNumber);
	}


	/*
	 * This method is called When cancel button is pressed. 
	 * The system will take it back to the Main UI.
	 */
	public void onCancelVerification(View v){
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		this.startActivity(intent);
	}

	private void authenticatePhone() {

		AuthenticationResponse response = new AuthenticationResponse();
		response.operationExecution = this;

		JSONDataAdapter<AuthenticationResponse> jsonAdapter = new
				JSONDataAdapter<AuthenticationResponse>(this, response);

		HttpRequestParams params = new HttpRequestParams();

		params.httpAction = ApplicationMaster.AUTHENTICATEPHONE;
		params.formData = new Bundle();
		params.formData.putString("uniqueID", common.lib.misc.SecurityManager.getPseudoUniqueID());
		jsonAdapter.execute(params);
	}

	public void identifyPhone(String number){

		IdentifyPhoneResponse response = new IdentifyPhoneResponse();
		response.operationExecution = this;

		JSONDataAdapter<IdentifyPhoneResponse> jsonAdapter = new
				JSONDataAdapter<IdentifyPhoneResponse>(this, response);

		HttpRequestParams params = new HttpRequestParams();

		params.httpAction = ApplicationMaster.IDENTIFYPHONE;
		params.formData = new Bundle();
		params.formData.putString("uniqueID", common.lib.misc.SecurityManager.getPseudoUniqueID());
		params.formData.putString("phoneNumber", number);

		if ( this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY) ){
		}else
		{
			params.formData.putString("sendSMS", "true");
		}

		jsonAdapter.execute(params);

	}

	public void registerPhone(String phoneNumber, String authenticationCode){
		RegisterPhoneResponse response = new RegisterPhoneResponse();
		response.operationExecution = this;

		JSONDataAdapter<RegisterPhoneResponse> jsonAdapter = new
				JSONDataAdapter<RegisterPhoneResponse>(this, response);

		HttpRequestParams params = new HttpRequestParams();

		params.httpAction = ApplicationMaster.REGISTERPHONE;
		params.formData = new Bundle();
		params.formData.putString("uniqueID", common.lib.misc.SecurityManager.getPseudoUniqueID());
		params.formData.putString("authenticationCode",authenticationCode);
		params.formData.putString("phoneNumber", phoneNumber);
		jsonAdapter.execute(params);
	}

	public void unRegisterPhone(String phoneNumber){
		UnRegisterPhoneResponse response = new UnRegisterPhoneResponse();
		response.operationExecution = this;

		JSONDataAdapter<UnRegisterPhoneResponse> jsonAdapter = new
				JSONDataAdapter<UnRegisterPhoneResponse>(this, response);

		HttpRequestParams params = new HttpRequestParams();

		params.httpAction = ApplicationMaster.UNREGISTERPHONE;
		params.formData = new Bundle();
		params.formData.putString("uniqueID", common.lib.misc.SecurityManager.getPseudoUniqueID());
		params.formData.putString("phoneNumber", phoneNumber);
		jsonAdapter.execute(params);
	}

	public boolean setLoginPreferenceValue(){
		PreferenceDataSource.setValue(PreferenceDataSource.PHONENUMBER, this.phoneNumber);
		PreferenceDataSource.setValue(PreferenceDataSource.UNIQUEID, SecurityManager.getPseudoUniqueID());
		
		// the moment we change login details. 
		// we need to inform the same to the connection config in xmpp too.
		ApplicationMaster.getSmackManager().setConnectionConfig(ApplicationMaster.getSmackConfig());
		
		return true;
	}

	public void login(){
		ApplicationMaster.getSmackManager().SmackOperationExecuted = this;
		ApplicationMaster.getSmackManager().login();
	}

	public void registerForGCM()
	{
		// Make sure the device has the proper dependencies.
		GCMRegistrar.checkDevice(this);

		// Make sure the manifest permissions was properly set 
		GCMRegistrar.checkManifest(this);

		// Get GCM registration id
		final String regId = GCMRegistrar.getRegistrationId(this);

		// Check if regid already presents
		if (regId.equals("")) {

			// Register with GCM			
			GCMRegistrar.register(this, ApplicationMaster.GOOGLE_SENDER_ID);

		} else {

			// Device is already registered on GCM Server
			if (GCMRegistrar.isRegisteredOnServer(this)) {

				// Skips registration.				
				Toast.makeText(getApplicationContext(), "Already registered with GCM Server", Toast.LENGTH_LONG).show();

			} else {

				// Try to register again, but not in the UI thread.
				// It's also necessary to cancel the thread onDestroy(),
				// hence the use of AsyncTask instead of a raw thread.

				final Context context = this;
				gsmRegisterationTask = new AsyncTask<Void, Void, Void>() {

					@Override
					protected Void doInBackground(Void... params) {

						// Register on our server
						// On server creates a new user
						((ChatifyApplication) getApplicationContext()).register(context, phoneNumber, regId);

						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						gsmRegisterationTask = null;
					}

				};

				// execute AsyncTask
				gsmRegisterationTask.execute(null, null, null);
			}
		}
	}

	private void moveHomeActivity()
	{
		Intent intent = new Intent(this, HomeActivity.class);
		this.startActivity(intent);      
		this.finish();
	}

	@Override
	public void onExecuted(HttpAction action, HttpResponseParams params) {

		int actionID = action.Key.getValue() ;

		if ( actionID == HttpActionOptions.AUTHENTICATEPHONE.getValue() )
		{
			if ( ((AuthenticationResponse)params).authenticated ){
				// Since it has been authenticated then we must save 
				// information in the share preference for future use.
				this.phoneNumber = ((AuthenticationResponse)params).phoneNumber;

				if ( this.setLoginPreferenceValue() )  {
					this.registerForGCM();
					this.login();
				}
				else
				{
					// If the validation has been failed that means the login stored into our 
					// database is wrong and is not matching with smack. Hence we will
					// unregister from our database and start the process of authentication.
					this.unRegisterPhone(this.phoneNumber);
				}
			}
			else
			{
				// authentication process activity.
				this.setContentView(R.layout.activity_authenticate);

				countrySpinner = (Spinner) this.findViewById(R.id.countryNameSpinner);

				countrySpinner.setOnItemSelectedListener(
						new AdapterView.OnItemSelectedListener() {

							@Override
							public void onItemSelected(AdapterView<?> arg0, View arg1,
									int arg2, long arg3) {

								String countryCode = 
										getResources().getStringArray(R.array.countrycode_array)[countrySpinner.getSelectedItemPosition()];

								((TextView) findViewById(R.id.phoneNumberPrefix)).setText(countryCode);
							}

							@Override
							public void onNothingSelected(AdapterView<?> arg0) {
								// TODO Auto-generated method stub

							}
							//add some code here
						});

				// get the locale of the user and set the right country in the country spinner.
				this.setCountryByLocale();

			}
		}
		else if ( actionID == HttpActionOptions.IDENTIFYPHONE.getValue()){
			if ( ((IdentifyPhoneResponse)params).hasidentified){
				this.setAuthenticationCode(((IdentifyPhoneResponse)params).authenticationCode);

				if (((IdentifyPhoneResponse)params).hassentSMS ){
					// that means the SMS has been sent by the Server. 
					// we don't need to send it through from client side.
				}
				else
				{
					if(this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY)){
						SMSAdapter sms = new SMSAdapter(this, null);
						sms.sendSMS(this.phoneNumber, "Welcome to Chatify Verification Process. Your Authentication code is: " + this.getAuthenticationCode(), false, false);
					}
					else
					{
						ActivityBase base = new ActivityBase();
						base.showAlert("We are unable to process this request. Please try again.");
					}
				}

				this.setContentView(R.layout.activity_verifysms);
				((TextView)this.findViewById(R.id.verifyCodeMessage)).setText(String.format(getResources().getString(R.string.VerifyCode), this.phoneNumber));
			}
			else{
				ActivityBase base = new ActivityBase();
				base.showAlert("We are unable to process this request. Please try again.");
			}

		} else if ( actionID == HttpActionOptions.REGISTERPHONE.getValue()){
			RegisterPhoneResponse response = (RegisterPhoneResponse) params;
			if ( response.hasregistered){
				if (response.uniqueID.equals(SecurityManager.getPseudoUniqueID().toString())){
					if ( ApplicationMaster.getSmackManager().createUser(this.phoneNumber, SecurityManager.getPseudoUniqueID()) ) {
						if ( this.setLoginPreferenceValue() ) 
						{
							this.login();
						}
						else
						{
							// If the validation has been failed that means the login stored into our 
							// database is wrong and is not matching with smack. Hence we will
							// unregister from our database and start the process of authentication.
							this.unRegisterPhone(this.phoneNumber);
						}
					}
					else
					{
						// If the create user functionality has not been successful then  
						// we must unregister from our database and start the process of 
						// authentication.
						this.unRegisterPhone(this.phoneNumber);
					}
				}
			}
		} else if ( actionID == HttpActionOptions.UNREGISTERPHONE.getValue()){
			UnRegisterPhoneResponse response = (UnRegisterPhoneResponse) params;
			
			if ( response.hasunregistered) {
				this.authenticatePhone();
			}
			else
			{
				this.openApplicationMessageActivity("Unable to unregister your phone.");
			}
		}
	}

 
	@Override
	public void onSmackExecuted(SmackActionOptions actionType, Object result) {

		int actionID = actionType.getValue() ;


		if ( actionID == SmackActionOptions.CONNECT.getValue() )
		{
			// if the acton was to connect with the smack then 
			// it is the time to authenticate the phone
			
			if ( ((Boolean) result ) ){	
				this.authenticatePhone();
			}
			else {
				this.showAlert("Error", R.string.ServerNotAvailable);
			}
		}else if ( actionID == SmackActionOptions.LOGIN.getValue()){
			
			// if the action was to login then look no further
			// call home activity; only lucky people can go!
			if ( ((Boolean) result ) ){
				this.moveHomeActivity();
			}
			else {
				// That means the device id, and phone number
				// is not registered at XMPP.
				this.unRegisterPhone(this.phoneNumber);
			}
		}
	}


	private void setCountryByLocale()
	{
		// @todo need to find the way to get current user country.
		String locale = "Australia";
		String[] androidStrings = getResources().getStringArray(R.array.countries_array);

		int index = 0;
		for (String s : androidStrings) {
			int i = s.toLowerCase().indexOf(locale.toLowerCase());
			if (i >= 0) {
				break;
			}

			index++;
		}

		if ( index <= androidStrings.length-1){
			countrySpinner.setSelection(index);
		}
	}

}
