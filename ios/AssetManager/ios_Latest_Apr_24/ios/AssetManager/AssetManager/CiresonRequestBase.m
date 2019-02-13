
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
//  CiresonRequestBase.m
//  AssetManager
//

#define TRACE_PARAMS
#import "CiresonRequestBase.h"


#define NUM_SECS_IN_24_HOURS 24*60*60


@interface CiresonRequestBase ()

@end

@implementation CiresonRequestBase

@synthesize successHandler, errorHandler, method;

+(NSMutableDictionary*) getParametersForRequestWithToken:(NSString*)token
                              typeProjectionId:(NSString*)typeProjectionId
                                      criteria:(NSDictionary*)criteria

{
    NSDictionary* params = Nil;
    if (criteria != Nil) {
        params =
            @{
              @"Token":token,
              @"Id":typeProjectionId,
              @"Criteria":criteria
          };
    }
    else {
        params =
        @{
          @"Token":token,
          @"Id":typeProjectionId
        };
    }
    
/*     NSDictionary* jsonParameters = @"{
    @"Token": -3912343,
    @"Action": "GetData",
    "Id": "62bc2bc9-14be-3ff9-1ff4-ba3e0930f03c",
    "Criteria": {
        "Base": {
            "Expression": {
                "SimpleExpression": {
                    "ValueExpressionLeft": {
                        "Property": "$Context/Property[Type='a604b942-4c7b-2fb2-28dc-61dc6f465c68']/28b1c58f-aefa-a449-7496-4805186bd94f$"
                    },
                    "Operator": "Equal",
                    "ValueExpressionRight": {
                        "Value": "IR16"
                    }
                }
            }
        }
    }
}
 */
    return [NSMutableDictionary dictionaryWithDictionary:params];
}

+(NSDictionary*) getAndExpressionCriteria:(NSDictionary*)left right:(NSDictionary*)right {
    NSArray* expressions = [NSArray arrayWithObjects:left,right, nil];
    return [CiresonRequestBase getAndExpressionCriteria:expressions];
}

+(NSDictionary*) getOrExpressionCriteria:(NSDictionary*)left right:(NSDictionary*)right {
    NSArray* expressions = [NSArray arrayWithObjects:left,right, nil];
    return [CiresonRequestBase getOrExpressionCriteria:expressions];
}

// given a list of expressions ands them together
+(NSDictionary*) getAndExpressionCriteria:(NSArray*)expressions {
    NSDictionary* andExpressionCriteria =  @{@"And" :
                                                 @{@"Expression":
                                                       expressions
                                                   }
                                             };
    return andExpressionCriteria;
}

// given a list of epxressions ors them together
+(NSDictionary*) getOrExpressionCriteria:(NSArray*)expressions {
    NSDictionary* andExpressionCriteria =  @{@"Or" :
                                                 @{@"Expression":
                                                       expressions
                                                   }
                                             };
    return andExpressionCriteria;

}

+(NSDictionary*) getFinalExpressionCriteria:(NSDictionary*)expression {
    return @{@"Base":
                 @{@"Expression": expression }
    };
}

+(NSDictionary*) getFinalExpressionCriteriaWithTypeId:(NSString*)typeId
                                                      propertyId:(NSString*)propertyId
                                            usingGenericProperty:(BOOL)isGenericProperty
                                                        operator:(NSString*)oper
                                                           value:(NSString*)value {
    NSDictionary* expressionCriteria = [CiresonRequestBase getExpressionCriteriaWithTypeId:typeId propertyId:propertyId usingGenericProperty:isGenericProperty operator:oper value:value];
    return [CiresonRequestBase getFinalExpressionCriteria:expressionCriteria];
}

+(NSArray*) getExpressionsForObjects:(NSArray*)objects
                  relationshipTypeId:(NSString*)relationshipTypeId
                              typeId:(NSString*)typeId
                          propertyId:(NSString*)propertyId
                usingGenericProperty:(BOOL)isGenericProperty
                            operator:(NSString*)operator
                   valuePropertyName:(NSString*)propertyName /*property name in object being compared"*/ {
    NSMutableArray* expressions = Nil;
    if (objects != Nil && objects.count > 0) {
        expressions = [[NSMutableArray alloc] init];
        for (CiresonModelBase* baseObj in objects) {
            NSString* value = [baseObj valueForKey:propertyName];
            NSDictionary* exp = [CiresonRequestBase getExpressionCriteriaWithRelationshipTypeId:relationshipTypeId  typeId:typeId propertyId:propertyId usingGenericProperty:isGenericProperty operator:operator value:value];
            [expressions addObject:exp];
        }
    }
    return expressions;
}

+(NSArray*) getExpressionsForObjects:(NSArray*)objects
                              typeId:(NSString*)typeId
                          propertyId:(NSString*)propertyId
                usingGenericProperty:(BOOL)isGenericProperty
                            operator:(NSString*)operator
                   valuePropertyName:(NSString*)propertyName{
    NSMutableArray* expressions = Nil;
    if (objects != Nil && objects.count > 0) {
        expressions = [[NSMutableArray alloc] init];
        for (CiresonModelBase* baseObj in objects) {
            NSString* value = [baseObj valueForKey:propertyName];
            NSDictionary* exp = [CiresonRequestBase getExpressionCriteriaWithTypeId:typeId
                                                                         propertyId:propertyId
                                                               usingGenericProperty:isGenericProperty
                                                                           operator:operator value:value];
            [expressions addObject:exp];
        }
    }
    return expressions;
}


+(NSDictionary*) getFinalExpressionCriteriaWithRelationshipTypeId:(NSString *)relationshipTypeId
                                                           typeId:(NSString *)typeId
                                                       propertyId:(NSString *)propertyId
                                             usingGenericProperty:(BOOL)isGenericProperty
                                                         operator:(NSString *)oper
                                                            value:(NSString *)value {
    NSDictionary* expressionCriteria = [CiresonRequestBase getExpressionCriteriaWithRelationshipTypeId:relationshipTypeId typeId:typeId propertyId:propertyId usingGenericProperty:(BOOL)isGenericProperty operator:oper value:value];
    NSDictionary* simpleCriteria = @{@"Base":
                                         @{@"Expression": expressionCriteria }
                                     };
    
    return simpleCriteria;
}

+(NSDictionary*) getExpressionCriteriaWithRelationshipTypeId:(NSString *)relationshipTypeId
                                                      typeId:(NSString *)typeId
                                                  propertyId:(NSString *)propertyId
                                        usingGenericProperty:(BOOL)isGenericProperty
                                                    operator:(NSString *)oper
                                                       value:(NSString *)value{
    
    NSDictionary* criteriaObj =             @{@"SimpleExpression":
                                                  @{
                                                      @"ValueExpressionLeft":
                                                          [CiresonRequestBase getLeftExpressionWithRelationshipTypeId:relationshipTypeId typeId:typeId propertyId:propertyId usingGenericProperty:isGenericProperty],
                                                      @"Operator":oper,
                                                      @"ValueExpressionRight":
                                                          [CiresonRequestBase getRightExpressionWithValue:value]
                                                      }
                                              };
    return criteriaObj;
   
}


+(NSDictionary*) getExpressionCriteriaWithTypeId:(NSString*)typeId
                                            propertyId:(NSString*)propertyId
                                  usingGenericProperty:(BOOL)isGenericProperty
                                              operator:(NSString*)oper
                                                 value:(NSString*)value {
    NSDictionary* criteriaObj =             @{@"SimpleExpression":
                                                      @{
                                                          @"ValueExpressionLeft":
                                                              [CiresonRequestBase getLeftExpressionWithTypeId:typeId propertyId:propertyId usingGenericProperty:isGenericProperty],
                                                          @"Operator":oper,
                                                          @"ValueExpressionRight":
                                                              [CiresonRequestBase getRightExpressionWithValue:value]
                                                        }
                                              };
    return criteriaObj;
}

+(NSDictionary*) getLeftExpressionWithTypeId:(NSString*)typeId
                                  propertyId:(NSString*)propertyId
                        usingGenericProperty:(BOOL)isGenericProperty {
    if (isGenericProperty) {
        return @{@"GenericProperty": propertyId };
    }
    else {
        return @{@"Property":[NSString stringWithFormat:@"$Context/Property[Type='%@']/%@$", typeId, propertyId]};
    }
}

+(NSDictionary*) getLeftExpressionWithRelationshipTypeId:(NSString*)relationshipTypeId
                                                  typeId:(NSString*)typeId
                                              propertyId:(NSString*)propertyId
                                    usingGenericProperty:(BOOL)isGenericProperty {
    if (isGenericProperty) {
        return @{ @"GenericProperty":
                      @{
                          @"@Path":[NSString stringWithFormat:@"$Context/Path[Relationship='%@' SeedRole='Source' TypeConstraint='%@']$", relationshipTypeId, typeId],
                          @"#text":propertyId
                       }
                  };
    }
    else {
        return @{@"Property":[NSString stringWithFormat:@"$Context/Path[Relationship='%@' TypeConstraint='%@']/Property[Type='%@']/%@$", relationshipTypeId, typeId, typeId, propertyId]};
    }
}

+(NSDictionary*) getRightExpressionWithValue:(NSString*)value {
    return @{@"Value":[NSString stringWithFormat:@"%@", value]};
}



/*+(NSDictionary*)replaceNulls:(NSDictionary*)dict {
    const NSMutableDictionary *replaced = [dict mutableCopy];
    const id nul = [NSNull null];
    const NSString *blank = @"";
    
    for(NSString *key in dict) {
        const id object = [dict objectForKey:key];
        if(object == nul) {
            //pointer comparison is way faster than -isKindOfClass:
            //since [NSNull null] is a singleton, they'll all point to the same
            //location in memory.
            [replaced setObject:blank
                         forKey:key];
        }
    }
    
    return [replaced copy];
}*/

+(NSString*)getTodayPlusMinusDays:(NSInteger)days {
    NSDate* date = [NSDate dateWithTimeIntervalSinceNow:days * (24*60*60)];
    NSDateFormatter* formatter = [[NSDateFormatter alloc] init];
    [formatter setDateFormat:@"yyyy-MM-dd'T'HH:mm:ss"];
    NSString* dateString = [formatter stringFromDate:date];
    return dateString;
}

+(NSString*)getDateString: (NSDate*) date {
    if (date == nil) {
        return Nil;
    }
    NSDateFormatter* formatter = [[NSDateFormatter alloc] init];
    [formatter setDateFormat:@"yyyy-MM-dd'T'HH:mm:ss"];
    NSString* dateString = [formatter stringFromDate:date];
    return dateString;
}

+(NSDate *) tomorrow {
    NSCalendar* cal = [NSCalendar currentCalendar];
    NSDateComponents *components= [[NSDateComponents alloc] init];
    [components setDay:1];
    NSDate *increased= [cal dateByAddingComponents:components toDate:[NSDate date] options:0];
    return increased;
}

+(NSDate*) yesterday {
    NSCalendar* cal = [NSCalendar currentCalendar];
    NSDateComponents *decrementComponents= [[NSDateComponents alloc] init];
    [decrementComponents setDay:-1];
    NSDate *dateDecreased= [cal dateByAddingComponents:decrementComponents toDate:[NSDate date] options:0];
    return dateDecreased;
}

+(NSDate *)beginningOfDay:(NSDate*)d {
    NSCalendar *calendar = [NSCalendar currentCalendar];
    NSDateComponents *components = [calendar components:NSYearCalendarUnit | NSMonthCalendarUnit | NSDayCalendarUnit fromDate:d];
    return [calendar dateFromComponents:components];
}

+(NSDate*)endOfDay:(NSDate*)d {
    NSCalendar *calendar = [NSCalendar currentCalendar];
    
    NSDateComponents *components = [[NSDateComponents alloc] init];
    [components setDay:1];
    
    return [[calendar dateByAddingComponents:components toDate:[self beginningOfDay:d] options:0] dateByAddingTimeInterval:-1];
}




-(void)parseSuccessResponse:(id) responseObject {
    // subclasses can decide to parse the response as needed.

}

-(void)onError:(NSError*)error {
 // subclasses can decide something to do here if needed.
}


-(void)makeRequestWithParameters :(NSDictionary *)parameters {
    NSAssert(self.method != Nil, @"Method must be set");
    NSString* queryPart = [NSString stringWithFormat:@"%@%@", @"api/DataService/", self.method];
    NSString* baseUrl = [[CiresonApiService sharedService] baseUrl];
    NSString* urlString = [NSString stringWithFormat:@"%@%@", baseUrl, queryPart];
    NSAssert(self.successHandler != Nil, @"success handler must be set");
    NSAssert(self.errorHandler != Nil, @"error handler must be set");
    
    AFHTTPRequestOperationManager *manager = [AFHTTPRequestOperationManager manager];
    
    manager.requestSerializer = [AFJSONRequestSerializer serializer];
    manager.responseSerializer = [AFJSONResponseSerializer serializer];
    __block __strong CiresonRequestBase* me = self; // we want to keep myself around
    
#ifdef TRACE_PARAMS
    NSError* error = Nil;
    NSData* data = [NSJSONSerialization dataWithJSONObject:parameters options:NSJSONWritingPrettyPrinted error:&error];
    NSString * str = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
    NSLog(@"Parameters: %@", str);
#endif
    
    [manager POST:urlString parameters:parameters success:^(AFHTTPRequestOperation *operation, id responseObject) {
        [me parseSuccessResponse:responseObject];
        me.successHandler(responseObject);	
        me = Nil; // break cycle.
    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
        NSLog(@"Error: %@", error);
        if (operation.responseString && operation.responseString.length > 0) {
            NSError* error2 = Nil;
            NSDictionary *json = [NSJSONSerialization JSONObjectWithData:operation.responseData options:0 error:&error2];//response object is your response from server as NSData
            if (error2 == Nil) {
                NSString* errorMessage = [json objectForKey:@"message"];
                if (errorMessage) {
                    NSDictionary* userInfo = [error userInfo];
                    if (userInfo) {
                        userInfo = [NSMutableDictionary dictionaryWithDictionary:userInfo];
                        NSString* existing = [userInfo objectForKey:NSLocalizedDescriptionKey];
                        if (existing) {
                            [userInfo setValue:[NSString stringWithFormat:@"%@\r\n%@", errorMessage, existing] forKey:NSLocalizedDescriptionKey];
                        }
                        else {
                            [userInfo setValue:errorMessage forKey:NSLocalizedDescriptionKey];
                            
                        }
                    }
                    else {
                        NSMutableDictionary* mutableDic = [[NSMutableDictionary alloc] init];
                        userInfo = mutableDic;
                        [mutableDic setValue:[json objectForKey:@"message"] forKey:NSLocalizedDescriptionKey];
                    }
                    error = [NSError errorWithDomain:error.domain code:error.code userInfo:userInfo];
                }
            }

        }
        [self onError:error];
        self.errorHandler(error);
        me = Nil; // break cycle
    }];
}

-(void)dealloc {
    
}


@end
