
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


package com.cireson.assetmanager.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;

import com.cireson.assetmanager.model.CiresonEnumeration;

import java.lang.ref.WeakReference;
import java.util.ArrayList;



public class TreeView extends ScrollView {

    private CiresonEnumeration dataSource;
    private ArrayList<CiresonEnumeration> checkedItems;
    public boolean isMultiSelect;
    private LinearLayout rootView;
    private WeakReference<RadioButton> checkedRadioButton;
    private WeakReference<Checkable> checkedItem;
    private View itemHeaderView;

    public TreeView(Context context) {
        super(context);
        initTreeView(context);
    }

    public TreeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initTreeView(context);
    }

    public TreeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initTreeView(context);
    }

    private void initTreeView(Context context){
        rootView = new LinearLayout(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        rootView.setLayoutParams(params);
        rootView.setOrientation(LinearLayout.VERTICAL);
        this.addView(rootView);
    }

    //Getter setters
    public ArrayList<CiresonEnumeration> getDataSource() {
        return dataSource==null?null:dataSource.children;
    }

    public void setDataSource(ArrayList<CiresonEnumeration> dataSource) {
        if(this.dataSource==null){
            this.dataSource = new CiresonEnumeration();
        }
        this.dataSource.children = dataSource;
        rootView.removeAllViews();
        TreeViewItem newTreeViewItem = new TreeViewItem(getContext());
        newTreeViewItem.setTreeViewParent(this);
        newTreeViewItem.setRoot(true);
        newTreeViewItem.setDataSource(this.dataSource, rootView);
        this.invalidate();
    }

    public void setCheckedItem(CompoundButton checkedRb, CiresonEnumeration item){
        if(!isMultiSelect && checkedRb.isChecked() ){
            if(checkedItems!=null && checkedItems.size()>0){
                checkedItems.get(0).isChecked = false;
            }
            checkedItems = new ArrayList<CiresonEnumeration>();
            //checkedItems.add(item);
            if(checkedRadioButton!=null) {
                RadioButton rb = checkedRadioButton.get();
                if (rb != null) {
                    rb.setChecked(false);
                }
            }
            checkedRadioButton = new WeakReference<RadioButton>((RadioButton)checkedRb);
        }

        else if(checkedItems==null){
            checkedItems = new ArrayList<CiresonEnumeration>();
        }

        if(checkedRb.isChecked()) {
            item.isChecked = true;
            checkedItems.add(item);
        }
        else{
            item.isChecked = false;
            checkedItems.remove(item);
        }
    }

    public void setSelectedItem(View itemHeader, CiresonEnumeration item){
        if(!isMultiSelect&&((Checkable)itemHeader).isChecked()){
            if(checkedItems!=null && checkedItems.size()>0){
                checkedItems.get(0).isChecked = false;
            }
            checkedItems = new ArrayList<CiresonEnumeration>();

            //checkedItems.add(item);
            if(checkedItem!=null) {
                Checkable view = checkedItem.get();
                if (view != null) {
                    view.setChecked(false);
                }
            }
            checkedItem = new WeakReference<Checkable>((Checkable)itemHeader);

        } else if(checkedItems==null){
            checkedItems = new ArrayList<CiresonEnumeration>();
        }

        if(((Checkable)itemHeader).isChecked()) {
            item.isChecked = true;
            checkedItems.add(item);
        }
        else{
            item.isChecked = false;
            checkedItems.remove(item);
        }
    }

    public void clearSelection(){
        if(checkedItems!=null && checkedItems.size()>0) {
            for (CiresonEnumeration item : checkedItems) {
                item.isChecked = false;
            }
            checkedItems.clear();
            setDataSource(this.dataSource.children);
        }
    }

    public ArrayList<CiresonEnumeration> getCheckedItems() {
        return checkedItems;
    }

    public CiresonEnumeration getFirstCheckedItem(){
        if(checkedItems!=null && checkedItems.size()>0){
            return checkedItems.get(0);
        }
        return null;
    }
}
