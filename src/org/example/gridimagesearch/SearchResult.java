package org.example.gridimagesearch;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class SearchResult {

	public static final int STATUS_OK = 200;
	
	private JSONObject response;
	private int responseStatus;
	private String responseDetail;
	private List<Image> images;
	
	public SearchResult() {
		images = new ArrayList<Image>();
	}
	
	public void parseResponse(JSONObject response) {
		if (response == null) {
			response = new JSONObject();
		}
		this.response = response;
		parseResponseStatus();
		parseResponseDetail();
		parseResponseData();
	}
	
	public int getResponseStatus() {
		return responseStatus;
	}
	
	public String getResponseDetail() {
		return responseDetail;
	}
	
	public List<Image> getImages() {
		return images;
	}
	
	private void parseResponseStatus() {
		responseStatus = response.optInt("responseStatus");
	}
	
	private void parseResponseDetail() {
		responseDetail = response.optString("responseDetails");
	}
	
	private void parseResponseData() {
		JSONObject responseData = response.optJSONObject("responseData");
		if (responseData != null) {
			JSONArray results = responseData.optJSONArray("results");
			if (results != null) {
				parseResults(results);
			}
		}
	}
	
	private void parseResults(JSONArray results) {
		for (int i = 0; i < results.length(); i++) {
			JSONObject data = results.optJSONObject(i);
			if (data != null) {
				Image image = new Image(data);
				images.add(image);
			}
		}
	}
	
}
