package com.ahmetroid.popularmovies.utils;

import android.content.Context;

public class Codes {

    public static final String ADMOB_APP_ID = "ca-app-pub-3758024742170084~8867125254";

    public static final String POSTER_URL = "http://image.tmdb.org/t/p/w342";
    public static final String BACKDROP_URL = "http://image.tmdb.org/t/p/w780";

    // internet status codes
    public static final int LOADING = 0;
    public static final int SUCCESS = 1;
    public static final int ERROR = 2;


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
            case Codes.THRILLER:
                return "53";
            case Codes.WAR:
                return "10752";
            case Codes.WESTERN:
            default:
                return "37";
        }
    }

    public static String getSortingName(Context context, int sortingCode) {
        switch (sortingCode) {
            case Codes.POPULAR:
                return "Popular";
            case Codes.TOP_RATED:
                return "Top Rated";
            case Codes.NOW_PLAYING:
                return "Now Playing";
            case Codes.UPCOMING:
                return "Upcoming";
            case Codes.FAVORITES:
                return "Favorites";
            case Codes.ACTION:
                return "Action";
            case Codes.ADVENTURE:
                return "Adventure";
            case Codes.ANIMATION:
                return "Animation";
            case Codes.COMEDY:
                return "Comedy";
            case Codes.CRIME:
                return "Crime";
            case Codes.DRAMA:
                return "Drama";
            case Codes.FAMILY:
                return "Family";
            case Codes.FANTASY:
                return "Fantasy";
            case Codes.HISTORY:
                return "History";
            case Codes.HORROR:
                return "Horror";
            case Codes.MUSIC:
                return "Music";
            case Codes.MYSTERY:
                return "Mystery";
            case Codes.ROMANCE:
                return "Romance";
            case Codes.SCIENCE_FICTION:
                return "Science Fiction";
            case Codes.THRILLER:
                return "Thriller";
            case Codes.WAR:
                return "War";
            case Codes.WESTERN:
            default:
                return "Western";
        }
    }
}
