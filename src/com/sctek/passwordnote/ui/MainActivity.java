// @author Bhavya Mehta
package com.sctek.passwordnote.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

import com.sctek.passwordnote.R;
import com.sctek.passwordnote.Web;
import com.sctek.passwordnote.Web.WebData;
import com.sctek.passwordnote.sortlistview.CharacterParser;
import com.sctek.passwordnote.sortlistview.IndexBarView;
import com.sctek.passwordnote.sortlistview.PinnedHeaderAdapter;
import com.sctek.passwordnote.sortlistview.PinnedHeaderListView;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

// Activity that display customized list view and search box
public class MainActivity extends Activity {
	
	private final static String TAG = "MainActivity";
	private final static int NEWWEBREQUEST = 1;
	private final static int EDITWEBRESQUEST = 2;
	// an array of countries to display in the list
	static final String[] ITEMS = new String[] {"@#$$$#@@", "你好", "我在", "East Timor", "Ecuador", "Egypt", "El Salvador", "Equatorial Guinea",
			"Eritrea", "Estonia", "Ethiopia", "Faeroe Islands", "Falkland Islands", "Fiji", "Finland", "Afghanistan",
			"Albania", "Algeria", "American Samoa", "Andorra", "Angola", "Anguilla", "Antarctica",
			"Antigua and Barbuda", "Argentina", "Armenia", "Aruba", "Australia", "Austria", "Azerbaijan", "Bahrain",
			"Bangladesh", "Barbados", "Belarus", "Belgium", "Monaco", "Mongolia", "Montserrat", "Morocco",
			"Mozambique", "Myanmar", "Namibia", "Nauru", "Nepal", "Netherlands", "Netherlands Antilles",
			"New Caledonia", "New Zealand", "Guyana", "Haiti", "Heard Island and McDonald Islands", "Honduras",
			"Hong Kong", "Hungary", "Iceland", "India", "Indonesia", "Iran", "Iraq", "Ireland", "Israel", "Italy",
			"Jamaica", "Japan", "Jordan", "Kazakhstan", "Kenya", "Kiribati", "Kuwait", "Kyrgyzstan", "Laos", "Latvia",
			"Lebanon", "Lesotho", "Liberia", "Libya", "Liechtenstein", "Lithuania", "Luxembourg", "Nicaragua", "Niger",
			"Nigeria", "Niue", "Norfolk Island", "North Korea", "Northern Marianas", "Norway", "Oman", "Pakistan",
			"Palau", "Panama", "Papua New Guinea", "Paraguay", "Peru", "Philippines", "Pitcairn Islands", "Poland",
			"Portugal", "Puerto Rico", "Qatar", "French Southern Territories", "Gabon", "Georgia", "Germany", "Ghana",
			"Gibraltar", "Greece", "Greenland", "Grenada", "Guadeloupe", "Guam", "Guatemala", "Guinea",
			"Guinea-Bissau", "Martinique", "Mauritania", "Mauritius", "Mayotte", "Mexico", "Micronesia", "Moldova",
			"Bosnia and Herzegovina", "Botswana", "Bouvet Island", "Brazil", "British Indian Ocean Territory",
			"Saint Vincent and the Grenadines", "Samoa", "San Marino", "Saudi Arabia", "Senegal", "Seychelles",
			"Sierra Leone", "Singapore", "Slovakia", "Slovenia", "Solomon Islands", "Somalia", "South Africa",
			"South Georgia and the South Sandwich Islands", "South Korea", "Spain", "Sri Lanka", "Sudan", "Suriname",
			"Svalbard and Jan Mayen", "Swaziland", "Sweden", "Switzerland", "Syria", "Taiwan", "Tajikistan",
			"Tanzania", "Thailand", "The Bahamas", "The Gambia", "Togo", "Tokelau", "Tonga", "Trinidad and Tobago",
			"Tunisia", "Turkey", "Turkmenistan", "Turks and Caicos Islands", "Tuvalu", "Uganda", "Ukraine",
			"United Arab Emirates", "United Kingdom", "United States", "United States Minor Outlying Islands",
			"Uruguay", "Uzbekistan", "Vanuatu", "Vatican City", "Venezuela", "Vietnam", "Virgin Islands",
			"Wallis and Futuna", "Western Sahara", "British Virgin Islands", "Brunei", "Bulgaria", "Burkina Faso",
			"Burundi", "Cote d'Ivoire", "Cambodia", "Cameroon", "Canada", "Cape Verde", "Cayman Islands",
			"Central African Republic", "Chad", "Chile", "China", "Reunion", "Romania", "Russia", "Rwanda",
			"Sqo Tome and Principe", "Saint Helena", "Saint Kitts and Nevis", "Saint Lucia",
			"Saint Pierre and Miquelon", "Belize", "Benin", "Bermuda", "Bhutan", "Bolivia", "Christmas Island",
			"Cocos (Keeling) Islands", "Colombia", "Comoros", "Congo", "Cook Islands", "Costa Rica", "Croatia", "Cuba",
			"Cyprus", "Czech Republic", "Democratic Republic of the Congo", "Denmark", "Djibouti", "Dominica",
			"Dominican Republic", "Former Yugoslav Republic of Macedonia", "France", "French Guiana",
			"French Polynesia", "Macau", "Madagascar", "Malawi", "Malaysia", "Maldives", "Mali", "Malta",
			"Marshall Islands", "Yemen", "Yugoslavia", "Zambia", "Zimbabwe" };

	// unsorted list items
//	ArrayList<String> mItems;

	// array list to store section positions
	ArrayList<Integer> mListSectionPos;

	// array list to store listView data
	ArrayList<WebData> mListItems;

	// custom list view with pinned header
	PinnedHeaderListView mListView;

	// custom adapter
	PinnedHeaderAdapter mAdaptor;
	
	CharacterParser characterParser;

	// search box
	EditText mSearchView;

	// loading view
	ProgressBar mLoadingView;

	// empty view
	TextView mEmptyView;
	
	private Web mWeb;
	
	private boolean AsyncTaskRunning;
	
	private boolean needLoadData;
	
	private Button addButton;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 

		// UI elements
		setupViews();
		
		needLoadData = true;

		// Array to ArrayList
//		mItems = new ArrayList<String>(Arrays.asList(ITEMS));
		mListSectionPos = new ArrayList<Integer>();
		mListItems = new ArrayList<WebData>();
		mWeb = Web.getInstance(this);
		
		characterParser = CharacterParser.getInstance();
		
		new Poplulate().execute();

		// for handling configuration change
//		if (savedInstanceState != null) {
//			mListItems = savedInstanceState.getStringArrayList("mListItems");
//			mListSectionPos = savedInstanceState.getIntegerArrayList("mListSectionPos");
//
//			if (mListItems != null && mListItems.size() > 0 && mListSectionPos != null && mListSectionPos.size() > 0) {
//				setListAdaptor();
//			}
//
//			String constraint = savedInstanceState.getString("constraint");
//			if (constraint != null && constraint.length() > 0) {
//				mSearchView.setText(constraint);
//				setIndexBarViewVisibility(constraint);
//			}
//		} else {
//			new Poplulate().execute(mItems);
//		}
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		mWeb.setContext(this);
		super.onResume();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		Log.e(TAG, "onActivityResult");
		if(resultCode == RESULT_OK) {
			new Poplulate().execute(mWeb.getWebs());
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	public void onAddButtonClicked(View v){
		
		Intent intent = new Intent(MainActivity.this, NewWebActivity.class);
		startActivityForResult(intent, NEWWEBREQUEST);
	}

	private void setupViews() {
		setContentView(R.layout.main_act);
		
		ActionBar actionBar = this.getActionBar();
		if(actionBar != null) {
			actionBar.setCustomView(R.layout.title_bar);
			actionBar.setDisplayShowCustomEnabled(true);
			actionBar.setDisplayShowHomeEnabled(false);
			actionBar.setDisplayShowTitleEnabled(false);
			
			actionBar.show();
		}
		
		mSearchView = (EditText) findViewById(R.id.search_view);
		mLoadingView = (ProgressBar) findViewById(R.id.loading_view);
		mListView = (PinnedHeaderListView) findViewById(R.id.list_view);
		mEmptyView = (TextView) findViewById(R.id.empty_view);
		addButton = (Button) findViewById(R.id.add_bt);
		
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainActivity.this, LoginActivity.class);
				intent.putExtra("url", mListItems.get(position).url);
				intent.putExtra("tabs", mListItems.get(position).tabs);
				startActivity(intent);
			}
		});
		
		mListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				showPopupMenu(view, position);
				return true;
			}
		});
		
	}

	// I encountered an interesting problem with a TextWatcher listening for
	// changes in an EditText.
	// The afterTextChanged method was called, each time, the device orientation
	// changed.
	// An answer on Stackoverflow let me understand what was happening: Android
	// recreates the activity, and
	// the automatic restoration of the state of the input fields, is happening
	// after onCreate had finished,
	// where the TextWatcher was added as a TextChangedListener.The solution to
	// the problem consisted in adding
	// the TextWatcher in onPostCreate, which is called after restoration has
	// taken place
	//
	// http://stackoverflow.com/questions/6028218/android-retain-callback-state-after-configuration-change/6029070#6029070
	// http://stackoverflow.com/questions/5151095/textwatcher-called-even-if-text-is-set-before-adding-the-watcher
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		mSearchView.addTextChangedListener(filterTextWatcher);
		super.onPostCreate(savedInstanceState);
	}

	private void setListAdaptor() {
		
		// create instance of PinnedHeaderAdapter and set adapter to list view
		mAdaptor = new PinnedHeaderAdapter(this, mListItems, mListSectionPos);
		mListView.setAdapter(mAdaptor);

		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

		// set header view
		View pinnedHeaderView = inflater.inflate(R.layout.section_row_view, mListView, false);
		mListView.setPinnedHeaderView(pinnedHeaderView);

		// set index bar view
		IndexBarView indexBarView = (IndexBarView) inflater.inflate(R.layout.index_bar_view, mListView, false);
		indexBarView.setData(mListView, mListItems, mListSectionPos);
		mListView.setIndexBarView(indexBarView);

		// set preview text view
		View previewTextView = inflater.inflate(R.layout.preview_view, mListView, false);
		mListView.setPreviewView(previewTextView);

		// for configure pinned header view on scroll change
		mListView.setOnScrollListener(mAdaptor);
	}

	private TextWatcher filterTextWatcher = new TextWatcher() {
		public void afterTextChanged(Editable s) {
			String str = s.toString();
			if (mAdaptor != null && str != null && !AsyncTaskRunning)
				mAdaptor.getFilter().filter(str);
		}

		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		public void onTextChanged(CharSequence s, int start, int before, int count) {

		}
	};

	public class ListFilter extends Filter {
		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			// NOTE: this function is *always* called from a background thread,
			// and
			// not the UI thread.
			String constraintStr = constraint.toString().toLowerCase(Locale.getDefault());
			FilterResults result = new FilterResults();

			if (constraint != null && constraint.toString().length() > 0) {
				ArrayList<WebData> filterItems = new ArrayList<WebData>();

				synchronized (this) {
					for (WebData item : mWeb.getWebs()) {
						String item_lable = item.name.length() != 0?item.name:item.url;
						String itemPy = characterParser.getSelling(item_lable);
						if (itemPy.toLowerCase(Locale.getDefault()).startsWith(constraintStr)) {
							filterItems.add(item);
						}
					}
					result.count = filterItems.size();
					result.values = filterItems;
				}
			} else {
				synchronized (this) {
					result.count = mWeb.getWebs().size();
					result.values = mWeb.getWebs();
				}
			}
			return result;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {
			ArrayList<WebData> filtered = (ArrayList<WebData>) results.values;
			setIndexBarViewVisibility(constraint.toString());
			// sort array and extract sections in background Thread
			new Poplulate().execute(filtered);
		}

	}

	private void setIndexBarViewVisibility(String constraint) {
		// hide index bar for search results
		if (constraint != null && constraint.length() > 0) {
			mListView.setIndexBarVisibility(false);
		} else {
			mListView.setIndexBarVisibility(true);
		}
	}

	// sort array and extract sections in background Thread here we use
	// AsyncTask
	private class Poplulate extends AsyncTask<ArrayList<WebData>, Void, Void> {

		private void showLoading(View contentView, View loadingView, View emptyView) {
			contentView.setVisibility(View.GONE);
			loadingView.setVisibility(View.VISIBLE);
			emptyView.setVisibility(View.GONE);
		}

		private void showContent(View contentView, View loadingView, View emptyView) {
			contentView.setVisibility(View.VISIBLE);
			loadingView.setVisibility(View.GONE);
			emptyView.setVisibility(View.GONE);
		}

		private void showEmptyText(View contentView, View loadingView, View emptyView) {
			contentView.setVisibility(View.GONE);
			loadingView.setVisibility(View.GONE);
			emptyView.setVisibility(View.VISIBLE);
		}

		@Override
		protected void onPreExecute() {
			// show loading indicator
			AsyncTaskRunning = true;
			showLoading(mListView, mLoadingView, mEmptyView);
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(ArrayList<WebData>... params) {
			Log.e(TAG, "doInBackground");
			mListItems.clear();
			mListSectionPos.clear();
			
			ArrayList<WebData> items = null;
			if(needLoadData) {
				needLoadData = false;
				mWeb.loadWebData();
				mWeb.loadAccountData();
				
				items = mWeb.getWebs();
			}
			else {
				items = params[0];
			}
			
			
			if (items.size() > 0) {

				// NOT forget to sort array
				Collections.sort(items, new SortIgnoreCase());

				String prev_section = "";
				int current_section_index = 0;
				for (WebData current_item : items) {
					String current_item_lable = current_item.name.length() != 0?
							current_item.name:current_item.url;
					String current_item_py = characterParser.getSelling(current_item_lable);
					String current_section = current_item_py.substring(0, 1).toUpperCase(Locale.getDefault());
					WebData section_data = mWeb.new WebData();
					section_data.name = current_section;

					if (!prev_section.equals(current_section)) {
						mListItems.add(section_data);
						mListItems.add(current_item);
						// array list of section positions
						mListSectionPos.add(current_section_index);
						current_section_index += 2;
						prev_section = current_section;
					} else {
						mListItems.add(current_item);
						current_section_index++;
					}
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			
			if (!isCancelled()) {
				if (mListItems.size() <= 0) {
					showEmptyText(mListView, mLoadingView, mEmptyView);
				} else {
					setListAdaptor();
					showContent(mListView, mLoadingView, mEmptyView);
				}
			}
			AsyncTaskRunning = false;
			super.onPostExecute(result);
		}
	}

	public class SortIgnoreCase implements Comparator<WebData> {
		public int compare(WebData w1, WebData w2) {
			
			String s1 = w1.name.length() != 0?w1.name:w1.url;
			String s2 = w2.name.length() != 0?w2.name:w2.url;
			
			String str1 = characterParser.getSelling(s1);
			String str2 = characterParser.getSelling(s2);
			return str1.compareToIgnoreCase(str2);
		}
	}
	
	public void showPopupMenu(View v, final int position) {
		
		final PopupMenu popup = new PopupMenu(MainActivity.this, v);
		
		popup.getMenuInflater().inflate(R.menu.web_operation_menu, popup.getMenu());
		
		popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				switch (item.getItemId()) {
				case R.id.edit:
					Intent intent = new Intent(MainActivity.this, EditWebActivity.class);
					intent.putExtra("url", mListItems.get(position).url);
					startActivityForResult(intent, EDITWEBRESQUEST);
					break;
					
				case R.id.delete:
					showDeleteDialog(position);
					break;
				default:
					break;
				}
				return true;
			}
		});
		popup.show();
	}
	
	public void showDeleteDialog(final int  position) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.delete_selected_item);
		
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				mWeb.deleteWeb(mListItems.get(position));
				new Poplulate().execute(mWeb.getWebs());
			}
		});
		
		builder.create().show();
		
	}

//	@Override
//	protected void onSaveInstanceState(Bundle outState) {
//		if (mListItems != null && mListItems.size() > 0) {
//			outState.putStringArrayList("mListItems", mListItems);
//		}
//		if (mListSectionPos != null && mListSectionPos.size() > 0) {
//			outState.putIntegerArrayList("mListSectionPos", mListSectionPos);
//		}
//		String searchText = mSearchView.getText().toString();
//		if (searchText != null && searchText.length() > 0) {
//			outState.putString("constraint", searchText);
//		}
//		super.onSaveInstanceState(outState);
//	}
}
