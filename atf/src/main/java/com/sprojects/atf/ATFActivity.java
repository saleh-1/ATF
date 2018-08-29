package com.sprojects.atf;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;


public class ATFActivity extends AppCompatActivity
{
    // #####################################################################

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_layout);

        setupTabs();
    }

    // #####################################################################

    protected void setupTabs()
    {
        final TabManager tabManager = new TabManager(getSupportFragmentManager());

        tabManager.tabs = getTabs(tabManager);

        final BottomNavigationViewEx bnve = (BottomNavigationViewEx) findViewById(R.id.bnve);
        tabManager.setupView(bnve);
    }

    // #####################################################################

    protected ArrayList<TabItem> getTabs(TabManager tabManager)
    {
        return null;
    }

    // #####################################################################

    protected void restartActivity()
    {
        finish();
        startActivity(getIntent());
    }

    // #####################################################################

    public static class TabManager implements ATFNavigationInterface
    {
        public TabItem currentTab;
        public ArrayList<TabItem> tabs = new ArrayList<>();
        private FragmentManager fragmentManager;

        // #####################################################################

        public TabManager(FragmentManager fragmentManager)
        {
            this.fragmentManager = fragmentManager;
        }

        // #####################################################################

        public void setupView(BottomNavigationViewEx bnve)
        {
            if(bnve == null) return;
            if(tabs == null || tabs.size() == 0) return;

            //
            bnve.getMenu().clear();

            for(TabItem itm : tabs)
            {
                bnve.getMenu().add(Menu.NONE, itm.tabId, Menu.NONE, "").setIcon(itm.tabIconResource);
            }

            bnve.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item)
                {
                    onNavigationItemSelectedListener(item.getItemId());
                    return true;
                }
            });

            //
            bnve.enableAnimation(false);
            bnve.enableShiftingMode(false);
            bnve.enableItemShiftingMode(false);
            bnve.setIconSize(36, 36);

            //
            onNavigationItemSelectedListener(tabs.get(0).tabId);
        }

        // #####################################################################

        public void onNavigationItemSelectedListener(int tabId)
        {
            currentTab = getTabById(tabId);
            if(currentTab != null)
            {
                ATFFragment f = currentTab.getFragment();
                if(f != null) viewFragment(f);
            }
        }

        // #####################################################################

        public TabItem getTabById(int tabId)
        {
            if(tabs == null || tabs.size() == 0) return null;

            for(TabItem itm : tabs)
            {
                if(itm.tabId == tabId) return itm;
            }

            return null;
        }

        // #####################################################################

        public void viewFragment(ATFFragment fragment)
        {
            if(fragment == null || fragmentManager == null) return;

            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.frame_layout, fragment);
            transaction.commit();

        }

        // #####################################################################

        @Override
        public void pushFragment(ATFFragment newFragment)
        {
            if(currentTab == null) return;
            currentTab.stackFragments.add(newFragment);
            viewFragment(newFragment);
        }

        // #####################################################################

        @Override
        public void popFragment()
        {
            popFragment(1, null);
        }

        // #####################################################################

        @Override
        public void popFragment(int position, String notification)
        {
            if(currentTab == null) return;
            if(currentTab.stackFragments == null || currentTab.stackFragments.size() < (position+1)) return;

            for(int i=0; i < position ;i++)
            {
                currentTab.stackFragments.remove(currentTab.getFragment());
            }

            ATFFragment f = currentTab.getFragment();
            //f.addNotificationObserver(notification);

            viewFragment(f);
        }

        // #####################################################################

        @Override
        public int getDepth()
        {
            return (currentTab.stackFragments != null) ? currentTab.stackFragments.size() : 0;
        }
    }

    // #####################################################################

    public static class TabItem
    {
        public int tabId;
        public int tabIconResource;
        public ArrayList<ATFFragment> stackFragments = new ArrayList<>();

        // #####################################################################

        public TabItem(int tabId, ATFFragment fragment, int tabIconResource)
        {
            this.tabId = tabId;
            this.tabIconResource = tabIconResource;
            this.stackFragments.add(fragment);
        }

        // #####################################################################

        public ATFFragment getFragment()
        {
            if(stackFragments != null && stackFragments.size() > 0)
            {
                return stackFragments.get(stackFragments.size()-1);
            }

            return null;
        }
    }

    // #####################################################################

    public class NavBarManager implements ATFNavBarManagerInterface
    {

        // #####################################################################

        @Override
        public void setTitle(String title)
        {
            if(title == null) return;

            View navbar_container = findViewById(R.id.navbar_view);

            if(navbar_container == null) return;

            TextView titleView = (TextView) navbar_container.findViewById(R.id.navbar_title);
            if(titleView != null) titleView.setText(title);
        }

        // #####################################################################

        @Override
        public void setRightButton(View v)
        {
            View navbar_container = findViewById(R.id.navbar_view);

            if(navbar_container == null) return;

            RelativeLayout rightButtonView = (RelativeLayout) navbar_container.findViewById(R.id.navbar_right);
            if(rightButtonView != null)
            {
                rightButtonView.removeAllViews();
                if(v != null) rightButtonView.addView(v);
            }
        }

        // #####################################################################

        @Override
        public void setLeftButton(View v)
        {
            View navbar_container = findViewById(R.id.navbar_view);

            if(navbar_container == null) return;

            RelativeLayout leftButtonView = (RelativeLayout) navbar_container.findViewById(R.id.navbar_left);
            if(leftButtonView != null)
            {
                leftButtonView.removeAllViews();
                if(v != null) leftButtonView.addView(v);
            }
        }

        // #####################################################################


        @Override
        public void refreshActivity()
        {
            restartActivity();
        }
    }
}