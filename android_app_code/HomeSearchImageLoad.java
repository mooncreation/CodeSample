package com.mapapp.async;

import java.io.InputStream;
import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;
import android.os.AsyncTask;
import android.widget.ImageView;

// Load Image Home Screen ListView Image

public class HomeSearchImageLoad extends AsyncTask<String, String, Bitmap> {

	Context ctx;
	private Bitmap bitmap;
	ImageView img;
	private Bitmap getthebitmapyouwanttoshowinacirclefromsomewhere;

	public HomeSearchImageLoad(Context applicationcontext, ImageView img) {
		this.ctx = applicationcontext;
		this.img = img;

	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		
	}

	@Override
	protected Bitmap doInBackground(String... args) {
		// TODO Auto-generated method stub
		try {

			bitmap = BitmapFactory.decodeStream((InputStream) new URL(args[0])
					.getContent());
			

		} catch (Exception e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	protected void onPostExecute(Bitmap image) {
		if (image != null) {
			img.setImageBitmap(image);
			
		} else {
			
		}
	}

}
