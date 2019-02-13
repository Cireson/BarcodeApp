
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

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.ParcelUuid;
import android.os.Parcelable;
import android.widget.Toast;

import com.cireson.assetmanager.R;
import com.cireson.assetmanager.controller.BluetoothPairedDeviceListActivity;
import com.cireson.assetmanager.controller.CiresonBaseActivity;
import com.cireson.assetmanager.model.GlobalData;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class BluetoothClient<T extends CiresonBaseActivity> implements Serializable {

    /*Instances..*/
    private final static String TAG = "BluetoothClient";

    private static BluetoothAdapter mBluetoothAdapter;
    private BluetoothClientCallbackForResult callbackForResult;
    private BluetoothClientCallbackForDeviceDiscovered callbackForDeviceDiscovery;
    private TaskCompleted<Boolean> bluetoothConnectionCallback;
    private BluetoothSocket socket;
    private boolean isSocketMobileScanner;
    private CiresonBaseActivity activity;
    private boolean receiverRegistered = false;
    private String scannedBarcode = "";

    private static boolean connectionInitState = false,
                        isBluetoothDevice = false;

    /*Public static instances.*/
    public final static String BLUETOOTH_CLIENT_EXTRA = "BLUETOOTH_CLIENT_EXTRA";
    public final static int REQ_DISCOVER_DEVICE = 2;
    public final static int REQ_ENABLE_BLUETOOTH = 3;
    public final static int REQ_DEVICESELECTED = 4;
    public final static int REQ_DISCOVER_TIME = 240;
//    public final static String UUID_STRING = "5d85ff00-dd7d-11e3-8b68-0800200c9a66";//UUID_STRING
    public final static String UUID_STRING = "00001101-0000-1000-8000-00805F9B34FB";//UUID_STRING
    public static BluetoothDevice selectedDevice;

    /*Bluetooth client callers..*/
    public final static int SCAN_ASSETS_ACTIVITY = 0;
    public final static int SETTINGS_ACTIVITY = 1;
    private boolean connectionInProgress;

    public BluetoothClient(T activity){
        this.activity = activity;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
        }
    }

    public boolean enableBlueTooth(int requestCode){
        /*If bluetooth is not supported, infrom user and return...*/
        if(mBluetoothAdapter==null){
            Toast.makeText(activity, String.valueOf(activity.getResources().getText(R.string.bluetooth_not_supported)),Toast.LENGTH_SHORT);
            return false;
        }
        /*If bluetooth is not enabled, go for a request to enable it..*/
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBtIntent, requestCode);
            Toast.makeText(activity, String.valueOf(activity.getResources().getText(R.string.bluetooth_enabling)),Toast.LENGTH_LONG);
            return false;
        }
        else{
            Toast.makeText(activity, String.valueOf(activity.getResources().getText(R.string.bluetooth_enabled)),Toast.LENGTH_LONG);
            return true;
        }
    }

    public Set<BluetoothDevice> getPairedDevices(){
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        return pairedDevices;
    }

    public boolean startDiscovering(BluetoothClientCallbackForDeviceDiscovered callback){
        // Register the BroadcastReceiver
        this.callbackForDeviceDiscovery = callback;
        stopDiscovering();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_UUID);
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        activity.registerReceiver(mReceiver, intentFilter); // Don't forget to unregister during onDestroy

        receiverRegistered = true;
        this.callbackForDeviceDiscovery = callback;
        return mBluetoothAdapter.startDiscovery();
    }

    public void proceedToBluetoothScan(){
        Intent i = new Intent(activity, BluetoothPairedDeviceListActivity.class);
        GlobalData.getInstance().bluetoothClient = this;
        activity.startActivityForResult(i, REQ_DEVICESELECTED);
    }

    public void stopDiscovering(){
        if(receiverRegistered) {
            mBluetoothAdapter.cancelDiscovery();
            activity.unregisterReceiver(mReceiver);
            receiverRegistered = false;
        }
    }

    /***/
    public boolean connectWith(BluetoothDevice device, final TaskCompleted<Boolean> callback){
        this.bluetoothConnectionCallback = callback;
        connectionInitState = false;
        try {
            Parcelable[] uuids = device.getUuids();
            if (uuids == null) {
                if (!device.fetchUuidsWithSdp()) {
                        /*Uuids not supported. So, display an appropriate message.*/
                    callback.onTaskCompleted(false, "Sorry, could not connect to this device.");
                }
            } else {
                proceedToConnect(device, callback, getUUID(uuids));
            }
            return true;
        } catch (Exception e) {
            closeLoadingDialog();
            e.printStackTrace();
            return false;
        }
    }

    /**Return a UUID from the provided parcelable uuids..*/
    private UUID getUUID( Parcelable[] parcelableUUIDs){
        Parcelable[] uuids = null;
        UUID uuid = null;
        try{
            uuids = parcelableUUIDs;
            uuid = UUID.fromString(UUID_STRING);
            int i=0;
            for(i=0;i<uuids.length;i++){
                if(UUID_STRING.equals(((ParcelUuid)uuids[i]).getUuid().toString())){
                    break;
                }
            }
            if(i>=uuids.length){
                uuid = ((ParcelUuid)uuids[i]).getUuid();
            }
        }catch(NullPointerException e){
            e.printStackTrace();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return uuid;
    }

    /**Proceed to connect and communicate via bluetooth..**/
    private void proceedToConnect(BluetoothDevice device, final TaskCompleted<Boolean> callback, UUID uuid){
        if(connectionInProgress){
            bluetoothConnectionCallback.onTaskCompleted(false,"Connection already in progress");
            return;
        }

        this.connectionInProgress = true;
        if(uuid==null){
            closeLoadingDialog();
            bluetoothConnectionCallback.onTaskCompleted(false,"Sorry, could not connect to this device.");
            return;
        }

        /*Try for bluetooth connection..*/
        try{
            socket = device.createInsecureRfcommSocketToServiceRecord(uuid);
            String deviceName = device.getName();
            if (deviceName != null && deviceName.length() > 0) {
                isSocketMobileScanner = deviceName.toLowerCase().indexOf("socket") >= 0;
            }

            AsyncTask<BluetoothSocket, String, BluetoothSocket> connectAndRead = new AsyncTask<BluetoothSocket, String, BluetoothSocket>() {
                /*Some temproary instances required..*/
                private int TIME_DELAY = 150;
                private String theBarCode = "",tempBarCode = "";
                private boolean isFirstScan = true,isBarcodePublished = false;
                private long savedTime = 0l, timeDifference = 0l;
                private long timerDelay = 150;
                /**Timer task to publish the barcode result..**/
                private Timer barcodePublishTimer = new Timer();
                private TimerTask barcodePublishTimerTask = null;


                @Override
                protected BluetoothSocket doInBackground(BluetoothSocket... params) {
                    try {
                        connectionInitState = true;
                        socket.connect();
                        InputStream stream = socket.getInputStream();
                        byte[] buffer = new byte[1024];  // buffer store for the stream
                        int numBytes; // bytes returned from read()
                        publishProgress(activity.getString(R.string.bluetooth_connected), activity.getString(R.string.bluetooth_dialog_message_after_connected_to_barcode_scanner));
                        // Keep listening to the InputStream until an exception occurs
                        closeLoadingDialog();
                        while (true) {
                            try {
                                // Read from the InputStream
                                numBytes = stream.read(buffer);
                                if(isBarcodePublished){
                                    try{
                                        isBarcodePublished = false;
                                        theBarCode = "";
                                        timerDelay = 150;
                                        barcodePublishTimer.cancel();
                                        barcodePublishTimer.purge();
                                        barcodePublishTimerTask = null;
                                        barcodePublishTimerTask = new TimerTask() {
                                            @Override
                                            public void run() {
//                                                Log.d("Published Barcode",theBarCode);
                                                publishProgress(activity.getString(R.string.bluetooth_got_barcode), theBarCode);
                                            }
                                        };
                                        barcodePublishTimer = null;
                                        barcodePublishTimer = new Timer();
                                        barcodePublishTimer.schedule(barcodePublishTimerTask,timerDelay);
                                    }catch(IllegalStateException e){
                                        e.printStackTrace();
                                    }catch(Exception e){
                                        e.printStackTrace();
                                    }
                                }
                                savedTime = System.currentTimeMillis();
                                byte[] msgBytes = new byte[numBytes];
                                for(int i=0;i<numBytes;i++){
                                    msgBytes[i] = buffer[i];
                                }
                                String barcode = "";
                                if (isSocketMobileScanner) {
                                    // custom processing for the barcode value returned by the
                                    // socket barcode chs 7ci - they have prefix and postfix
                                    // 5 bytes and 2 bytes respectively - why that is i have no idea.
                                    if (numBytes >= 8) {
                                        int actualLength = numBytes - 7;
                                        byte[] temp = new byte[actualLength];
                                        for (int i = 0, j = 5; i < actualLength; i++, j++) {
                                            temp[i] = msgBytes[j];
                                        }
                                        barcode = new String(temp, "UTF-8");
                                    }
                                }else {
                                    barcode = new String(msgBytes, "UTF-8");
                                }
                                timeDifference = System.currentTimeMillis() - savedTime;
                                if((timeDifference<TIME_DELAY)){
                                    theBarCode += barcode;
                                    timerDelay += 150;
                                }
                            } catch (IOException e) {
                                try{
                                    CiresonUtilites.logMessage(TAG,"Exception");
                                    e.printStackTrace();
                                    closeLoadingDialog();
                                    publishProgress(activity.getString(R.string.warning), activity.getString(R.string.bluetooth_dialog_message_disconnected_with_barcode_scanner));
                                    socket.close();
                                    socket=null;
                                }catch(NullPointerException ex){
                                    ex.printStackTrace();
                                }catch(Exception ex){
                                    ex.printStackTrace();
                                }
                                break;
                            }catch(Exception e){
                                CiresonUtilites.logMessage(TAG,"Exception");
                                e.printStackTrace();
                                closeLoadingDialog();
                                socket.close();
                                socket=null;
                                publishProgress(activity.getString(R.string.warning), activity.getString(R.string.bluetooth_dialog_message_disconnected_with_barcode_scanner));
                           }

                        }
                        return socket;
                    }catch (IOException e) {
                        closeLoadingDialog();
                        try {
                            if(socket!=null) {
                                socket.close();
                            }
                        } catch (IOException e1) {
                            if(e1!=null){
                                e1.printStackTrace();
                            }
                        }
                        socket = null;
                        if(connectionInitState){
                            if(e!=null){
                                publishProgress(activity.getString(R.string.error), activity.getString(R.string.bluetooth_dialog_message_failed_to_connect_to_scanner) + e.getMessage());
                            }else{
                                publishProgress(activity.getString(R.string.error), activity.getString(R.string.bluetooth_dialog_message_failed_to_connect_to_scanner));
                            }
                            connectionInitState = false;
                        }
                        return null;
                    }
                }

                @Override
                protected void onProgressUpdate(String... values) {
                    super.onProgressUpdate(values);
                    String progressType = values[0];
                    if(activity.getString(R.string.bluetooth_got_barcode).equals(progressType)){
                        callbackForResult.bluetoothResult(values[1]);
                    }
                    else if(activity.getString(R.string.bluetooth_connected).equals(progressType)){
                        if(callback!=null) {
                            callback.onTaskCompleted(true, values[1]);
                        }
                    }
                    else if(activity.getString(R.string.error).equals(progressType)) {
                        if (callback != null) {
                            callback.onTaskCompleted(false, values[1]);
                        }
                    }
                    else{
                        Toast.makeText(activity, values[1], Toast.LENGTH_SHORT).show();
                    }
                    isBarcodePublished = true;
                    barcodePublishTimer.cancel();
                    barcodePublishTimer.purge();
                    barcodePublishTimerTask = null;
                }

                @Override
                protected void onPostExecute(BluetoothSocket bluetoothSocket) {
                    super.onPostExecute(bluetoothSocket);
                    connectionInProgress = false;
                    closeLoadingDialog();

                }
            };
            connectAndRead.execute(socket);
        }catch(IOException exception){
            closeLoadingDialog();
            exception.printStackTrace();
        }catch(Exception exception){
            closeLoadingDialog();
            exception.printStackTrace();
        }
    }

    // Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
//            Log.i(TAG, "On Found broadcast");
            String action = intent.getAction();
            BluetoothDevice bluetoothDevice = null;
            
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
//                Log.i(TAG, "Device found");
                // Get the BluetoothDevice object from the Intent
                bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Add the name and address to an array adapter to show in a ListView
                if(callbackForDeviceDiscovery!=null){
                    callbackForDeviceDiscovery.bluetoothClientFoundDevice(bluetoothDevice);
                }
            }else if(BluetoothDevice.ACTION_UUID.equals(action)){
                bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Parcelable[] uuids = intent.getParcelableArrayExtra(BluetoothDevice.EXTRA_UUID);
                if(uuids==null || uuids.length==0){
                    UUID uuid = UUID.fromString(UUID_STRING);
                    proceedToConnect(bluetoothDevice,
                            BluetoothClient.this.bluetoothConnectionCallback,
                            uuid);
                }
                else {
                    proceedToConnect(bluetoothDevice,
                            BluetoothClient.this.bluetoothConnectionCallback,
                            BluetoothClient.this.getUUID(uuids));
                }
            }
        }
    };

    /*close loading dialog..*/
    private static void closeLoadingDialog(){
        if(CiresonUtilites.getLoadingDialog()!=null&&CiresonUtilites.getLoadingDialog().isShowing()){
            CiresonUtilites.getLoadingDialog().dismiss();
        }
    }

    /*Getters and setters..*/

    public void setCallbackForResult(BluetoothClientCallbackForResult callback){
        this.callbackForResult = callback;
    }

    public void setCallbackForDeviceDiscovered(BluetoothClientCallbackForDeviceDiscovered callback){
        this.callbackForDeviceDiscovery = callback;
    }

    public static interface BluetoothClientCallbackForResult{
        public void bluetoothResult(String value);
    }

    public static interface BluetoothClientCallbackForDeviceDiscovered{
        public void bluetoothClientFoundDevice(BluetoothDevice device);
    }

    public void release(){
        stopDiscovering();
        if(socket!=null){
            try {
                socket.close();
                socket = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setBluetoothConnectionCallback(TaskCompleted<Boolean> bluetoothConnectionCallback) {
        this.bluetoothConnectionCallback = bluetoothConnectionCallback;
    }
}
