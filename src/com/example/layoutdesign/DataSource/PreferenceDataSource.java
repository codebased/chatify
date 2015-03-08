package com.example.layoutdesign.DataSource;

import java.io.ByteArrayOutputStream;
import com.example.layoutdesign.ChatifyApplication;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;

public class PreferenceDataSource {

	static String PREFERENCE_NAME = "chatify.preference";
	public static String PHONENUMBER = "PHONENUMBER";
	public static String UNIQUEID = "UNIQUEID";
	public static String IMAGE = "IMAGE";

	public static SharedPreferences getSharedPreferences (Context ctxt) {
		return ctxt.getSharedPreferences(PreferenceDataSource.PREFERENCE_NAME, Context.MODE_PRIVATE);
	}

	public static void setValue(String key, String value){
		Editor editor = PreferenceDataSource.getSharedPreferences(ChatifyApplication.getAppContext()).edit();
		editor.putString(key, value);
		editor.commit();
	}

	public static String getValue(String key){
		SharedPreferences preference =  
				PreferenceDataSource.getSharedPreferences(ChatifyApplication.getAppContext());
		return preference.getString(key, "");
	}

	public static String encodeTobase64(Bitmap image) {
		Bitmap immage = image;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		immage.compress(Bitmap.CompressFormat.PNG, 50, baos);
		byte[] b = baos.toByteArray();
		String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
		return imageEncoded;
	}
	
	public static byte[] encodeBitmapToBytes(Bitmap image) {
		Bitmap immage = image;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		immage.compress(Bitmap.CompressFormat.PNG, 50, baos);
		return baos.toByteArray();
	}

	public static Bitmap decodeBase64(String base64) {
		byte[] decodedByte = Base64.decode(base64, 0);
		return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
	}
	
	public static Bitmap decodeByteToBitmap(byte[] decodedByte) {
		
		return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
		}
	
	public static Bitmap drawableToBitmap (Drawable drawable) {
	    if (drawable instanceof BitmapDrawable) {
	        return ((BitmapDrawable)drawable).getBitmap();
	    }

	    Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Config.ARGB_8888);
	    Canvas canvas = new Canvas(bitmap); 
	    drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
	    drawable.draw(canvas);

	    return bitmap;
	}
	
	public static Drawable getDrwableFromBytes(byte[] imageBytes) {
		  
		  if (imageBytes != null)
		   return new BitmapDrawable(BitmapFactory.decodeByteArray(imageBytes,
		     0, imageBytes.length));
		  else
		   return null;
		 }
	
	public static Bitmap ShrinkBitmap(String file, int width, int height){
		   
	     BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
	        bmpFactoryOptions.inJustDecodeBounds = true;
	        Bitmap bitmap = BitmapFactory.decodeFile(file, bmpFactoryOptions);
	         
	        int heightRatio = (int)Math.ceil(bmpFactoryOptions.outHeight/(float)height);
	        int widthRatio = (int)Math.ceil(bmpFactoryOptions.outWidth/(float)width);
	         
	        if (heightRatio > 1 || widthRatio > 1)
	        {
	         if (heightRatio > widthRatio)
	         {
	          bmpFactoryOptions.inSampleSize = heightRatio;
	         } else {
	          bmpFactoryOptions.inSampleSize = widthRatio; 
	         }
	        }
	         
	        bmpFactoryOptions.inJustDecodeBounds = false;
	        bitmap = BitmapFactory.decodeFile(file, bmpFactoryOptions);
	     return bitmap;
	    }
	
	public static String getRealPathFromURI(Uri contentURI,Context ctx) {
	    Cursor cursor = ctx.getContentResolver().query(contentURI, null, null, null, null);
	    if (cursor == null) { // Source is Dropbox or other similar local file path
	        return contentURI.getPath();
	    } else { 
	        cursor.moveToFirst(); 
	        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA); 
	        return cursor.getString(idx); 
	    }
	}
}
