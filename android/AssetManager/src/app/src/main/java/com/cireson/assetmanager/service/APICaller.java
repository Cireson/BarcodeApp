
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
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.cireson.assetmanager.R;
import com.cireson.assetmanager.model.GlobalData;
import com.cireson.assetmanager.util.CiresonConstants;
import com.cireson.assetmanager.util.CiresonUtilites;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class APICaller<T extends BaseJsonObject> extends AsyncHttpResponseHandler {

    /*API Instances..*/
    private boolean inProgress = false;
    private Callback<T> objectCallback;
    private Callback<ArrayList<T>> arrayCallback;
    private Class<T> returnObjType;
    private Type returnArrayType;
    private GlobalData globalData = GlobalData.getInstance();

    private static StringBuilder BASE_URL = null;
    private static StringBuilder apiUrl = null;
    private static String methodName = "Login";
    private AsyncHttpClient httpClient = new AsyncHttpClient();
    static Context appContext = null;

    private static final Pattern jsonArrayPattern = Pattern.compile("^\\s*\\[");//JSON is array if starts with [
    private static final String ENETUNREACH = "ENETUNREACH";

    /*Public Instacances..*/
    public static Integer FILE_ID = -1;
    public static boolean SERVICE_CALL_FAILED = false;

    /*Interfaces to be implemented by aby API Caller..*/
    public interface Callback<T> {
        void onApiCallComplete(T obj, String error);
    }

    /*This is a default constructor and ..*/
    public APICaller() {
        this.returnArrayType = null;
        this.returnObjType = null;
    }

    /*Takes object type and app context..*/
    public APICaller(Class<T> type, Context context) {
        this(type, null, context);
    }

    /*Takes object type, array type hold by a generic collection and app context..*/
    public APICaller(Class<T> objType, Type arrayType, Context context) {
        this.returnArrayType = arrayType;
        this.returnObjType = objType;
        appContext = context;
    }

    /*Call this method to complete the URL with corresponding method name..*/
    public void setMethodForBaseUrl(String methodName) {
        BASE_URL = getBaseUrl();
        apiUrl = new StringBuilder(BASE_URL);
        apiUrl = apiUrl.append(methodName);
    }

    /*Call this method with an object of type BaseJsonObject and Callback*/
    public void callApi(BaseJsonObject obj, Callback<T> callback) {
        callApi(obj.toJson(), callback);
    }

    /*Call this method with a JSON string and Callback*/
    public void callApi(String json, Callback<T> callback) {
        this.objectCallback = callback;
        callApiWithString(json);
    }

    /*Call this method with object of type BaseJasonObject and Callback with ArrayList of an Object which is to be obtained as an array..*/
    public void callArrayApi(BaseJsonObject obj, Callback<ArrayList<T>> callback) {
        callArrayApi(obj.toJson(), callback);
    }

    /*Call this method with JSON string and Callback with ArrayList of an Object which is to be filled in as an array..*/
    public void callArrayApi(String json, Callback<ArrayList<T>> callback) {
        this.arrayCallback = callback;
        callApiWithString(json);
    }

    /*Call this method only with json string.*/
    private void callApiWithString(String json) {
        if (APICaller.SERVICE_CALL_FAILED) {
            localJSONReader.execute(FILE_ID);
        } else if (!inProgress) {
            inProgress = true;
            if (!json.equals("") || json != null) {
                try {
                    StringEntity entity = new StringEntity(json);
                    httpClient.setTimeout(60000);
                    httpClient.post(appContext, apiUrl.toString(), entity, "application/json", this);
                } catch (UnsupportedEncodingException e) {
                    sendCallback(null, null, appContext.getResources().getString(R.string.api_call_unsupported_encoding));
                } catch (IllegalArgumentException e) {
                    sendCallback(null, null, appContext.getResources().getString(R.string.api_call_illigal_argument));
                }
            } else {
                sendCallback(null, null, appContext.getResources().getString(R.string.api_call_json_empty));
            }
        } else {
            sendCallback(null, null, appContext.getResources().getString(R.string.api_call_in_progress));
        }
}

    /*If server connection has failed or service call is failed, assist with response data locally..*/
    private AsyncTask<Integer, Integer, String> localJSONReader = new AsyncTask<Integer, Integer, String>(){
        @Override
        protected String doInBackground(Integer... ids) {
            InputStream inputStream = appContext.getResources().openRawResource(ids[0]);
            String jsonString = "";
            Writer writer = new StringWriter();
            char[] buffer = new char[1024];
            try {
                Reader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            }catch (Exception e) {  e.printStackTrace();   }
            finally{
                try {   inputStream.close(); }
                catch (IOException e) {     e.printStackTrace();    }
            }
            jsonString = writer.toString();
            return jsonString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s==null||s.isEmpty()){
                s = "No Json";
                onFailure(null,s);
            }else{
                onSuccess(0,s);
            }
        }
    };

    /*After asynchronous call is successful (with both http and local calls..)..*/
    @Override
    public void onSuccess(int code, String resp) {
        /*Set current apiUrl instance to null before a new instance gets cteated during api call..*/
        apiUrl = null;
        if(resp==null){
            sendCallback(null, null, appContext.getResources().getString(R.string.api_call_parsing_error));
        }else{
            boolean isRespArray = jsonArrayPattern.matcher(resp).find();
            /*If this request call did not require any response data to be parsed into an object..*/
            if(isRespArray && objectCallback!=null){
                sendCallback(null, null, appContext.getResources().getString(R.string.api_call_array_response_instead));
            }
            else {
                boolean success = false;
                ArrayList<T> arrayRetVal = null;
                T objRetVal = null;
                try {
                    Log.d("Response Success::", resp);
                    if(isRespArray) {
                        arrayRetVal = BaseJsonObject.arrayFromJson(resp, this.returnArrayType);
                        globalData.jsonArray = new JSONArray(resp);
                    }
                    else{
                        objRetVal = BaseJsonObject.fromJson(resp, this.returnObjType);
                        globalData.jsonObject = new JSONObject(resp);
                    }
                    success = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (success) {
                    sendCallback(objRetVal, arrayRetVal,"");
                } else {
                    sendCallback(null, null, appContext.getResources().getString(R.string.api_call_parsing_error));
                }
            }
        }
    }

    /*After asynchronous call fails (with both http and local calls)..*/
    @Override
    public void onFailure(Throwable err, String resp) {
        /*Set current apiUrl instance to null before a new instance gets cteated during api call..*/
        apiUrl = null;
        /*First of all, check if this error  due to wifi disconnection or problem with internet connection.
        If this is the case, check for wifi connection and if wifi connection is fine check for other error
        message from Throwable instance err in method parameter..*/
        Log.d("Response on failure :",(resp = (resp==null?"No response":resp)));
        boolean internetConnectionState = true;
        if(err!=null&&err.getCause()!=null){
            String message = err.getCause().getMessage();
            if(message!=null){
                int indexOfENETUNREACH = message.indexOf(ENETUNREACH);
                if(indexOfENETUNREACH>-1){
                    if(!CiresonUtilites.isWifiConnectionAvailable(appContext)){
                        internetConnectionState = false;
                        sendCallback(null,null,appContext.getResources().getString(R.string.api_call_response_no_wifi_connection));
                    }
                }
            }
          }
        /*If wifi connection is ok..*/
        if(internetConnectionState){
            /*There is no problem with Internet connection so, go on..*/
            try{
                Log.d("Response Failed::",globalData.apiCallFailureHandler.message.toString());
                if(globalData.apiCallFailureHandler.message.length()>0){
                    globalData.apiCallFailureHandler.message.delete(0,globalData.apiCallFailureHandler.message.length()-1);
                }
                globalData.apiCallFailureHandler = BaseJsonObject.fromJson(resp,globalData.apiCallFailureHandler.getClass());
                /*Handle invalid token state..*/
                if(globalData.apiCallFailureHandler.success==false
                 &&globalData.apiCallFailureHandler.message.toString().equalsIgnoreCase(GlobalData.USER_TOKEN_INVALID)){
                    if(appContext!=null){
                        if(CiresonUtilites.getLoadingDialog()!=null&&CiresonUtilites.getLoadingDialog().isShowing()){
                            CiresonUtilites.getLoadingDialog().dismiss();
                        }
                        sendCallback(null, null,"");
                        Toast.makeText(appContext, CiresonConstants.TOKEN_EXPIRED_MESSAGE, Toast.LENGTH_LONG).show();
                        CiresonUtilites.redirectForLogin(appContext);
                    }
                }else{
                    sendCallback(null, null, globalData.apiCallFailureHandler.message.toString());
                }
            }catch(Exception e){
                e.printStackTrace();
                sendCallback(null, null, err.getMessage());
            }
        }
    }

    /*Send callback result to the caller..*/
    private void sendCallback(T obj, ArrayList<T> array, String error){
        if(objectCallback!=null){
            objectCallback.onApiCallComplete(obj, error);
        }
        else if(arrayCallback!=null){
            ArrayList<T> retArray = array;
            if(retArray==null && obj!=null){
                retArray =new ArrayList<T>();
                retArray.add(obj);
            }
            arrayCallback.onApiCallComplete(retArray, error);
        }
    }

    public static void setBaseUrl(String url){
        GlobalData gd = GlobalData.getInstance();
        gd.saveToStorage("com.cireson.assetmanager.service.APICaller.BaseUrl", url);
        BASE_URL = new StringBuilder(url);
    }

    public static StringBuilder getBaseUrl(){
        if(BASE_URL==null || "".equals(BASE_URL)){
            GlobalData gd = GlobalData.getInstance();
            BASE_URL = new StringBuilder(gd.getFromStorage("com.cireson.assetmanager.service.APICaller.BaseUrl"));
        }
        return  BASE_URL;
    }

}

