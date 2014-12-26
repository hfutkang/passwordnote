package com.sctek.passwordnote.database;

import android.net.Uri;
import android.provider.BaseColumns;

public class PasswordNoteProvideData {
	
	public static final String AUTHORITY = "com.sctek.provider.PasswordNoteProvider";
	
	public static final String DATABASE_NAME = "passwordnote.db";
	public static final int DATABASE_VERSION = 1;
	public static final String WEBS_TABLE_NAME = "webs";
	public static final String ACCOUNT_TABLE_NAME = "accounts";
	
	private PasswordNoteProvideData() {}
	
	public static final class PwNoteTableData implements BaseColumns {
		
		private PwNoteTableData() {}
		
		public static final String TABLE_NAME_WEBS = "webs";
		public static final String TABLE_NAME_ACCOUNTS = "accounts";
		
		public static final Uri CONTENT_URI_WEBS = 
				Uri.parse("content://" + AUTHORITY + "/webs");
		public static final Uri CONTENT_URI_ACCOUNTS =
				Uri.parse("content://" + AUTHORITY + "/accounts");
		
		public static final String CONTENT_TYPE = 
				"vnd.android.cursor.dir/vnd.sctek.passwordnote";
		
		public static final String CONTENT_ITEM_TYPE =
				"vnd.android.cursor.item/vnd.sctek.passwordnote";
		
		public static final String WEB_NAME = "name";
		
		public static final String WEB_URL = "url";
		
		public static final String WEB_TABS = "tabs";
		
		public static final String WEB_CATEGORY = "category";
		
		public static final String WEB_DEFAULT_USER = "defaultuser";
		
		
		public static final String ACCOUNT_NAME ="user";
		
		public static final String ACCOUNT_PW = "pw";
		
		public static final String ACCOUNT_URL = "url";
		
	}

}
