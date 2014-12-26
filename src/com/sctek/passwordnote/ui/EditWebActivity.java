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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class EditWebActivity extends Activity {

	private final static String TAG = "EditWebActivity";
	private Web mWeb;
	
	private EditText nameEt;
	private EditText urlEt;
	private EditText tabsEt;
	
	private WebData mWebData;
	
	private boolean textChanged;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_web);
		
		mWeb =  Web.getInstance(this);
		
		ActionBar actionBar = this.getActionBar();
		if(actionBar != null) {
			actionBar.setCustomView(R.layout.title_bar);
			actionBar.setDisplayShowCustomEnabled(true);
			actionBar.setDisplayShowHomeEnabled(false);
			actionBar.setDisplayShowTitleEnabled(false);
			
			actionBar.show();
		}
		
		String url = getIntent().getStringExtra("url");
		getWeb(url);
		initView();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	
	public void initView() {
		
		nameEt = (EditText)findViewById(R.id.name_et);
		urlEt = (EditText)findViewById(R.id.url_et);
		tabsEt = (EditText)findViewById(R.id.tabs_et);
		
		if(mWebData != null) {
			nameEt.setText(mWebData.name);
			urlEt.setText(mWebData.url);
			tabsEt.setText(""+ mWebData.tabs);
		}
		
		textChanged = false;
		nameEt.addTextChangedListener(textChangeWatcher);
		tabsEt.addTextChangedListener(textChangeWatcher);
			
	}
	
	public void getWeb(String url) {
		if(url != null) {
			ArrayList<WebData> mWebList = mWeb.getWebs();
			for(int i = 0; i< mWebList.size(); i++)
				if(url.equals(mWebList.get(i).url))
					mWebData = mWebList.get(i);
		}
	}
	
	public void onSaveButtonClicked(View v) {
		
		if(!textChanged) {
			setResult(RESULT_CANCELED);
			finish();
			return;
		}
		
		String tabs = tabsEt.getText().toString();
		if(!isNumeric(tabs)) {
			Toast.makeText(this, R.string.input_legal_tabs, Toast.LENGTH_SHORT).show();
			return;
		}
		mWebData.name = nameEt.getText().toString();
		mWebData.tabs = Integer.parseInt(tabs);
		mWeb.updateWeb(mWebData);
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
	
	
	private TextWatcher textChangeWatcher = new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			// TODO Auto-generated method stub
			textChanged = true;
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			
		}
	};
	
}
