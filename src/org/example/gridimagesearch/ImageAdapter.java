package org.example.gridimagesearch;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class ImageAdapter extends ArrayAdapter<Image> {
	
	private Context context;
	private List<Image> images;

	public ImageAdapter(Context context, int resource, List<Image> images) {
		super(context, 0, images);
		this.context = context;
		this.images = images;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.image, parent, false);
		}
		
		ImageView ivSearchImage = (ImageView) convertView.findViewById(R.id.ivSearchImage);
		TextView tvSearchImageTitle = (TextView) convertView.findViewById(R.id.tvSearchImageTitle);
		
		Image image = images.get(position);
		String imageTbUrl = image.getTbUrl();
		String imageVisiblUrl = image.getVisibleUrl();
		
		Picasso.with(context).load(imageTbUrl).placeholder(R.drawable.camera_gray).into(ivSearchImage);
		tvSearchImageTitle.setText(imageVisiblUrl);
		
		return convertView;
	}

}
