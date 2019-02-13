
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


import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.cireson.assetmanager.R;


public class CaptureHandler extends Handler {
    public static final String DECODED_DATA = "decoded_data";
    private CameraManager cameraManager;
    private Context context;
    private OnDecodedCallback callback;

    public CaptureHandler(CameraManager cameraManager, Context context, OnDecodedCallback callback) {
        this.cameraManager = cameraManager;
        this.context = context;
        this.callback = callback;
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case R.id.decoded:
                String data = msg.getData().getString(DECODED_DATA);
                Toast.makeText(context, data, Toast.LENGTH_LONG).show();
                if (callback != null){
                    callback.onDecoded(data);
                }
                break;
            case R.id.decode_failed:
                //getting new frame
                cameraManager.requestNextFrame(new PreviewCallback(this, cameraManager));
                break;
        }
    }

    public static interface OnDecodedCallback {
        void onDecoded(String decodedData);
    }
}
