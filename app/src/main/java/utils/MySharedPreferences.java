package utils;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by jigsaw on 7/1/18.
 */

public class MySharedPreferences {

    private static String PREF_RECIPE_ARRAY_JSON = "allRecipeArrayList";
    private static String PREF_FAVRT_RECIPE_ARRAY_JSON = "favrtRecipeArrayList";
    private static String PREF_RECIPE_BYCATEGORY = "recipeByCategory";
    private static String PREF_RECIPE_BYCUISINES = "recipeByCuisines";
    private static final String PREF_LOGIN_STATE = "login state";
    private static final String PREF_ADMIN_LOGIN_STATE = "admin login state";

    private static final String PREF_USERNAME = "username";



    public static Boolean getStoredLoginStatus (Context context){
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(PREF_LOGIN_STATE,false);
    }

    public static void setStoredLoginStatus (Context context, Boolean status){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(PREF_LOGIN_STATE,status)
                .apply();
    }

    public static String getStoredUsername (Context context){
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_USERNAME,null);
    }

    public static void setStoredUsername (Context context, String status){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF_USERNAME,status)
                .apply();
    }

    public static Boolean isAdminLoggedOn (Context context){
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(PREF_ADMIN_LOGIN_STATE,false);
    }

    public static void setIsAdminLoggedOn (Context context, Boolean status){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(PREF_ADMIN_LOGIN_STATE,status)
                .apply();
    }



}
