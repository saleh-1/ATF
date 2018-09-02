package com.sprojects.atf;

public interface ATFRequestInterface
{
    void onDataReceived(ATFResponse xResponse);
    void onError(String message);
    //void onAuthFailure();
}
