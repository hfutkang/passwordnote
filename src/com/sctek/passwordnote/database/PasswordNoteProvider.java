package com.sctek.passwordnote.database;

import com.sctek.passwordnote.database.PasswordNoteProvideData.PwNoteTableData;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class PasswordNoteProvider extends ContentProvider {
	
	final String TAG = "PasswordNoteProvider";
	
	private static final UriMatcher mUriMatcher;
	private static final int THE_WHOLE_WEBS_TABLE_URI = 1;
	private static final int SINGLE_WEB_URI = 2;
	private static final int THE_WHOLE_ACOUNTS_TABLE_URI = 3;
	private static final int SINGLE_ACCOUNT_URI = 4;
	
	private PasswordNoteDatabaseHelper mDBHelper;
	
	static
	{
		mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		mUriMatcher.addURI(PasswordNoteProvideData.AUTHORITY, "webs", 
							THE_WHOLE_WEBS_TABLE_URI);
		mUriMatcher.addURI(PasswordNoteProvideData.AUTHORITY, "webs/#", 
							SINGLE_WEB_URI);
		
		mUriMatcher.addURI(PasswordNoteProvideData.AUTHORITY, "accounts", 
							THE_WHOLE_ACOUNTS_TABLE_URI);
		mUriMatcher.addURI(PasswordNoteProvideData.AUTHORITY, "accounts/#", 
							SINGLE_ACCOUNT_URI);
	}

	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		Log.e(TAG, "onCreate");
		
		mDBHelper = new PasswordNoteDatabaseHelper(getContext());
		
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// TODO Auto-generated method stub
		Log.e(TAG, "query");
		
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		SQLiteDatabase database = mDBHelper.getReadableDatabase();
		
		switch (mUriMatcher.match(uri)) {
		
				case THE_WHOLE_WEBS_TABLE_URI:
					queryBuilder.setTables(PwNoteTableData.TABLE_NAME_WEBS);
					break;
					
				case SINGLE_WEB_URI:
					queryBuilder.setTables(PwNoteTableData.TABLE_NAME_WEBS);
					queryBuilder.appendWhere(PwNoteTableData._ID + "="
							+ uri.getPathSegments().get(1));
					break;
					
				case THE_WHOLE_ACOUNTS_TABLE_URI:
					queryBuilder.setTables(PwNoteTableData.TABLE_NAME_ACCOUNTS);
					break;
					
				case SINGLE_ACCOUNT_URI:
					queryBuilder.setTables(PwNoteTableData.TABLE_NAME_ACCOUNTS);
					queryBuilder.appendWhere(PwNoteTableData._ID + "="
							+ uri.getPathSegments().get(1));
					break;
				default:
					throw new IllegalArgumentException("Unknow URI " + uri);
			
		}
		
		Cursor cursor = queryBuilder.query(database, projection, selection, 
				selectionArgs, null, null, sortOrder);

		cursor.setNotificationUri(getContext().getContentResolver(), uri);

		return cursor;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		Log.e(TAG, "getType");
		
		switch (mUriMatcher.match(uri)) {
		
			case THE_WHOLE_WEBS_TABLE_URI:
				return PwNoteTableData.CONTENT_TYPE;
				
			case SINGLE_WEB_URI:
				return PwNoteTableData.CONTENT_ITEM_TYPE;
				
			case THE_WHOLE_ACOUNTS_TABLE_URI:
				return PwNoteTableData.CONTENT_TYPE;
				
			case SINGLE_ACCOUNT_URI:
				return PwNoteTableData.CONTENT_ITEM_TYPE;
				
			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
				
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		Log.e(TAG, "insert");
		
		if(mUriMatcher.match(uri) != THE_WHOLE_WEBS_TABLE_URI
				&&mUriMatcher.match(uri) != THE_WHOLE_ACOUNTS_TABLE_URI) {
			throw new IllegalArgumentException("Unknow URI " + uri);
		}
		
		SQLiteDatabase database = mDBHelper.getWritableDatabase();
		long rowId;
		if(mUriMatcher.match(uri) == THE_WHOLE_WEBS_TABLE_URI)
			rowId = database.insert(PwNoteTableData.TABLE_NAME_WEBS, 
					PwNoteTableData._ID, values);
		else
			rowId = database.insert(PwNoteTableData.TABLE_NAME_ACCOUNTS,
					PwNoteTableData._ID, values);
		
		if(rowId > 0) {
			
			Uri insertUri = ContentUris.withAppendedId(uri, rowId);
			getContext().getContentResolver().notifyChange(insertUri, null);
			
			return insertUri;
		}
		
		throw new SQLException("Failded to insert row into " + uri);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		Log.e(TAG, "delete");
		
		SQLiteDatabase database = mDBHelper.getWritableDatabase();
		int count;
		
		switch (mUriMatcher.match(uri)){
		
				case THE_WHOLE_WEBS_TABLE_URI:
					count = database.delete(PwNoteTableData.TABLE_NAME_WEBS, 
							selection, selectionArgs);
					break;
					
				case SINGLE_WEB_URI:
					String rowId = uri.getPathSegments().get(1);
					count = database.delete(PwNoteTableData.TABLE_NAME_WEBS, 
							PwNoteTableData._ID + "=" + rowId +(!TextUtils.isEmpty(selection)? 
							"AND (" + selection + ')' : ""), selectionArgs);
					break;
					
				case THE_WHOLE_ACOUNTS_TABLE_URI:
					count = database.delete(PwNoteTableData.TABLE_NAME_ACCOUNTS,
							selection, selectionArgs);
					break;
				case SINGLE_ACCOUNT_URI:
					String rowId1 = uri.getPathSegments().get(1);
					count = database.delete(PwNoteTableData.TABLE_NAME_ACCOUNTS,
							PwNoteTableData._ID + "=" + rowId1 + (!TextUtils.isEmpty(selection)?
									"AND (" + selection + ')' : ""), selectionArgs);
					break;
				default:
					throw new IllegalArgumentException("Unkown URI" + uri);
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
		
		return count;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		Log.e(TAG, "update");
		
		SQLiteDatabase database = mDBHelper.getWritableDatabase();
		int count;
		
		switch (mUriMatcher.match(uri)){
		
				case THE_WHOLE_WEBS_TABLE_URI:
					count = database.update(PwNoteTableData.TABLE_NAME_WEBS, 
							values, selection, selectionArgs);
					break;
					
				case SINGLE_WEB_URI:
					String rowId = uri.getPathSegments().get(1);
					
					count = database.update(PwNoteTableData.TABLE_NAME_WEBS, 
							values, PwNoteTableData._ID + "=" + rowId + (!TextUtils.isEmpty(selection) ? 
							"AND (" + selection + ')' : ""), selectionArgs);
					break;
					
				case THE_WHOLE_ACOUNTS_TABLE_URI:
					count = database.update(PwNoteTableData.TABLE_NAME_ACCOUNTS,
							values, selection, selectionArgs);
					break;
					
				case SINGLE_ACCOUNT_URI:
					String rowId1 = uri.getPathSegments().get(1);
					count = database.update(PwNoteTableData.TABLE_NAME_ACCOUNTS,
							values, PwNoteTableData._ID + "=" + rowId1 + (!TextUtils.isEmpty(selection) ? 
									"AND (" + selection + ')' : ""), selectionArgs);
					break;
					
				default:
					throw new IllegalArgumentException("Unkonw Uri" + uri);
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
		
		return count;
	}
	
}
