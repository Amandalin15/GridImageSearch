package com.yahoo.gridimagesearch.models;

import java.io.Serializable;

public class Settings implements Serializable {
	private static final long serialVersionUID = 7539932941298601998L;

    public static final String OPTION_ALL = "All";

    private String size;
    private String color;
    private String type;
    private String site;

    public Settings() {
        this.size = OPTION_ALL;
        this.color = OPTION_ALL;
        this.type = OPTION_ALL;
        this.site = "";
    }

    public Settings(String size, String color, String type, String site) {
        this.size = size;
        this.color = color;
        this.type = type;
        this.site = site;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    @Override
    public String toString() {
        return "Settings [size=" + size + ", color=" + color + ", type=" + type + ", site=" + site
                + "]";
    }
}
