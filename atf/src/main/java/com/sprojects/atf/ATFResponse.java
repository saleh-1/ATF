package com.sprojects.atf;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class ATFResponse
{
    public enum ResponseContentType { None, ListObject, Object, Message}
    public ResponseContentType contentType;

    public String responseSource;
    public JSONObject responseJsonObject;
    public int statusCode = 0;

    // #####################################################################

    public ATFResponse(Context context, String responseSource) throws ATFException
    {
        this.responseSource = responseSource;

        try {
            this.responseJsonObject = new JSONObject(responseSource);
            this.contentType = getBodyContentType();

        }catch (JSONException je){

            throw new ATFException("response_parse_error", context.getString(R.string.response_parse_error));
        }
    }

    // #####################################################################

    public JSONObject getHeader()
    {
        return ATFModels.ModelBase.getJsonObjectFromJson(responseJsonObject, "header");
    }

    // #####################################################################

    public JSONObject getBody()
    {
        return ATFModels.ModelBase.getJsonObjectFromJson(responseJsonObject, "body");
    }

    // #####################################################################

    public JSONObject getBodyMeta()
    {
        return ATFModels.ModelBase.getJsonObjectFromJson(getBody(), "meta");
    }

    // #####################################################################

    public ResponseContentType getBodyContentType()
    {
        String contentType = ATFModels.ModelBase.getStringJson(getBodyMeta(), "content_type");

        if(contentType == null) return ResponseContentType.None;

        switch(contentType)
        {
            case "message" :
                return ResponseContentType.Message;

            case "object" :
                return ResponseContentType.Object;

            case "list_objects" :
                return ResponseContentType.ListObject;

            default :
                return ResponseContentType.None;
        }
    }

    // #####################################################################

    public Object getBodyContent()
    {
        JSONObject body = getBody();

        if(body == null) return null;

        try {
            Object bc = body.get("content_object");

            if(bc == null) return null;

            if(bc instanceof JSONArray)
            {
                return body.getJSONArray("content_object");

            }else if(bc instanceof JSONObject) {

                return body.getJSONObject("content_object");
            }

        }catch(JSONException je){}

        return null;
    }
}