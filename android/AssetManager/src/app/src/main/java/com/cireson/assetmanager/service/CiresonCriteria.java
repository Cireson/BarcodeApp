
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

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;


public class CiresonCriteria  extends BaseJsonObject {

    public ExpressionBase Base;

    public CiresonCriteria(){

    }

    public CiresonCriteria(SimpleExpression exp){
        Base = new ExpressionBase();
        Base.Expression = new Expression((exp));
    }

    public CiresonCriteria(Expression exp){
        Base = new ExpressionBase();
        Base.Expression = exp;
    }

    public static class ExpressionBase{
        public Expression Expression;
    }

    public static class SimpleExpression{
        public String typeId;
        public String propertyId;
        public String operator;
        public String value;
        public boolean isGenericProperty;
        public String relationshipTypeId;

        public SimpleExpression(String typeId, String propertyId, String operator, String value, String relationshipTypeId, boolean isGenericProperty){
            this.typeId = typeId;
            this.propertyId = propertyId;
            this.operator = operator;
            this.value = value;
            this.relationshipTypeId = relationshipTypeId;
            this.isGenericProperty = isGenericProperty;
        }

        public SimpleExpression(String typeId, String propertyId, String operator, String value){
            this(typeId,propertyId,operator,value,null,false);
        }

        public SimpleExpression(){

        }

    }

    public static class SimpleExpressionSerializer implements JsonSerializer<SimpleExpression>{

        @Override
        public JsonElement serialize(SimpleExpression simpleExpression, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonParser parser = new JsonParser();
            return parser.parse(getJsonString(simpleExpression));
        }

        private static String getJsonString(SimpleExpression expression){
            String assetsSearchResultJsonString = "{"+
                    "\"ValueExpressionLeft\":"+
                    getLeftExpression(expression) +
                    ",\"Operator\": \"%s\","+
                    "\"ValueExpressionRight\": {"+
                    "\"Value\":\"%s\""+
                    "}"+
                    "}";

            return  String.format(assetsSearchResultJsonString,expression.operator,expression.value);
        }

        private static String getLeftExpression(SimpleExpression expression){
           JSONObject jsonObj = new JSONObject();
            try {
                if(expression.relationshipTypeId==null || "".equals(expression.relationshipTypeId)){
                    if (expression.isGenericProperty) {
                        jsonObj.put("GenericProperty", expression.propertyId);
                    }
                    else {
                        jsonObj.put("Property", String.format("$Context/Property[Type='%s']/%s$", expression.typeId, expression.propertyId));
                    }
                }
                else{
                    if (expression.isGenericProperty) {

                        JSONObject genericProp = new JSONObject();

                        genericProp.put("@Path", String.format("$Context/Path[Relationship='%s' SeedRole='Source' TypeConstraint='%s']$", expression.relationshipTypeId, expression.typeId));
                        genericProp.put("#text", expression.propertyId);

                        jsonObj.put("GenericProperty", genericProp);
                    }
                    else {
                        jsonObj.put("Property", String.format("$Context/Path[Relationship='%s' TypeConstraint='%s']/Property[Type='%s']/%s$", expression.relationshipTypeId, expression.typeId,expression.typeId, expression.propertyId));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return jsonObj.toString();
        }

    }

    public  static class Expression{
        public CompoundExpression And;
        public CompoundExpression Or;
        public SimpleExpression SimpleExpression;

        public Expression(SimpleExpression exp){
            this.And = null;
            this.Or = null;
            this.SimpleExpression = exp;
        }

        public Expression(){
        }

        public static Expression AndExpressions(ArrayList<Expression> expressions){
            Expression exp = null;
            if(expressions.size()>1) {
                exp = new Expression();
                exp.And = new CompoundExpression(expressions);
            }
            else{
                if(expressions.size()==1){
                    exp = expressions.get(0);
                }else{
                    exp = new Expression();
                }
            }
            return exp;
        }

        public static Expression OrExpressions(ArrayList<Expression> expressions){
            Expression exp = null;
            if(expressions.size()>1) {
                exp = new Expression();
                exp.Or = new CompoundExpression(expressions);
            }
            else{
                exp = expressions.get(0);
            }
            return exp;
        }
    }

    public static class CompoundExpression{
        ArrayList<Expression> Expression;
        public CompoundExpression(){
        }
        public CompoundExpression(ArrayList<Expression> expArray){
            Expression = expArray;
        }
    }

}




