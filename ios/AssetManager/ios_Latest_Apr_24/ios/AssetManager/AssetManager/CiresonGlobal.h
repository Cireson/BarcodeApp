
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
//
//  CiresonGlobal.h
//  AssetManager
//

extern NSString * const SelectedBluetoothDeviceName;
extern NSString * const SelectedProtocolStringName;


typedef NS_ENUM(NSInteger, TextPickerItemMode) {
    AssetName,
    AssetSerialNumber,
    AssetTagNumber,
    DisposalReferenceNumber,
    Manufacturer,
    Model
};

typedef NS_ENUM(NSInteger, LongTextViewMode) {
    Notes
};

typedef NS_ENUM(NSInteger, AssetProperties) {
    APStatus,
    APLocation,
    APCostCenter,
    APOrganization,
    APCustodian,
    APLoanReturnedDate,
    APNotes,
    APLoanedDate,
    APDisposalDate,
    APDisposalReferenceNumber,
    APClearPrimaryUser,
    APClearCustodian,
    APReceivedDate,
    APModel,
    APManufacturer,
    APAssetName,
    APSerialNumber,
    APAssetTag
};


