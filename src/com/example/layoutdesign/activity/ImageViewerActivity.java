package com.example.layoutdesign.activity;

import com.example.layoutdesign.R;
import com.example.layoutdesign.DataSource.PreferenceDataSource;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

public class ImageViewerActivity extends Activity {
	public  Uri selectedImageUri;
	@Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.image_viewer);
    
    Bundle extras = getIntent().getExtras();
    if (extras != null) {
   	 //mRecipient = extras.getString("Recipient");
       // and get whatever type user account id is
    	byte[] file= extras.getByteArray("gallery");
    	ImageView image = (ImageView) findViewById(R.id.imgCamera);
    	image.setImageBitmap(PreferenceDataSource.decodeByteToBitmap(file));
		}
    
   
  }
 
} 