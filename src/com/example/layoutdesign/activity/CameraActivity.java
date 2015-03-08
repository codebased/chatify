package com.example.layoutdesign.activity;

import org.jivesoftware.smackx.packet.VCard;

import com.example.layoutdesign.ApplicationMaster;
import com.example.layoutdesign.R;
import com.example.layoutdesign.DataSource.PreferenceDataSource;
import com.example.layoutdesign.DataSource.UserDataSource;
import com.example.layoutdesign.Model.UserDetail;
import com.example.layoutdesign.interfaceport.SmackManager;
 


import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class CameraActivity extends Activity {
	public  Uri selectedImageUri;
	@Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.camera_capture);
    
    Bundle extras = getIntent().getExtras();
    if (extras != null) {
   	 //mRecipient = extras.getString("Recipient");
       // and get whatever type user account id is
    	String file= extras.getString("gallery");
    	  selectedImageUri = Uri.parse(file);
    	
    	ImageView image = (ImageView) findViewById(R.id.imgCamera);
    	image.setImageURI(selectedImageUri);
   }
    
    Button submitPicture = (Button) findViewById(R.id.btnSave);
    submitPicture.setOnClickListener(new View.OnClickListener() {
		public void onClick(View view) {
			Bitmap bm=PreferenceDataSource.ShrinkBitmap(getRealPathFromURI(selectedImageUri), 250, 250);
			VCard vc=new VCard();
			vc.setAvatar(PreferenceDataSource.encodeBitmapToBytes(bm));
			ApplicationMaster.getSmackManager().saveVCard(vc);
			//SmackManager.saveVCard(vc);
			
			UserDataSource uds=new UserDataSource(CameraActivity.this);
			UserDetail user=new UserDetail();
			user.Image=PreferenceDataSource.encodeBitmapToBytes(bm);
			user.UserName=PreferenceDataSource.PHONENUMBER;
			uds.update(user);
			
			finish();
		}
	});
    
    Button cancelButton = (Button) findViewById(R.id.btnCancel);
    cancelButton.setOnClickListener(new View.OnClickListener() {
		public void onClick(View view) {
			finish();	
		}
	});
  }
	
  public String getRealPathFromURI(Uri contentURI) {
	    Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
	    if (cursor == null) { // Source is Dropbox or other similar local file path
	        return contentURI.getPath();
	    } else { 
	        cursor.moveToFirst(); 
	        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA); 
	        return cursor.getString(idx); 
	    }
	}
 
} 