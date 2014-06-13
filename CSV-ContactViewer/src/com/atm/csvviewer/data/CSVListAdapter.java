package com.atm.csvviewer.data;

import java.util.Vector;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.Filter;

import com.atm.csvviewer.util.Utils;

public class CSVListAdapter extends ArrayAdapter<String> implements Filterable{

	Context ctx;
	private ItemFilter mFilter = new ItemFilter();
	public CSVListAdapter(Context ctx, int typeId) {
		super(ctx, typeId);
		this.ctx = ctx;
		//inflater = LayoutInflater.from(ctx);
	}
	Vector<CSVRowItem> filterItems;
	 Vector<CSVRowItem> items = new Vector<CSVRowItem>();
	//LayoutInflater inflater;
	/*public CSVListAdapter(Context ctx) {
		inflater = LayoutInflater.from(ctx);
	}*/
	public void setCSVItems(Vector<CSVRowItem> items){
		this.filterItems = items;
		this.items = items;
		for (int i = 0; i < items.size(); i++) {
			add(items.elementAt(i).getTitle());
		}
	}
	
	public void addCSVItem(CSVRowItem item){
		Log.d(" ", "Before adding "+items+" ---  "+filterItems);
		this.items.add(item);
		//this.filterItems.add(item);
		add(item.getTitle());
		
		System.out.println("after adding "+items+" count"+getCount()+ " filterItems "+filterItems);
	}
	
	public void updateCSVItem(CSVRowItem item, int index){
		this.items.set(index, item);
		this.filterItems.set(index, item);
		//add(item.getTitle());
		
		System.out.println("after updating "+items+" count"+getCount()+ " filterItems "+filterItems);
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return filterItems == null ? 0 : filterItems.size();
	}

	@Override
	public String getItem(int position) {
		// TODO Auto-generated method stub
		return filterItems == null ? null : filterItems.get(position).getTitle();
	}
	
	public void toggleItem(int position){
		if(filterItems != null){
			filterItems.get(position).toggle();
		}
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	/*@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		convertView = inflater.inflate(R.layout.csv_list_item, null);
		((TextView)convertView).setText(filterItems.get(position).getTitle());
		return convertView;
	}*/
	
	public void updateAdapter(String s) {
		System.out.println("in Update Adapter for  filtering "+s+" ::"+filterItems+" items "+items);
		filterItems.removeAllElements();
		System.out.println("in Update Adapter after  filtering "+filterItems+" :: items "+items);
		if(s.length() == 0) {
			filterItems.addAll(items);
			notifyDataSetChanged();
			return;
		}
		System.out.println("filterItems : "+filterItems+" items "+items);
		for (int i = 0; i < items.size(); i++) {
			//System.out.println("before comparing ");
			String title = items.get(i).getTitle().toLowerCase();
			//System.out.println("title "+title);
			if(title.contains(s))filterItems.add(items.get(i));
		}
		System.out.println("After updating filterItems"+filterItems.size()+" :: original "+items.size());
		notifyDataSetChanged();
	}
	
	public CSVRowItem getCSVItem(int index){
		return filterItems.get(index);
	}
	public String printAll() {
		// TODO Auto-generated method stub
		for (int i = 0; i < filterItems.size(); i++) {
			System.out.println(filterItems.elementAt(i));
		}
		return "";
	}
	public void selectAll() {
		for (int i = 0; i < filterItems.size(); i++) {
			filterItems.get(i).setChecked(true);
		}
	}
	public void deSelectAll() {
		// TODO Auto-generated method stub
		
	}
	
	public Vector<CSVRowItem> getAllItems(){
		return items;
	}
	public void saveSelectedContact() {
		for (int i = 0; i < filterItems.size(); i++) {
			if(filterItems.get(i).isChecked())
			Utils.saveContact(ctx, filterItems.get(i).getColumnValues());
		}
	}
	public void sendSMS() {
		StringBuffer address = new StringBuffer();
		for (int i = 0; i < filterItems.size(); i++) {
			if(filterItems.get(i).isChecked()){
				try{
					address.append(filterItems.get(i).getColumnValues()[1]).append(";");
				}catch (Exception e) {
					// TODO: handle exception
				}
			}
		}
		Intent smsIntent = new Intent(Intent.ACTION_SENDTO,Uri.parse("smsto:"+address.toString()));
		//smsIntent.putExtra("sms_body", "sms message goes here");
		ctx.startActivity(smsIntent);
	}
	public void sendEmail() {
		// TODO Auto-generated method stub
		//Utils.sendEmail(ctx, address);
		Vector<String> address = new Vector<String>();
		for (int i = 0; i < filterItems.size(); i++) {
			if(filterItems.get(i).isChecked()){
				try{
					address.add(filterItems.get(i).getColumnValues()[2]);
				}catch (Exception e) {
					// TODO: handle exception
				}
			}
		}
		String[] arr = new String[address.size()];
		address.copyInto(arr);
		Utils.sendEmail(ctx, arr);
	}

	public Filter getFilter() {
		return mFilter;
	}
 
	private class ItemFilter extends Filter {
		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			
			String filterString = constraint.toString().toLowerCase();
			
			FilterResults results = new FilterResults();
			
			final Vector<CSVRowItem> list = items;
 
			int count = list.size();
			final Vector<CSVRowItem> nlist = new Vector<CSVRowItem>(count);
 
			String filterableString ;
			
			for (int i = 0; i < count; i++) {
				filterableString = list.get(i).getTitle();
				if (filterableString.toLowerCase().contains(filterString)) {
					nlist.add(list.get(i));
				}
			}
			
			results.values = nlist;
			results.count = nlist.size();
 
			return results;
		}
 
		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {
			filterItems = (Vector<CSVRowItem>) results.values;
			notifyDataSetChanged();
		}
 
	}

}
