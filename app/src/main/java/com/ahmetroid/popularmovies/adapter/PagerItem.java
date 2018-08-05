package com.ahmetroid.popularmovies.adapter;

public class PagerItem {
    private final String title;
    private final int sortingCode;


    public PagerItem(String title, int sortingCode) {
        this.title = title;
        this.sortingCode = sortingCode;
    }

    public String getTitle() {
        return title;
    }

    public int getSortingCode() {
        return sortingCode;
    }
}
