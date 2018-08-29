package com.sprojects.atf;

public interface ATFNavigationInterface
{
    void pushFragment(ATFFragment newFragment);
    void popFragment();
    void popFragment(int position, String notification);
    int getDepth();
}