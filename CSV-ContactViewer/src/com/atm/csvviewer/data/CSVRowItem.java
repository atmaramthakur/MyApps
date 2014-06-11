package com.atm.csvviewer.data;

public class CSVRowItem {
	boolean checked = false;
	private String[] columnValues;
	public CSVRowItem(String str) {
		System.out.println("Str Imported :: "+str);
		columnValues = str.split(",");
		//System.out.println("columnValues length "+columnValues.length);
		for (int i = 0; i < columnValues.length; i++) {
			//System.out.println(""+columnValues[i]);
		}
	}
	
	public CSVRowItem(String[] str) {
		//System.out.println("Str Imported :: "+str);
		columnValues = str;
	}
	
	public String getTitle(){
		return columnValues[0];
	}
	public String[] getColumnValues() {
		return columnValues;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		StringBuffer buf = new StringBuffer("");
		for (int i = 0; i < columnValues.length; i++) {
			buf.append(i+". "+columnValues[i]+" ");
		}
		buf.append("Checked status "+checked);
		return buf.toString();
	}
	
	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public void toggle() {
		// TODO Auto-generated method stub
		checked = !checked;
	}
	
}
