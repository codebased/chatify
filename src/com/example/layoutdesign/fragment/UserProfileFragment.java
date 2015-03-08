package com.example.layoutdesign.fragment;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.packet.VCard;

import com.example.layoutdesign.ApplicationMaster;
import com.example.layoutdesign.MainActivity;
import com.example.layoutdesign.R;
import com.example.layoutdesign.R.id;
import com.example.layoutdesign.DataSource.PreferenceDataSource;
import com.example.layoutdesign.DataSource.UserDataSource;
import com.example.layoutdesign.Model.HttpAction;
import com.example.layoutdesign.activity.CameraActivity;
import com.example.layoutdesign.activity.HomeActivity;
import com.example.layoutdesign.activity.ImageViewerActivity;
import com.example.layoutdesign.base.ListFragmentBase;
import com.example.layoutdesign.interfaceport.SmackManager;

import common.lib.http.HttpRequestParams;
import common.lib.http.HttpResponseParams;
import common.lib.http.IOperationExecution;
import common.lib.http.JSONDataAdapter;
import common.lib.http.UnRegisterPhoneResponse;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class UserProfileFragment extends ListFragmentBase 
implements IOperationExecution {
	int currentPosition = -1;
	String currentReceiverName = "";
	
	public final static String ARG_POSITION = "position";
	public final static String ARG_RECEIVER_NAME = "receivername";
	
	private static final int GALLERY_REQUEST = 2501;
	public UserProfileFragment() {
		// Empty constructor required for fragment subclasses
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState) {

		
		// If activity recreated (such as from screen rotate), restore
		// the previous article selection set by onSaveInstanceState().
		// This is primarily necessary when in the two-pane layout.
		if (savedInstanceState != null) {
			this.currentPosition= savedInstanceState.getInt(ARG_POSITION);
			this.currentReceiverName= savedInstanceState.getString(ARG_RECEIVER_NAME);
		}

		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.activity_profile, container, false);
	}

	@Override
	public void onStart() {
		super.onStart();

		Button btnUnRegister = (Button) getActivity().findViewById(R.id.btnUnregisterProfile);
		btnUnRegister.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				deleteRegisteration(PreferenceDataSource.getValue(PreferenceDataSource.PHONENUMBER));
			}
		});



		// During startup, check if there are arguments passed to the fragment.
		// onStart is a good place to do this because the layout has already been
		// applied to the fragment at this point so we can safely call the method
		// below that sets the article text.
		
		this.currentReceiverName="";
		if(this.currentReceiverName.isEmpty())
		{
		getPersonalProfileData();
		setPersonalProfileData();
		}
		else
		{
			getUserProfileData();
		}
		
	}
		//added by syed
		private void dispatchGalleryPictureIntent() {
			Intent intent = new Intent();
	    	
	        intent.setType("image/*");

	        intent.setAction(Intent.ACTION_GET_CONTENT);

	        startActivityForResult(Intent.createChooser(intent,"Select Picture"), GALLERY_REQUEST);
	 
		}
		
		@Override
		public  void onActivityResult(int requestCode, int resultCode, Intent data) {
	    	if (resultCode == getActivity().RESULT_OK) {
	    	       if (requestCode == GALLERY_REQUEST) {
	    	    	   	
	    	    	   	Intent intent = new Intent(getActivity(), CameraActivity.class);
	    	    	   	Bundle b = new Bundle();
	    	    	   	b.putString("gallery",PreferenceDataSource.getRealPathFromURI(data.getData(),getActivity())); //Your id
	    	    	   	intent.putExtras(b); //Put your id to your next Intent
	    	    	   	getActivity().startActivity(intent);
						
					}
	    	}
	    }
	
		
	protected void actionBacktoChat() {
		((HomeActivity)getActivity()).itemClicked(0);
	}

	@Override
	public void itemClicked(int position) {
		
	}
	
	public void getPersonalProfileData()
	{
		TextView txtStatus=(TextView) getActivity().findViewById(R.id.txtStatus);
		txtStatus.setText(SmackManager.getPersonalVCard().getField("status"));
		
		TextView txtNickName=(TextView) getActivity().findViewById(R.id.txtNickName);
		txtNickName.setText(SmackManager.getPersonalVCard().getFirstName());
		
		ImageView imgPic=(ImageView) getActivity().findViewById(R.id.imgProfilePicture);
		if(SmackManager.getPersonalVCard().getAvatar()==null){
			
		}else{
		imgPic.setImageBitmap(PreferenceDataSource.decodeByteToBitmap(SmackManager.getPersonalVCard().getAvatar()));
		}
	}
	
	public void getUserProfileData()
	{
		TextView txtStatus=(TextView) getActivity().findViewById(R.id.txtStatus);
		txtStatus.setText(SmackManager.getUserVCard(this.currentReceiverName).getField("status"));
		
		TextView txtNickName=(TextView) getActivity().findViewById(R.id.txtNickName);
		txtNickName.setText(SmackManager.getUserVCard(this.currentReceiverName).getFirstName());
		
		ImageView imgPic=(ImageView) getActivity().findViewById(R.id.imgProfilePicture);
		if(SmackManager.getUserVCard(this.currentReceiverName).getAvatar()==null){
			
		}else{
		imgPic.setImageBitmap(PreferenceDataSource.decodeByteToBitmap(SmackManager.getUserVCard(this.currentReceiverName).getAvatar()));
		}
		
		Button statusDialog = (Button) getActivity().findViewById(R.id.btnOpenStatusDialog);
		statusDialog.setVisibility(View.GONE);
		Button nickNameDialog = (Button) getActivity().findViewById(R.id.btnSetNickName);
		nickNameDialog.setVisibility(View.GONE);
		
		ImageView imgProfile = (ImageView) getActivity().findViewById(R.id.imgProfilePicture);
		imgProfile.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Intent intent = new Intent(getActivity(), ImageViewerActivity.class);
	    	   	Bundle b = new Bundle();
	    	   	b.putByteArray("gallery",SmackManager.getUserVCard(currentReceiverName).getAvatar()); //Your id
	    	   	intent.putExtras(b); //Put your id to your next Intent
	    	   	getActivity().startActivity(intent);
			}
		});
	}
	
	public void setPersonalProfileData()
	{
		Button statusDialog = (Button) getActivity().findViewById(R.id.btnOpenStatusDialog);
		statusDialog.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				openStatusDialogOnClick();
			}
		});
		
		Button nickNameDialog = (Button) getActivity().findViewById(R.id.btnSetNickName);
		nickNameDialog.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				openNickNameDialogOnClick();
			}
		});
		
		ImageView imgProfilePic = (ImageView) getActivity().findViewById(R.id.imgProfilePicture);
		imgProfilePic.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				dispatchGalleryPictureIntent();
			}
		});
	}
	UserDataSource uds=new UserDataSource(getActivity());
	public void openStatusDialogOnClick()
	{
		// custom dialog
					final Dialog dialog = new Dialog(getActivity());
					dialog.setContentView(R.layout.edittext_dialog);
					dialog.setTitle("Set Status");
		 
					// set the custom dialog components - text, image and button
					Button dialogButton = (Button) dialog.findViewById(R.id.btnSubmitStatus);
					// if button is clicked, close the custom dialog
					dialogButton.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							EditText statusText=(EditText)dialog.findViewById(R.id.statusText);
							VCard vc=new VCard();
							vc.setLastName(statusText.getText().toString());
							//SmackManager.saveVCard(vc);
							
							uds.updateStatus(PreferenceDataSource.PHONENUMBER, statusText.getText().toString());
							
							TextView txtStatus=(TextView)getActivity().findViewById(id.txtStatus);
							txtStatus.setText(statusText.getText().toString());
							Toast.makeText(getActivity(), "Status Updated", Toast.LENGTH_LONG).show();
							dialog.dismiss();
						}
					});
					
					// set the custom dialog components - text, image and button
					Button dialogCancelButton = (Button) dialog.findViewById(R.id.btnCancelDialog);
					// if button is clicked, close the custom dialog
					dialogCancelButton.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							dialog.dismiss();
						}
					});
		 
					dialog.show();
        
	}
	
	public void openNickNameDialogOnClick()
	{
		// custom dialog
					final Dialog dialog = new Dialog(getActivity());
					dialog.setContentView(R.layout.edittext_dialog);
					dialog.setTitle("Nick Name");
		 
					// set the custom dialog components - text, image and button
					Button dialogButton = (Button) dialog.findViewById(R.id.btnSubmitStatus);
					// if button is clicked, close the custom dialog
					dialogButton.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							EditText statusText=(EditText)dialog.findViewById(R.id.statusText);
							VCard vc=new VCard();
							vc.setFirstName(statusText.getText().toString());
							//SmackManager.saveVCard(vc);
							
							uds.updateNickName(PreferenceDataSource.PHONENUMBER, statusText.getText().toString());
							TextView txtNickName=(TextView)getActivity().findViewById(id.txtNickName);
							txtNickName.setText(statusText.getText().toString());
							
							Toast.makeText(getActivity(), "Name has been Updated", Toast.LENGTH_LONG).show();
							dialog.dismiss();
						}
					});
					
					// set the custom dialog components - text, image and button
					Button dialogCancelButton = (Button) dialog.findViewById(R.id.btnCancelDialog);
					// if button is clicked, close the custom dialog
					dialogCancelButton.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							dialog.dismiss();
						}
					});
		 
					dialog.show();
        
	}
	
	@Override
	public void setListData(Bundle data) {
		// TODO Auto-generated method stub
		
	}

	public void deleteRegisteration(String phoneNumber){
		UnRegisterPhoneResponse response = new UnRegisterPhoneResponse();
		response.operationExecution = this;

		JSONDataAdapter<UnRegisterPhoneResponse> jsonAdapter = new
				JSONDataAdapter<UnRegisterPhoneResponse>(getActivity(), response);

		HttpRequestParams params = new HttpRequestParams();

		params.httpAction = ApplicationMaster.UNREGISTERPHONE;
		params.formData = new Bundle();
		params.formData.putString("uniqueID", common.lib.misc.SecurityManager.getPseudoUniqueID());
		params.formData.putString("phoneNumber", phoneNumber);
		jsonAdapter.execute(params);
	}



	
	@Override
	public void onExecuted(HttpAction action, HttpResponseParams params) {
		
		try {
			
			if ( ApplicationMaster.hasValidChatServerConnection() && ApplicationMaster.hasValidChatServerLogin()){
				ApplicationMaster.getSmackManager().xmppConnection.getAccountManager().deleteAccount();
			}
		} catch (XMPPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		PreferenceDataSource.setValue(PreferenceDataSource.PHONENUMBER, "");
		Intent intent = new Intent(getActivity(), MainActivity.class);
		getActivity().startActivity(intent);      
		getActivity().finish();
	}


  
}