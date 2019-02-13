
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
//  CiresonApiService.h
//  AssetManager
//

#import <Foundation/Foundation.h>

#import "GenericCiresonApiCall.h"
#import "CiresonRequestBase.h"
#import "WorkflowData.h"
#import "ReceiveAssetsData.h"
#import "BatchAPICall.h"

@interface CiresonApiService : NSObject

@property(nonatomic,copy) NSString* baseUrl;

+(CiresonApiService*) sharedService;

-(GenericCiresonApiCall*)getSearchAssetsWithBarcodeValues:(NSArray*)barcodes;
-(GenericCiresonApiCall*) getSearchByPurchaseOrderRequest:(NSString*)purchaseOrderNumber;
-(GenericCiresonApiCall*) getAllPurchaseOrders;
-(GenericCiresonApiCall*) getEnumeration: (NSString*)enumerationTypeId;
-(GenericCiresonApiCall*) getLocations;
-(GenericCiresonApiCall*) getOrganizations;
-(GenericCiresonApiCall*) getSearchUsers:(NSString*)searchText;
-(GenericCiresonApiCall*) getCostCenters;
-(GenericCiresonApiCall*) getSearchByAssetPropertiesWithLocations:(NSArray*)locations status:(NSArray*)status organizations:(NSArray*)organizations custodians:(NSArray*)custodians costcenters:(NSArray*)costcenters receivedDate:(NSDate*)date;
-(GenericCiresonApiCall*)getSaveReceiveAssetsRequest:(ReceiveAssetsData*)data;
-(BatchAPICall*)getSaveAssetsRequest:(NSArray*)assets;


@end
