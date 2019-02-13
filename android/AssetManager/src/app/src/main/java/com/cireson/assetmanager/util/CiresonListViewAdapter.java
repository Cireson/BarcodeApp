
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

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by welcome on 4/15/2014.
 */
public abstract class CiresonListViewAdapter<T> extends BaseAdapter {
    /*Required Instances..*/
    private List<T> displayList;

    public CiresonListViewAdapter(){
    }

    public void setItems(List<T> items){
        displayList = items;
        this.notifyDataSetChanged();
    }

    public CiresonListViewAdapter(List<T> items){
        displayList = items;
    }

    /*Operations */
    /*Operations to override..*/
    @Override
    public int getCount() {
        return displayList.size();
    }

    @Override
    public T getItem(int position) {
        return displayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public abstract View getView(int position, View convertView, ViewGroup parent) ;

    public List<T> getDisplayList(){
        return displayList;
    }
}
