package com.atm.csvviewer;

import android.content.Context;

import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherInterstitialAd;

public class GAdManager {
	private static PublisherInterstitialAd interstitial;
	private static final String MY_AD_UNIT_ID = "ca-app-pub-3828424391276486/9426618655";
	
	public static void initInterstitialAd(Context ctx){
	 interstitial = new PublisherInterstitialAd(ctx);
     interstitial.setAdUnitId(MY_AD_UNIT_ID);
     PublisherAdRequest adRequest = new PublisherAdRequest.Builder().build();
     // Begin loading your interstitial.
     interstitial.loadAd(adRequest);
	}
     
     
     public static boolean displayInterstitial() {
 	    if (interstitial.isLoaded()) {
 	      interstitial.show();
 	      return true;
 	    }
 	    return false;
 	  }
}
