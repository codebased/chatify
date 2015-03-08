package com.example.layoutdesign.DataSource;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.packet.Presence;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.example.layoutdesign.ApplicationMaster;
import com.example.layoutdesign.Model.ListModel;
import com.example.layoutdesign.Model.UserDetail;
import com.example.layoutdesign.base.ActivityBase;

import common.lib.misc.SecurityManager;

public class UserDataSource extends DataSourceBase {
	
	public final String MYDATABASE_TABLE = "userprofile";

	private String SCRIPT_CREATE_DATABASE_TABLE =
			"create table " + MYDATABASE_TABLE + " (username text not null,nickname text null,status text null,image BLOB null,presence text null);";

	public UserDataSource(Context c){
		super(c);
	}

	public String getCreateTableQuery() {
		return this.SCRIPT_CREATE_DATABASE_TABLE;
	};

	@Override
	public String getTableName() {
		return this.MYDATABASE_TABLE;
	}
	
	public long insert(UserDetail u){

		ContentValues contentValues = new ContentValues();
		contentValues.put("username", u.getUserName());
		contentValues.put("nickname", u.getNickname());
		contentValues.put("status", u.getStatus());
		contentValues.put("image", u.getImage());
		contentValues.put("presence", u.getPresence());
		this.openToWrite();
		return sqLiteDatabase.insert(MYDATABASE_TABLE, null, contentValues);
	}
	
	public long update(UserDetail u){

		ContentValues contentValues = new ContentValues();
		contentValues.put("username", u.getUserName());
		contentValues.put("nickname", u.getNickname());
		contentValues.put("status", u.getStatus());
		contentValues.put("image", u.getImage());
		contentValues.put("presence", u.getPresence());
		this.openToWrite();
		
		return sqLiteDatabase.update(MYDATABASE_TABLE, contentValues, "username='"+u.getUserName()+"'", null);
	}
	
	public long updatePresence(String uname,String presence){

		ContentValues contentValues = new ContentValues();
		contentValues.put("username", uname);
		contentValues.put("presence", presence);
		this.openToWrite();
		
		return sqLiteDatabase.update(MYDATABASE_TABLE, contentValues, "username='"+uname+"'", null);
	}
	
	public long updateStatus(String uname,String status){

		ContentValues contentValues = new ContentValues();
		contentValues.put("username", uname);
		contentValues.put("status", status);
		this.openToWrite();
		
		return sqLiteDatabase.update(MYDATABASE_TABLE, contentValues, "username='"+uname+"'", null);
	}
	
	public long updateNickName(String uname,String nick){

		ContentValues contentValues = new ContentValues();
		contentValues.put("username", uname);
		contentValues.put("nickname", nick);
		this.openToWrite();
		
		return sqLiteDatabase.update(MYDATABASE_TABLE, contentValues, "username='"+uname+"'", null);
	}
	
	private UserDetail cursorToUserDetail(Cursor cursor) {
		UserDetail user = new UserDetail();
		user.setName(cursor.getString(0));
		user.setNickname(cursor.getString(1));
		user.setStatus(cursor.getString(2));
		user.setImage(cursor.getBlob(3));
		user.setPresence(cursor.getString(4));
		return user;
	}
	
	public UserDetail query(String rawQuery){
		UserDetail user = new UserDetail();
		this.openToRead();
		Cursor cursor = sqLiteDatabase.rawQuery(rawQuery, null);
		user = this.cursorToUserDetail(cursor);
		return user;
	}
	
	public List<UserDetail> queryList(String rawQuery){
		List<UserDetail> userList = new ArrayList<UserDetail>();
		this.openToRead();
		Cursor cursor = sqLiteDatabase.rawQuery(rawQuery, null);

		for(cursor.moveToFirst(); !(cursor.isAfterLast()); cursor.moveToNext()){
			UserDetail user = this.cursorToUserDetail(cursor);
			userList.add(user);
		}

		return userList;
	}
	
	public List<ListModel> getUsers (ActivityBase view){
 
			List<ListModel> listValues = new ArrayList<ListModel>();
			ListModel m;
			if(view.getSmackManager().getRosters().size()>0)
			{
			for (RosterEntry entry : view.getSmackManager().getRosters()) {
				m =new ListModel();
				m.setText(entry.getUser());
				//m.setText(entry.getUser());
				Presence presence = view.getSmackManager().getPresence(entry.getUser());

				if (presence.isAvailable()) {
				//	m.setImage("online");   
				}else if (presence.isAway()) {
					//m.setImage("away");
				}
				else{
					//m.setImage("offline");
				}

				listValues.add(m);
			}
		}
		
		
		return listValues;
	}

	
	
	public Bitmap getProfilePicture(){

		Bitmap bitmap = null;
		try {
			
			URL imageURL = new URL(ApplicationMaster.PROFILEIMAGE + "&uniqueID=" + SecurityManager.getPseudoUniqueID());
			bitmap = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bitmap;
	}
}
