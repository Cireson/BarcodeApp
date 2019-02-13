
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


package com.cireson.scanner.model;

public class ScanItemModel {

    private String scanItemText;
    private Boolean scanItemCheckbox;
    private Boolean showScanItemButton;

    public String getScanItemText(){
        return scanItemText;
    }

    public void setScanItemText(String value){
        scanItemText = value;
    }

    public Boolean getScanItemCheckbox(){
        return scanItemCheckbox;
    }

    public void setScanItemCheckbox(Boolean value){
        scanItemCheckbox = value;
    }

    public Boolean getShowScanItemButton(){
        return showScanItemButton;
    }

    public void setShowScanItemButton(Boolean value){
        showScanItemButton = value;
    }

}
