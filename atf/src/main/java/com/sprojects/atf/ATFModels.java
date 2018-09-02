package com.sprojects.atf;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// #####################################################################

public class ATFModels
{
    // #####################################################################

    public interface ModelInterfaceList
    {
        void onDataReceived(ArrayList<Object> objects);
        void onError(String message);
    }

    public interface ModelInterfaceDetails
    {
        void onDataReceived(Object object);
        void onError(String message);
    }

    public interface ModelInterfaceAction
    {
        void onSuccess(String message);
        void onError(String message);
    }


    // #####################################################################

    public static class ModelBase
    {
        protected Context context;
        public ATFClient atfClient;
        public ATFResponse atfResponse;
        public PaginationObj pagination;

        // #####################################################################

        public ModelBase(Context context)
        {
            this.context = context;
            this.atfClient = new ATFClient(context);
        }

        // #####################################################################

        public void responseHandlerList(ATFResponse response, ModelInterfaceList modelInterfaceList)
        {
            if(modelInterfaceList == null) return;

            if(response == null)
            {
                modelInterfaceList.onError(context.getString(R.string.no_response));
                return;
            }

            //
            this.atfResponse = response;

            if(response.contentType == ATFResponse.ResponseContentType.ListObject)
            {
                //
                pagination = getPagination();

                ArrayList<Object> objects = getResponseContentListData();

                if(objects != null && objects.size() > 0)
                {
                    modelInterfaceList.onDataReceived(objects);
                    return;
                }

            }else if(response.contentType == ATFResponse.ResponseContentType.Message){

                //
                ActionResponseObj aro = getActionObj(response);
                if(aro != null)
                {
                    modelInterfaceList.onError(aro.Message);
                    return;
                }
            }

            modelInterfaceList.onError(context.getString(R.string.error));
        }

        // #####################################################################

        public void responseHandlerDetails(ATFResponse response, ModelInterfaceDetails modelInterfaceDetails)
        {
            if(modelInterfaceDetails == null) return;

            if(response == null)
            {
                modelInterfaceDetails.onError(context.getString(R.string.no_response));
                return;
            }

            //
            this.atfResponse = response;

            if(response.contentType == ATFResponse.ResponseContentType.Object)
            {
                Object bc = response.getBodyContent();

                if(bc instanceof JSONObject)
                {
                    JSONObject jo = (JSONObject)bc;
                    Object obj = jsontToObject(context, jo);

                    if(obj != null)
                    {
                        modelInterfaceDetails.onDataReceived(obj);
                        return;
                    }
                }
            }else if(response.contentType == ATFResponse.ResponseContentType.Message){

                //
                ActionResponseObj aro = getActionObj(response);
                if(aro != null)
                {
                    modelInterfaceDetails.onError(aro.Message);
                    return;
                }
            }

            modelInterfaceDetails.onError(context.getString(R.string.error));
        }

        // #####################################################################

        public void responseHandlerAction(ATFResponse response, ModelInterfaceAction modelInterfaceAction)
        {
            if(modelInterfaceAction == null) return;

            if(response == null)
            {
                modelInterfaceAction.onError(context.getString(R.string.no_response));
                return;
            }

            //
            this.atfResponse = response;

            if(response.contentType == ATFResponse.ResponseContentType.Message)
            {
                ActionResponseObj aro = getActionObj(response);
                if(aro != null)
                {
                    if(aro.status == ActionResponseObj.ActionResponseStatus.Success)
                    {
                        modelInterfaceAction.onSuccess(aro.Message);

                    }else{

                        modelInterfaceAction.onError(aro.Message);
                    }
                    return;
                }
            }

            modelInterfaceAction.onError(context.getString(R.string.error));
        }

        // #####################################################################

        public ActionResponseObj getActionObj(ATFResponse response)
        {
            if(response == null) return null;

            Object bc = response.getBodyContent();

            if(bc instanceof JSONObject)
            {
                JSONObject jo = (JSONObject)bc;
                ActionResponseObj actionObj = ActionResponseObj.fromJson(context, jo);
                return actionObj;
            }

            return null;
        }

        // #####################################################################

        public ArrayList<Object> getResponseContentListData()
        {
            if(atfResponse == null) return null;

            Object bc = atfResponse.getBodyContent();

            if(bc instanceof JSONArray)
            {
                try {
                    JSONArray ja = (JSONArray) bc;

                    ArrayList<Object> objs = new ArrayList<>();

                    for (int i = 0; i < ja.length(); i++)
                    {
                        Object itm = ja.get(i);

                        if (itm instanceof JSONObject)
                        {
                            JSONObject jo = (JSONObject) itm;
                            Object obj = jsontToObject(context, jo);
                            if (obj != null) objs.add(obj);
                        }
                    }

                    return objs;

                } catch (JSONException je) {
                }
            }

            return null;
        }

        // #####################################################################

        public PaginationObj getPagination()
        {
            try {
                JSONObject paginationInfo = getJsonObjectFromJson(atfResponse.getBodyMeta(), "pagination_info");
                return PaginationObj.fromJson(context, paginationInfo);

            }catch (Exception e){}

            return null;
        }

        // #####################################################################

        public JSONObject getMetaInfo(String key)
        {
            try {
                return getJsonObjectFromJson(atfResponse.getBodyMeta(), key);

            }catch (Exception e){}

            return null;
        }

        // #####################################################################

        public Object jsontToObject(Context context, JSONObject jsonObject)
        {
            return  null;
        }

        // #####################################################################

        public static String getStringJson(JSONObject jsonObject, String key)
        {
            if(jsonObject == null || key == null || key.isEmpty()) return null;

            try {
                return jsonObject.getString(key);

            }catch(JSONException je){}

            return null;
        }

        // #####################################################################

        public static boolean getBooleanJson(JSONObject jsonObject, String key)
        {
            try {
                return jsonObject.getBoolean(key);

            }catch(JSONException je){}

            return false;
        }

        // #####################################################################

        public static JSONObject getJsonObjectFromJson(JSONObject jsonObject, String key)
        {
            if(jsonObject == null || key == null || key.equals("")) return null;

            try {
                return jsonObject.getJSONObject(key);

            }catch(JSONException je){}

            return null;
        }

        // #####################################################################

        public static JSONArray getJsonArrayFromJson(JSONObject jsonObject, String key)
        {
            if(jsonObject == null || key == null || key.equals("")) return null;

            try {
                return jsonObject.getJSONArray(key);

            }catch(JSONException je){}

            return null;
        }


        // #####################################################################

        public void requestForList(String url,Map<String,String> params, int pageNumber, final ModelInterfaceList modelInterfaceList)
        {
            if(url == null || url.length() == 0) return;

            Map<String, String> params_ = new HashMap<String, String>();

            if(pageNumber < 1) pageNumber = 0;
            params_.put("page", Integer.toString(pageNumber));

            if(params != null && params.size() > 0) params_.putAll(params);

            ATFRequest request = new ATFRequest(context, ATFRequest.HttpMethod.GET, ATFRequest.RequestType.Normal, url, params_, new ATFRequestInterface() {
                @Override
                public void onDataReceived(ATFResponse response) {
                    responseHandlerList(response, modelInterfaceList);
                }

                @Override
                public void onError(String message) {
                    modelInterfaceList.onError(message);
                }
            });

            atfClient.sendRequest(request);
        }

        // #####################################################################

        public void requestForDetails(String url, Map<String,String> params, final ModelInterfaceDetails modelInterfaceDetails)
        {
            if(url == null || url.length() == 0) return;

            ATFRequest request = new ATFRequest(context, ATFRequest.HttpMethod.GET, ATFRequest.RequestType.Normal, url, params, new ATFRequestInterface() {
                @Override
                public void onDataReceived(ATFResponse response) {
                    responseHandlerDetails(response, modelInterfaceDetails);
                }

                @Override
                public void onError(String message) {
                    modelInterfaceDetails.onError(message);
                }
            });

            atfClient.sendRequest(request);
        }

        // #####################################################################

        public void requestForAction(String url, ATFRequest.HttpMethod httpMethod, Map<String,String> params, final ModelInterfaceAction modelInterfaceAction)
        {
            ATFRequest request = new ATFRequest(context, httpMethod, ATFRequest.RequestType.Normal, url, params, new ATFRequestInterface() {
                @Override
                public void onDataReceived(ATFResponse response) {
                    responseHandlerAction(response, modelInterfaceAction);
                }

                @Override
                public void onError(String message) {
                    modelInterfaceAction.onError(message);
                }
            });

            atfClient.sendRequest(request);
        }

    }


    // #####################################################################

    public static class PaginationObj extends ModelBase
    {
        public boolean hasNext;
        public int pagesCount;
        public int itemsCount;

        // #####################################################################

        public PaginationObj(Context context)
        {
            super(context);
        }

        // #####################################################################

        public static PaginationObj fromJson(Context context, JSONObject jsonObject)
        {
            if (jsonObject == null) return null;
            if (jsonObject.length() == 0) return null;

            PaginationObj obj = new PaginationObj(context);

            obj.context = context;
            obj.hasNext = getBooleanJson(jsonObject, "has_next");
            try
            {
                obj.pagesCount = Integer.parseInt(getStringJson(jsonObject, "pages_count"));
                obj.itemsCount = Integer.parseInt(getStringJson(jsonObject, "items_count"));

            }catch (Exception e){}

            return obj;
        }
    }


    // #####################################################################

    public static class OAuth extends ModelBase
    {
        public String accessToken;
        public String tokenType;
        public String refreshToken;
        public String expiresIn;
        public String scope;

        // #####################################################################

        public OAuth(Context context)
        {
            super(context);
        }

        // #####################################################################

        public static OAuth fromJson(Context context, JSONObject jsonObject)
        {
            if (jsonObject == null) return null;
            if (jsonObject.length() == 0) return null;

            OAuth obj = new OAuth(context);

            obj.context = context;
            obj.accessToken = getStringJson(jsonObject, "access_token");
            obj.tokenType = getStringJson(jsonObject, "token_type");
            obj.expiresIn = getStringJson(jsonObject, "expires_in");
            obj.refreshToken = getStringJson(jsonObject, "refresh_token");
            obj.scope = getStringJson(jsonObject, "scope");

            return obj;
        }

        // #####################################################################

        public static String getKey()
        {
            return "oauth_data";
        }

        // #####################################################################

        public static boolean isSigned(Context context)
        {
            return (retrieve(context) != null);
        }

        // #####################################################################

        public boolean save()
        {
            if(atfResponse != null)
            {
                ATFUtils.saveAppData(context, getKey(), atfResponse.responseSource);

                return true;
            }

            return false;
        }

        // #####################################################################

        public static OAuth retrieve(Context context)
        {
            if(context == null) return null;

            String authDataSource = ATFUtils.getAppData(context, getKey());

            if(authDataSource == null || authDataSource.isEmpty()) return null;

            ATFResponse authOldResponse = null;

            try {
                authOldResponse = new ATFResponse(context, authDataSource);

            }catch(ATFException e){}

            if(authOldResponse == null) return null;

            OAuth oa = OAuth.fromJson(context, authOldResponse.responseJsonObject);
            if(oa != null)
            {
                oa.atfResponse = authOldResponse;
                return oa;
            }

            return null;
        }

        // #####################################################################

        public static void clear(Context context)
        {
            ATFUtils.saveAppData(context, getKey(), null);
        }

        // #####################################################################

        public void auth(String Id, String password, final ModelInterfaceAction modelInterfaceAction)
        {
            Map<String, String> params = new HashMap<String, String>();
            params.put("username", Id);
            params.put("password", password);

            ATFRequest request = new ATFRequest(context, ATFRequest.HttpMethod.POST, ATFRequest.RequestType.Auth, "o/token/", params, new ATFRequestInterface() {
                @Override
                public void onDataReceived(ATFResponse response) {
                    if(AuthResponseHandler(context, response))
                    {
                        modelInterfaceAction.onSuccess("");
                    }else{
                        modelInterfaceAction.onError(context.getString(R.string.error));
                    }
                }

                @Override
                public void onError(String message) {
                    modelInterfaceAction.onError(message);
                }
            });

            atfClient.sendRequest(request);
        }

        // ########################################################################

        public static boolean AuthResponseHandler(Context context, ATFResponse response)
        {
            if(response == null) return false;

            OAuth oa = OAuth.fromJson(context, response.responseJsonObject);
            if(oa != null && oa.save()) return true;

            return false;
        }
    }


    // #####################################################################

    public static class ActionResponseObj extends ModelBase
    {
        private enum ActionResponseStatus { Fail, Success}
        public ActionResponseStatus status;

        public String Message;

        // #####################################################################

        public ActionResponseObj(Context context)
        {
            super(context);
        }

        // #####################################################################

        public static ActionResponseObj fromJson(Context context, JSONObject jsonObject)
        {
            if (jsonObject == null) return null;
            if (jsonObject.length() == 0) return null;

            ActionResponseObj obj = new ActionResponseObj(context);

            obj.context = context;
            obj.Message = getStringJson(jsonObject, "message");
            obj.status = ActionResponseStatus.Fail;

            String status = getStringJson(jsonObject, "status");

            if(status != null && status.equals("success"))
            {
                obj.status = ActionResponseStatus.Success;
            }

            return obj;
        }
    }


    // #####################################################################

}