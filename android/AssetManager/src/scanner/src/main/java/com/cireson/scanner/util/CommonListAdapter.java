
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


package com.cireson.scanner.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

public abstract class CommonListAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    private List listOfObjects; //generic Object List
    private int viewId;

    /**
     * Holder that provides fast access to objects and views.
     * Subclass it for use.
     */
    public static class ViewHolder{
        //reference to list object.
        public Object data;
    }

    /**
     * Click listener base class.
     */
    public static abstract class OnClickListener implements View.OnClickListener{

        private ViewHolder viewHolder;

        /**
         * @param holder The holder of the clickable items.
         */
        public OnClickListener(ViewHolder holder){
            viewHolder = holder;
        }

        //delegate the click event
        public void onClick(View view){
            onClick(view, viewHolder);
        }

        /**
         * Implement click behavior
         * @param view  The clicked view
         */
        public abstract void onClick(View view, ViewHolder viewHolder);
    }

    /**
     *  The long click listener base class.
     */
    public static abstract class OnLongClickListener implements View.OnLongClickListener {

        private ViewHolder  viewHolder;

        /**
         * @param holder The holder of the clickable item.
         */
        public OnLongClickListener(ViewHolder holder){
            viewHolder = holder;
        }

        //delegate the click event
        public boolean onLongClick(View view){
            onLongClick(view, viewHolder);
            return true;
        }

        /**
         * Implement click behavior here
         * @param view The clicked view
         */
        public abstract void onLongClick(View view, ViewHolder viewHolder);
    }


    /**
     * @param context The current context
     * @param currentViewId  The resource if of the list view item
     * @param objectList The objects list or null, if you like to indicate an empty list
     */
    public CommonListAdapter(Context context, int currentViewId, List objectList){

        layoutInflater = LayoutInflater.from(context);
        listOfObjects = objectList;
        viewId = currentViewId;

        if(listOfObjects == null ){
            listOfObjects = new ArrayList<Object>();
        }
    }

    /**
     *  The number of objects in the list.
     */
    public int getCount(){
        return  listOfObjects.size();
    }

    /**
     *
     * @param itemAt int representing an objects position in the list
     * @return return the object at the position itemAt
     */
    public Object getItem(int itemAt){
        return listOfObjects.get(itemAt);
    }

    /**
     *  Make a view to hold each row. The method is instantiated for each list object.
     *  Using the holder pattern avoids findViewById() calls.
     */
    public View getView( int position, View view, ViewGroup parent){

       //keep reference to the child views to avoid unnecessary findViewById() on each row.
        ViewHolder holder;

        //When views is not null, reuse it directly, there is no need to re-inflate it.
        if(view == null) {
            view = layoutInflater.inflate(viewId, null);
            //create the user's implementation
            holder = createHolder(view);
            // we set the holder as tag
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        //update the object's reference
        holder.data = getItem(position);
        //call user's implementation
        bindHolder(holder);

        return view;
    }

    /**
     * Creates custom holder, that carries references - ImageView and /or TextView.
     * If necessary connect your clickable View object with the PrivateOnClickListener, or PrivateOnLongClickListener
     * @param view The view for the new holder object
     * @return ViewHolder object
     */
    protected abstract ViewHolder createHolder(View view);

    /**
     * Binds the data from user's object to the holder
     * @param holder The holder that shall represent the data object.
     */
    protected abstract void bindHolder(ViewHolder holder);
}
