package com.ahmetroid.popularmovies.utils;

public class Codes {

    // Movie List Activity Codes
    public static final int POPULAR = 0;
    public static final int TOP_RATED = 1;
    public static final int NOW_PLAYING = 2;
    public static final int UPCOMING = 3;

    // Movie Genre Activity Codes
    public static final int ACTION = 4;
    public static final int ADVENTURE = 5;
    public static final int ANIMATION = 6;
    public static final int COMEDY = 7;
    public static final int CRIME = 8;
    public static final int DRAMA = 10;
    public static final int FAMILY = 11;
    public static final int FANTASY = 12;
    public static final int HISTORY = 13;
    public static final int HORROR = 14;
    public static final int MUSIC = 15;
    public static final int MYSTERY = 16;
    public static final int ROMANCE = 17;
    public static final int SCIENCE_FICTION = 18;
    public static final int TV_MOVIE = 19;
    public static final int THRILLER = 20;
    public static final int WAR = 21;
    public static final int WESTERN = 22;

    // Favorites Activity Codes
    public static final int FAVORITES = 23;

    public static String getGenreCode(int sortingCode) {
        switch (sortingCode) {
            case Codes.ACTION:
                return "28";
            case Codes.ADVENTURE:
                return "12";
            case Codes.ANIMATION:
                return "16";
            case Codes.COMEDY:
                return "35";
            case Codes.CRIME:
                return "80";
            case Codes.DRAMA:
                return "18";
            case Codes.FAMILY:
                return "10751";
            case Codes.FANTASY:
                return "14";
            case Codes.HISTORY:
                return "36";
            case Codes.HORROR:
                return "27";
            case Codes.MUSIC:
                return "10402";
            case Codes.MYSTERY:
                return "9648";
            case Codes.ROMANCE:
                return "10749";
            case Codes.SCIENCE_FICTION:
                return "878";
            case Codes.TV_MOVIE:
                return "10770";
            case Codes.THRILLER:
                return "53";
            case Codes.WAR:
                return "10752";
            case Codes.WESTERN:
            default:
                return "37";
        }
    }
}
