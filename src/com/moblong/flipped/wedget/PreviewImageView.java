package com.moblong.flipped.wedget;

import android.annotation.SuppressLint;
import android.graphics.SurfaceTexture;
import android.view.Surface;

public final class PreviewImageView extends Surface {

	@SuppressLint("NewApi")
	public PreviewImageView(SurfaceTexture surfaceTexture) {
		super(surfaceTexture);
	}

}
