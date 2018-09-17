package com.sprojects.atf;

public interface ATFNavigationInterface
{
    void pushFragment(ATFFragment newFragment);
    void popFragment();
    void popFragment(int position);
    void popFragment(int position, String notificationObserverMessage);
    int getDepth();
}