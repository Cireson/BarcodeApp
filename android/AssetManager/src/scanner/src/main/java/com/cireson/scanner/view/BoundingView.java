
//////////////////////////////////////////////////////////////////////////////
//This file is part of Cireson Barcode Scanner. 
//
//Cireson Barcode Scanner is free software: you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation, either version 3 of the License, or
//(at your option) any later version.
//
//Cireson Barcode Scanner is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with Cireson Barcode Scanner.  If not, see<https://www.gnu.org/licenses/>.
/////////////////////////////////////////////////////////////////////////////


package com.cireson.scanner.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.cireson.scanner.util.CameraManager;

/**
 * View for displaying bounds for active camera region
 */
public class BoundingView extends View {
    /**
     * Camera manager
     */
    private CameraManager cameraManager;

    public BoundingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Sets camera manger
     * @param cameraManager
     */
    public void setCameraManager(CameraManager cameraManager) {
        this.cameraManager = cameraManager;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (cameraManager != null) {
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setARGB(110, 110, 110, 50);
            Rect boundingRect = cameraManager.getBoundingRectUi(canvas.getWidth(), canvas.getHeight());
            canvas.drawRect(boundingRect, paint);
        }
    }
}
