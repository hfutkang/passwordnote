package com.sctek.passwordnote;

import java.util.ArrayList;

import com.sctek.passwordnote.database.PasswordNoteProvideData;
import com.sctek.passwordnote.database.PasswordNoteProvideData.PwNoteTableData;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class Web {
	
	private final String TAG = "Web";
	
	private final String UPDAT_EWHERE = PwNoteTableData.ACCOUNT_URL + "=%s" + 
									"AND (" + PwNoteTableData.ACCOUNT_NAME + "=%s)";
	
	private static Web instance;
	
	private Context mContext;
	private ArrayList<WebData> webList;
	private ArrayList<AccountData> accountList;
	
	
	public Web() {
		webList = new ArrayList<Web.WebData>();
		accountList = new ArrayList<Web.AccountData>();
	}
	
	public static Web getInstance(Context c) {
		
		if(instance == null)
			instance = new Web();
		instance.setContext(c);
		return instance;
	}
	
	public void setContext(Context c) {
		mContext = c;
	}
	
	public void loadWebData() {
		
		Log.e(TAG, "loadWebData");
		webList.clear();
		
		ContentResolver contentResolver = mContext.getContentResolver();
		Cursor cursor = contentResolver.query(
				PwNoteTableData.CONTENT_URI_WEBS, null, null, null, null);
		
		while(cursor.moveToNext()) {

			int nameIndex = cursor.getColumnIndex(PwNoteTableData.WEB_NAME);
			int urlIndex = cursor.getColumnIndex(PwNoteTableData.WEB_URL);
			int categoryIndex = cursor.getColumnIndex(PwNoteTableData.WEB_CATEGORY);
			int tabsIndex = cursor.getColumnIndex(PwNoteTableData.WEB_TABS);
			int defaultUserIndex = cursor.getColumnIndex(PwNoteTableData.WEB_DEFAULT_USER);
			int idIndex = cursor.getColumnIndex(PwNoteTableData._ID);
			
			int id = cursor.getInt(idIndex);
			String name = cursor.getString(nameIndex);
			String url = cursor.getString(urlIndex);
			String category = cursor.getString(categoryIndex);
			int tabs = cursor.getInt(tabsIndex);
			String defaultUser = cursor.getString(defaultUserIndex);
			
			WebData wd = new WebData();
			wd.name = name;
			wd.url = url;
			wd.category = category;
			wd.tabs = tabs;
			wd.defaultUser = defaultUser;
			
			webList.add(wd);

		}
		cursor.close();
	}
	
	public void loadAccountData() {
		Log.e(TAG, "loadWebData");
		accountList.clear();
		
		ContentResolver contentResolver = mContext.getContentResolver();
		Cursor cursor = contentResolver.query(
				PwNoteTableData.CONTENT_URI_ACCOUNTS, null, null, null, null);
		
		while(cursor.moveToNext()) {

			int nameIndex = cursor.getColumnIndex(PwNoteTableData.ACCOUNT_NAME);
			int urlIndex = cursor.getColumnIndex(PwNoteTableData.ACCOUNT_URL);
			int pwIndex = cursor.getColumnIndex(PwNoteTableData.ACCOUNT_PW);
			int idIndex = cursor.getColumnIndex(PwNoteTableData._ID);
			
			int id = cursor.getInt(idIndex);
			String name = cursor.getString(nameIndex);
			String url = cursor.getString(urlIndex);
			String pw = cursor.getString(pwIndex);
			
			AccountData ad = new AccountData();
			ad.name = name;
			ad.url = url;
			ad.pw = pw;
			
			accountList.add(ad);

		}
		cursor.close();
	}
	
	public void newWeb(WebData wd) {
		Log.e(TAG, "newWeb");
		
		webList.add(wd);
		
		ContentResolver contentResolver = mContext.getContentResolver();
		
		ContentValues values = new ContentValues();
		values.put(PwNoteTableData.WEB_NAME, wd.name);
		values.put(PwNoteTableData.WEB_URL, wd.url);
		values.put(PwNoteTableData.WEB_TABS, wd.tabs);
		
		contentResolver.insert(PwNoteTableData.CONTENT_URI_WEBS, values);
	}
	
	public void newAccount(AccountData ad) {
		Log.e(TAG, "newAccount");
		
		ContentResolver contentResolver = mContext.getContentResolver();
		
		ContentValues aValues = new ContentValues();
		aValues.put(PwNoteTableData.ACCOUNT_NAME, ad.name);
		aValues.put(PwNoteTableData.ACCOUNT_PW, ad.pw);
		aValues.put(PwNoteTableData.ACCOUNT_URL, ad.url);
		Log.e(TAG, ad.url);
		
		contentResolver.insert(PwNoteTableData.CONTENT_URI_ACCOUNTS, aValues);
		
		ContentValues wValues = new ContentValues();
		wValues.put(PwNoteTableData.WEB_DEFAULT_USER, ad.name);
		contentResolver.update(PwNoteTableData.CONTENT_URI_WEBS, wValues, 
				PwNoteTableData.WEB_URL + "=?", new String[]{ad.url});
		
		accountList.add(ad);
		WebData wd = getWeb(ad.url);
		wd.defaultUser = ad.name;
		
	}
	
	public void deleteWeb( WebData wd) {
		
		
		ContentResolver contentResolver = mContext.getContentResolver();
		contentResolver.delete(PwNoteTableData.CONTENT_URI_WEBS, 
				PwNoteTableData.WEB_URL + "=?", new String[] {wd.url});
		contentResolver.delete(PwNoteTableData.CONTENT_URI_ACCOUNTS, 
				PwNoteTableData.ACCOUNT_URL + "=?", new String[]{wd.url});
		
		for(int j = 0; j < webList.size(); j++)
			if(wd.url.equals(webList.get(j).url))
				webList.remove(j);
		for(int i = 0; i <accountList.size(); i++)
			if(wd.url.equals(accountList.get(i).url))
					accountList.remove(i);
	}
	
	public void updateWeb(WebData wd) {
		
		ContentResolver contentResolver = mContext.getContentResolver();
		
		ContentValues values = new ContentValues();
		values.put(PwNoteTableData.WEB_NAME, wd.name);
		values.put(PwNoteTableData.WEB_TABS, wd.tabs);
		
		contentResolver.update(PwNoteTableData.CONTENT_URI_WEBS, values, 
				PwNoteTableData.WEB_URL + "=?", new String[]{wd.url});
	}
	
	public void updateAccount(AccountData ad, String oldName) {
		Log.e(TAG, "updateAccount");
		String where = String.format(UPDAT_EWHERE, ad.url, oldName);
		Log.e(TAG, where);
		ContentResolver contentResolver = mContext.getContentResolver();
		
		ContentValues values = new ContentValues();
		values.put(PwNoteTableData.ACCOUNT_NAME, ad.name);
		values.put(PwNoteTableData.ACCOUNT_PW, ad.pw);
		
		contentResolver.update(PwNoteTableData.CONTENT_URI_ACCOUNTS, values, 
						PwNoteTableData.ACCOUNT_URL + "=? AND " 
						+ PwNoteTableData.ACCOUNT_NAME + "=?", 
						new String[]{ad.url, oldName});
		
		ContentValues wValues = new ContentValues();
		wValues.put(PwNoteTableData.WEB_DEFAULT_USER, ad.name);
		contentResolver.update(PwNoteTableData.CONTENT_URI_WEBS, wValues, 
				PwNoteTableData.WEB_URL + "=?", new String[]{ad.url});
		
		WebData wd = getWeb(ad.url);
		wd.defaultUser = ad.name;
	}
	
	public AccountData getAccount( String url) {
		for(WebData wd : webList) {
			if(url.equals(wd.url)) {
				String useName = wd.defaultUser;
				for(AccountData ad : accountList) {
					if(ad.name.equals(useName))
						return ad;
				}
			}
		}
		return null;
	}
	
	public WebData getWeb(String url) {
		for(WebData wd : webList) {
			if(url.equals(wd.url))
				return wd;
		}
		return null;
	}
	
	public ArrayList<WebData> getWebs() {
		return webList;
	}
	
	public class WebData {
		public String url;
		public String name;
		public String defaultUser;
		public String category;
		public int tabs;
	}
	
	public class AccountData {
		public String name;
		public String pw;
		public String url;
	}

}
