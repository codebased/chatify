package com.example.layoutdesign.interfaceport;

import java.util.Collection;
import java.util.List;

import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.packet.VCard;
import org.jivesoftware.smackx.provider.VCardProvider;
import org.jivesoftware.smackx.search.UserSearchManager;

import com.example.layoutdesign.ApplicationMaster;
import com.example.layoutdesign.ChatifyApplication;
import com.example.layoutdesign.DataSource.DefaultContentProvider;
import com.example.layoutdesign.DataSource.PreferenceDataSource;
import com.example.layoutdesign.DataSource.UserDataSource;
import com.example.layoutdesign.Model.ContactNumber;
import com.example.layoutdesign.Model.SmackConfiguration; 
import com.example.layoutdesign.Model.UserDetail; 
import android.content.Context;
 
import android.os.AsyncTask;
import android.util.Log;

public class SmackManager {

	public XMPPConnection xmppConnection;
	public ConnectAsyncTask connectAsync; 
	public LoginAsyncTask loginAsync;
	private SmackConfiguration connectionConfig = null;
	public  ISmackOperationResult SmackOperationExecuted;

	public SmackManager(SmackConfiguration config )
	{
		this(config, false);
	}

	public SmackManager(SmackConfiguration config, boolean openConnection){
		this.connectionConfig = config;
		if ( openConnection) { 
			new ConnectAsyncTask().execute(this);
		}
	}
	
	public void setConnectionConfig(SmackConfiguration config){
		this.connectionConfig = config;
	}

	// Connect with the XMPP server.
	public void connect(){
		connectAsync = new ConnectAsyncTask();
		connectAsync.execute(this);
	}

	// check if the connection is happening.
	public boolean isConnecting() {
		if ( connectAsync.getStatus() == AsyncTask.Status.FINISHED ) 
		{
			return false;
		}

		return true;
	}

	public boolean isActiveSession()
	{
		return this.xmppConnection.isConnected() && this.xmppConnection.isAuthenticated();
	}

	// Login with the XMPP server through Async task.
	public void login(){
		SmackConfiguration config= this.connectionConfig;
		if ( this.xmppConnection.isConnected() && xmppConnection.isAuthenticated() ) {
		}
		else {
			loginAsync = new LoginAsyncTask();
			loginAsync.execute(this);
		}
	}

	// Save the VCard of the logged in user.
	public void saveVCard(VCard vcard)
	{
		if ( this.isActiveSession()){

			final class SaveVCardAsyncTask extends AsyncTask<VCard, Void, Boolean> {

				@Override
				protected Boolean doInBackground(VCard... params) {

					VCard vcard = params[0];
					try {
						vcard.save(ApplicationMaster.getSmackManager().xmppConnection);
					} catch (XMPPException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					return null; 
				}

			}

			new SaveVCardAsyncTask ().execute(vcard);
		}
	}

	public  static VCard getPersonalVCard(){

		try {

			VCard vcard=new VCard();
			ProviderManager.getInstance().addIQProvider("vCard", "vcard-temp", new VCardProvider());
			org.jivesoftware.smack.SmackConfiguration.setPacketReplyTimeout(10000);
			vcard.load(ApplicationMaster.getSmackManager().xmppConnection);
			return vcard;
		} catch (XMPPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;

		}
	}

	public  static VCard getUserVCard(String jid){

		try {
			ProviderManager.getInstance().addIQProvider("vCard", "vcard-temp", new VCardProvider());
			org.jivesoftware.smack.SmackConfiguration.setPacketReplyTimeout(10000);
			VCard vcard=new VCard();
			vcard.load(ApplicationMaster.getSmackManager().xmppConnection,jid);

			return vcard;
		} catch (XMPPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new VCard();

		}
	}

	public void sendMessage(String to, Message.Type messageType, String message  )
	{
		Message msg = new Message(to, messageType);
		msg.setBody(message);
		this.xmppConnection.sendPacket(msg);
	}

	public void changeStatus(Presence.Type status)
	{
		Presence presence = new Presence(status);
		xmppConnection.sendPacket(presence);
	}

	public boolean createUser(String userName, String password)
	{
		AccountManager manager = xmppConnection.getAccountManager();

		if (manager.supportsAccountCreation ())
		{
			try {
				manager.createAccount (userName, password);
			} catch (XMPPException e) {
				// todo - we need to check if the user already exist then we just need to reset the password.
				// we need to find the way!!!
				// at present I am returning - true straight.
				if ( e != null ){
					Log.d("Chatify", e.getStackTrace().toString());
				}
				return true;
			}

			return true;
		}

		return true;
	}

	public boolean isUserExist(String userName){ 

		UserSearchManager search = new UserSearchManager(xmppConnection);

		try
		{

			Form searchForm = search.getSearchForm("search."+xmppConnection.getServiceName());

			Form answerForm = searchForm.createAnswerForm();  
			answerForm.setAnswer("Username", true);  
			answerForm.setAnswer("search", userName);  
			org.jivesoftware.smackx.ReportedData data = search.getSearchResults(answerForm,"search."+xmppConnection.getServiceName());  

			if(data.getRows() != null)
			{
				return true;
			}
		}
		catch(Exception ex)
		{}

		return false;

	}

	public Collection<RosterEntry> getRosters()
	{
		Roster roster = xmppConnection.getRoster();
		Collection<RosterEntry> entries = roster.getEntries();

		return entries;
	}

	public static void setRosterName(String userID,String value){
		ApplicationMaster.getSmackManager().xmppConnection.getRoster().getEntry(userID).setName(value);
	}

	public RosterEntry getRoster(String userID){
		return xmppConnection.getRoster().getEntry(userID);
	}

	public boolean createRoster(String userName, String nickName){
		try {
			Roster roster = xmppConnection.getRoster();
			roster.createEntry(userName, nickName, null);
		} catch (XMPPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public Presence getPresence(String user) {
		Roster roster = xmppConnection.getRoster();
		return roster.getPresence(user);
	}

	public void SynchroniseContacts(){
		new SyncConnectionBackground().execute();
	}

	final private class ConnectAsyncTask extends AsyncTask<SmackManager, Void, Boolean> {

		SmackManager smackManager = null;

		@Override
		protected Boolean doInBackground(SmackManager... args) { 

			smackManager = args[0];

			SmackConfiguration config= smackManager.connectionConfig;

			ConnectionConfiguration connectioncnfg =
					new ConnectionConfiguration(config.hostName, 
							config.portNumber,
							config.serviceName);
			connectioncnfg.setCompressionEnabled(false);
			connectioncnfg.setSASLAuthenticationEnabled(false);
			smackManager.xmppConnection = new XMPPConnection(connectioncnfg);

			try {
				smackManager.xmppConnection.connect();
				Log.i("XMPPClient", "[SettingsDialog] Connected to " + smackManager.xmppConnection.getHost());

				return true;
			} catch (XMPPException ex) {
				Log.e("XMPPClient", "[SettingsDialog] Failed to connect to " + smackManager.xmppConnection.getHost());
				Log.e("XMPPClient", ex.toString());
			}


			return false;
		}

		@Override
		protected void onProgressUpdate(Void... progress) {

		}

		@Override
		protected void onPostExecute(Boolean result) {
			if ( SmackOperationExecuted != null ) {
				SmackOperationExecuted.onSmackExecuted(SmackActionOptions.CONNECT, result);
			}
		}
	}

	final private class LoginAsyncTask extends AsyncTask<SmackManager, Void, Boolean> {

		SmackManager smackManager = null;

		@Override
		protected Boolean doInBackground(SmackManager... args) { 

			smackManager = args[0];


			if ( smackManager != null && smackManager.xmppConnection != null){
				try {
					smackManager.xmppConnection.login(smackManager.connectionConfig.userName, smackManager.connectionConfig.password);
					Presence presence = new Presence(Presence.Type.available);
					smackManager.xmppConnection.sendPacket(presence);

					return true;

				} catch (XMPPException ex) {
					Log.e("XMPPClient", "[SettingsDialog] Failed to connect to " + smackManager.xmppConnection.getHost());
					Log.e("XMPPClient", ex.toString());
					ex.printStackTrace();
				}

			}

			return false;
		}

		@Override
		protected void onProgressUpdate(Void... progress) {

		}

		@Override
		protected void onPostExecute(Boolean result) {
			if ( SmackOperationExecuted != null ) {
				SmackOperationExecuted.onSmackExecuted(SmackActionOptions.LOGIN, result);
			}
		}
	}

	final class SyncConnectionBackground extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			UserDataSource uds=new UserDataSource(ChatifyApplication.getAppContext());
			UserDetail u;
			List<ContactNumber> contactNumbers = DefaultContentProvider.getContacts(ChatifyApplication.getAppContext());
			for(ContactNumber number: contactNumbers){
				for(String phoneNumber: number.getPhoneNumber()){
					phoneNumber = phoneNumber.replaceAll("\\W", "");
					Collection<RosterEntry> roasters = getRosters();
					if ( roasters.contains(phoneNumber) ) {
					}
					else {

						if ( isUserExist(phoneNumber)){

						}
						else
						{
							u=new UserDetail();
							u.setUserName(phoneNumber);
							u.setNickname(number.getName());
							u.setStatus(SmackManager.getUserVCard(phoneNumber).getField("status"));
							if(SmackManager.getUserVCard(phoneNumber).getAvatar()==null){
								
							}else{
								u.setImage(SmackManager.getUserVCard(phoneNumber).getAvatar());
							}
							//create database 
							uds.insert(u);
							
							//create entery in roster online 
							createRoster(phoneNumber, number.getName());
							
						}
					}
				}
			}

			return null;
		}

	}
}