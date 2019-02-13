
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


package com.cireson.scanner.view;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.cireson.scanner.R;
import com.cireson.scanner.model.ScanItemModel;


/**
 * Created by Ravindra on 2/17/14.
 */
public class MatchAssetsAdapter extends ArrayAdapter<ScanItemModel> {

    private static String TAG = "MatchAssetsAdapter";
    private final LayoutInflater layoutInflater;

    static class ViewHolder {
        TextView textView;
        CheckBox checkBoxView;
        Button buttonView;
    }

    public MatchAssetsAdapter(final Context context, final int resource) {
        super(context, resource);
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public void notifyDataSetChanged(){
        super.notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        ViewHolder viewHolder;

        if(convertView == null) {
            convertView = layoutInflater.inflate(R.layout.matched_assets_row, parent, false);
            viewHolder = new ViewHolder();

            viewHolder.textView = (TextView) convertView.findViewById(R.id.matched_asset_item_number);
            viewHolder.checkBoxView = (CheckBox) convertView.findViewById(R.id.matched_asset_item_check_box);
            viewHolder.buttonView = (Button) convertView.findViewById(R.id.matched_asset_item_button);

            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.textView.setText(getItem(position).getScanItemText());
        viewHolder.textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16.0f);

        viewHolder.checkBoxView.setChecked(getItem(position).getScanItemCheckbox());

        if(getItem(position).getShowScanItemButton()){
            viewHolder.buttonView.setVisibility(View.VISIBLE);
        }else {
            viewHolder.buttonView.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }
}
