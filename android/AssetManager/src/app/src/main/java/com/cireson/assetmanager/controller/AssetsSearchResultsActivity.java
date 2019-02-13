
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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.cireson.assetmanager.R;
import com.cireson.assetmanager.model.Assets;
import com.cireson.assetmanager.model.GlobalData;
import com.cireson.assetmanager.model.PurchaseOrder;
import com.cireson.assetmanager.service.APICaller;
import com.cireson.assetmanager.service.BrowsePORequest;
import com.cireson.assetmanager.service.SearchPurchaseOrderRequest;
import com.cireson.assetmanager.util.CiresonConstants;
import com.cireson.assetmanager.util.CiresonUtilites;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AssetsSearchResultsActivity extends CiresonBaseActivity implements APICaller.Callback<ArrayList<PurchaseOrder>>, AdapterView.OnItemClickListener{

    /*Instances..*/
    public static String TAG = "Cireson Scanner AssetsSearchResultsActivity";
    private TextView resultCountTextView,subTitleTextView;
    private ListView listView;

    private GlobalData globalData;
    private int valueFromIntent = 0;
    private final static int day=0;
    private final static int month=1;
    private final static int year=2;
    private final static String [] months={"January","February","March","April","May","June","July","August","September","October","November","December"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asset_search_results);

        /*Get view to be updated dynamically..*/
        resultCountTextView = (TextView) findViewById(R.id.assetsSearchResultTitleNumber);
        subTitleTextView = (TextView) findViewById(R.id.assetsSearchResultSubTitleText);

        globalData = GlobalData.getInstance();

        /*Prepare APICaller..*/
        final Type type = new TypeToken<ArrayList<PurchaseOrder>>(){}.getType();
        APICaller<PurchaseOrder> apiCaller = new APICaller<PurchaseOrder>(PurchaseOrder.class,type, this );
        apiCaller.setMethodForBaseUrl("GetProjectionByCriteria");
        valueFromIntent = getIntent().getIntExtra(CiresonConstants.NAV_FROM_SEARCH_ASSETS,0);

        CiresonUtilites.initLoadingDialog(R.string.progress_dialog_message,R.style.cireson_progress_dialog_theme,this);
        CiresonUtilites.getLoadingDialog().show();

        switch (valueFromIntent){
            case CiresonConstants.SEARCH_ASSETS:
                /*If this is about searching assets..*/
                String purchaseOrderNumber = getIntent().getExtras().getString(AssetsSearchActivity.PURCHASE_ORDER_NUMBER);
                resultCountTextView.setText("");
                subTitleTextView.setText(purchaseOrderNumber);
                apiCaller.callArrayApi(new SearchPurchaseOrderRequest(purchaseOrderNumber),this);
                break;
            case CiresonConstants.BROWSE_ORDERS:
                /*If this is about browsing purchase order..*/
                apiCaller.callArrayApi(new BrowsePORequest(),this);
                subTitleTextView.setText(getResources().getText(R.string.search_assets_result_sub_title));
                break;
            default:
                break;
        }
        listView = (ListView) findViewById(android.R.id.list);
        listView.setOnItemClickListener(this);
    }

    public void goNext(View v){
        proceedToScanProducts();
    }

    public void proceedToScanProducts(){
        Intent i = new Intent(this, AssetsScannerActivity.class);
        /*Here, AssetsScannerActivity.TYPE_OF_NEXT_ACTIVITY_AFTER_SCAN is a static field that holds a reference
        * to next activity to be navigated from AssetsScannerActivity. In this case next activity is AssetsEditorActivity. */
        AssetsScannerActivity.TYPE_OF_NEXT_ACTIVITY_AFTER_SCAN = AssetsEditorActivity.class;
        StringBuilder titleMatchAssetsFor = new StringBuilder();
        titleMatchAssetsFor.append(getResources().getString(R.string.search_assets_text_for_scan_assets_title)+" ");
        titleMatchAssetsFor.append(String.valueOf(subTitleTextView.getText()));
        i.putExtra(AssetsScannerActivity.ASSETS_SCANNER_EXTRA, titleMatchAssetsFor.toString());
        startActivity(i);
    }

    private static int[] getDate(String passedDate){
        String date=passedDate;
        int dateArray[] = new int[3];
        Calendar cal2= Calendar.getInstance();
        Date formattedDate=null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try{
            formattedDate = sdf.parse(date);
            cal2.setTime(formattedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        dateArray[day]= cal2.get(Calendar.DAY_OF_MONTH);
        dateArray[month]=cal2.get(Calendar.MONTH);
        dateArray[year]=cal2.get(Calendar.YEAR);
        return dateArray;
    }

    @Override
    public void onApiCallComplete(ArrayList<PurchaseOrder> object, String error) {
        if(object!=null&&object.size()>0){
            SearchResultAdapter adapter = new SearchResultAdapter(object,this);
            listView.setAdapter(adapter);
            resultCountTextView.setText(String.valueOf(object.size()));
            /*Assign this response object to a field of SaveReceiveAssets model..*/
            globalData.getSavedReceiveAssets().projectionObject = object.get(0);
            CiresonUtilites.getLoadingDialog().dismiss();
        }
        /*If no product is found as a search result or any error occured display provided message from the service call..*/
        else{
            /*Destroy this page and display a relavant message in the former page..*/
            Intent i = new Intent();
            i.putExtra(AssetsSearchActivity.MESSAGE,error);
            setResult(RESULT_OK,i);
            finish();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        view.setBackgroundResource(R.drawable.list_row_background);
        globalData.searchedAssets = ((SearchResultAdapter) listView.getAdapter()).getItem(position).associatedAssets;
        if(globalData.searchedAssets!=null){
            for(Assets asset:globalData.searchedAssets){
                asset.isMatched = false;
            }
        }
        /*If there is no associated assets (as Source_HardwareAssetHasPurchaseOrder in PurchaseOrder model),
         a null value is assigned and handled as below..*/
        if(globalData.searchedAssets==null||
                (globalData.searchedAssets!=null&&globalData.searchedAssets.size()==0)){
            globalData.isAssociatedAssetsAvailable = false;
            globalData.searchedAssets = new ArrayList<Assets>();
        }else{
            globalData.isAssociatedAssetsAvailable = true;
        }
        proceedToScanProducts();
    }

    public static class SearchResultAdapter extends BaseAdapter{
        private ArrayList<PurchaseOrder> purOrder;
        private Context context = null;
        private LayoutInflater inflater = null;
        private String displayName = null, statusName = null, cost = null;

        public SearchResultAdapter(ArrayList<PurchaseOrder> purOrder,Context ctx){
            this.purOrder = purOrder;
            context = ctx;
            inflater = (LayoutInflater) ctx.getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() { return purOrder.size(); }

        @Override
        public PurchaseOrder getItem(int position) {
            return purOrder.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View cellView = convertView;
            if(convertView==null){
                cellView = inflater.inflate(R.layout.asset_search_result_row, null);
            }
            TextView txtOrderId = (TextView) cellView.findViewById(R.id.orderId);
            TextView txtOrderStatus = (TextView) cellView.findViewById(R.id.orderStatus);
            TextView txtOrderCost = (TextView) cellView.findViewById(R.id.orderCost);

            /*Set display name, statusName and cost of a purchase order..*/
            displayName = purOrder.get(position).displayName;
            txtOrderId.setText(purOrder.get(position).displayName);

            if(purOrder.get(position).purchaseOrderStatus!=null&&
                    purOrder.get(position).purchaseOrderStatus.name!=null&&
                    !purOrder.get(position).purchaseOrderStatus.name.isEmpty()){
                statusName = purOrder.get(position).purchaseOrderStatus.name;
            }else{
                statusName = new String("N/A");
            }

            txtOrderStatus.setText(String.format("Status: %s",statusName));

            /*Specify total amount for this purchase order with local currency value..*/
            NumberFormat nf = NumberFormat.getInstance(Locale.getDefault());
            // edited by subash
            cost = String.format("Total Cost: %s",purOrder.get(position).amount+"");
            //original
            //cost = String.format("Total Cost: %s",NumberFormat.getCurrencyInstance(Locale.getDefault()).format(purOrder.get(position).amount));
            txtOrderCost.setText(cost);

            TextView txtMonth=(TextView)cellView.findViewById(R.id.month);
            TextView txtDay=(TextView)cellView.findViewById(R.id.day);
            TextView txtYear=(TextView)cellView.findViewById(R.id.year);
            txtMonth.setVisibility(View.VISIBLE);
            txtDay.setVisibility(View.VISIBLE);
            txtYear.setVisibility(View.VISIBLE);
            txtDay.setGravity(Gravity.CENTER_HORIZONTAL);
            txtDay.setTextSize(15);
            String purchaseOrderDate=purOrder.get(position).purchaseOrderDate;
            if(purchaseOrderDate!=null &&  purchaseOrderDate!="") {
                int[] date = getDate(purchaseOrderDate);
                String monthOfDate = months[date[month]];
                String dayOfDate = String.valueOf(date[day]);
                String yearOfDate = String.valueOf(date[year]);
                txtMonth.setText(monthOfDate);
                txtDay.setText(dayOfDate);
                txtYear.setText(yearOfDate);
            }
            else{
                txtMonth.setVisibility(View.INVISIBLE);
                txtYear.setVisibility(View.GONE);
                txtDay.setText("N/A");
                txtDay.setTextSize(20);
            }
            return cellView;
        }
    }
}
