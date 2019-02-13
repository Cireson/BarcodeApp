
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


package com.cireson.assetmanager.service;

import android.content.Context;
import android.util.Log;

import com.cireson.assetmanager.model.ServiceResponse;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.ArrayList;

/**
 * Created by welcome on 7/13/2014.
 */
/**The BatchAPICaller accepts one or more object to be passed to the service.
 * Here, object type T has to be a type of BaseJsonObject.
 * Each object is forwarded with a request in JSON format and successive responses are collected in a structure.
 * If a response is successful, field batchSuccessResponse holds success response messages and
 * if a response is failed, field batchFailureResponse holds failure messages..*/

 public class BatchAPICaller<T extends BaseJsonObject> extends AsyncHttpResponseHandler implements APICaller.Callback{

    /*Instances..*/
    private ArrayList<T> requestObjects = null;
    private ArrayList<String> batchSuccessResponse;
    private ArrayList<String> batchFailureResponse;
    private BatchAPICallback batchAPICallback;
    private int counter = 0;
    private Context context;

    /**Default constructor that initializes instances as ArrayLists..*/
    public BatchAPICaller(){
        requestObjects = new ArrayList<T>();
        batchSuccessResponse = new ArrayList<String>();
        batchFailureResponse = new ArrayList<String>();
    }

    /**Constructor accepts a single object of type BaseJsonObject.
     * This object is added to requestObjects array list and
     * a callback is initialized to pass responses back..*/
    public BatchAPICaller(T requestObject, Context context, BatchAPICallback batchAPICallback){
        this();
        requestObjects.add(requestObject);
        this.batchAPICallback = batchAPICallback;
        this.context = context;
    }

    /**Constructor accepts a list of objects of type BaseJsonObject.
     * This lists of object is assigned to requestObjects array list and
     * a callback is initialized to pass responses back....*/
    public BatchAPICaller(ArrayList<T> requestObjects,Context context, BatchAPICallback batchAPICallback){
        this();
        this.requestObjects = requestObjects;
        this.batchAPICallback = batchAPICallback;
        this.context = context;
    }

    public BatchAPICaller(T[] assets,Context context, BatchAPICallback batchAPICallback){
        this();
        for(T a : assets){
            this.requestObjects.add(a);
        }
        this.batchAPICallback = batchAPICallback;
        this.context = context;
    }

    /*Make successive API calls for corresponding request objects..*/
    public void sendBatchRequests(String currentMethodName){
        /*This variable holds a request string in JSON format..*/
        String jsonRequest = "";
        for(int i=0;i<requestObjects.size();i++){
            jsonRequest = requestObjects.get(i).toJson();
            if(!jsonRequest.equals("")||jsonRequest!=null){
                APICaller<ServiceResponse> apiCaller = new APICaller<ServiceResponse>(ServiceResponse.class,context);
                apiCaller.setMethodForBaseUrl(currentMethodName);
                apiCaller.callApi(jsonRequest,this);
            }else{
                Log.d("Json State", "JSON is empty");
            }
        }
    }

    /**From both http client's response callbacks i.e. onSuccess and onFailure, collect response and add to an array list,
     * if response is from success, add response to batchSuccessResponse array list and
     * if response is from failure, add response to batchFailureResponse array list..*/
    private void collectResponse(boolean state,String resp){
        if(state){
            batchSuccessResponse.add(resp);
        }else{
            batchFailureResponse.add(resp);
        }
        counter++;
        if(counter==requestObjects.size()){
            /*Create empty string arrays to hold corresponding responses..*/
            String[] tempBatchSuccessResponses = new String[batchSuccessResponse.size()];
            String[] tempBatchFailureResponses = new String[batchFailureResponse.size()];

            /*All responses are collected as batch and are ready to be forwarded to the callback..*/
            batchAPICallback.onBatchRequestComplete(
                    batchSuccessResponse.toArray(tempBatchSuccessResponses),
                    batchFailureResponse.toArray(tempBatchFailureResponses), null);
            /*Clear counter..*/
            counter = 0;
        }
    }

    /*Callback responded with a message in case of exception during http call..*/
    public void onException(String exceptionMessage){
        batchAPICallback.onBatchRequestComplete(null, null, exceptionMessage);
    }

    @Override
    public void onApiCallComplete(Object obj, String message) {
        /*If no response object is received back from service..*/
        if(obj==null){
            collectResponse(false,message);
        }else{
            collectResponse(true,message);
        }
    }

    /*Callback interface..*/
    public interface BatchAPICallback {
        public void onBatchRequestComplete(String[] successBatchResponses,String[] failureBatchResponses,String exceptionMessage);
    }

}
