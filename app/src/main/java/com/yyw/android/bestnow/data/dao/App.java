package com.yyw.android.bestnow.data.dao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "APP".
 */
public class App {

    private String packageName;
    private String label;

    public App() {
    }

    public App(String packageName) {
        this.packageName = packageName;
    }

    public App(String packageName, String label) {
        this.packageName = packageName;
        this.label = label;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

}