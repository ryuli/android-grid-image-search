package org.example.gridimagesearch;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.example.gridimagesearch.AdvancedDialog.OnAdvancedCompleteListener;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

import com.loopj.android.http.JsonHttpResponseHandler;

public class GridImageSearchActivity extends FragmentActivity implements OnAdvancedCompleteListener {
	
	public static final String ZOOM_INTENT_EXTRA_KEY = "image";

	private static final int NUMBER_OF_RESULTS_PER_PAGE = 8;
	private static final int MAXIMUM_RESULTS = 64;
	private static final int NUMBER_OF_IMAGES_PER_ROW = 3;
	private static final int NUMBER_OF_DP_GRID_SPACING = 3;
	private static final int NUMBER_OF_DP_IMAGE_TITLE_HEIGHT = 20;

	private GridView gvImageGrid;
	private SearchView searchView;
	private List<Image> images;
	private ImageAdapter adapter;
	private Map<String, String> advancedFilters;

	private int pageIndex = 0;
	private int estimatedNumsPerPage = 0;
	private boolean isReady = false;
	private boolean isSearching = false;
	private String keyword = null;
	
	private int searchImageViewSize;
	private int searchImageLayoutHeight;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_grid_image_search);
		init();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_grid_image_search, menu);
		MenuItem searchItem = menu.findItem(R.id.mSearch);
		searchView = (SearchView) searchItem.getActionView();
		searchView.setOnQueryTextListener(new OnQueryTextListener() {
			
			@Override
			public boolean onQueryTextSubmit(String query) {
				keyword = query;
				images.clear();
				pageIndex = 0;
				gvImageGrid.setSelection(0);
				searchImage();
				return true;
			}
			
			@Override
			public boolean onQueryTextChange(String newText) {
				return false;
			}
		});

		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		int itemId = item.getItemId();
		if (itemId == R.id.mAdvanced) {
			FragmentManager fm = getSupportFragmentManager();
			AdvancedDialog advancedDialog = new AdvancedDialog(this,
					advancedFilters);
			advancedDialog.show(fm, "fragment_advanced");
			return true;
		} else if (itemId == R.id.mSearch) {
			return true;
		} else {
		}
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	protected void onPostResume() {
		super.onPostResume();
		isReady = true;
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public void onAdvancedComplete(Map<String, String> filter) {
		advancedFilters.clear();
		for (Map.Entry<String, String> entry : filter.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			if (value.equals(AdvancedDialog.FILTER_NONE)) {
				continue;
			}
			advancedFilters.put(key, value);
		}
		
		images.clear();
		pageIndex = 0;
		gvImageGrid.setSelection(0);
		searchImage();
	}

	public Map<String, String> getAdvancedFilters() {
		return advancedFilters;
	}

	private void init() {
		images = new ArrayList<Image>();
		advancedFilters = new HashMap<String, String>();
		gvImageGrid = (GridView) findViewById(R.id.gvImageGrid);

		gvImageGrid.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {

					@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
					@SuppressWarnings("deprecation")
					@Override
					public void onGlobalLayout() {
						ViewTreeObserver viewTreeObserver = gvImageGrid
								.getViewTreeObserver();

						if (Build.VERSION.SDK_INT < 16) {
							viewTreeObserver.removeGlobalOnLayoutListener(this);
						} else {
							viewTreeObserver.removeOnGlobalLayoutListener(this);
						}
						
						float withPx = gvImageGrid.getWidth();
						float heightPx = gvImageGrid.getHeight();
						
						float spacingPx = convertDpToPx(NUMBER_OF_DP_GRID_SPACING);
						float availableWithForImage = withPx - (2 * spacingPx);
						searchImageViewSize = (int) (availableWithForImage / NUMBER_OF_IMAGES_PER_ROW);
						searchImageLayoutHeight = (int) (searchImageViewSize + convertDpToPx(NUMBER_OF_DP_IMAGE_TITLE_HEIGHT));
						
						int colNums = (int) (Math.floor(withPx) / searchImageViewSize);
						int rowNums = (int) (Math.ceil(heightPx) / searchImageLayoutHeight);

						estimatedNumsPerPage = colNums * rowNums;
					}

				});

		gvImageGrid.setOnScrollListener(new CustomerScrollListener());
		gvImageGrid.setOnItemClickListener(new ImageItemClickListener());
	}

	private float convertPxToDp(float px) {
		Resources resources = this.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		float dp = px / (metrics.densityDpi / 160f);

		return dp;
	}
	
	public float convertDpToPx(float dp){
	    Resources resources = this.getResources();
	    DisplayMetrics metrics = resources.getDisplayMetrics();
	    float px = dp * (metrics.densityDpi / 160f);
	    
	    return px;
	}

	private void searchImage() {
		if (isSearching) {
			return;
		}
		isSearching = true;
		if (keyword == null || keyword.equals("")) {
			isSearching = false;
			return;
		}

		Map<String, String> query = new HashMap<String, String>();
		String start = String.valueOf(NUMBER_OF_RESULTS_PER_PAGE * pageIndex);
		query.put("q", keyword);
		query.put("rsz", String.valueOf(NUMBER_OF_RESULTS_PER_PAGE));
		query.put("start", start);
		if (advancedFilters.size() > 0) {
			for (Map.Entry<String, String> filterEntry : advancedFilters
					.entrySet()) {
				query.put(filterEntry.getKey(), filterEntry.getValue());
			}
		}
		try {
			ImageSearchClient.searchImage(query,
					new ImageSearchResponseHandler());
		} catch (UnsupportedEncodingException e) {
			showMessageDialog(getString(R.string.unsupport_encoding_error));
		}
	}

	private void showMessageDialog(String message) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(
				GridImageSearchActivity.this);
		dialog.setTitle(R.string.information).setMessage(message)
				.setNeutralButton(R.string.label_btn_ok, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				}).show();
	}

	private void renderSearchResult() {

		if (adapter == null) {
			adapter = new ImageAdapter(this, 0, images);
			gvImageGrid.setAdapter(adapter);
		} else {
			adapter.notifyDataSetChanged();
		}
	}

	private void appendImages(List<Image> lists) {
		for (Image image : lists) {
			images.add(image);
		}
	}

	private class ImageSearchResponseHandler extends JsonHttpResponseHandler {

		@Override
		public void onSuccess(int statusCode, Header[] headers,
				JSONObject response) {
			isSearching = false;
			String errorMessage = null;
			if (response != null) {
				SearchResult searchResult = new SearchResult();
				searchResult.parseResponse(response);
				if (searchResult.getResponseStatus() == SearchResult.STATUS_OK) {

					List<Image> resultImages = searchResult.getImages();

					if (resultImages.size() > 0) {
						
						appendImages(resultImages);
						pageIndex++;
						if (images.size() < estimatedNumsPerPage) {
							searchImage();
							return;
						}
						renderSearchResult();
					} else {
						errorMessage = getString(R.string.search_not_found);
					}
				} else {
					errorMessage = searchResult.getResponseDetail();
				}
			} else {
				errorMessage = getString(R.string.fail_to_search_image);
			}

			if (errorMessage != null) {
				showMessageDialog(errorMessage);
			}

			
		}

		@Override
		public void onFailure(int statusCode, Header[] headers,
				Throwable throwable, JSONObject errorResponse) {
			showMessageDialog(getString(R.string.fail_to_search_image));
			isSearching = false;
		}

	}

	private class CustomerScrollListener implements OnScrollListener {

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {

		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			if (!isReady) {
				return;
			}
			if ((firstVisibleItem + visibleItemCount) < totalItemCount
					|| totalItemCount == MAXIMUM_RESULTS) {
				return;
			}

			searchImage();
		}

	}
	
	private class ImageItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Image image = images.get(position);
			Intent intent = new Intent(GridImageSearchActivity.this, ZoomImageActivity.class);
			intent.putExtra(ZOOM_INTENT_EXTRA_KEY, image);
			startActivity(intent);
			
		}}
	

}
