package com.example.layoutdesign.DataSource;

import java.util.ArrayList;
import java.util.List;

import com.example.layoutdesign.Model.ChatHistory;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.Html;

public class ChatHistoryDataSource extends DataSourceBase  {
 
	public final String KEY_RECEIVER = "receiver";
	public final String KEY_SENTTEXT = "senttext";
	public final String KEY_RECEIVEDTEXT = "receivedtext";
	public final String KEY_DATERECEIVED = "datereceived";
	public final String MYDATABASE_TABLE = "chathistory";

	private String SCRIPT_CREATE_DATABASE_TABLE =
			"create table " + MYDATABASE_TABLE + " ("
					+ KEY_RECEIVER + " text not null," 
					+ KEY_SENTTEXT + " text null,"
					+ KEY_RECEIVEDTEXT + " text null,"
					+ KEY_DATERECEIVED + " datetime default current_timestamp );";

	public ChatHistoryDataSource(Context c){
		super(c);
	}

	public String getCreateTableQuery() {
		return this.SCRIPT_CREATE_DATABASE_TABLE;
	};

	@Override
	public String getTableName() {
		return this.MYDATABASE_TABLE;
	}

	public long insert(ChatHistory c){

		ContentValues contentValues = new ContentValues();
		contentValues.put(KEY_RECEIVER, c.getReceiver());
		contentValues.put(KEY_SENTTEXT, c.getText1());
		contentValues.put(KEY_RECEIVEDTEXT, c.getText());
		this.openToWrite();
		return sqLiteDatabase.insert(MYDATABASE_TABLE, null, contentValues);
	}

	public List<ChatHistory> getReceiverMatches(String query, String[] columns) {
		String selection =  "RECEIVER MATCH ?";
		String[] selectionArgs = new String[] {query+"*"};
		return query(selection, selectionArgs, columns);
	}

	public List<ChatHistory> getRecentChatSummary(){

		String sql =  "SELECT ch.receiver, ch.senttext, ch.receivedtext, ch.datereceived FROM chathistory ch "
				+ " where rowid in (select rowid from chathistory "
				+ " where receiver = ch.receiver order by datereceived desc,rowid desc "
				+ " limit 1)  order by  ch.receiver asc,  ch.datereceived desc;";
		
		return this.query(sql);
	}

	public List<ChatHistory> query(String rawQuery){
		List<ChatHistory> historyRecords = new ArrayList<ChatHistory>();
		this.openToRead();
		Cursor cursor = sqLiteDatabase.rawQuery(rawQuery, null);

		for(cursor.moveToFirst(); !(cursor.isAfterLast()); cursor.moveToNext()){
			ChatHistory history = this.cursorToChatHistory(cursor);
			historyRecords.add(history);
		}

		return historyRecords;
	}

	public List<ChatHistory> query(String selection, String[] selectionArgs, String[] columns){
		List<ChatHistory> historyRecords = new ArrayList<ChatHistory>();

		if ( columns == null ){
			columns = new String[]{KEY_RECEIVER, KEY_SENTTEXT,KEY_RECEIVEDTEXT, KEY_DATERECEIVED};
		}

		this.openToRead();
		Cursor cursor = sqLiteDatabase.query(MYDATABASE_TABLE, columns,selection, selectionArgs, null, null, null);

		for(cursor.moveToFirst(); !(cursor.isAfterLast()); cursor.moveToNext()){
			ChatHistory history = this.cursorToChatHistory(cursor);
			historyRecords.add(history);
		}

		return historyRecords;
	}
 
	private ChatHistory cursorToChatHistory(Cursor cursor) {
		ChatHistory history = new ChatHistory();
		history.setReceiver(cursor.getString(0));
		history.setText1(cursor.getString(1));
		history.setText(cursor.getString(2));
		history.setDateTime(cursor.getString(3));
		return history;
	}
}
