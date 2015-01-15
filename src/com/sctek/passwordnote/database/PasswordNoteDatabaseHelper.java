package com.sctek.passwordnote.database;

import com.sctek.passwordnote.database.PasswordNoteProvideData.PwNoteTableData;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class PasswordNoteDatabaseHelper extends SQLiteOpenHelper{
	
	final String[] urls = new String[] {"https://login.taobao.com/", "http://login.1688.com/", "https://passport.jd.com/new/login.aspx", "https://passport.vip.com/login", 
			"https://passport.yhd.com/", "http://mail.163.com/", "http://mail.126.com/", "https://mail.aliyun.com/", "http://mail.sina.com.cn/", "https://mail.qq.com/"};
	
	final String[] names = new String[] {"淘宝", "阿里巴巴", "京东", "唯品会", "1号店", "163邮箱", "126邮箱", "阿里云邮箱", "新浪邮箱", "QQ邮箱"};
	
	final int[] tabs = new int[] {2, 6, 1, 2, 1, 1, 1, 1, 1, 1};
	
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
		for(int i = 0; i < urls.length; i++) {
			try {
			db.execSQL("INSERT INTO " + PwNoteTableData.TABLE_NAME_WEBS + " ("
					+ PwNoteTableData.WEB_NAME + ","
					+ PwNoteTableData.WEB_URL + ","
					+ PwNoteTableData.WEB_TABS + ") VALUES ("
					+ "'" + names[i] + "'" + "," 
					+ "'" + urls[i] + "'" + "," + tabs[i]
					+ ");");
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
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
