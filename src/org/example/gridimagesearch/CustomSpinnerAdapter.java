package org.example.gridimagesearch;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CustomSpinnerAdapter extends ArrayAdapter<String> {
	
	private Context context;
	private String[] values;

	public CustomSpinnerAdapter(Context context, int resource) {
		super(context, 0);
		this.context = context;
		Resources res = context.getResources();
		values = res.getStringArray(resource);
		Log.i("CustomSpinnerAdapter", String.valueOf(values.length));
		for (int i = 0; i < values.length; i++) {
			Log.i("CustomSpinnerAdapter", values[i]);
		}
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.i("CustomSpinnerAdapter", "getView: "+values[position]);
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.custom_spinner, parent, false);
		}
		
		
		TextView tvSpValue = (TextView) convertView.findViewById(R.id.tvSpValue);
		tvSpValue.setText(values[position]);
		
		return convertView;
	}
}
