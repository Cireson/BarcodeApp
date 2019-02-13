
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

import com.cireson.scanner.R;
import com.cireson.scanner.model.NavUIModel;
import com.cireson.scanner.util.DynamicHeightImageView;
import com.cireson.scanner.util.DynamicHeightTextView;

public class MainNavAdapter extends ArrayAdapter<NavUIModel> {

    private static String TAG = "MainNavAdapter";
    private final LayoutInflater layoutInflater;


    static class ViewHolder {
        DynamicHeightTextView textLine;
        DynamicHeightImageView imageView;
    }


    public MainNavAdapter(final Context context, final int resource) {
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
            convertView = layoutInflater.inflate(R.layout.main_nav_row, parent, false);
            viewHolder = new ViewHolder();

            viewHolder.imageView = (DynamicHeightImageView) convertView.findViewById(R.id.navImage);
            viewHolder.textLine = (DynamicHeightTextView) convertView.findViewById(R.id.navTextLine);

           convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.textLine.setText(getItem(position).getNavText());
        viewHolder.textLine.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16.0f);
        viewHolder.imageView.setImageDrawable(getItem(position).getNavImageDrawable());


        return convertView;
    }
}
