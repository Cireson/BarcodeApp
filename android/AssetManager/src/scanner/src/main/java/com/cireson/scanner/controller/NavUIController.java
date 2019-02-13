
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


package com.cireson.scanner.controller;

import android.graphics.drawable.Drawable;

import com.cireson.scanner.model.NavUIModel;

import java.util.ArrayList;

public class NavUIController {

    private NavUIModel model = new NavUIModel();
    // private ArrayList<NavUIModel> navigationItems = new ArrayList();

    public String getNavText(){
        return this.model.getNavText();
    }

    public void setNavText(String value){
        this.model.setNavText(value);
    }

    public Drawable getNavImageDrawable(){
        return this.model.getNavImageDrawable();
    }

    public void setNavImageDrawable(Drawable value){
        this.model.setNavImageDrawable(value);
    }
}
