package org.example.gridimagesearch;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

import com.ortiz.touch.TouchImageView;
import com.squareup.picasso.Picasso;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.MimeTypeMap;
import android.widget.TextView;

public class ZoomImageActivity extends Activity {
	
	private static final String TMP_IMAGE_FILE_NAME = "tmpImage.jpg";
	
	private Image image;
	private TouchImageView tivZoomImage;
	private MediaScannerConnection mediaScannerConn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_zoom_image);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		Intent intent = getIntent();
		image = intent.getParcelableExtra(GridImageSearchActivity.ZOOM_INTENT_EXTRA_KEY);
		//Toast.makeText(this, "image url: " + image.getUrl(), Toast.LENGTH_SHORT).show();
		
		init();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_zoom_image, menu);
		
		return true;
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {

		int itemId = item.getItemId();
		if (itemId == android.R.id.home) {
			finish();
			return true;
		} else if (itemId == R.id.mImageShare) {
			mediaScannerConn = new MediaScannerConnection(this, new MediaScannerConnectionClient() {
				
				@Override
				public void onScanCompleted(String path, Uri uri) {
					Log.i("MediaScannerConnection", uri.toString());
					mediaScannerConn.disconnect();
					
					Intent intent = new Intent();
					intent.setAction(Intent.ACTION_SEND);
					intent.putExtra(Intent.EXTRA_STREAM, uri);
					Log.i("MediaScannerConnection", image.getMimeType());
					intent.setType(image.getMimeType());
					startActivity(Intent.createChooser(intent, getString(R.string.share_to)));
					
				}
				
				@Override
				public void onMediaScannerConnected() {
					Log.i("MediaScannerConnection", "media scanner connected");
					
					File imgFile = new File(getFilesDir() + "/" + TMP_IMAGE_FILE_NAME);
					
					try {
						URL url = new URL(image.getUrl());
						URLConnection urlConn = url.openConnection();
						
						BufferedInputStream in = new BufferedInputStream(urlConn.getInputStream());
						BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(imgFile));
						
						int c;
						while ((c = in.read()) != -1) {
							out.write(c);
						}
						
						if (in != null) {
							in.close();
						}
						
						if (out != null) {							
							out.close();
						}
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					mediaScannerConn.scanFile(imgFile.getAbsolutePath(), image.getMimeType());
					
				}
			});
			mediaScannerConn.connect();
		} else {
		}
		return super.onMenuItemSelected(featureId, item);
	}
	
	private void init() {
		tivZoomImage = (TouchImageView) findViewById(R.id.tivZoomImage);
		Picasso.with(this).load(image.getUrl()).placeholder(R.drawable.camera_gray).into(tivZoomImage);
		TextView tvImageDesc = (TextView) findViewById(R.id.tvImageDesc);
		TextView tvImageVisibleUrl = (TextView) findViewById(R.id.tvImageVisibleUrl);
		tvImageDesc.setText(image.getContent());
		tvImageVisibleUrl.setText(image.getVisibleUrl());
	}
	


}
