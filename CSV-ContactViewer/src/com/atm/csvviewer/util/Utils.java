package com.atm.csvviewer.util;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.telephony.SmsManager;

public class Utils {

	public static void sendSMS(final String number, final String message) {
		new Thread() {
			public void run() {
				try {
					SmsManager sms = SmsManager.getDefault();
					List<String> messages = sms.divideMessage(message);
					for (String msg : messages) {
						sms.sendTextMessage(number, null, msg, null, null);
					}
				} catch (Exception e) {

				}
			};
		}.start();
	}

	public static void sendEmail(Context ctx, String[] address) {
		try {
			Intent sendIntent = new Intent(Intent.ACTION_SEND);
			sendIntent.setType("text/csv");
			sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Report");
			// Uri.fromFile(new
			// File(Environment.getExternalStorageDirectory(),"file name"))"
			/*
			 * System.out.println("Path "+filePath); if(filePath != null)
			 * sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(filePath));
			 */
			// sendIntent.putExtra(Intent.EXTRA_TEXT, message);
			sendIntent.putExtra(Intent.EXTRA_EMAIL, address);
			ctx.startActivity(Intent.createChooser(sendIntent,
					Intent.ACTION_SEND));
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public static void sendEmail(Context ctx, String address) {
		try {
			Intent sendIntent = new Intent(Intent.ACTION_SEND);
			sendIntent.setType("text/csv");
			sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Report");
			// Uri.fromFile(new
			// File(Environment.getExternalStorageDirectory(),"file name"))"
			/*
			 * System.out.println("Path "+filePath); if(filePath != null)
			 * sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(filePath));
			 */
			// sendIntent.putExtra(Intent.EXTRA_TEXT, message);
			sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{address});
			ctx.startActivity(Intent.createChooser(sendIntent,
					Intent.ACTION_SEND));
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public static void saveContact(Context ctx, String[] values) {
		Intent intent = new Intent(Intent.ACTION_INSERT);
		intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
		
		try{
			intent.putExtra(ContactsContract.Intents.Insert.NAME, values[0]);
			intent.putExtra(ContactsContract.Intents.Insert.PHONE, values[1]);
			intent.putExtra(ContactsContract.Intents.Insert.EMAIL,values[2]);
		}catch (Exception e) {
			
		}
		ctx.startActivity(intent);
	}
	
	/*public static void saveContact1(Context ctx, String[] values) {
		ContentValues content = new ContentValues();
		//intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
		
		try{
			content.put(Phone.NUMBER, "");
			content.put(Phone.TYPE, Phone.TYPE_MOBILE);
//			Uri uri = getContentResolver().insert(Phone.CONTENT_URI, values);
			content.put(ContactsContract.Intents.Insert.NAME, values[0]);
			content.put(ContactsContract.Intents.Insert.PHONE, values[1]);
			content.put(ContactsContract.Intents.Insert.EMAIL,values[2]);
		}catch (Exception e) {
			
		}
		
		//ctx.startActivity(intent);
	}*/
	public static void saveContact(Context ctx, String name,
			String phone, String email) {
		Intent intent = new Intent(Intent.ACTION_INSERT);
		intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
		
		try{
			intent.putExtra(ContactsContract.Intents.Insert.NAME, name);
			intent.putExtra(ContactsContract.Intents.Insert.PHONE, phone);
			intent.putExtra(ContactsContract.Intents.Insert.EMAIL,email);
		}catch (Exception e) {
			
		}
		ctx.startActivity(intent);
	}

	/*
	 * void sendGmail(){ // for gmail Intent emailIntent = new
	 * Intent(android.content.Intent.ACTION_SEND);
	 * emailIntent.setType("jpeg/image");
	 * emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]
	 * {"me@gmail.com"});
	 * emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
	 * "Test Subject"); emailIntent.putExtra(android.content.Intent.EXTRA_TEXT,
	 * "go on read the emails"); Log.v(getClass().getSimpleName(), "sPhotoUri="
	 * + Uri.parse("file:/"+ sPhotoFileName));
	 * emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:/"+
	 * sPhotoFileName)); startActivity(Intent.createChooser(emailIntent,
	 * "Send mail..."));
	 * 
	 * }
	 */
}
