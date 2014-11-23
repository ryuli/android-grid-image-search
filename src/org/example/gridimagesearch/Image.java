package org.example.gridimagesearch;

import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;
import android.webkit.MimeTypeMap;

public class Image implements Parcelable {

	private String url;
	private String title;
	private String visibleUrl;
	private String tbUrl;
	private String content;
	private String mimeType;
	
	public Image(JSONObject data) {
		parseImage(data);
	}
	
	public String getUrl() {
		return url;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getVisibleUrl() {
		return visibleUrl;
	}
	
	public String getTbUrl() {
		return tbUrl;
	}
	
	public String getContent() {
		return content;
	}
	
	public String getMimeType() {
		return mimeType;
	}
	
	private void parseImage(JSONObject data) {
		if (data != null) {
			url = data.optString("url");
			title = data.optString("title");
			visibleUrl = data.optString("visibleUrl");
			tbUrl = data.optString("tbUrl");
			content = data.optString("contentNoFormatting");
			mimeType = Image.getMimeType(url);
			if (mimeType == null) {
				mimeType = "*/*";
			}
		} else {
			url = "";
			title = "";
			visibleUrl = "";
			tbUrl = "";
			content = "";
			mimeType = "";
		}
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(url);
		dest.writeString(title);
		dest.writeString(visibleUrl);
		dest.writeString(tbUrl);
		dest.writeString(content);
		dest.writeString(mimeType);
	}
	
	public static final Parcelable.Creator<Image> CREATOR = new Parcelable.Creator<Image>() {

		@Override
		public Image createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			return new Image(source);
		}

		@Override
		public Image[] newArray(int size) {
			return new Image[size];
		}
	};
	
	private Image(Parcel in) {
		url = in.readString();
		title = in.readString();
		visibleUrl = in.readString();
		tbUrl = in.readString();
		content = in.readString();
		mimeType = in.readString();
	}
	
	private static String getMimeType(String url) {
		String mimeType = null;
		String extension = MimeTypeMap.getFileExtensionFromUrl(url);
		if (!extension.equals("")) {
			MimeTypeMap mime = MimeTypeMap.getSingleton();
			mimeType = mime.getMimeTypeFromExtension(extension);
		}
		
		return mimeType;
	}
}
