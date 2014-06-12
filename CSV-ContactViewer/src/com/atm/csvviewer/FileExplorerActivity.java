package com.atm.csvviewer;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.atm.csvviewer.util.Constants;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherInterstitialAd;


public class FileExplorerActivity extends Activity {
	

    /* special string denotes upper directory */
    private static final String UP_DIRECTORY = "..";

    /* special string that denotes upper directory accessible by this browser.
     * this virtual directory contains all roots.
     */
    private String ROOT_DIR = "/";

    /* separator string as defined by FC specification */
    private static final String SEP_STR = "/";

    /* separator character as defined by FC specification */
    private static final char SEP = '/';

    private String currDirName;
    
    ListView list;
    FileListAdapter adapter;
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_explorer);
        

        //currDirName = ROOT_DIR;

        list = (ListView)findViewById(R.id.file_list);
        adapter = new FileListAdapter(this, android.R.layout.simple_list_item_1);
       
        list.setAdapter(adapter);
        
        GAdManager.initInterstitialAd(this);
        //list.re
        
        list.setOnItemClickListener(listItemClickListner);
    
        try {
             String state = android.os.Environment.getExternalStorageState();
             System.out.println(" State : "+state);
             if(!state.equals(android.os.Environment.MEDIA_MOUNTED))  {
                 //throw new IOException("SD Card is not mounted.  It is " + state + ".");
                 return;
             }
            ROOT_DIR = Environment.getExternalStorageDirectory()
            .getAbsolutePath();
            currDirName = ROOT_DIR;
            showCurrDirNow ();
        }
        catch (Exception e) {
            
        }
        
    }

    
    boolean adShown;
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	// TODO Auto-generated method stub
//    	if(keyCode == KeyEvent.KEYCODE_BACK && !adShown){
////    		if(!GAdManager.displayInterstitial())
////    			return super.onKeyDown(keyCode, event);
////    		adShown = true;
//    		return true;
//    	}else
    	return super.onKeyDown(keyCode, event);
    }

    OnItemClickListener listItemClickListner = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View listView,
                int position, long id) {
            FileStructure str = (FileStructure) adapter.getItem(position);
            final String currFile = str.name;
            System.out.println(" Selected file : "+currFile);
            //list.setAdapter(adapter);
            traverseDirectory(currFile);            
        }
    }; 


   

    /**
     * Show file list in the current directory .
     */
    void showCurrDirNow() {
        File[] fileList;
        File currDir = null;
        System.out.println(" before clearing size " + adapter.getCount());
        adapter.clear();
        //adapter.notifyDataSetInvalidated();
        adapter.notifyDataSetChanged();
        list.invalidate();
        //list.setAdapter(adapter);
        //list.removeAllViews();
        System.out.println(" After clearing size " + adapter.getCount());
        if (ROOT_DIR.equals(currDirName)) {
            currDir = new File(currDirName);
            fileList = currDir.listFiles();
            System.out.println(" FileList " + fileList);
        } else {
            currDir = new File(currDirName);
            fileList = currDir.listFiles();
            // not root - draw UP_DIRECTORY
            FileStructure str = new FileStructure();
            str.name = UP_DIRECTORY;
            str.fileIcon = R.drawable.up;
            System.out.println(" Adding "+str);
            adapter.add(str);
        }

        System.out.println("currDir : " + currDir.getName() + " ,file "
                + currDir.isFile() + " dir " + currDir.isDirectory());
        if (currDir.isDirectory() && fileList != null) {
            System.out.println(" fileList " + fileList.length);
            for (int i = 0; i < fileList.length; i++) {
                FileStructure str = new FileStructure();
                str.name = fileList[i].getName();
                str.fileIcon = fileList[i].isFile() ? R.drawable.file
                        : R.drawable.folder;
                adapter.add(str);
            }
        } else if (currDir.isFile()) {
            System.out.println(" This is a file .... " + currDirName);
            Intent intent = new Intent();
            intent.putExtra(Constants.SELECTED_FILE_PATH, currDirName);
            setResult(RESULT_OK, intent);
            finish();
        }

    }

    void traverseDirectory (String fileName) {
        System.out.println(" in Traverse Directory : currDirName "+currDirName+" ROOT_DIR "+ROOT_DIR+" fileName "+fileName);
       if (currDirName.equals (ROOT_DIR)) {
            if (fileName.equals (UP_DIRECTORY)) {
                // can not go up from MEGA_ROOT
                return;
            }

            //currDirName = fileName;
            currDirName = currDirName +"/"+ fileName;
        }
        else if (fileName.equals (UP_DIRECTORY)) {
            // Go up one directory
            int i = currDirName.lastIndexOf (SEP, currDirName.length () - 2);

            if (i != -1) {
                currDirName = currDirName.substring (0, i + 1);
            }
            else {
                currDirName = ROOT_DIR;
            }
        }
        else {
            currDirName = currDirName +"/"+ fileName;
        }

        System.out.println("currDirName changed to "+currDirName);
        showCurrDirNow();
    }

  

   

  
    
    class FileListAdapter extends ArrayAdapter<FileStructure>{
        private LayoutInflater mInflater;
        public FileListAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
            mInflater = LayoutInflater.from(context);
            // TODO Auto-generated constructor stub
        }
        @Override
        public void add(FileStructure object) {
            // TODO Auto-generated method stub
            super.add(object);
            notifyDataSetChanged();
        }
        
        
        
        
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            System.out.println(" -- in getView --- "+position);
            //if (convertView == null) {
                convertView = mInflater.inflate(R.layout.file_list_items,
                        null);
                TextView text = (TextView) convertView;
                FileStructure str = getItem(position);
                System.out.println(" str "+str);
                 text.setText(getItem(position).name);
                  
                 Drawable d = convertView.getContext().getResources().getDrawable(getItem(position).fileIcon);
                 text.setCompoundDrawablesWithIntrinsicBounds(d, null, null, null);
                 
            //} else {
                
            //}
            return convertView;
        }
        
    }
    class FileStructure{
        String name;
        int fileIcon;
        @Override
        public String toString() {
            return "Name: "+name+", File : "+fileIcon;
        }
    }
}
