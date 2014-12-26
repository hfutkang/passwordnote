package com.sctek.passwordnote.ui;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sctek.passwordnote.R;
import com.sctek.passwordnote.R.id;
import com.sctek.passwordnote.R.layout;
import com.sctek.passwordnote.R.menu;
import com.sctek.passwordnote.Web;
import com.sctek.passwordnote.Web.WebData;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class NewWebActivity extends Activity {

	private Web mWeb;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_web);
		
		ActionBar actionBar = this.getActionBar();
		if(actionBar != null) {
			actionBar.setCustomView(R.layout.title_bar);
			actionBar.setDisplayShowCustomEnabled(true);
			actionBar.setDisplayShowHomeEnabled(false);
			actionBar.setDisplayShowTitleEnabled(false);
			
			actionBar.show();
		}
		
		mWeb = Web.getInstance(this);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		mWeb.setContext(this);
		super.onResume();
	}
	
	public void onSaveButtonClicked(View v) {
		
		EditText nameEt = (EditText) findViewById(R.id.name_et);
		EditText urlEt = (EditText) findViewById(R.id.url_et);
		EditText tabsEt = (EditText) findViewById(R.id.tabs_et);
		
		String name = nameEt.getText().toString();
		String url = urlEt.getText().toString();
		String tabs = tabsEt.getText().toString();
		
		if(url.length() == 0) {
			Toast.makeText(this, R.string.input_url, Toast.LENGTH_SHORT).show();
			return;
		}
		else {
			ArrayList<WebData> webList = mWeb.getWebs();
			for(int i = 0; i < webList.size(); i++) {
				if(url.equals(webList.get(i).url)) {
					Toast.makeText(this, R.string.url_exist,
							Toast.LENGTH_SHORT).show();
					return;
				}
			}
		}
		if(!isNumeric(tabs)) {
			Toast.makeText(this, R.string.input_legal_tabs, Toast.LENGTH_SHORT).show();
			return;
		}
		
		WebData wd = mWeb.new WebData();
		wd.name = name;
		wd.url = url;
		wd.tabs = Integer.valueOf(tabs);
		
		mWeb.newWeb(wd);
		
		setResult(RESULT_OK);
		finish();
	}
	
	public boolean isNumeric(String s) {
		if(s.length() == 0)
			return false;
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher macther = pattern.matcher(s);
		if(macther.matches())
			return true;
		return false;
	}
}
