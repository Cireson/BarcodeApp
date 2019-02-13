
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
//  CiresonRequestBase.h
//  AssetManager
//

#import <Foundation/Foundation.h>

typedef void (^CiresonAPIRequestErrorHandler) (NSError* error);
typedef void (^CiresonAPIRequestSuccessHandler) (id response);

@interface CiresonRequestBase :NSObject

@property (nonatomic, copy) CiresonAPIRequestSuccessHandler successHandler;
@property (nonatomic, copy) CiresonAPIRequestErrorHandler errorHandler;
@property(nonatomic, copy) NSString* method;

+(NSMutableDictionary*) getParametersForRequestWithToken:(NSString*)token
                                        typeProjectionId:(NSString*)typeProjectionId
                                                criteria:(NSDictionary*)criteria;

+(NSDictionary*) getFinalExpressionCriteria:(NSDictionary*)expression;

+(NSDictionary*) getFinalExpressionCriteriaWithTypeId:(NSString*)typeId
                                                      propertyId:(NSString*)propertyId
                                            usingGenericProperty:(BOOL)isGenericProperty
                                                        operator:(NSString*)oper
                                                           value:(NSString*)value;

+(NSDictionary*) getFinalExpressionCriteriaWithRelationshipTypeId:(NSString*)relationshipTypeId
                                                            typeId:(NSString*)typeId
                                                        propertyId:(NSString*)propertyId
                                              usingGenericProperty:(BOOL)isGenericProperty
                                                          operator:(NSString*)oper
                                                             value:(NSString*)value;

+(NSArray*) getExpressionsForObjects:(NSArray*)objects
                  relationshipTypeId:(NSString*)relationshipTypeId
                              typeId:(NSString*)typeId
                          propertyId:(NSString*)propertyId
                usingGenericProperty:(BOOL)isGenericProperty
                            operator:(NSString*)oper
                   valuePropertyName:(NSString*)propertyName;

+(NSArray*) getExpressionsForObjects:(NSArray*)objects
                              typeId:(NSString*)typeId
                          propertyId:(NSString*)propertyId
                usingGenericProperty:(BOOL)isGenericProperty
                            operator:(NSString*)oper
                   valuePropertyName:(NSString*)propertyName;

+(NSDictionary*) getExpressionCriteriaWithRelationshipTypeId:(NSString*)relationshipTypeId
                                                            typeId:(NSString*)typeId
                                                        propertyId:(NSString*)propertyId
                                              usingGenericProperty:(BOOL)isGenericProperty
                                                          operator:(NSString*)oper
                                                             value:(NSString*)value;

+(NSDictionary*) getExpressionCriteriaWithTypeId:(NSString*)typeId
                                            propertyId:(NSString*)propertyId
                                  usingGenericProperty:(BOOL)isGenericProperty
                                              operator:(NSString*)oper
                                                 value:(NSString*)value;

+(NSDictionary*) getAndExpressionCriteria:(NSDictionary*)left right:(NSDictionary*)right;
+(NSDictionary*) getOrExpressionCriteria:(NSDictionary*)left right:(NSDictionary*)right;
+(NSDictionary*) getAndExpressionCriteria:(NSArray*)expressions;
+(NSDictionary*) getOrExpressionCriteria:(NSArray*)expressions;
+(NSDictionary*) getLeftExpressionWithTypeId:(NSString*)typeId propertyId:(NSString*)propertyId usingGenericProperty:(BOOL)isGenericProperty;
+(NSDictionary*) getLeftExpressionWithRelationshipTypeId:(NSString*)relationshipTypeId typeId:(NSString*)typeId propertyId:(NSString*)propertyId usingGenericProperty:(BOOL)isGenericProperty;
+(NSDictionary*) getRightExpressionWithValue:(NSString*)value;

//+(NSDictionary*) replaceNulls:(NSDictionary*)dict;
// helpers, use negative for before today.
+(NSString*)getTodayPlusMinusDays:(NSInteger)days;
+(NSString*)getDateString: (NSDate*) date;
+(NSDate*)yesterday;
+(NSDate*)tomorrow;
+(NSDate*)endOfDay:(NSDate*)d;
+(NSDate*)beginningOfDay:(NSDate*)d;

-(void)parseSuccessResponse:(id) responseObject;
-(void)onError:(NSError*) error;
-(void)makeRequestWithParameters:(NSDictionary*)parameters;

@end
