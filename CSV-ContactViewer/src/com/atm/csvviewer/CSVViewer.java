package com.atm.csvviewer;

import java.util.Vector;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.atm.csvviewer.data.CSVListAdapter;
import com.atm.csvviewer.data.CSVManager;
import com.atm.csvviewer.data.CSVRowItem;
import com.atm.csvviewer.util.Constants;
import com.atm.csvviewer.util.Utils;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class CSVViewer extends ActionBarActivity implements OnItemLongClickListener , OnItemClickListener{

	CSVListAdapter adapter;
	CSVManager manager;
	String selectedContact = "";
	LayoutInflater inflater;
	int selectedPosition;
	boolean listModified = false;
	public static final int SELECTION_MODE_SINGLE = 0;
	public static final int SELECTION_MODE_MULTIPLE = 1;
	int selectionMode = SELECTION_MODE_SINGLE;
	ListView listView;
	EditText searchField;
	boolean bNewFileCreated;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		inflater = LayoutInflater.from(this);
		
		listView = (ListView)findViewById(R.id.csv_list);
		listView.setOnItemClickListener(this);
		searchField = (EditText) findViewById(R.id.search_field);
		searchField.setEnabled(false);
		searchField.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// if(adapter.getCount() >0)
				adapter.updateAdapter(s.toString().toLowerCase());
				if (adapter.getCount() == 0) {
					TextView empty = (TextView) findViewById(android.R.id.empty);
					empty.setText("No match found.");
				}
			}
		});

		loadScreen();

		GAdManager.initInterstitialAd(this);
		AdView adView = (AdView)findViewById(R.id.ad_banner);
//	    adView.setAdSize(AdSize.BANNER);
//	    adView.setAdUnitId("ca-app-pub-3828424391276486/4967896250");
	    
	    AdRequest adrequest = new AdRequest.Builder().build();
	    adView.loadAd(adrequest);
	    adView.setAdListener(new AdListener() {
	    	@Override
	    	public void onAdFailedToLoad(int errorCode) {
	    		// TODO Auto-generated method stub
	    		super.onAdFailedToLoad(errorCode);
	    		Log.d("", "Failed to load "+errorCode);
	    	}
		});
	    
	    Button create = (Button) findViewById(R.id.create_csv);
	    create.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//showDialog(id);
				loadItemsFromCSV(null);
				bNewFileCreated = true;
				findViewById(R.id.empty_layout).setVisibility(View.GONE);
			}
		});
		
	}


	private void loadScreen() {
		Vector<CSVRowItem> items = null;
		if(adapter != null) items = adapter.getAllItems();
		if(selectionMode == SELECTION_MODE_SINGLE){
			adapter = new CSVListAdapter(this, android.R.layout.simple_list_item_1);
			//android.R.layout.simple_list_item_multiple_choice
		}else{
			adapter = new CSVListAdapter(this, android.R.layout.simple_list_item_multiple_choice);
		}
		if(items == null){
			String storedPath = getStoredPath();
			if(storedPath == null){
				searchField.setVisibility(View.GONE);
			}else{
				loadItemsFromCSV(storedPath);
				findViewById(R.id.empty_layout).setVisibility(View.GONE);
			}
		}else{
			adapter.setCSVItems(items);
			listView.setAdapter(adapter);
		}
		
		listView.setTextFilterEnabled(true);
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
	}


	private String getStoredPath() {
		SharedPreferences pref = getSharedPreferences(Constants.SETTINGS,
				MODE_PRIVATE);
		return pref.getString(Constants.STORED_PATH, null);
	}

	

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.clear();
		if(selectionMode == SELECTION_MODE_SINGLE){
			if(adapter.getCount() == 0){
				getMenuInflater().inflate(R.menu.import_menu, menu);
			}else{
				
				getMenuInflater().inflate(R.menu.single_selection_menu, menu);
			}
//			menu.add(0, 0, 0, "Import CSV");
//			if(adapter.getCount() > 0){
//				menu.add(0, 1, 1, "Select Multiple");
//				menu.add(0, 7, 7, "New Entry");
//				menu.add(0, 8, 8, "Save List");
//			}
		}else{
			getMenuInflater().inflate(R.menu.multi_selection_menu, menu);
//			menu.add(0, 2, 2, "Select All");
//			menu.add(0, 3, 3, "Deselect All");
//			menu.add(0, 4, 4, "Save Contact");
//			menu.add(0, 5, 5, "Send SMS");
//			menu.add(0, 6, 6, "Send Email");
		}
		return super.onPrepareOptionsMenu(menu);
	}

	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		//loadItemsFromCSV(getStoredPath());
		switch (item.getItemId()) {
		case R.id.action_import:
			Intent browserIntent = new Intent(this, FileExplorerActivity.class);
			startActivityForResult(browserIntent, Constants.REQUEST_FILE_PATH);
			break;
		case R.id.select_multiple:
			selectionMode = SELECTION_MODE_MULTIPLE;
			loadScreen();
			break;
		case R.id.select_all:
			// select all
			for (int i = 0; i < adapter.getCount(); i++) {
				listView.setItemChecked(i, true);
				((CSVRowItem)((CSVListAdapter)adapter).getCSVItem(i)).setChecked(true);
			}
			System.out.println("Select All"+((CSVListAdapter)adapter).printAll());
			break;
		case R.id.deselect_all:
			// deselect all
			((CSVListAdapter)adapter).deSelectAll();
			for (int i = 0; i < adapter.getCount(); i++) {
				listView.setItemChecked(i, false);
				((CSVRowItem)((CSVListAdapter)adapter).getCSVItem(i)).setChecked(false);
			}
			System.out.println("Deselect all"+((CSVListAdapter)adapter).printAll());
			break;
		case R.id.save_contact:
			// Save contact
			((CSVListAdapter)adapter).saveSelectedContact();
			System.out.println("Save contact "+((CSVListAdapter)adapter).printAll());
			break;
		case R.id.send_sms:
			// Send SMS
			((CSVListAdapter)adapter).sendSMS();
			System.out.println("Send SMS "+((CSVListAdapter)adapter).printAll());
			break;
		case R.id.send_email:
			// SEND email
			((CSVListAdapter)adapter).sendEmail();
			System.out.println("Send email "+((CSVListAdapter)adapter).printAll());
			break;
		case R.id.new_entry:
			//New Entry
			showDialog(Constants.DIALOG_NEW_ENTRY);
			break;
		case R.id.save_list:
			//Save List
			if(listModified){
				// save the file (if needed)
				manager.exportData(getStoredPath(), adapter.getAllItems());
			}
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(requestCode == Constants.REQUEST_FILE_PATH && resultCode == RESULT_OK){
			if(data != null){
				String path = data.getStringExtra(Constants.SELECTED_FILE_PATH);
				loadItemsFromCSV(path);
				findViewById(R.id.empty_layout).setVisibility(View.GONE);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	private void loadItemsFromCSV(String path) {
		if (manager == null)
			manager = new CSVManager(this);
		Vector<CSVRowItem> items;
		if(path == null){
			items = manager.importData(getResources().openRawResource(R.raw.test));
		}else{
			items = manager.importData(path);
		}
		if (items == null)
			return;
		((EditText) findViewById(R.id.search_field)).setEnabled(true);
		// System.out.println("Items "+items);
		adapter.setCSVItems(items);
		listView.setAdapter(adapter);
	}


	

//	@Override
//	protected void onListItemClick(ListView l, View v, int position, long id) {
//		// TODO Auto-generated method stub
//		super.onListItemClick(l, v, position, id);
//		selectedPosition = position;
//		if(selectionMode == SELECTION_MODE_SINGLE){
//			showDialog(Constants.DIALOG_CONTACT_DETAILS);
//		}else{
//			((CSVListAdapter)adapter).toggleItem(position);
//		}
//	}
	
	

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		// TODO Auto-generated method stub
		switch (id) {
		case Constants.DIALOG_CONTACT_DETAILS:
		case Constants.DIALOG_EDIT_ENTRY:
		case Constants.DIALOG_NEW_ENTRY:
			final String[] columnValues = ((CSVRowItem) adapter
					.getCSVItem(selectedPosition)).getColumnValues();
			((ContactDetailsDialog) dialog).setMode(id);
			((ContactDetailsDialog) dialog).updateDialog(manager.getTitle()
					.getColumnValues(), columnValues);
			break;
		/*case Constants.DIALOG_EDIT_ENTRY:
			final String[] columnValues1 = ((CSVRowItem) adapter
					.getCSVItem(selectedPosition)).getColumnValues();
			((ContactDetailsDialog) dialog).updateDialog(manager.getTitle()
					.getColumnValues(), columnValues1);
			break;*/
		default:
			super.onPrepareDialog(id, dialog);
			break;
		}

	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog d = null;
		switch (id) {
		case Constants.DIALOG_FILE_READ_ERROR:
			d = createAlert("Alert", "Error while reading selected csv file");
			break;
		case Constants.DIALOG_UNSUPPORTED_FILE:
			d = createAlert("Alert", "Unsupported file selected.");
			break;
		case Constants.DIALOG_NO_FILE_FOUND:
			d = createAlert("Alert", "No csv file found");
			break;
		case Constants.DIALOG_SMS_CALL_LIST:
			AlertDialog.Builder callListbuilder = new AlertDialog.Builder(this);
			callListbuilder.setTitle("Select");
			callListbuilder.setItems(new String[] { "Message", "Call" },
					listActionListner);
			d = callListbuilder.create();
			break;
		case Constants.DIALOG_CONTACT_DETAILS:
		case Constants.DIALOG_NEW_ENTRY:
		case Constants.DIALOG_EDIT_ENTRY:
			final String[] columnValues = ((CSVRowItem) adapter
					.getCSVItem(selectedPosition)).getColumnValues();
			ContactDetailsDialog dialog = new ContactDetailsDialog(this, id, adapter);
			dialog.updateDialog(manager.getTitle().getColumnValues(),
					columnValues);
			d = dialog;
			break;
			/*case Constants.DIALOG_NEW_ENTRY:
			AlertDialog.Builder newEntryBuilder = new AlertDialog.Builder(this);
			newEntryBuilder.setTitle("Create New Entry");
			final ScrollView scroll = (ScrollView)inflater.inflate(R.layout.new_entry, null);
			TableLayout table = (TableLayout)scroll.findViewById(R.id.new_entry_table);
			for (int i = 0; i < manager.getTitle().getColumnValues().length; i++) {
				TableRow row = new TableRow(this);
				TextView tv = new TextView(this);
				tv.setText(manager.getTitle().getColumnValues()[i]);
				row.addView(tv);
				EditText et = new EditText(this);
				//et.setText(manager.getTitle().getColumnValues()[i]);
				row.addView(et);
				table.addView(row);
			}
			newEntryBuilder.setView(scroll);
			
			newEntryBuilder.setPositiveButton("Save", new OnClickListener(){

				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// to be saved
					String[] values = new String[3];
					values[0] = ((EditText)scroll.findViewById(R.id.new_entry_name)).getText().toString();
					values[1] = ((EditText)scroll.findViewById(R.id.new_entry_phone)).getText().toString();
					values[2] = ((EditText)scroll.findViewById(R.id.new_entry_email)).getText().toString();
					CSVRowItem item = new CSVRowItem(values);
					adapter.addCSVItem(item);
					adapter.notifyDataSetChanged();
					//getListView().invalidate();
					listModified = true;
					arg0.cancel();
				}});
			
			newEntryBuilder.setNegativeButton("Cancel", new OnClickListener(){

				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					arg0.cancel();
				}});
			
			d = newEntryBuilder.create();
			break;
		case Constants.DIALOG_EDIT_ENTRY:
			AlertDialog.Builder editEntryBuilder = new AlertDialog.Builder(this);
			editEntryBuilder.setTitle("Edit Entry");
			final ScrollView scroll1 = (ScrollView)inflater.inflate(R.layout.new_entry, null);
			TableLayout table1 = (TableLayout)scroll1.findViewById(R.id.new_entry_table);
			table1.removeAllViews();
			for (int i = 0; i < manager.getTitle().getColumnValues().length; i++) {
				TableRow row = new TableRow(this);
				TextView tv = new TextView(this);
				tv.setText(manager.getTitle().getColumnValues()[i]);
				row.addView(tv);
				EditText et = new EditText(this);
				System.out.println("SelectedIndex "+selectedPosition);
				System.out.println("index "+i);
				String str = "";
				if(adapter.getCSVItem(selectedPosition).getColumnValues().length > i){
					str  = adapter.getCSVItem(selectedPosition).getColumnValues()[i];
				}
				System.out.println("selected csv item  "+adapter.getCSVItem(selectedPosition));
				et.setText(str);
				row.addView(et);
				table1.addView(row);
				System.out.println("Added "+i);
			}
			editEntryBuilder.setView(scroll1);
			
			editEntryBuilder.setPositiveButton("Save", new OnClickListener(){

				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// to be saved
					String[] values = new String[4];
					values[0] = ((EditText)scroll1.findViewById(R.id.new_entry_name)).getText().toString();
					values[1] = ((EditText)scroll1.findViewById(R.id.new_entry_phone)).getText().toString();
					values[2] = ((EditText)scroll1.findViewById(R.id.new_entry_email)).getText().toString();
					values[2] = ((EditText)scroll1.findViewById(R.id.new_entry_remark)).getText().toString();
					CSVRowItem item = new CSVRowItem(values);
					adapter.updateCSVItem(item, selectedPosition);
					adapter.notifyDataSetChanged();
					//getListView().invalidate();
					listModified = true;
					arg0.cancel();
				}});
			
			editEntryBuilder.setNegativeButton("Cancel", new OnClickListener(){

				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					arg0.cancel();
				}});
			
			d = editEntryBuilder.create();
			break;*/
		case Constants.DIALOG_FILE_NAME:
			d = createFileNameDialog();
			break;
		default:
			d = super.onCreateDialog(id);
			break;
		}
		return d;
	}

	private Dialog createFileNameDialog() {
		AlertDialog.Builder newEntryBuilder = new AlertDialog.Builder(this);
		newEntryBuilder.setTitle("Save File");
		final LinearLayout layout = (LinearLayout)inflater.inflate(R.layout.unsaved_dialog, null);

		newEntryBuilder.setView(layout);
		
		newEntryBuilder.setPositiveButton("Save", new OnClickListener(){

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				EditText tv = (EditText)layout.findViewById(R.id.edit_filename);
				String fileName = tv.getText().toString();
				if(!TextUtils.isEmpty(fileName)){
					if(Environment.isExternalStorageEmulated()){
						String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+fileName+".csv";
						manager.exportData(path, adapter.getAllItems());
						manager.saveFilePath(path);
						bNewFileCreated = false;
					}
				}else{
					showDialog(Constants.DIALOG_FILE_NAME);
				}
			}});
		
		newEntryBuilder.setNegativeButton("Cancel", new OnClickListener(){

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				arg0.cancel();
			}});
		
		return newEntryBuilder.create();
	}


	public Dialog createAlert(String title, String msg) {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle(title).setMessage(msg).setCancelable(false)
				.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		return alert.create();
	}

	OnClickListener listActionListner = new OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case 0:
				Uri smsUri = Uri.parse("smsto:" + selectedContact);
				Intent smsIntent = new Intent(Intent.ACTION_SENDTO, smsUri);
				// smsIntent.setType("vnd.android-dir/mms-sms");
				startActivity(smsIntent);
				break;
			case 1:
				Intent intent = new Intent(Intent.ACTION_CALL);
				intent.setData(Uri.parse("tel:" + selectedContact));
				startActivity(intent);
				break;
			default:
				break;
			}
		}

	};

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		outState.putInt(Constants.SELECTED_INDEX, selectedPosition);
		outState.putString(Constants.SELECTED_CONTACT, selectedContact);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle state) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(state);
		selectedPosition = state.getInt(Constants.SELECTED_INDEX, 0);
		selectedContact = state.getString(Constants.SELECTED_CONTACT);
	}

	View.OnClickListener callListner = new View.OnClickListener() {

		@Override
		public void onClick(View v) {

			showDialog(Constants.DIALOG_SMS_CALL_LIST);
			selectedContact = ((TextView) v).getText().toString();
		}
	};

	View.OnClickListener emailListner = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// System.out.println("mail me "+((TextView)v).getText());
			Utils
					.sendEmail(v.getContext(), ((TextView) v).getText()
							.toString());
		}
	};

	public View.OnClickListener getCallListner() {
		return callListner;
	}

	public View.OnClickListener getEmailListner() {
		return emailListner;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == KeyEvent.KEYCODE_BACK){
			if(listModified){
				if(bNewFileCreated){
					showDialog(Constants.DIALOG_FILE_NAME);
					bNewFileCreated = false;
					return false;
				}else{
				// save the file (if needed)
				manager.exportData(getStoredPath(), adapter.getAllItems());
				}
			}
			if(selectionMode == SELECTION_MODE_MULTIPLE){
				selectionMode = SELECTION_MODE_SINGLE;
				loadScreen();
				return false;
			}
			else if(GAdManager.displayInterstitial()){
				
			}
		}
		
		return super.onKeyDown(keyCode, event);
	}


	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		selectedPosition = position;
		if(selectionMode == SELECTION_MODE_SINGLE){
			showDialog(Constants.DIALOG_EDIT_ENTRY);
		}else{
			((CSVListAdapter)adapter).toggleItem(position);
		}
		return false;
	}


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		
		selectedPosition = position;
		if(selectionMode == SELECTION_MODE_SINGLE){
			showDialog(Constants.DIALOG_CONTACT_DETAILS);
		}else{
			((CSVListAdapter)adapter).toggleItem(position);
		}
		
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		((CSVApplication)getApplication()).startScreen(this);
	}
	
//	protected void onListItemClick(ListView l, View v, int position, long id) {
//		// TODO Auto-generated method stub
//		super.onListItemClick(l, v, position, id);
//		selectedPosition = position;
//		if(selectionMode == SELECTION_MODE_SINGLE){
//			showDialog(Constants.DIALOG_CONTACT_DETAILS);
//		}else{
//			((CSVListAdapter)adapter).toggleItem(position);
//		}
//	}
}