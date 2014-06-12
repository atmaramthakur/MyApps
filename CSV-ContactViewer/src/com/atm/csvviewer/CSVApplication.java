package com.atm.csvviewer;

import android.app.Activity;
import android.app.Application;
import android.util.Log;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class CSVApplication extends Application {
	
	 private  Tracker mTracker ;
	 public  Tracker getTracker() {
	    	if(mTracker == null){
	            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
	            mTracker = analytics.newTracker(R.xml.global_tracker);
	        }
	        return mTracker;
	    }
	
	public  void startScreen(Activity activity ){
		Tracker t = getTracker();
		Log.d("GAnalyticsUtils", "In startScreen t "+t);
        t.setScreenName(activity.getClass().getSimpleName());
        t.send(new HitBuilders.AppViewBuilder().build());
        GoogleAnalytics.getInstance(activity).dispatchLocalHits();
	}

	public  void sendEvent(Activity application,  String action, String label) {
		getTracker()
		.send(new HitBuilders.EventBuilder()
			.setCategory(application.getClass().getSimpleName())
                .setAction(action)
                .setLabel(label).build());
        GoogleAnalytics.getInstance(application).dispatchLocalHits();
	}
	

	
}

