package com.sprojects.atf;

import android.view.View;

public interface ATFNavBarInterface
{
    void setTitle(String title);
    void setRightButton(View v);
    void setLeftButton(View v);
    void refreshActivity();
}
