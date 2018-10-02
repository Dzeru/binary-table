package com.pie.binarytable.dto;

/*
For sending screenshot to VK
*/
public class UploadScreenshotUrl
{
    private String uploadUrl;
    private String imageUrl;

    public String getUploadUrl()
    {
        return uploadUrl;
    }

    public void setUploadUrl(String uploadUrl)
    {
        this.uploadUrl = uploadUrl;
    }

    public String getImageUrl()
    {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl)
    {
        this.imageUrl = imageUrl;
    }
}
