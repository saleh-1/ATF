package com.sprojects.atf;

public interface ATFNavigationInterface
{
    void pushFragment(ATFFragment newFragment);
    void popFragment();
    void popFragment(int position);
    int getDepth();
}