
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


package com.cireson.assetmanager.util;

import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.cireson.assetmanager.R;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

/**
 * Camera preview callback
 */
public class PreviewCallback implements Camera.PreviewCallback {

    private static final String TAG = PreviewCallback.class.getSimpleName();
    /**
     * Xzing multi format reader
     */
    private final MultiFormatReader multiFormatReader = new MultiFormatReader();
    /**
     * Handler to send messages
     *
     * @see CaptureHandler
     */
    private Handler handler;
    /**
     * Camera manager
     */
    private CameraManager cameraManager;

    public PreviewCallback(Handler handler, CameraManager cameraManager) {
        this.handler = handler;
        this.cameraManager = cameraManager;
    }

    @Override
    public void onPreviewFrame(byte[] bytes, Camera camera) {
        Camera.Size previewSize = camera.getParameters().getPreviewSize();
        new DecodeAsyncTask(previewSize.width, previewSize.height).execute(bytes);
    }

    /**
     * Asynchronous task for decoding and finding barcode
     */
    private class DecodeAsyncTask extends AsyncTask<byte[], Void, Result> {
        /**
         * Width of image
         */
        private int width;
        /**
         * Height of image
         */
        private int height;

        /**
         * @param width  Width of image
         * @param height Height of image
         */
        private DecodeAsyncTask(int width, int height) {
            this.width = width;
            this.height = height;
        }

        @Override
        protected void onPostExecute(Result result) {
            if (result != null) {
                Log.i(TAG, "Decode success.");
                if (handler != null) {
                    Message message = Message.obtain(handler, R.id.decoded);
                    Bundle bundle = new Bundle();
                    bundle.putString(CaptureHandler.DECODED_DATA, result.toString());
                    message.setData(bundle);
                    message.sendToTarget();
                }
            } else {
                Log.i(TAG, "Decode fail.");
                if (handler != null) {
                    Message message = Message.obtain(handler, R.id.decode_failed);
                    message.sendToTarget();
                }
            }
        }

        @Override
        protected Result doInBackground(byte[]... datas) {
            if (!cameraManager.hasCamera()) {
                return null;
            }
            Result rawResult = null;
            final PlanarYUVLuminanceSource source =
                    cameraManager.buildLuminanceSource(datas[0], width,
                            height, cameraManager.getBoundingRect());
            if (source != null) {
                BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
                try {
                    rawResult = multiFormatReader.decodeWithState(bitmap);
                } catch (ReaderException re) {
                    // continue
                } finally {
                    multiFormatReader.reset();
                }
            }

            return rawResult;
        }
    }
}
