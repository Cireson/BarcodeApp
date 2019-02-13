
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
//  CiresonApiService.m
//  AssetManager
//

#import "CiresonApiService.h"
#import "GenericCiresonApiCall.h"
#import "BatchAPICall.h"

@interface CiresonApiService ()
@end

@implementation CiresonApiService

static CiresonApiService* _sharedService = Nil;

@synthesize baseUrl;

+(CiresonApiService*)sharedService {
    if (_sharedService == Nil) {
        _sharedService = [[CiresonApiService alloc] init];
    }
    return _sharedService;
}

-(GenericCiresonApiCall*)getSaveReceiveAssetsRequest:(ReceiveAssetsData *)data{
    GenericCiresonApiCall* updateProperties = [[GenericCiresonApiCall alloc] init];
    User* loggedInUser = [User loggedInUser];
    NSDictionary* params =
            @{
              @"Token":loggedInUser.token,
              @"ProjectionObject":
                  data.purchaseOrder.jsonObject
              };
    [data.purchaseOrder updateAssets];
    updateProperties.method = UpdateProjectionMethod;
    updateProperties.params = params;
    return updateProperties;
}

// Returns a batch api object that saves assets using the HardwareAsset_MobileProjectionType
-(BatchAPICall*)getSaveAssetsRequest:(NSArray *)assets {
    if (assets == Nil || assets.count == 0) {
        return Nil;
    }
    BatchAPICall* batchCall = [[BatchAPICall alloc] init];
    User* loggedInUser = [User loggedInUser];
    for (Asset* a in assets) {
        GenericCiresonApiCall* updateProperties = [[GenericCiresonApiCall alloc] init];
        
        NSDictionary* params =
        @{
            @"Token": loggedInUser.token,
            @"ProjectionObject": a.jsonObject
        };

        updateProperties.method = UpdateProjectionMethod;
        updateProperties.params = params;
        [batchCall addRequest:updateProperties];
    }
    return batchCall;
}

-(GenericCiresonApiCall*)getSearchAssetsWithBarcodeValues:(NSArray*)barcodes {
    GenericCiresonApiCall* searchRequest = [[GenericCiresonApiCall alloc] init];
    
    NSMutableArray* expressions = [[NSMutableArray alloc] init];
    for (NSString* barcode in barcodes) {
        NSDictionary* assetTagCriteria = [CiresonRequestBase getExpressionCriteriaWithTypeId:HardwareAsset_Type propertyId:HardwareAsset_Property_AssetTag usingGenericProperty:NO operator:Equals_Operator value:barcode];
        NSDictionary* serialNumberCriteria = [CiresonRequestBase getExpressionCriteriaWithTypeId:HardwareAsset_Type propertyId:HardwareAsset_Property_SerialNumber usingGenericProperty:NO operator:Equals_Operator value:barcode];
        [expressions addObject:assetTagCriteria];
        [expressions addObject:serialNumberCriteria];
    }
    searchRequest.method = GetProjectionDataMethod;
    User* loggedInUser = [User loggedInUser];
    NSDictionary* criteria = [CiresonRequestBase getOrExpressionCriteria:expressions];
    criteria = [CiresonRequestBase getFinalExpressionCriteria:criteria];
    searchRequest.params = [CiresonRequestBase getParametersForRequestWithToken:loggedInUser.token typeProjectionId:HardwareAsset_MobileProjectionType criteria:criteria];
    return searchRequest;
}

// get all purchase orders is done by searching all purchase orders
// with displayname = %% and purchase order date > now - 90
-(GenericCiresonApiCall*) getAllPurchaseOrders {
    GenericCiresonApiCall* searchRequest = [[GenericCiresonApiCall alloc] init];
    User* loggedInUser = [User loggedInUser];
    searchRequest.method = GetProjectionDataMethod;
    NSDictionary* matchAllPurchaseOrderCiteria = [CiresonRequestBase getExpressionCriteriaWithTypeId:PurchaseOrder_Type propertyId:PurchaseOrder_Property_DisplayName usingGenericProperty:NO operator:Like_Operator value:WildcardValue];

    NSDictionary* dateCriteria = [CiresonRequestBase getExpressionCriteriaWithTypeId:PurchaseOrder_Type propertyId:PurchaseOrder_property_PurchaseOrderDate usingGenericProperty:NO operator:GreaterThanOrEqual_Operator value:[CiresonRequestBase getTodayPlusMinusDays:-500]]; // TODO: change this to 90 days
    
    NSDictionary* criteria = [CiresonRequestBase getAndExpressionCriteria:matchAllPurchaseOrderCiteria right:dateCriteria];
    criteria = [CiresonRequestBase getFinalExpressionCriteria:criteria];
    searchRequest.params = [CiresonRequestBase getParametersForRequestWithToken:loggedInUser.token
                                                     typeProjectionId:PurchaseOrder_MobileProjectionType criteria:criteria];
    
    return searchRequest;
}

-(GenericCiresonApiCall*) getSearchByPurchaseOrderRequest:(NSString*)purchaseOrderNumber {
    GenericCiresonApiCall* searchRequest = [[GenericCiresonApiCall alloc] init];
    User* loggedInUser = [User loggedInUser];
    
    searchRequest.method = GetProjectionDataMethod;
    
    NSDictionary* criteria = [CiresonRequestBase getFinalExpressionCriteriaWithTypeId:PurchaseOrder_Type propertyId:PurchaseOrder_Property_PurchaseOrderNumber usingGenericProperty:NO operator:Equals_Operator value:purchaseOrderNumber];
    
    searchRequest.params = [CiresonRequestBase getParametersForRequestWithToken:loggedInUser.token
                                                     typeProjectionId:PurchaseOrder_MobileProjectionType criteria:criteria];

    return searchRequest;
}

-(GenericCiresonApiCall*) getEnumeration: (NSString*)enumerationTypeId {
    GenericCiresonApiCall* enumRequest = [[GenericCiresonApiCall alloc] init];
    User* loggedInUser = [User loggedInUser];

    enumRequest.method = GetEnumerationMethod;
    enumRequest.params = [CiresonRequestBase getParametersForRequestWithToken:loggedInUser.token
                                                     typeProjectionId:HardwareAsset_Status_Enumeration_Id criteria:Nil];
    
    return enumRequest;
}

-(GenericCiresonApiCall*) getLocations {
    GenericCiresonApiCall* locationRequest = [[GenericCiresonApiCall alloc] init];
    User* loggedInUser = [User loggedInUser];
    
    locationRequest.method = GetProjectionDataMethod;
    NSDictionary* criteria = [CiresonRequestBase getFinalExpressionCriteriaWithTypeId:Location_Type propertyId:Location_Property_DisplayName usingGenericProperty:NO operator:Like_Operator value:WildcardValue];
    
    locationRequest.params = [CiresonRequestBase getParametersForRequestWithToken:loggedInUser.token
                                                     typeProjectionId:Location_Minimum_ProjectionType criteria:criteria];
    
    return locationRequest;
}

-(GenericCiresonApiCall*) getOrganizations {
    GenericCiresonApiCall* searchRequest = [[GenericCiresonApiCall alloc] init];
    User* loggedInUser = [User loggedInUser];
    
    NSDictionary* criteria = [CiresonRequestBase getFinalExpressionCriteriaWithTypeId:Organization_Type propertyId:Organization_Property_DisplayName usingGenericProperty:NO operator:Like_Operator value:WildcardValue];
    
    searchRequest.method = GetProjectionDataMethod;
    searchRequest.params = [CiresonRequestBase getParametersForRequestWithToken:loggedInUser.token
                                                     typeProjectionId:Organization_Minimum_ProjectionType criteria:criteria];
    
    return searchRequest;
}

-(GenericCiresonApiCall*) getSearchUsers:(NSString*)searchText {
    GenericCiresonApiCall* searchRequest = [[GenericCiresonApiCall alloc] init];
    User* loggedInUser = [User loggedInUser];
    searchRequest.method = GetProjectionDataMethod;
    // %searchtext% - equivalent to contains
    NSString* searchQuery = [NSString stringWithFormat:@"%@%@%@", @"%", searchText, @"%"];
    NSDictionary* criteria = [CiresonRequestBase getFinalExpressionCriteriaWithTypeId:User_Type propertyId:User_Property_DisplayName usingGenericProperty:NO operator:Like_Operator value:searchQuery];
    
    searchRequest.params = [CiresonRequestBase getParametersForRequestWithToken:loggedInUser.token
                                                     typeProjectionId:User_ProjectionType criteria:criteria];
    
    return searchRequest;
}

-(GenericCiresonApiCall*) getCostCenters {
    GenericCiresonApiCall* searchRequest = [[GenericCiresonApiCall alloc] init];
    User* loggedInUser = [User loggedInUser];
    
    searchRequest.method = GetProjectionDataMethod;
    NSDictionary* criteria = [CiresonRequestBase getFinalExpressionCriteriaWithTypeId:CostCenter_Type propertyId:CostCenter_Property_DisplayName usingGenericProperty:NO operator:Like_Operator value:WildcardValue];
    
    searchRequest.params = [CiresonRequestBase getParametersForRequestWithToken:loggedInUser.token
                                                     typeProjectionId:CostCenter_Minimum_ProjectType criteria:criteria];
    
    return searchRequest;
}

// properties are ANDED and array values are ored.
-(GenericCiresonApiCall*) getSearchByAssetPropertiesWithLocations:(NSArray*)locations status:(NSArray*)status organizations:(NSArray*)organizations custodians:(NSArray*)custodians costcenters:(NSArray*)costcenters receivedDate:(NSDate*)date {
    typedef void (^ProcessExpressions)(NSArray* expressionsToProcess);
    
    NSMutableArray* andExpressions = [[NSMutableArray alloc] init];
    
    ProcessExpressions processor = ^(NSArray* expressionsToProcess) {
        if (expressionsToProcess != Nil) {
            // if more than one expressions
            if (expressionsToProcess.count > 1) {
                NSDictionary* orExpressions = [CiresonRequestBase getOrExpressionCriteria:expressionsToProcess];
                [andExpressions addObject:orExpressions];
            }
            else {
                [andExpressions addObject:[expressionsToProcess objectAtIndex:0]];
            }
        }
    };

    
    // location expressions.
    NSArray* objectExpressions = [CiresonRequestBase getExpressionsForObjects:locations relationshipTypeId:HardwareAsset_HasLocation_RelationshipType typeId:Location_Type propertyId:Property_Id usingGenericProperty:YES operator:Equals_Operator valuePropertyName:@"objectId"];
    processor(objectExpressions);
    
    // status expressions
    objectExpressions = [CiresonRequestBase getExpressionsForObjects:status typeId:HardwareAsset_Type propertyId:HardwareAsset_Property_Status usingGenericProperty:NO operator:Equals_Operator valuePropertyName:@"enumId"];
    processor(objectExpressions);
    
    // organization expressions
    objectExpressions = [CiresonRequestBase getExpressionsForObjects:organizations relationshipTypeId:HardwareAsset_HasOrganization_RelationshipType typeId:Organization_Type propertyId:Property_Id usingGenericProperty:YES operator:Equals_Operator valuePropertyName:@"objectId"];
    processor(objectExpressions);

    // custodian expressions
    objectExpressions = [CiresonRequestBase getExpressionsForObjects:custodians relationshipTypeId:HardwareAsset_HasPrimaryUser_RelationshipType typeId:User_Type propertyId:Property_Id usingGenericProperty:YES operator:Equals_Operator valuePropertyName:@"objectId"];
    processor(objectExpressions);
    
    // cost centers
    objectExpressions = [CiresonRequestBase getExpressionsForObjects:costcenters relationshipTypeId:HardwareAsset_HasCostCenter_RelationshipType typeId:CostCenter_Type propertyId:Property_Id usingGenericProperty:YES operator:Equals_Operator valuePropertyName:@"objectId"];
    processor(objectExpressions);
    
    // received date.
    if (date != Nil) {
        NSDate* yesterday = [CiresonRequestBase yesterday];
        yesterday = [CiresonRequestBase endOfDay:yesterday];
        NSDate* tomorrow = [CiresonRequestBase tomorrow];
        tomorrow = [CiresonRequestBase beginningOfDay:tomorrow];
        NSString* yesterdayDateStr = [CiresonRequestBase getDateString:yesterday];
        NSString* tomorrowDateStr = [CiresonRequestBase getDateString:tomorrow];
        
        NSDictionary* yesterdayExpression = [CiresonRequestBase getExpressionCriteriaWithTypeId:HardwareAsset_Type propertyId:HardwareAsset_Property_ReceivedDate usingGenericProperty:NO operator:GreaterThanOrEqual_Operator value:yesterdayDateStr];
        NSDictionary* tomorrowExpression = [CiresonRequestBase getExpressionCriteriaWithTypeId:HardwareAsset_Type propertyId:HardwareAsset_Property_ReceivedDate usingGenericProperty:NO operator:LessThanOrEqual_Operator value:tomorrowDateStr];
        
        NSString *localDate = [NSDateFormatter localizedStringFromDate:yesterday dateStyle:NSDateFormatterMediumStyle timeStyle:NSDateFormatterMediumStyle];
        NSLog(@"Local date %@", localDate);
        
        localDate = [NSDateFormatter localizedStringFromDate:tomorrow dateStyle:NSDateFormatterMediumStyle timeStyle:NSDateFormatterMediumStyle];
        NSLog(@"Local date %@", localDate);
        

        
        [andExpressions addObject:yesterdayExpression];
        [andExpressions addObject:tomorrowExpression];
    }
    GenericCiresonApiCall* searchRequest = [[GenericCiresonApiCall alloc] init];
    
    NSDictionary* criteria = Nil;
    if (andExpressions.count > 1) {
        criteria = [CiresonRequestBase getAndExpressionCriteria:andExpressions];
    }
    else {
        criteria = [andExpressions objectAtIndex:0];
    }
    criteria = [CiresonRequestBase getFinalExpressionCriteria:criteria];
    User* loggedInUser = [User loggedInUser];
    searchRequest.method = GetProjectionDataMethod;
    searchRequest.params = [CiresonRequestBase getParametersForRequestWithToken:loggedInUser.token
                                                               typeProjectionId:HardwareAsset_MobileProjectionType criteria:criteria];
    return searchRequest;
    
}

@end
