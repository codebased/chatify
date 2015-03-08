package com.example.layoutdesign;
 
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.layoutdesign.DataSource.PreferenceDataSource;
import com.example.layoutdesign.Model.HttpAction;
import com.example.layoutdesign.Model.HttpActionOptions;
import com.example.layoutdesign.Model.SmackConfiguration;
 
import com.example.layoutdesign.interfaceport.SmackManager;
import com.example.layoutdesign.interfaceport.ISmackOperationResult;
 

public class ApplicationMaster  {
	
	public static HttpAction AUTHENTICATEPHONE;
	public static HttpAction REGISTERPHONE;
	public static HttpAction IDENTIFYPHONE;
	public static HttpAction ROASTER;
	public static HttpAction PROFILEIMAGE;
	public static HttpAction UNREGISTERPHONE;
	public static final String SERVER = "http://www.doeaccian.com/interfaceport/";
	public static final String AUTH_SERVER = SERVER + "auth.php";
	public static final String GCM_SERVER =  SERVER + "register.php";
	public static final String GOOGLE_SENDER_ID = "275813158626";  // Place here your Google project id
	private static SmackManager smacky;
	
	// Log TAG.
    public static final String TAG = "Chatify Android";
    
    // A static variable value that can be used to catch the post connect to the GSM system.
    public static final String DISPLAY_MESSAGE_ACTION = "com.example.layoutdesign.gcm.DISPLAY_MESSAGE";
    public static final String EXTRA_MESSAGE = "message";
    
	static
	{
		HttpAction action1 = new HttpAction();
		action1.Key =  HttpActionOptions.AUTHENTICATEPHONE;
		action1.Url = AUTH_SERVER + "?action=AUTHENTICATEPHONE";

		HttpAction action2 = new HttpAction();
		action2.Key =  HttpActionOptions.REGISTERPHONE;
		action2.Url = AUTH_SERVER + "?action=REGISTERPHONE";

		HttpAction action3 = new HttpAction();
		action3.Key =  HttpActionOptions.IDENTIFYPHONE;
		action3.Url = AUTH_SERVER + "?action=IDENTIFYPHONE";

		HttpAction action4= new HttpAction();
		action4.Key =  HttpActionOptions.ROASTER;
		action4.Url = AUTH_SERVER + "?action=ROASTER";

		HttpAction action5= new HttpAction();
		action5.Key =  HttpActionOptions.PROFILEIMAGE;
		action5.Url = AUTH_SERVER + "?action=PROFILEIMAGE";

		HttpAction action6= new HttpAction();
		action6.Key =  HttpActionOptions.UNREGISTERPHONE;
		action6.Url = AUTH_SERVER + "?action=UNREGISTERPHONE";


		ApplicationMaster.AUTHENTICATEPHONE = action1;
		ApplicationMaster.REGISTERPHONE = action2;
		ApplicationMaster.IDENTIFYPHONE= action3;
		ApplicationMaster.ROASTER= action4;
		ApplicationMaster.PROFILEIMAGE = action5;
		ApplicationMaster.UNREGISTERPHONE = action6;
	}

	public static SmackConfiguration getSmackConfig(){
 
		SmackConfiguration config = new SmackConfiguration();
		config.hostName = ChatifyApplication.getAppContext().getString(R.string.Host);
		config.portNumber = Integer.parseInt(ChatifyApplication.getAppContext().getString(R.string.ClientPort));
		config.serviceName = ChatifyApplication.getAppContext().getString(R.string.Service);
		config.userName = PreferenceDataSource.getValue(PreferenceDataSource.PHONENUMBER);
		config.password = PreferenceDataSource.getValue(PreferenceDataSource.UNIQUEID);
		return config;
	}

	public static SmackManager getSmackManager() {
		
		if ( ApplicationMaster.smacky == null ) { 
			ApplicationMaster.smacky = new SmackManager(ApplicationMaster.getSmackConfig());
		}
		 
		return ApplicationMaster.smacky;
	}

	public static void setSmackManager(SmackManager smacky) {
		ApplicationMaster.smacky = smacky;
	}

	public static boolean hasValidChatServerConnection(){

		boolean returnValue = false;

		if ( ApplicationMaster.smacky == null ) { 
			returnValue = false;
		} else if ( ApplicationMaster.getSmackManager().xmppConnection != null ) { 
			if ( ApplicationMaster.getSmackManager().xmppConnection.isConnected())
			{
				returnValue = true;
			}
		}

		return returnValue;
	}

	public static boolean hasValidChatServerLogin(){
		boolean returnValue = false;

		if ( ApplicationMaster.smacky == null ) { 
			returnValue = false;
		} else if ( ApplicationMaster.getSmackManager().xmppConnection != null ) { 
			if ( ApplicationMaster.getSmackManager().xmppConnection.isConnected()
					&& ApplicationMaster.getSmackManager().xmppConnection.isAuthenticated()
					)
			{
				returnValue = true;
			}
		}

		return returnValue;
	}

	public static void connectChatServer(ISmackOperationResult resultCallback)
	{
		if ( ApplicationMaster.hasValidChatServerConnection()) {
			// If it is already connected then there is no need to reconnect.
		}
		else {
			
			ApplicationMaster.setSmackManager(new SmackManager(ApplicationMaster.getSmackConfig()));
			ApplicationMaster.getSmackManager().SmackOperationExecuted = resultCallback;
			ApplicationMaster.getSmackManager().connect();
		}
	}

	// Checking for all possible internet providers
	public static boolean isInternetAvailable(){
			
			ConnectivityManager connectivity = 
					(ConnectivityManager) ChatifyApplication.getAppContext().getSystemService(
							Context.CONNECTIVITY_SERVICE);
			
			if (connectivity != null)
			{
				NetworkInfo[] info = connectivity.getAllNetworkInfo();
				if (info != null)
					for (int i = 0; i < info.length; i++)
						if (info[i].getState() == NetworkInfo.State.CONNECTED)
						{
							return true;
						}

			}
			
			return false;
	}
}