package com.sprojects.atf;

import android.view.View;

public interface ATFNavBarManagerInterface
{
    void setTitle(String title);
    void setRightButton(View v);
    void setLeftButton(View v);
    void refreshActivity();
}
