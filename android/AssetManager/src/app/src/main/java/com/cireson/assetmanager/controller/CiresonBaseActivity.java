
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

import android.app.Activity;
import android.os.Bundle;

import com.cireson.assetmanager.R;
import com.cireson.assetmanager.util.CiresonUtilites;

/**
 * Created by devkhadka on 3/29/14.
 */
public class CiresonBaseActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(CiresonUtilites.getLoadingDialog()!=null&&CiresonUtilites.getLoadingDialog().isShowing()){
            try {
                CiresonUtilites.getLoadingDialog().dismiss();
            }
            catch (Exception ex){
                ex.printStackTrace();
            }
        }
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}

