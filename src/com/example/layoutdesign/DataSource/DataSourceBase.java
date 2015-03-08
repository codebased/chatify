package com.example.layoutdesign.DataSource;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public abstract class DataSourceBase {
	 public static final String DATABASE_NAME = "Chatify.db";
	 public static final int MYDATABASE_VERSION = 4;
	 protected SQLiteHelper sqLiteHelper;
	 protected SQLiteDatabase sqLiteDatabase;
	 protected Context context;
	 
	 public DataSourceBase(Context c) {
		 this.context = c;
	 }
	 
	 public void openToRead() throws android.database.SQLException {
		  sqLiteHelper = new SQLiteHelper(context, DATABASE_NAME, null, MYDATABASE_VERSION);
		  sqLiteDatabase = sqLiteHelper.getReadableDatabase();
	 }
	 
	 public void openToWrite() throws android.database.SQLException {
		  this.sqLiteHelper = new SQLiteHelper(this.context, DATABASE_NAME, null, MYDATABASE_VERSION);
		  sqLiteDatabase = sqLiteHelper.getWritableDatabase();
	}
	 
	public int deleteAll(){
		  return sqLiteDatabase.delete(this.getTableName(), null, null);
	}
		 
	 
	 public void close(){
		  sqLiteHelper.close();
	 }
	 
	 public abstract String getCreateTableQuery();
	 
	 public abstract String getTableName();
	 
	 public class SQLiteHelper extends SQLiteOpenHelper {

		  public SQLiteHelper(Context context, String name,
		    CursorFactory factory, int version) {
		   super(context, name, factory, version);
		  }

		  @Override
		  public void onCreate(SQLiteDatabase db) {
		   // TODO Auto-generated method stub
		   db.execSQL(getCreateTableQuery());
		  }

		  @Override
		  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			  db.execSQL("DROP TABLE IF EXISTS " + getTableName());
			  onCreate(db);
		  }
	}
}
