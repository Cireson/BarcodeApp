
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


package com.cireson.assetmanager.controller;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Checkable;
import android.widget.ListView;
import android.widget.TextView;

import com.cireson.assetmanager.R;
import com.cireson.assetmanager.model.GlobalData;
import com.cireson.assetmanager.util.BluetoothClient;
import com.cireson.assetmanager.util.CiresonDialogs;
import com.cireson.assetmanager.util.CiresonUtilites;
import com.cireson.assetmanager.util.TaskCompleted;

import java.util.ArrayList;
import java.util.Set;

public class BluetoothPairedDeviceListActivity extends CiresonBaseActivity implements BluetoothClient.BluetoothClientCallbackForDeviceDiscovered {

    /*Instances..*/
    private ArrayList<CompareableDevice> bluetoothDevices;
    private ListView listView;
    private BluetoothClient bluetoothClient;
    private String bluetoothMacAddress = "";
    private CiresonDialogs dialog = null;
    private ProgressDialog progressDialog = null;

    public static boolean DEVICE_SAVED_STATE = false;
    public static BluetoothPairedDeviceListActivity bluetoothPairedDeviceListActivity = null;

    private TaskCompleted<Boolean> blueToothConnectedCallback = new TaskCompleted<Boolean>() {
        @Override
        public void onTaskCompleted(final Boolean connected, String message) {
            CiresonUtilites.displayMessage(getResources().getString(R.string.bluetooth_message),message, BluetoothPairedDeviceListActivity.this).setCiresonDialogActions(new CiresonDialogs.CiresonDialogActions() {
                @Override
                public void handleDialogsClickEvent(DialogInterface dialogInterface, int i) {
                    if(connected){
                        setResult(RESULT_OK);
                        finish();
                    }
                    else{

                    }
                }
            }).show(BluetoothPairedDeviceListActivity.this.getFragmentManager(), "");
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paired_device_list);
        /*..Bluetooth client..*/
        bluetoothClient = GlobalData.getInstance().bluetoothClient;
        bluetoothClient.setBluetoothConnectionCallback(blueToothConnectedCallback);
        bluetoothMacAddress = GlobalData.getInstance().getFromStorage("bluetooth_mac_address");
        Set<BluetoothDevice> devicesSet = bluetoothClient.getPairedDevices();
        DEVICE_SAVED_STATE = false;
        setResult(RESULT_CANCELED);
        bluetoothDevices = new ArrayList<CompareableDevice>();
        if(devicesSet!=null){
            for(BluetoothDevice device:devicesSet){
                bluetoothDevices.add(new CompareableDevice(device));
            }
        }
        listView =(ListView) findViewById(R.id.list);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setResult(RESULT_OK);
                BluetoothClient.selectedDevice = (BluetoothDevice)listAdapter.getItem(position);
                /**/
                ((Checkable)view).setChecked(true);

                switch (GlobalData.ACTIVE_BLUETOOTH_CALLER) {
                    case BluetoothClient.SCAN_ASSETS_ACTIVITY:
                        try{
                            bluetoothClient.connectWith(BluetoothClient.selectedDevice,blueToothConnectedCallback);
                            /*Show loading to indicate it is working..*/
                            CiresonUtilites.initLoadingDialog(R.string.progress_dialog_message,R.style.cireson_progress_dialog_theme,BluetoothPairedDeviceListActivity.this);
                            CiresonUtilites.getLoadingDialog().setCancelable(false);
                            CiresonUtilites.getLoadingDialog().setCanceledOnTouchOutside(false);
                            CiresonUtilites.getLoadingDialog().show();
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                        break;
                    case BluetoothClient.SETTINGS_ACTIVITY:
                        if(BluetoothClient.selectedDevice.getAddress().equals(bluetoothMacAddress)){
                            CiresonUtilites.displayMessage(getResources().getString(R.string.bluetooth_message),getResources().getString(R.string.bluetooth_dialog_message_device_available), BluetoothPairedDeviceListActivity.this).show(BluetoothPairedDeviceListActivity.this.getFragmentManager(),"");
                        }else if(!BluetoothClient.selectedDevice.getAddress().equals("")||!BluetoothClient.selectedDevice.getAddress().isEmpty()){
                            useDeviceFor(getResources().getString(R.string.bluetooth_dialog_message_to_replace_device));
                        }else{
                            useDeviceFor(getResources().getString(R.string.bluetooth_dialog_message_for_storage_of_device));
                        }
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.paired_device_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void startScanning(View view){
        bluetoothClient.startDiscovering(this);
    }

    public void useDeviceFor(String message){
        dialog = new CiresonDialogs();
        dialog.setContext(this);
        dialog.setTitle(getResources().getString(R.string.dialog_confirmation))
                .setNumberOfButtons(2)
                .setMessage(message)
                .setPositiveButtonMessage(getResources().getString(R.string.dialog_yes))
                .setNegativeButtonMessage(getResources().getString(R.string.dialog_no));
        /*Handle requests for each cases (Store bluetooth mac address or use bluetooth device or replace previously stored mac
        address with the new one.), if positive button is clicked.*/
        class PairedDeviceListDialog implements CiresonDialogs.CiresonDialogActions<PairedDeviceListDialog> {
            public void handleDialogsClickEvent(DialogInterface di, int i) {
                switch (i) {
                    case DialogInterface.BUTTON_POSITIVE:
                        GlobalData.getInstance().saveToStorage("bluetooth_mac_address", BluetoothClient.selectedDevice.getAddress());
                        DEVICE_SAVED_STATE = true;
                        bluetoothClient.release();
                        BluetoothPairedDeviceListActivity.this.finish();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        /*Dispose dialog..*/
                        dialog.dismiss();
                        break;
                    default:
                        break;
                }
            }
        };
        dialog.setCiresonDialogActions(new PairedDeviceListDialog());
        dialog.show(getFragmentManager(),"");
    }

    @Override
    public void bluetoothClientFoundDevice(BluetoothDevice device) {
        CompareableDevice compDevice = new CompareableDevice(device);
        if(!bluetoothDevices.contains(compDevice)){
            bluetoothDevices.add(0, compDevice);
            listAdapter.notifyDataSetChanged();
        }
//        bluetoothDevices.remove(compDevice);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bluetoothClient.stopDiscovering();
    }

    public static BluetoothPairedDeviceListActivity getInstance(){
        if(bluetoothPairedDeviceListActivity==null){
            bluetoothPairedDeviceListActivity = new BluetoothPairedDeviceListActivity();
        }
        return bluetoothPairedDeviceListActivity;
    }

    /*List base class..*/
    private BaseAdapter listAdapter = new BaseAdapter() {

        @Override
        public int getCount() {
            return bluetoothDevices.size();
        }

        @Override
        public BluetoothDevice getItem(int position) {
            return bluetoothDevices.get(position).device;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View listRow = convertView;
            if(listRow==null){
                listRow = getLayoutInflater().inflate(R.layout.device_list_row, parent,false);
            }
            TextView txtName = (TextView)listRow.findViewById(R.id.name);
            BluetoothDevice device = getItem(position);
            if(device.getAddress().equals(bluetoothMacAddress)){
                ((Checkable)listRow).setChecked(true);
            }
            else{
                ((Checkable)listRow).setChecked(false);
            }
            txtName.setText(device.getName());
            return listRow;
        }

    };

    private static class CompareableDevice {
        private BluetoothDevice device;
        public CompareableDevice(BluetoothDevice device){
            this.device = device;
        }

        @Override
        public boolean equals(Object o) {
            boolean isEqual = false;
            if(o!=null && o.getClass()==CompareableDevice.class){
                isEqual = ((CompareableDevice)o).device.getAddress().equals(device.getAddress());
            }
            return  isEqual;
        }

        @Override
        public int hashCode() {
            return device.getAddress().hashCode();
        }
    }
}
