package org.example.gridimagesearch;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class ImageSearchClient {

	private static final String SERVICE_URL = "https://ajax.googleapis.com/ajax/services/search/images";
	private static final String VERSION = "1.0";
	private static final String ENCODE_CHARSET = "UTF-8";

	private static AsyncHttpClient client = new AsyncHttpClient();

	public static void searchImage(Map<String, String> query, AsyncHttpResponseHandler handler)
			throws UnsupportedEncodingException {
		RequestParams params = new RequestParams();
		params.put("v", VERSION);
		for (Map.Entry<String, String> entry : query.entrySet()) {
			String key = entry.getKey();
			String value = URLEncoder.encode(entry.getValue(), ENCODE_CHARSET);
			params.put(key, value);
		}

		client.get(SERVICE_URL, params, handler);
	}

}
