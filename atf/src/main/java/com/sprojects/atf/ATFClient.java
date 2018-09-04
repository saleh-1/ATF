package com.sprojects.atf;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.UnsupportedEncodingException;
import java.util.Map;


public class ATFClient
{
    public Context context = null;
    public CustomStringRequest customStringRequest;
    public static ATFClientConfig ClientConfig;

    // ########################################################################

    public ATFClient(Context context)
    {
        this.context = context;
    }

    // ########################################################################

    public void sendRequest(final ATFRequest atfRequest)
    {
            if(atfRequest == null)  return;

            //
            requestLog(atfRequest);

            //
            final Thread thread = new Thread(new Runnable(){
                @Override
                public void run() {

                    customStringRequest = new CustomStringRequest(atfRequest, responseListener(atfRequest), errorListener(atfRequest));
                    RequestQueue queue = Volley.newRequestQueue(context);
                    queue.add(customStringRequest);
                }});

            thread.start();
    }

    // ########################################################################

    private Response.Listener<String> responseListener(final ATFRequest atfRequest)
    {
        return new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response) {

            try{
                ATFResponse xResponse = new ATFResponse(context, response);
                xResponse.statusCode = customStringRequest.getStatusCode();
                responseLog(xResponse);
                atfRequest.requestInterface.onDataReceived(xResponse);

            }catch(ATFException e){

                atfRequest.requestInterface.onError(e.message);
            }
            }
        };
    }

    // ########################################################################

    private Response.ErrorListener errorListener(final ATFRequest atfRequest)
    {
        return new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                //
                if(error.networkResponse != null)
                {
                    //
                    responseLog(error.networkResponse.statusCode);

                    //
                    if(error instanceof NoConnectionError){

                        atfRequest.requestInterface.onError(context.getString(R.string.no_internet_connection));
                        return;

                    }else if (error instanceof NetworkError){

                        atfRequest.requestInterface.onError(context.getString(R.string.err_network_error));
                        return;

                    }else if (error instanceof TimeoutError){

                        atfRequest.requestInterface.onError(context.getString(R.string.err_timeout_error));
                        return;

                    }else if (error instanceof AuthFailureError){

                        if(atfRequest.requestType == ATFRequest.RequestType.RefreshToken)
                        {
                            // logout ....
                            atfRequest.requestInterface.onError(context.getString(R.string.error_auth));
                            ATFFragment.addNotificationObserver("AuthFailure");
                            return;

                        }else{

                            ATFRequest rtRequest = ATFRequest.createRefreshTokenRequest(context, new ATFRequestInterface() {
                                @Override
                                public void onDataReceived(ATFResponse xResponse) {
                                    if(ATFModels.OAuth.AuthResponseHandler(context, xResponse))
                                    {
                                        sendRequest(atfRequest);

                                    }else{

                                        atfRequest.requestInterface.onError(context.getString(R.string.error_auth));
                                    }
                                }

                                @Override
                                public void onError(String message) {
                                    atfRequest.requestInterface.onError(context.getString(R.string.error_auth));
                                }
                            });

                            //
                            sendRequest(rtRequest);
                            return;
                        }


                    }else if (error instanceof ServerError){

                        // For tracking errors
                        ///*
                        try {
                            String resSource = new String(error.networkResponse.data,"UTF-8");
                            Log.i("NetworkResponse-Log", resSource);

                        } catch (UnsupportedEncodingException e) {
                            Log.i("NetworkResponse-Log", e.getMessage());
                        }
                        //*/

                        atfRequest.requestInterface.onError(context.getString(R.string.error_response));
                        return;
                    }
                }

                atfRequest.requestInterface.onError(context.getString(R.string.error_request));
                return;
            }
        };
    }


    // ########################################################################

    private void responseLog(ATFResponse atfRequest)
    {
        Log.i("Request_Log", "###############   Response    ###############");
        Log.i("Request_Log", "Status code: " + atfRequest.statusCode);
        Log.i("Request_Log", atfRequest.responseSource);
        Log.i("Request_Log", "############################################");
    }

    // ########################################################################

    private void responseLog(int errorStatusCode)
    {
        Log.i("Request_Log", "###############   Response    ###############");
        Log.i("Request_Log", "Status code: " + errorStatusCode);
        Log.i("Request_Log", "############################################");
    }

    // ########################################################################

    private void requestLog(ATFRequest atfRequest)
    {
        Log.i("Request_log", "###############   Request    ###############");
        Log.i("Request_Log", "URL: " + atfRequest.getUrl());
        Log.i("Request_Log", "Headers: " + atfRequest.getHeaders());
        Log.i("Request_Log", "Params: " + atfRequest.params);
        Log.i("Request_Log", "############################################");
    }


    // ########################################################################

    public class CustomStringRequest extends StringRequest
    {
        private ATFRequest atfRequest;
        private int mStatusCode;

        // ########################################################################

        public CustomStringRequest(ATFRequest atfRequest, Response.Listener<String> response, Response.ErrorListener error)
        {
            super(atfRequest.getMethodType(), atfRequest.getUrl(), response, error);

            this.atfRequest = atfRequest;

            setRetryPolicy(new DefaultRetryPolicy(7000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        }

        // ########################################################################

        @Override
        public Map<String, String> getHeaders()
        {
            return atfRequest.getHeaders();
        }

        // ########################################################################

        @Override
        protected Map<String, String> getParams()
        {
            return atfRequest.getParams();
        }

        // ########################################################################

        public int getStatusCode()
        {
            return mStatusCode;
        }

        // ########################################################################

        @Override
        protected Response<String> parseNetworkResponse(NetworkResponse response)
        {
            String utf8String = null;
            mStatusCode = response.statusCode;

            try {
                utf8String = new String(response.data, "UTF-8");
                return Response.success(utf8String, HttpHeaderParser.parseCacheHeaders(response));

            } catch (UnsupportedEncodingException e) {

                return Response.error(new ParseError(e));
            }
        }
    }
}