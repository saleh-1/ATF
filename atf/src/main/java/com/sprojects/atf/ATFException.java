package com.sprojects.atf;

public class ATFException extends Exception
{
    public String tag;
    public String message;

    public ATFException(String tag, String message)
    {
        this.tag = tag;
        this.message = message;
    }
}
