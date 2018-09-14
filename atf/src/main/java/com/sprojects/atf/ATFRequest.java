package com.sprojects.atf;

import android.content.Context;
import android.net.Uri;

import com.android.volley.Request;

import java.util.HashMap;
import java.util.Map;


public class ATFRequest
{
    public Context context;

    public enum HttpMethod { GET, PUT, POST, PATCH, DELETE }
    public HttpMethod httpMethod;

    public enum RequestType { Normal, Auth, RefreshToken }
    public RequestType requestType;

    public String url;
    protected Map<String, String> params;

    public ATFRequestInterface requestInterface;


    // ########################################################################

    public ATFRequest(Context context, HttpMethod httpMethod, RequestType requestType, String url, Map<String, String> params, ATFRequestInterface requestInterface)
    {
        this.context = context;
        this.httpMethod = httpMethod;
        this.requestType = requestType;
        this.url = url;
        this.params = params;
        this.requestInterface = requestInterface;
    }

    // ########################################################################

    public Map<String, String> getHeaders()
    {
        Map<String, String> headers = new HashMap<>();

        if(ATFClient.ClientConfig != null) headers.put("locale", ATFClient.ClientConfig.ServiceLang);

        if(requestType == RequestType.Auth || requestType == RequestType.RefreshToken)
        {
            headers.put("content-type", "application/x-www-form-urlencoded;chrset=UTF-8");
            headers.put("cache-control", "no-cashe");

        }else{

            String accessToken = getAccessToken();
            headers.put("Authorization", accessToken);
            headers.put("content-type", "application/x-www-form-urlencoded;chrset=UTF-8");
        }

        return headers;
    }

    // ########################################################################

    public Map<String, String> getParams()
    {
        if(requestType == RequestType.Auth)
        {
            Map<String, String> p = new HashMap<>();
            if(this.params != null && this.params.size() > 0) p.putAll(this.params);
            p.put("grant_type", "password");
            p.put("client_id", ATFClient.ClientConfig.ServiceClientID);
            p.put("client_secret", ATFClient.ClientConfig.ServiceClientSecret);
            return p;

        }else if(requestType == RequestType.RefreshToken){

            ATFModels.OAuth oAuth = ATFModels.OAuth.retrieve(context);
            String refresh_token = (oAuth != null) ? oAuth.refreshToken : "";

            Map<String, String> p = new HashMap<>();
            p.put("refresh_token", refresh_token);
            p.put("grant_type", "refresh_token");
            p.put("client_id", ATFClient.ClientConfig.ServiceClientID);
            p.put("client_secret", ATFClient.ClientConfig.ServiceClientSecret);
            return p;

        }else{

            return this.params;
        }
    }

    // ########################################################################

    private String getAccessToken()
    {
        ATFModels.OAuth oAuth = ATFModels.OAuth.retrieve(context);

        if(oAuth != null) return " Bearer " + oAuth.accessToken;

        return "";
    }

    // ########################################################################

    public String getUrl()
    {
        String default_url = ATFClient.ClientConfig.ServiceURL + this.url;

        if(httpMethod == HttpMethod.GET || httpMethod == HttpMethod.DELETE)
        {
            if(this.params != null && this.params.size() > 0)
            {
                Uri.Builder builder = new Uri.Builder();

                for (Map.Entry<String, String> entry : this.params.entrySet())
                {
                    builder.appendQueryParameter(entry.getKey(), entry.getValue());
                }

                return default_url + builder.build().toString();
            }
        }

        return default_url;
    }

    // ########################################################################

    public int getMethodType()
    {
        if(httpMethod == HttpMethod.GET)
        {
            return Request.Method.GET;

        }else if(httpMethod == HttpMethod.PUT){

            return Request.Method.PUT;

        }else if(httpMethod == HttpMethod.POST){

            return Request.Method.POST;

        }else if(httpMethod == HttpMethod.PATCH){

            return Request.Method.PATCH;

        }else if(httpMethod == HttpMethod.DELETE){

            return Request.Method.DELETE;
        }

        return 0;
    }


    // ########################################################################

    public static ATFRequest createRefreshTokenRequest(final Context context, final ATFRequestInterface requestInterface)
    {
        return new ATFRequest(context, HttpMethod.POST, RequestType.RefreshToken, "o/token/", null, requestInterface);
    }
}