package org.example.gridimagesearch;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class AdvancedDialog extends DialogFragment {
	
	public static final String FILTER_NONE = "none";
	
	private static final String QUERY_KEY_FOR_SITE_FILTER = "as_sitesearch";
	private static final String QUERY_KEY_FOR_IMAGE_TYPE = "imgtype";
	private static final String QUERY_KEY_FOR_COLOR_FILTER = "imgcolor";
	private static final String QUERY_KEY_FOR_IMAGE_SIZE = "imgsz";
	
	private OnAdvancedCompleteListener mListener;
	private Context context;
	private Resources resources;
	private Spinner spImageSize;
	private Spinner spColorFilter;
	private Spinner spImageType;
	private EditText etSiteFilter;
	private Map<String, String> filters;

	public interface OnAdvancedCompleteListener {
		public abstract void onAdvancedComplete(Map<String, String> filter);
	}

	public AdvancedDialog(Context context, Map<String, String> filters) {
		this.context = context;
		this.filters = filters;
		resources = context.getResources();
		//this.setStyle(STYLE_NORMAL, theme)
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnAdvancedCompleteListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnAdvancedCompleteListener");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.search_advanced, container);
		Dialog dialog = getDialog();
		dialog.setTitle(R.string.advanced_search_options);

		spImageSize = (Spinner) view.findViewById(R.id.spImageSize);
		spColorFilter = (Spinner) view.findViewById(R.id.spColorFilter);
		spImageType = (Spinner) view.findViewById(R.id.spImageType);
		etSiteFilter = (EditText) view.findViewById(R.id.etSiteFilter);

		ArrayAdapter<CharSequence> imageSizeAdapter = ArrayAdapter
				.createFromResource(context, R.array.sp_image_size,
						android.R.layout.simple_spinner_item);
		spImageSize.setAdapter(imageSizeAdapter);
		initFilterValue(spImageSize);

		ArrayAdapter<CharSequence> colorFilterAdapter = ArrayAdapter
				.createFromResource(context, R.array.sp_color_filter,
						android.R.layout.simple_spinner_item);
		spColorFilter.setAdapter(colorFilterAdapter);
		initFilterValue(spColorFilter);

		ArrayAdapter<CharSequence> imageTypeAdapter = ArrayAdapter
				.createFromResource(context, R.array.sp_image_type,
						android.R.layout.simple_spinner_item);
		spImageType.setAdapter(imageTypeAdapter);
		initFilterValue(spImageType);
		
		initFilterValue(etSiteFilter);
		
		Button btnSave = (Button) view.findViewById(R.id.btnAdvancedSave);
		btnSave.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Map<String, String> filter = new HashMap<String, String>();
				String imageSize = spImageSize.getSelectedItem().toString();
				filter.put(QUERY_KEY_FOR_IMAGE_SIZE, imageSize);
				
				String colorFilter = spColorFilter.getSelectedItem().toString();
				filter.put(QUERY_KEY_FOR_COLOR_FILTER, colorFilter);
				
				String imageType = spImageType.getSelectedItem().toString();
				filter.put(QUERY_KEY_FOR_IMAGE_TYPE, imageType);
				
				String siteFilter = etSiteFilter.getText().toString();
				filter.put(QUERY_KEY_FOR_SITE_FILTER, siteFilter);
				
				mListener.onAdvancedComplete(filter);
				dismiss();
			}
		});
		
		return view;
	}
	
	private void initFilterValue(View filterView) {
		int id = filterView.getId();
		Spinner sp;
		String filterValue = null;
		String[] filterInfo;
		if (id == R.id.spImageSize) {
			sp = (Spinner) filterView;
			filterValue = filters.get(QUERY_KEY_FOR_IMAGE_SIZE);
			filterInfo = getImageSizeArray();
			setSpinnerSelection(sp, filterValue, filterInfo);
		} else if (id == R.id.spColorFilter) {
			sp = (Spinner) filterView;
			filterValue = filters.get(QUERY_KEY_FOR_COLOR_FILTER);
			filterInfo = getColorFilterArray();
			setSpinnerSelection(sp, filterValue, filterInfo);
		} else if (id == R.id.spImageType) {
			sp = (Spinner) filterView;
			filterValue = filters.get(QUERY_KEY_FOR_IMAGE_TYPE);
			filterInfo = getImageTypeArray();
			setSpinnerSelection(sp, filterValue, filterInfo);
		} else if (id == R.id.etSiteFilter) {
			EditText et = (EditText) filterView;
			filterValue = filters.get(QUERY_KEY_FOR_SITE_FILTER);
			if (filterValue != null) {
				et.setText(filterValue);
			}
		} else {
		}
	}
	
	private void setSpinnerSelection(Spinner sp, String selectValue, String[] filterInfo) {
		if (selectValue == null || filterInfo == null) {
			return;
		}
		
		for (int i = 0; i < filterInfo.length; i++) {
			if (filterInfo[i].equals(selectValue)) {
				sp.setSelection(i);
				break;
			}
		}
	}
	
	private String[] getImageSizeArray() {
		return resources.getStringArray(R.array.sp_image_size);
	}
	
	private String[] getColorFilterArray() {
		return resources.getStringArray(R.array.sp_color_filter);
	}
	
	private String[] getImageTypeArray() {
		return resources.getStringArray(R.array.sp_image_type);
	}
}
