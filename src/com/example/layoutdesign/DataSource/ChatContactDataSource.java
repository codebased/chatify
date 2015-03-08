package com.example.layoutdesign.DataSource;

import java.util.ArrayList;
import java.util.List;

import com.example.layoutdesign.Model.ChatContact;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;


public class ChatContactDataSource extends DataSourceBase  {

	 public final String KEY_CHATCONTACTID= "chatcontactid";
	 public final String KEY_NAME = "name";
	 public final String MYDATABASE_TABLE = "mycontacts";
	 
	 private String SCRIPT_CREATE_DATABASE_TABLE =
	  "create table " + MYDATABASE_TABLE + " ("
	  + KEY_CHATCONTACTID + " text not null," + KEY_NAME + " text not null);";
	 
	 public ChatContactDataSource(Context c){
		 super(c);
	 }
	 
	 public String getCreateTableQuery() {
		return this.SCRIPT_CREATE_DATABASE_TABLE;
	};
	  
	@Override
	public String getTableName() {
		return this.MYDATABASE_TABLE;
	}
	
	public long insert(ChatContact c){
	  
	  ContentValues contentValues = new ContentValues();
	  contentValues.put(KEY_CHATCONTACTID, c.getChatContactID());
	  contentValues.put(KEY_NAME, c.getName());
	  
	  return sqLiteDatabase.insert(MYDATABASE_TABLE, null, contentValues);
	 }
	
	public List<ChatContact> getWordMatches(String query, String[] columns) {
	    String selection =  "TEXT MATCH ?";
	    String[] selectionArgs = new String[] {query+"*"};

	    return query(selection, selectionArgs, columns);
	}
	
	public List<ChatContact> query(String selection, String[] selectionArgs, String[] columns){
		List<ChatContact> contacts = new ArrayList<ChatContact>();
		 
		if ( columns == null ){
			 columns = new String[]{KEY_CHATCONTACTID, KEY_NAME};
		 }
		 
		 Cursor cursor = sqLiteDatabase.query(MYDATABASE_TABLE, columns,selection, selectionArgs, null, null, null);
	
		 for(cursor.moveToFirst(); !(cursor.isAfterLast()); cursor.moveToNext()){
			 ChatContact contact = this.cursorToChatContact(cursor);
			 contacts.add(contact);
		 }
		 
		 return contacts;
	}
 
 
	private ChatContact cursorToChatContact(Cursor cursor) {
		ChatContact chatContact = new ChatContact();
		    chatContact.setChatContactID(cursor.getString(0));
		    chatContact.setName(cursor.getString(1));
		    return chatContact;

	}
}
