
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cireson.assetmanager.R;
import com.cireson.assetmanager.model.CiresonEnumeration;


public class TreeViewItem extends LinearLayout implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private TreeView treeViewParent;
    private boolean _isExpanded = false;
    private CiresonEnumeration _dataSource;
    private ViewGroup parentView;
    private LinearLayout childrenHolder;
    private LayoutInflater inflater;
    private static final int LEVEL_PADDING = 50;
    private View itemHeader;
    private ImageView imgExpandCollapse;
    private boolean isRoot = false;

    public TreeViewItem(Context context) {
        super(context);
        initTreeViewItem(context);
    }

    public TreeViewItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        initTreeViewItem(context);
    }

    public TreeViewItem(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initTreeViewItem(context);
    }

    private void initTreeViewItem(Context context){
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setLayoutParams(params);
        this.setOrientation(LinearLayout.VERTICAL);
    }

    //Getter and setters
    public boolean isExpanded(){
        return _isExpanded;
    }

    public void setExpanded(boolean expanded){
        if(_dataSource==null){
            return;
        }
        _isExpanded = expanded;
        if(expanded){
            imgExpandCollapse.setImageResource(R.drawable.minus);
            if(childrenHolder==null){
                childrenHolder = new LinearLayout(this.getContext());
                LinearLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                childrenHolder.setOrientation(LinearLayout.VERTICAL);
                params.leftMargin = LEVEL_PADDING;
                childrenHolder.setLayoutParams(params);
                this.addView(childrenHolder);
            }
            for(CiresonEnumeration cenum: _dataSource.children){
                TreeViewItem newTreeViewItem = new TreeViewItem(getContext());
                newTreeViewItem.setTreeViewParent(treeViewParent);
                newTreeViewItem.setDataSource(cenum, childrenHolder);
            }
        }
        else{
            imgExpandCollapse.setImageResource(R.drawable.plus);
            if(childrenHolder!=null){
                childrenHolder.removeAllViews();
            }
        }
        this.invalidate();
    }

    public CiresonEnumeration getDataSource(){
        return _dataSource;
    }

    public void setDataSource(CiresonEnumeration dataSource, ViewGroup parentView){
        _dataSource=dataSource;
        this.parentView = parentView;
        itemHeader = inflater.inflate(R.layout.tree_view_item, this, false);
        imgExpandCollapse = (ImageView) itemHeader.findViewById(R.id.imgExpand);
        imgExpandCollapse.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setExpanded(!_isExpanded);
            }
        });
        TextView txtTitle = (TextView) itemHeader.findViewById(R.id.txtTitle);
        if(isRoot){
            itemHeader.setVisibility(GONE);
            setExpanded(true);
        }
        else {
            txtTitle.setText(dataSource.text);
            if (dataSource.children != null && dataSource.children.size() > 0) {
                imgExpandCollapse.setVisibility(VISIBLE);
            } else {
                imgExpandCollapse.setVisibility(INVISIBLE);
            }
            itemHeader.setOnClickListener(this);
            ((Checkable)itemHeader).setChecked(dataSource.isChecked);
            treeViewParent.setSelectedItem(itemHeader,_dataSource);
        }
        this.addView(itemHeader);
        parentView.addView(this);
        this.invalidate();
    }

    public void setTreeViewParent(TreeView parent){
        treeViewParent = parent;
    }

    public void setRoot(boolean isRoot) {
        this.isRoot = isRoot;
    }

    public void expandOrCollapseItems(View view){
        setExpanded(!_isExpanded);
    }

    //events
    @Override
    public void onClick(View v) {
        ((Checkable)v).toggle();
        treeViewParent.setSelectedItem(itemHeader,_dataSource);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        treeViewParent.setCheckedItem(buttonView, _dataSource);
    }
}
