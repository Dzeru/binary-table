package com.pie.binarytable.dto;

import java.util.HashMap;

public class AddGoalReturnParams
{
    private String url;
    private HashMap<String, Object> params;

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public HashMap<String, Object> getParams()
    {
        return params;
    }

    public void setParams(HashMap<String, Object> params)
    {
        this.params = params;
    }
}
