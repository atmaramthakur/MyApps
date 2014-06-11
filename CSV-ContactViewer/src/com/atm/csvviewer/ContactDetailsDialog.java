package com.atm.csvviewer;

import android.app.Dialog;
import android.content.Intent;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.atm.csvviewer.data.CSVListAdapter;
import com.atm.csvviewer.data.CSVRowItem;
import com.atm.csvviewer.util.Constants;
public class ContactDetailsDialog extends Dialog{

	LayoutInflater inflater ;
	CSVViewer csvViewer;
	String selectedContact;
	int mode ;
	CSVListAdapter adapter;
	//StatusActivity context;
	public ContactDetailsDialog(final CSVViewer csvViewer, int mode, CSVListAdapter adapter) {
		super(csvViewer);
		inflater = LayoutInflater.from(csvViewer);
		this.csvViewer = csvViewer;
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		this.mode = mode;
		this.adapter = adapter;
		//setTitle(R.layout.dialog_title);
		//setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, android.R.drawable.btn_plus);
		setContentView(R.layout.csv_details);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.dialog_title);
		Button cancelButton = (Button)findViewById(R.id.csv_details_cancel);
		if(mode == Constants.DIALOG_CONTACT_DETAILS){
			cancelButton.setText("Edit");
		}
		cancelButton.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				manageNegetiveBtn();
			}});
		
		
	}
	
	protected void manageNegetiveBtn() {
		if(mode == Constants.DIALOG_CONTACT_DETAILS){
			mode = Constants.DIALOG_EDIT_ENTRY;
			CSVRowItem item = (CSVRowItem) adapter.getCSVItem(csvViewer.selectedPosition);
			updateDialog(csvViewer.manager.getTitle().getColumnValues(), item.getColumnValues());
		}else {
			cancel();
		}
	}

	public void setMode(int mode) {
		this.mode = mode;
	}


	public void updateDialog(String[] title, final String[] columnValues) {
		if(title == null || columnValues == null) return ;
		final TableLayout table = (TableLayout) findViewById(R.id.csv_details_table);
		table.removeAllViews();
		int column = Math.max(title.length, columnValues.length);
		for (int i = 0; i < column; i++) {
			TableRow row = null;
			if(mode == Constants.DIALOG_CONTACT_DETAILS){
				row = (TableRow)inflater.inflate(R.layout.csv_details_item, null);
				TextView val = (TextView)row.findViewById(R.id.csv_value);
				val.setText(i <columnValues.length ? columnValues[i] : "");
				if(i < columnValues.length && 
						!TextUtils.isEmpty(columnValues[i])) {
					if(i == 1){ // make call enabled
						//if(TextUtils.isEmpty(str))
						val.setOnClickListener(csvViewer.getCallListner());
						//val
						//btn.setBackgroundResource(android.R.drawable.ic_menu_call);
					}else if(i ==2){ // make email enabled
						val.setOnClickListener(csvViewer.getEmailListner());
						//btn.setBackgroundResource(android.R.drawable.ic_dialog_email);
					}
				}
			}else if(mode == Constants.DIALOG_EDIT_ENTRY){
				row = (TableRow)inflater.inflate(R.layout.csv_edit_item, null);
				TextView val = (TextView)row.findViewById(R.id.csv_value);
				val.setText(i <columnValues.length ? columnValues[i] : "");
			}else if(mode == Constants.DIALOG_NEW_ENTRY){
				row = (TableRow)inflater.inflate(R.layout.csv_edit_item, null);
			}
			TextView key = (TextView)row.findViewById(R.id.csv_key);
			key.setText(i <title.length ? title[i] : "");
			
			table.addView(row);
		} 
		Button cancelButton = (Button)findViewById(R.id.csv_details_cancel);
		if(mode == Constants.DIALOG_CONTACT_DETAILS){
			cancelButton.setText("Edit");
		}else{
			cancelButton.setText("Cancel");
		}
		
		Button saveButton = (Button)findViewById(R.id.csv_details_save);
		if(mode != Constants.DIALOG_CONTACT_DETAILS){
			saveButton.setText("Save");
		}else{
			saveButton.setText("Save Contact");
		}
		saveButton.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				cancel();
				if(mode == Constants.DIALOG_CONTACT_DETAILS){
					Intent intent = new Intent(Intent.ACTION_INSERT);
					intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
					
					try{
						intent.putExtra(ContactsContract.Intents.Insert.NAME, columnValues[0]);
						intent.putExtra(ContactsContract.Intents.Insert.PHONE, columnValues[1]);
						intent.putExtra(ContactsContract.Intents.Insert.EMAIL, columnValues[2]);
					}catch (Exception e) {
						
					}
					csvViewer.startActivity(intent);
				}else if(mode == Constants.DIALOG_EDIT_ENTRY || 
						mode == Constants.DIALOG_NEW_ENTRY){
					csvViewer.listModified = true;
					// get no of rows
					int rowCount = table.getChildCount();
					String[] values = new String[rowCount];
					for (int j = 0; j < rowCount; j++) {
						// get edit text field value
						TableRow row = (TableRow) table.getChildAt(j);
						EditText edit = (EditText) row.getChildAt(1);
						values[j] = edit.getText().toString();
					}
					// collect all the entered value set it to adapter
					CSVRowItem item = new CSVRowItem(values);
					if(mode == Constants.DIALOG_NEW_ENTRY){
						adapter.addCSVItem(item);
					}else{
						adapter.updateCSVItem(item, csvViewer.selectedPosition);
					}
					adapter.notifyDataSetChanged();
				}
				
			}});
	}
	
    

}
