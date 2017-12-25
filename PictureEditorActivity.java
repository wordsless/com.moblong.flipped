package com.moblong.flipped;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import com.moblong.flipped.R;
import com.moblong.flipped.model.Account;
import com.moblong.flipped.model.User;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;

public final class PictureEditorActivity extends Activity {
	
	private int cw, ch, pw, ph, s, dw, dh;
	
	private float scale;
	
	private SurfaceHolder holder;
	
	private Bitmap scaleBmp;
	
	private boolean vertical = false;
	
	private Rect select, pb;
	
	private Paint yellow, black;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_picture_layout);
		final SurfaceView surface = (SurfaceView) findViewById(R.id.viewfinder);
		final Resources resources = getResources();
		final Shell flipped = (Shell)getApplication();
		surface.setFocusable(true);
		Intent data = getIntent();
		String path = data.getStringExtra("path");
		final Bitmap target = BitmapFactory.decodeFile(path);
		ViewTreeObserver observer = surface.getViewTreeObserver();
		observer.addOnPreDrawListener(new OnPreDrawListener() {

			@Override
			public boolean onPreDraw() {
				cw = surface.getWidth();
				ch = surface.getHeight();
				
				vertical = target.getHeight() > target.getWidth();
				
				if(target.getWidth() > cw) {
					scale = (float)cw/(float)target.getWidth();
					if(target.getHeight()*scale > ch) {
						scale = (float)ch/(float)target.getHeight();
					}
				} else if(target.getHeight() > ch) {
					scale = (float)ch/(float)target.getHeight();
				} else {
					scale = 1;
				}
				return true;
			}
		});
		
		holder = surface.getHolder();
		holder.addCallback(new Callback() {

			@Override
			public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
				
			}
			
			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				
				yellow = new Paint();
				yellow.setColor(Color.YELLOW);
				yellow.setStyle(Style.STROKE);
				yellow.setStrokeWidth(5);
				
				black = new Paint();
				black.setColor(Color.BLACK);
				black.setStyle(Style.FILL);
				
				Canvas canvas = holder.lockCanvas();
				
				scaleBmp = Bitmap.createScaledBitmap(target, (int)Math.rint(target.getWidth()*scale), (int)Math.rint(target.getHeight()*scale), false);
				pw = scaleBmp.getWidth();
				ph = scaleBmp.getHeight();
				s = pw > ph ? ph : pw;
				dw = (cw - pw)>>1;
				dh = (ch - ph)>>1;
				
				pb = new Rect();
				pb.top    = (ch - ph)>>1;
				pb.left	  = (cw - pw)>>1;
				pb.bottom = (ch + ph)>>1;
				pb.right  = (cw + pw)>>1;
				canvas.drawBitmap(scaleBmp, pb.left,  pb.top, null);
				
				select = new Rect();
				select.top    = (ch - s)>>1;
				select.left   = (cw - s)>>1;
				select.bottom = (ch + s)>>1;
				select.right  = (cw + s)>>1;
				canvas.drawRect(select.left, select.top, select.right, select.bottom, yellow);
				
				holder.unlockCanvasAndPost(canvas);
			}

			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				
			}
			
		});
		
		surface.setOnTouchListener(new OnTouchListener() {

			float start = 0.0f;
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				if(event.getAction()==MotionEvent.ACTION_DOWN) {
					start = vertical ? event.getY() : event.getX();
				} else if(event.getAction()==MotionEvent.ACTION_MOVE) {
					Canvas canvas = holder.lockCanvas();
					render(canvas, (int)((vertical ? event.getY() : event.getX()) - start));
					holder.unlockCanvasAndPost(canvas);
				} else if(event.getAction()==MotionEvent.ACTION_UP) {
					start = 0.0f;
				}
				return true;
			}
			
			private void render(final Canvas canvas, final int offset) {
				
				canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), black);
				canvas.drawBitmap(scaleBmp, pb.left, pb.top, null);
				
				if(vertical) {
					int top    = select.top    + offset;
					int bottom = select.bottom + offset;
					if(top >= pb.top && bottom <= pb.bottom) {
						select.top    = ((ch - s)>>1) + offset;
						select.bottom = ((ch + s)>>1) + offset;
					}
				} else {
					int left  = select.left  + offset;
					int right = select.right + offset;
					if(left >= pb.left && right < pb.right) {
						select.left  = ((cw - s)>>1) + offset;
						select.right = ((cw + s)>>1) + offset;
					}
				}
				canvas.drawRect(select.left, select.top, select.right, select.bottom, yellow);
			}
		});
		
		View take = findViewById(R.id.take);
		take.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Bitmap selected = Bitmap.createBitmap(scaleBmp, select.left - dw, select.top - dh, s, s);
				Bitmap portrait = Bitmap.createScaledBitmap(selected, (int)resources.getDimension(R.dimen.portrait_size), (int)resources.getDimension(R.dimen.portrait_size), false);
				
				File png;
				Account user = flipped.getAccount();
				if(user.getAvatar() != null) {
					png = new File(flipped.root(), user.getAvatar()+".png");
					if(png.exists())
						png.delete();
				}
				
				String uuid = UUID.randomUUID().toString().replace("-", "");
				png = new File(flipped.cache(), uuid+".png");
				if(png.exists()) {
					png.delete();
				}
				
				FileOutputStream output = null;
				try {
					png.createNewFile();
					output = new FileOutputStream(png);
					portrait.compress(Bitmap.CompressFormat.PNG, 90, output);
					output.flush();
					output.close();
					flipped.getAccount().setAvatar(uuid);
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if(output != null) {
						try {
							output.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
						output = null; 
					}
				}
				
				Intent result = new Intent();
				result.putExtra("portrait", png.getAbsolutePath());
				PictureEditorActivity.this.setResult(RESULT_OK, result);
				PictureEditorActivity.this.finish();
			}
			
		});
	}
	
	
}
