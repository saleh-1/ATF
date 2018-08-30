package com.sprojects.atf;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;

public class ATFFragment extends Fragment
{
    protected Context context;
    public ArrayList<View> lvViews = new ArrayList<>();
    public LoadingIndicator loadingIndicator;
    public SwipeRefreshLayout swipeRefresh;
    public ListView lv;
    public LVAdapter lVAdapter;
    public boolean viewAppeared = false;

    public ATFNavigationInterface navigationManager;
    public ATFNavBarManagerInterface navBarManager;
    public ArrayList<String> notificationObserverQueue = new ArrayList<>();

    // onAttach
    // onCreate
    // onCreateView
    // onActivityCreated
    // onStart
    // onResume

    // #####################################################################

    public static ATFFragment newInstantce(ATFNavBarManagerInterface nbmInterface, ATFNavigationInterface navigationInterface)
    {
        ATFFragment ins = new ATFFragment();
        ins.navigationManager = navigationInterface;
        ins.navBarManager = nbmInterface;

        return ins;
    }

    // #####################################################################

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        this.context = context;

    }

    // #####################################################################

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }

    // #####################################################################

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View mfv = inflater.inflate(R.layout.lv, container, false);

        lv = (ListView) mfv.findViewById(R.id.listview);
        lVAdapter = new LVAdapter(context);
        swipeRefresh = (SwipeRefreshLayout) mfv.findViewById(R.id.list_view_swipe_to_refresh);
        loadingIndicator = new LoadingIndicator(context);

        setupLV();

        return mfv;
    }

    // #####################################################################

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
    }

    // #####################################################################

    @Override
    public void onStart()
    {
        super.onStart();
        startNotificationObserver();
    }

    // #####################################################################

    @Override
    public void onResume()
    {
        super.onResume();

        View v = getView();

        if (v != null)
    {
            onVisible();

            v.setFocusableInTouchMode(true);
            v.requestFocus();
            v.setOnKeyListener(new View.OnKeyListener()
    {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event)
    {
                    if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK)
    {
                        return physicalButtonBackHandler();
                    }

                    return false;
                }
            });
        }
    }

    // #####################################################################

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);
    }

    // #####################################################################

    public void onVisible()
    {
        refreshNavBar();

        if (!viewAppeared) init();

        viewAppeared = true;

    }

    // #####################################################################

    public void init()
    {
        //
    }

    // #####################################################################

    public void addNotificationObserver(String notification)
    {
        //
        if (notificationObserverQueue == null) notificationObserverQueue = new ArrayList<>();
        notificationObserverQueue.add(notification);
    }

    // #####################################################################

    public void startNotificationObserver()
    {
        //
        if (notificationObserverQueue == null || notificationObserverQueue.size() == 0) return;

        for (String itm : notificationObserverQueue)
    {
            notificationObserverHandler(itm);
            notificationObserverQueue.remove(itm);
        }
    }

    // #####################################################################

    public void notificationObserverHandler(String notification)
    {
        //
    }


    // #####################################################################

    public boolean refreshingAllow()
    {
        return false;
    }

    public void refreshingAction()
    {
        //
    }

    public boolean paginationAllow()
    {
        return false;
    }

    public void paginationAction()
    {
        //
    }


    // #####################################################################

    public boolean physicalButtonBackHandler()
    {
        if (navigationManager != null && navigationManager.getDepth() > 1)
    {
            popFragment();
            return true;
        }

        return doubleBackToExit();
    }

    // #####################################################################

    boolean doubleBackToExitPressedOnce = false;

    public boolean doubleBackToExit()
    {
        if (doubleBackToExitPressedOnce)
    {
            getActivity().onBackPressed();
            return false;
        }

        doubleBackToExitPressedOnce = true;

        showToastInfo(context, getString(R.string.double_back_to_exit));

        new Handler().postDelayed(new Runnable()
    {
            @Override
            public void run()
    {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);

        return true;
    }

    // #####################################################################

    public void hideKeyboard()
    {
        try {

            FragmentActivity activity = getActivity();
            if (activity == null) return;

            //
            View view = activity.getCurrentFocus();

            if (view == null) view = new View(activity);

            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            if (imm != null) imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        } catch (Exception e)
    {
            //
        }
    }

    // #####################################################################

    public static void showToastError(Context context, String message)
    {
        try{

            Toast.makeText(context, message, Toast.LENGTH_LONG).show();

        }catch (Exception ex){}
    }

    // #####################################################################

    public static void showToastInfo(Context context, String message)
    {
        try{

            Toast.makeText(context, message, Toast.LENGTH_LONG).show();

        }catch (Exception ex){}
    }

    // #####################################################################

    public static void showToastSuccess(Context context, String message)
    {
        try{

            Toast.makeText(context, message, Toast.LENGTH_LONG).show();

        }catch (Exception ex){}
    }


    // #####################################################################

    public void setupLV()
    {
        if (lv == null) return;

        lv.setDivider(null);
        lv.setDividerHeight(0);
        lv.setAdapter(lVAdapter);

        if (paginationAllow())
    {
            lv.setOnScrollListener(new AbsListView.OnScrollListener()
    {

                public void onScrollStateChanged(AbsListView view, int scrollState)
    {
                }

                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
    {
                    if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount != 0)
    {
                        paginationAction();
                    }
                }
            });
        }

        if (swipeRefresh == null) return;

        if (refreshingAllow())
    {
            //SwipeRefresh.setColorSchemeResources(R.color.colorAccent);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
    {
                @Override
                public void onRefresh()
    {
                    refreshingAction();
                }
            });

        } else {

            swipeRefresh.setEnabled(false);
        }
    }


    // #####################################################################

    protected void refreshNavBar()
    {
        if (navBarManager == null) return;

        navBarManager.setTitle(getNavBarTitle());
        navBarManager.setRightButton(getNavBarRightButton());
        navBarManager.setLeftButton(getNavBarLeftButton());
    }


    // #####################################################################

    public String getNavBarTitle()
    {
        return getString(R.string.app_name);
    }

    // #####################################################################

    public View getNavBarRightButton()
    {
        if (navigationManager != null && navigationManager.getDepth() > 1)
    {
            return NavBarPopButton();
        }

        return null;
    }

    // #####################################################################

    public View getNavBarLeftButton()
    {
        return null;
    }

    // #####################################################################

    public void popFragment()
    {
        hideKeyboard();

        if (navigationManager != null) navigationManager.popFragment();
    }

    // #####################################################################

    public void popFragment(int position, String notification)
    {
        hideKeyboard();

        if (navigationManager != null) navigationManager.popFragment(position, notification);
    }


    // #####################################################################

    public void showLoadingView()
    {
        loadingIndicator.startLoading();
        ArrayList<View> views = new ArrayList<>();
        views.add(getLoadingView());
        refreshViews(views);
    }

    // #####################################################################

    protected View getAltView()
    {
        return ATFViews.viewAlert(context, getString(R.string.no_data));
    }

    // #####################################################################

    protected View getLoadingView()
    {
        return ATFViews.viewAlert(context, getString(R.string.loading));
    }

    // #####################################################################

    public void refreshViews(ArrayList<View> views)
    {
        refreshNavBar();
        createViews((views != null) ? views : getViews());
        if (lVAdapter != null) lVAdapter.notifyDataSetChanged();
        viewDidAppear();
    }

    // #####################################################################

    protected void createViews(ArrayList<View> views)
    {
        resetViews();

        ArrayList<View> headerViews = getHeaderViews();
        if (headerViews != null && headerViews.size() > 0) lvViews.addAll(getHeaderViews());

        if (views != null && views.size() != 0)
    {
            for (View itm : views)
    {
                if (itm != null) lvViews.add(itm);
            }

        } else {

            ArrayList<View> alt_views = getAltViews();

            for (View itm : alt_views)
    {
                if (itm != null) lvViews.add(itm);
            }
        }
    }

    // #####################################################################

    protected void viewDidAppear()
    {
        //
    }

    // #####################################################################

    protected void resetViews()
    {
        if (lvViews != null) lvViews.clear();
    }

    // #####################################################################

    protected ArrayList<View> getAltViews()
    {
        ArrayList<View> rtn = new ArrayList<>();

        rtn.add(getAltView());

        return rtn;
    }

    // #####################################################################

    protected ArrayList<View> getViews()
    {
        return new ArrayList<>();
    }

    // #####################################################################

    protected ArrayList<View> getHeaderViews()
    {
        return new ArrayList<>();
    }


    // #####################################################################

    protected ArrayList<View> simpleText(String text)
    {
        ArrayList<View> rtn = new ArrayList<>();

        rtn.add(ATFViews.viewAlert(context, text));

        return rtn;
    }

    // #####################################################################

    protected String getEditTextStringByTag(int tag)
    {
        try {

            EditText editText = (EditText) getView().findViewWithTag(tag);
            return editText.getText().toString();

        } catch (Exception ex)
    {
        }

        return null;
    }

    // #####################################################################

    public View NavBarPopButton()
    {
        return ATFViews.navBarButtonView(context, R.drawable.back, new Runnable()
    {
            @Override
            public void run()
    {
                popFragment();
            }
        });
    }

    // #####################################################################

    public void logout()
    {
        //Models.OAuth.clearLocally(context);
        //Models.UserObj.clearLocally(context);
        //if (navBarManager != null) navBarManager.refreshActivity();
    }



    // #####################################################################

    public void prepareRequest(Runnable runnable)
    {
        hideKeyboard();
        if(loadingIndicator == null || loadingIndicator.IsRequestLoading) return;

        if(runnable != null)
        {
            loadingIndicator.startLoading();
            runnable.run();
        }
    }


    // #####################################################################

    public class LVAdapter extends BaseAdapter
    {

        // #####################################################################

        public LVAdapter(Context context)
        {
            super();
        }

        // #####################################################################

        @Override
        public int getCount()
        {
            if(lvViews == null) return 1;
            if(lvViews.isEmpty()) return 1;
            if(lvViews.size() == 0) return 1;

            return lvViews.size();
        }

        // #####################################################################

        @Override
        public Object getItem(int position)
        {
            return null;
        }

        // #####################################################################

        @Override
        public long getItemId(int position)
        {
            return position;
        }

        // #####################################################################

        @Override
        public boolean isEnabled(int position)
        {
            return false;
        }

        // #####################################################################

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            View v = null;

            if(lvViews.size() != 0 && position <= lvViews.size()) v = (lvViews.get(position));

            if(v == null)
            {
                if(loadingIndicator.IsRequestLoading)
                {
                    v = getLoadingView();

                }else{

                    v = getAltView();
                }

            }

            return v;
        }
    }


    // #####################################################################

    public class LoadingIndicator
    {
        public Boolean IsRequestLoading = false;
        public ProgressDialog RequestIndicator;

        // #####################################################################

        public LoadingIndicator(Context context)
        {
            RequestIndicator = new ProgressDialog(context);
        }

        // #####################################################################

        public void startLoading()
        {
            try {

                IsRequestLoading = true;

                if(swipeRefresh != null && swipeRefresh.isRefreshing()) return;

                if(RequestIndicator != null)
                {
                    RequestIndicator.show();
                    RequestIndicator.setContentView(R.layout.indicator);
                    RequestIndicator.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    RequestIndicator.getWindow().setDimAmount(0);

                    ProgressBar pb = (ProgressBar)RequestIndicator.findViewById(R.id.progressBar);

                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
    {

                        Drawable wrapDrawable = DrawableCompat.wrap(pb.getIndeterminateDrawable());
                        //DrawableCompat.setTint(wrapDrawable, ContextCompat.getColor(getActivity(), R.color.colorPrimary));
                        DrawableCompat.setTint(wrapDrawable, ContextCompat.getColor(context, R.color.colorSecondary));
                        pb.setIndeterminateDrawable(DrawableCompat.unwrap(wrapDrawable));

                    } else {

                        pb.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(context, R.color.colorSecondary), PorterDuff.Mode.SRC_IN);
                    }

                }

            }catch (Exception ex){}
        }

        // #####################################################################

        public void stopLoading()
        {
            IsRequestLoading = false;
            if (RequestIndicator != null) RequestIndicator.dismiss();
            if(swipeRefresh != null) swipeRefresh.setRefreshing(false);
        }
    }

    // #####################################################################

}