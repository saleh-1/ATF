package com.sprojects.atf;

import android.view.View;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

public class ATFControllers
{


    // #####################################################################

    public static class ControllerBase extends ATFFragment
    {
        // #####################################################################

        public ATFModels.ModelInterfaceAction modelInterfaceActionHandler(final Runnable successRunnable)
        {
            return new ATFModels.ModelInterfaceAction() {

                @Override
                public void onSuccess(String message) {
                    //
                    loadingIndicator.stopLoading();
                    if(successRunnable != null) successRunnable.run();
                }

                @Override
                public void onError(String message) {
                    //
                    loadingIndicator.stopLoading();
                    showToastError(context, message);
                }
            };
        }
    }


    // #####################################################################

    public static class ControllerListBase extends ControllerBase
    {
        protected int currentDataPage = 1;
        protected boolean dataDidLoaded = false;

        // #####################################################################

        protected void prepare()
        {

        }

        // #####################################################################

        @Override
        public void init()
        {
            super.init();

            prepare();
            sendRequestForInit();
            showLoadingView();
        }

        // #####################################################################

        @Override
        public void onMessageEvent(NotificationMessageEvent nme)
        {
            super.onMessageEvent(nme);

            //
            if(nme.message == "Reload")
            {
                init();
            }
        }

        // #####################################################################

        @Override
        public boolean refreshingAllow()
        {
            return true;
        }

        @Override
        public void refreshingAction()
        {
            super.refreshingAction();
            sendRequestForInit();
        }

        @Override
        public boolean paginationAllow()
        {
            return true;
        }

        @Override
        public void paginationAction()
        {
            super.paginationAction();
            sendRequestForLoadMore();
        }


        // #####################################################################

        protected void sendRequest(boolean asNew)
        {
            //
        }

        protected void sendRequestForInit()
        {
            if(loadingIndicator.IsRequestLoading) return;
            currentDataPage = 1;
            sendRequest(true);
        }

        protected void sendRequestForLoadMore()
        {
            if(loadingIndicator.IsRequestLoading) return;
            if(!paginationAllow()) return;

            currentDataPage += 1;
            sendRequest(false);
        }



        // #####################################################################

        public ATFModels.ModelInterfaceList modelInterfaceListHandler(final boolean asNew)
        {
            return new ATFModels.ModelInterfaceList() {

                @Override
                public void onDataReceived(ArrayList<Object> objects) {

                    loadingIndicator.stopLoading();
                    if(objects == null || objects.size() == 0)
                    {
                        onError(getString(R.string.no_data));
                        return;

                    }else{

                        dataDidLoaded = true;
                    }


                    ArrayList<View> views = new ArrayList<>();

                    for(Object itm: objects)
                    {
                        View v = objectToView(itm);
                        if(v != null) views.add(v);
                    }

                    if(views.size() > 0)
                    {
                        if(asNew)
                        {
                            refreshViews(views);

                        }else{
                            ArrayList<View> v = new ArrayList<>();
                            v.addAll(lvViews);
                            v.addAll(views);
                            refreshViews(v);
                        }
                    }
                }

                @Override
                public void onError(String message) {

                    loadingIndicator.stopLoading();

                    if(currentDataPage == 1)
                    {
                        ArrayList<View> views = new ArrayList<>();
                        views.add(ATFViews.viewAlert(context, message));
                        refreshViews(views);

                    }else{

                        ATFUtils.showToastInfo(context, message);
                    }
                }
            };
        }

        // #####################################################################

        public View objectToView(Object object)
        {
            return null;
        }

        // #####################################################################

    }


    // #####################################################################

    public static class ControllerDetailsBase extends ControllerBase
    {
        // #####################################################################

        protected void prepare()
        {

        }

        // #####################################################################

        @Override
        public void init()
        {
            super.init();

            prepare();
            sendRequestForInit();
            showLoadingView();
        }


        // #####################################################################

        @Override
        public boolean refreshingAllow()
        {
            return true;
        }

        @Override
        public void refreshingAction()
        {
            super.refreshingAction();
            sendRequestForInit();
        }

        @Override
        public boolean paginationAllow()
        {
            return false;
        }

        @Override
        public void paginationAction()
        {
            super.paginationAction();
        }


        // #####################################################################

        protected void sendRequest()
        {
            //
        }


        // #####################################################################

        protected void sendRequestForInit()
        {
            if(loadingIndicator.IsRequestLoading) return;
            sendRequest();
        }


        // #####################################################################

        protected ATFModels.ModelInterfaceDetails modelInterfaceDetailsHandler()
        {
            //
            return new ATFModels.ModelInterfaceDetails() {

                @Override
                public void onDataReceived(Object object) {
                    loadingIndicator.stopLoading();
                    setupView(object);
                }

                @Override
                public void onError(String message) {
                    loadingIndicator.stopLoading();
                    ArrayList<View> views = new ArrayList<>();
                    views.add(ATFViews.viewAlert(context, message));
                    refreshViews(views);
                    ATFUtils.showToastInfo(context, message);
                }
            };
        }


        // #####################################################################

        protected void setupView(Object object)
        {
            //
        }

    }


    // #####################################################################

    public static class ControllerSimpleBase extends ControllerBase
    {

        // #####################################################################

        @Override
        public void init()
        {
            super.init();

            refreshViews(null);
        }

        // #####################################################################

        @Override
        protected ArrayList<View> getViews()
        {
            return new ArrayList<>();
        }


        // #####################################################################

        @Override
        public boolean refreshingAllow()
        {
            return false;
        }

        // #####################################################################

        @Override
        public boolean paginationAllow()
        {
            return false;
        }

        // #####################################################################

    }
}