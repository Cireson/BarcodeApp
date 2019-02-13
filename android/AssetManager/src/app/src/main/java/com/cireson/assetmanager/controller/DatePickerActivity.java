
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

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;

import com.cireson.assetmanager.R;
import com.cireson.assetmanager.model.GlobalData;
import com.cireson.assetmanager.util.CiresonConstants;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Created by welcome on 4/15/2014.
 */
public class DatePickerActivity extends CiresonBaseActivitySecond implements DatePicker.OnDateChangedListener{

    /*Instances..*/
    private android.widget.DatePicker datePicker = null;
    private Calendar gregorianCalender = null;
    private DateFormatSymbols dateFormatSymbols = null;
    private GlobalData globalData = null;
    private String[] months = null;

    /*Static fields..*/
    public final static String KEY_DATE_RESULT = "KEY_DATE_RESULT";
    public final static String UTC_TIME = "T00:00:00-08:00";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_picker);
        globalData = GlobalData.getInstance();
        /*Initializing datepicker..*/
        datePicker = (android.widget.DatePicker)findViewById(R.id.datePicker);
        /*If selected year instance has its default value, initialize year, month and day with current date with corresponding values.*/
        if(globalData.selectedYear == -1){
            globalData.selectedYear = datePicker.getYear();
            globalData.selectedMonth = datePicker.getMonth();
            globalData.selectedDayOfMonth = datePicker.getDayOfMonth();
        }

        datePicker.init(globalData.selectedYear,globalData.selectedMonth,globalData.selectedDayOfMonth,this);

        gregorianCalender = new GregorianCalendar();
        dateFormatSymbols = new DateFormatSymbols();
        months = dateFormatSymbols.getMonths();

        gregorianCalender.set(GregorianCalendar.YEAR,globalData.selectedYear);
        gregorianCalender.set(GregorianCalendar.MONTH, globalData.selectedMonth);
        gregorianCalender.set(GregorianCalendar.DAY_OF_MONTH,globalData.selectedDayOfMonth);

    }

    /*Done button clicked..*/
    public void done(View v){
        Intent i = new Intent();
        /*First temporarily save selected date to global instances.
           These date credentials are reassigned when reset..*/
        globalData.selectedYear = gregorianCalender.get(GregorianCalendar.YEAR);
        globalData.selectedMonth = gregorianCalender.get(GregorianCalendar.MONTH);
        globalData.selectedDayOfMonth = gregorianCalender.get(GregorianCalendar.DAY_OF_MONTH);

        /*Assign a selected date in a defined format..*/
        //2014-11-07T06:00:00.000Z
        SimpleDateFormat formatUTC = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        /*Using Timezone for washington, America*/
        //formatUTC.setTimeZone(TimeZone.getTimeZone("UTC−08:00 ET"));
        String dateToAssignInJson =  formatUTC.format(gregorianCalender.getTime());
        String dateToDisplay = String.format("%s %d, %d",
                months[globalData.selectedMonth],
                globalData.selectedDayOfMonth,
                globalData.selectedYear);

        globalData.currentDate = String.valueOf(dateToAssignInJson);

        i.putExtra(DatePickerActivity.KEY_DATE_RESULT, dateToDisplay);
        switch (GlobalData.ACTIVE_MAIN_MENU){
            case CiresonConstants.RECEIVED_ASSETS:
            case CiresonConstants.EDIT_ASSETS:
                globalData.getTemporaryAsset().receivedDate = dateToAssignInJson;
                break;
            case CiresonConstants.SWAP_ASSETS:
                if(globalData.getSwaper().getSwap()){
                    globalData.getTemporaryAsset().loanReturnedDate = dateToAssignInJson;
                }else{
                    globalData.getTemporaryAsset().loanedDate = dateToAssignInJson;
                }
                break;
            case CiresonConstants.DISPOSE_ASSETS:
                globalData.getTemporaryAsset().disposalDate = dateToAssignInJson;
                break;
            default:
                break;
        }
        setResult(RESULT_OK, i);
        finish();
    }

    public void cancel(View v){
        Intent i = new Intent();
        i.putExtra(DatePickerActivity.KEY_DATE_RESULT, "");
        globalData.currentDate = "";
        globalData.selectedYear = -1;
        globalData.getTemporaryAsset().receivedDate = null;
        globalData.getTemporaryAsset().loanReturnedDate = null;
        globalData.getTemporaryAsset().loanedDate = null;
        globalData.getTemporaryAsset().disposalDate = null;
        setResult(RESULT_OK, i);
        finish();
    }

    /*Clear/reset the date.*/
    public void reset(View v){
        //datePicker.init(globalData.selectedYear,globalData.selectedMonth,globalData.selectedDayOfMonth,this);
        datePicker.updateDate(globalData.selectedYear,globalData.selectedMonth,globalData.selectedDayOfMonth);
    }

    @Override
    public void onDateChanged(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
         /*Set and get date via Gregorian Calender..*/
        gregorianCalender.set(GregorianCalendar.YEAR,year);
        gregorianCalender.set(GregorianCalendar.MONTH, monthOfYear);
        gregorianCalender.set(GregorianCalendar.DAY_OF_MONTH,dayOfMonth);
    }
}

