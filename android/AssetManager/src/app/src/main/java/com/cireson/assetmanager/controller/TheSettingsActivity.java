
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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.cireson.assetmanager.R;
import com.cireson.assetmanager.model.GlobalData;
import com.cireson.assetmanager.model.User;
import com.cireson.assetmanager.util.BluetoothClient;
import com.cireson.assetmanager.util.CiresonUtilites;

public class TheSettingsActivity extends CiresonBaseActivity{

    /*Instances..*/
    private BluetoothClient bluetoothClient;
    private TextView currentBluetoothDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_);
        currentBluetoothDevice = (TextView)findViewById(R.id.id_settings_current_bluetooth_device);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case BluetoothClient.REQ_DISCOVER_DEVICE:
                    break;
                case BluetoothClient.REQ_ENABLE_BLUETOOTH:
                    GlobalData.ACTIVE_BLUETOOTH_CALLER = BluetoothClient.SETTINGS_ACTIVITY;
                    bluetoothClient.proceedToBluetoothScan();
                    break;
                case BluetoothClient.REQ_DEVICESELECTED:
                    if(BluetoothPairedDeviceListActivity.DEVICE_SAVED_STATE){
                        currentBluetoothDevice.setText(BluetoothClient.selectedDevice.getName());
                        CiresonUtilites.displayMessage(getResources().getString(R.string.bluetooth_message),getResources().getString(R.string.bluetooth_dialog_message_device_saved_sucess), this).show(this.getFragmentManager(),"");
                    }
                    break;
                default:
                    break;
            }
        }else if(resultCode==BluetoothClient.REQ_DISCOVER_TIME){
            Intent i = new Intent(this, BluetoothPairedDeviceListActivity.class);
            GlobalData.getInstance().bluetoothClient = bluetoothClient;
            startActivityForResult(i, BluetoothClient.REQ_DISCOVER_DEVICE);
        }
    }

    public void chooseBluetoothScanner(View view){
        /*Initiate bluetooth scanning..*/
        if(bluetoothClient==null){
            GlobalData.ACTIVE_BLUETOOTH_CALLER = BluetoothClient.SETTINGS_ACTIVITY;
            bluetoothClient = new BluetoothClient(this);
        }
        bluetoothClient.release();
        if(bluetoothClient.enableBlueTooth(BluetoothClient.REQ_ENABLE_BLUETOOTH)){
            GlobalData.ACTIVE_BLUETOOTH_CALLER = BluetoothClient.SETTINGS_ACTIVITY;
            bluetoothClient.proceedToBluetoothScan();
        }
    }

    public void Logout(View view){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(R.string.warning);
        alertDialogBuilder
                .setMessage(R.string.settings_message)
                .setCancelable(false)
                .setPositiveButton(R.string.dialog_yes,new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        logout();
                    }
                })
                .setNegativeButton(R.string.dialog_cancel,new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    void logout(){
        User user=new User();
        user.removeUserFromStorage();
        Intent i = new Intent(this,LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }
}
