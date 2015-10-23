package com.framgia.imagemanagement.adapters;

/**
 * Created by FRAMGIA\luu.vinh.loc on 20/10/2015.
 */

public class GoogleImageBean {
    String thumbUrl;
    String title;

    public GoogleImageBean() {
    }

    public String getThumbUrl() {
        return this.thumbUrl;
    }

    public void setThumbUrl(String url) {
        this.thumbUrl = url;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
