
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
 * Created by welcome on 7/9/2014.
 */
public class CiresonBaseActivitySecond extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_up,R.anim.no_effect);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(CiresonUtilites.getLoadingDialog()!=null&&CiresonUtilites.getLoadingDialog().isShowing()){
            CiresonUtilites.getLoadingDialog().dismiss();
        }
        overridePendingTransition(R.anim.no_effect,R.anim.slide_down);
    }

}
