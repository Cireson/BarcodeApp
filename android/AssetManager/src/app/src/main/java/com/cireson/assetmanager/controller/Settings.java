
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

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.cireson.assetmanager.R;

/**
 * Created by welcome on 4/2/14.
 */
public class Settings extends CiresonBaseActivity {
    public void onCreate(Bundle objectState){
        super.onCreate(objectState);
        setContentView(R.layout.activity_settings);
    }

    /*handle saving..*/
    public void save(View v){
        Intent i = new Intent(this,MainNavigationActivity.class);
        startActivity(i);
    }

}
