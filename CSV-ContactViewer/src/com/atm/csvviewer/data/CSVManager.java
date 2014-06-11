package com.atm.csvviewer.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import org.xml.sax.InputSource;

import com.atm.csvviewer.CSVViewer;
import com.atm.csvviewer.util.Constants;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;

public class CSVManager  {
	Activity activity;
	CSVRowItem title;
	
	public CSVManager(Activity act) {
		activity = act;
	}
	public  Vector<CSVRowItem> importData(String ImportFileName) 
	{

		InputSource is;
		try {	
			if(ImportFileName == null) return null;
			// return if invalid file
			int index = ImportFileName.lastIndexOf('.');
			if(index <= 0) return null;
			String ext = ImportFileName.substring(index).toLowerCase();
			System.out.println("ext"+ext);
			if(!ext.equals(".csv")) {
				activity.showDialog(Constants.DIALOG_UNSUPPORTED_FILE);
				return null;
			}
			///ImportFileName = Environment.getExternalStorageDirectory().getAbsolutePath()+"/contacts.csv";
			//File dir = Environment.getExternalStorageDirectory();
			//System.out.println("imprtfileName = "+ImportFileName);
			File file = new File(ImportFileName);		
			
			is = new InputSource(new FileInputStream(file));
			InputStream instream = is.getByteStream();  
			InputStreamReader inputreader = new InputStreamReader(instream); 
			BufferedReader buf = new BufferedReader(inputreader); 		
			String str = buf.readLine();//reading headers
			System.out.println("Heading : "+str);
			title = new CSVRowItem(str);
			Vector<CSVRowItem> csvData = new Vector<CSVRowItem>();
			//csvData.add(new CSVRowItem(str));
			while((str = buf.readLine())!=null)				
			{
				if(str.trim().length() == 0) continue;
				CSVRowItem rowItem = new CSVRowItem(str);
				if(rowItem.getColumnValues().length > 0){
					csvData.add(rowItem);
					System.out.println("Added" + rowItem);
				}
			}
			
			// save the file name
			SharedPreferences pref = activity.getSharedPreferences(Constants.SETTINGS, Activity.MODE_PRIVATE);
			Editor editor = pref.edit();
			editor.putString(Constants.STORED_PATH, ImportFileName);
			editor.commit();
			
			return csvData;
		} catch (FileNotFoundException e) {
			activity.showDialog(Constants.DIALOG_NO_FILE_FOUND);
			e.printStackTrace();
		} catch (IOException e) {
			activity.showDialog(Constants.DIALOG_FILE_READ_ERROR);
			e.printStackTrace();
		}	
		return null;
	
	}
	
	public CSVRowItem getTitle() {
		return title;
	}
	
	public void exportData(String path, Vector<CSVRowItem> items){
		OutputStreamWriter out =null;
		StringBuffer buf= new StringBuffer();
		File file = new File(path);
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(file);
			out = new OutputStreamWriter(fos);
			String values[] = title.getColumnValues();
			for (int i = 0; i < values.length; i++) {
				buf.append(values[i]).append(',');
			}
			buf.append("\n");
			for (int j = 0; j < items.size(); j++) {
			
				values = items.elementAt(j).getColumnValues();
				for (int i = 0; i < values.length; i++) {
					buf.append(values[i]).append(',');
				}
				buf.append("\n");
			}
			String str = buf.toString().trim();
			System.out.println("saving data "+str);
			out.write(str);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
}
