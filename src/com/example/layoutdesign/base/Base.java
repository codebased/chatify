package com.example.layoutdesign.base;
import com.example.layoutdesign.ApplicationMaster;
import com.example.layoutdesign.ApplicationMessageActivity;
import com.example.layoutdesign.ChatifyApplication;
import com.example.layoutdesign.R;
import com.example.layoutdesign.Model.SmackConfiguration;
import com.example.layoutdesign.interfaceport.SmackManager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;
import android.widget.*;


public class Base extends FragmentActivity {

	public boolean disableTitle;
	public TextView headerText; 
	
	public SmackManager getSmackManager(){
		return ApplicationMaster.getSmackManager();
	}

	public boolean isOffline(){
		return false;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		if ( this.disableTitle){
			this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		}
		 
		super.onCreate(savedInstanceState);
	}

	public void setHeader(int resourceID){
		if(this.headerText == null){
			this.headerText = (TextView) this.findViewById(R.id.headerText);
		}

		if(this.headerText != null){
			this.headerText.setText(resourceID);
		}
	}
	
	public void showAlert(String message) {
		if ( ChatifyApplication.getAppContext().getResources() != null ) {
			this.showAlert(ChatifyApplication.getAppContext().getResources().getString(R.string.app_name), message);	
		} else {
			this.showAlert("", message);
		}
	}
	
	public void showAlert(String title, int resourceID){
		this.showAlert(title, this.getString(resourceID));
	}

	public void showAlert(String title, String message)	{
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		builder.setTitle(title)
			.setMessage(message)
			.setCancelable(false)
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialogInterface, int i) {
					dialogInterface.dismiss();
				}
			}).create().show();
	}
	
	public void openApplicationMessageActivity(String message){
		Intent intent = new Intent(this, ApplicationMessageActivity.class);
		intent.putExtra("applicationMessage", message);
		this.startActivity(intent);
	}
} 