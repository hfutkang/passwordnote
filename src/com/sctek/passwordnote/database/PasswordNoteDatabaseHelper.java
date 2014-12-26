package com.sctek.passwordnote.database;

import com.sctek.passwordnote.database.PasswordNoteProvideData.PwNoteTableData;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class PasswordNoteDatabaseHelper extends SQLiteOpenHelper{
	
	final String TAG = " PasswordNoteDatabaseHelper";
	
	public PasswordNoteDatabaseHelper(Context context) {
		// TODO Auto-generated constructor stub
		super(context,
				PasswordNoteProvideData.DATABASE_NAME,
				null,
				PasswordNoteProvideData.DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		Log.e(TAG, "onCreate");
		
		db.execSQL("CREATE TABLE " + PwNoteTableData.TABLE_NAME_WEBS + " ("
				+ PwNoteTableData._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ PwNoteTableData.WEB_NAME + ","
				+ PwNoteTableData.WEB_URL + ","
				+ PwNoteTableData.WEB_TABS + ","
				+ PwNoteTableData.WEB_CATEGORY + ","
				+ PwNoteTableData.WEB_DEFAULT_USER 
				+ ");");
		
		db.execSQL("CREATE TABLE " + PwNoteTableData.TABLE_NAME_ACCOUNTS + " ("
				+ PwNoteTableData._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ PwNoteTableData.ACCOUNT_NAME + ","
				+ PwNoteTableData.ACCOUNT_URL + ","
				+ PwNoteTableData.ACCOUNT_PW 
				+ ");");
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		Log.e(TAG, "onUpgrade");
		
		db.execSQL("DROP TABLE IF EXISTS " +
				PwNoteTableData.TABLE_NAME_WEBS);
		db.execSQL("DROP TABLE IF EXISTS " +
				PwNoteTableData.TABLE_NAME_WEBS);
		onCreate(db);
	}
	
	

}
