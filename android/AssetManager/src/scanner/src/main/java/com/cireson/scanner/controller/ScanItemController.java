
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

import com.cireson.scanner.model.ScanItemModel;

import java.util.ArrayList;

public class ScanItemController {

    private static ArrayList<ScanItemModel> modelItems;

    public ScanItemController(){
        if (modelItems == null){
            modelItems = new ArrayList<ScanItemModel>();
        }
    }

    public String getScanItemText(int index){
        return modelItems.get(index).getScanItemText();
    }

    public void setScanItemText(int index, String value){
        modelItems.get(index).setScanItemText(value);
    }

    public Boolean getScanItemCheckbox(int index){
        return modelItems.get(index).getScanItemCheckbox();
    }

    public void setScanItemCheckbox(int index, Boolean value){
        modelItems.get(index).setScanItemCheckbox(value);
    }

    public Boolean getShowScanItemButton(int index){
        return modelItems.get(index).getShowScanItemButton();
    }

    public void setShowScanItemButton(int index, Boolean value){
        modelItems.get(index).setShowScanItemButton(value);
    }

    public ArrayList<ScanItemModel> getItems(){
        return this.modelItems;
    }

    public void add(ScanItemModel item){
        this.modelItems.add(item);
    }

    public boolean containsItem(String text){
        for (ScanItemModel data : modelItems) {
            if(data.getScanItemText().toString().toLowerCase().trim() == text.toLowerCase().trim()){
                return true;
            }
        }
        return false;
    }
}
