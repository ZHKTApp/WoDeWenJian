package com.zwyl.myfile.util;

import android.content.Context;

public class SharedPrefsUtil {
	public final static String SETTING = "MLRJ";
	public final static String SHOWGUIDE = "GUIDE";
	public final static String USERKEY = "USER";

	public static void put(Context context, String key, int value) {
		context.getSharedPreferences(SETTING, Context.MODE_PRIVATE).edit().putInt(key, value).commit();
	}
	public static void put(Context context, String key, long value) {
		context.getSharedPreferences(SETTING, Context.MODE_PRIVATE).edit().putLong(key, value).commit();
	}

	public static void put(Context context, String key, boolean value) {
		context.getSharedPreferences(SETTING, Context.MODE_PRIVATE).edit().putBoolean(key, value).commit();
	}

	public static void put(Context context, String key, String value) {
		context.getSharedPreferences(SETTING, Context.MODE_PRIVATE).edit().putString(key, value).commit();
	}

	public static int get(Context context, String key, int defValue) {
		return context.getSharedPreferences(SETTING, Context.MODE_PRIVATE).getInt(key, defValue);
	}
	public static long get(Context context, String key, long defValue) {
		return context.getSharedPreferences(SETTING, Context.MODE_PRIVATE).getLong(key, defValue);
	}

	public static boolean get(Context context, String key, boolean defValue) {
		return context.getSharedPreferences(SETTING, Context.MODE_PRIVATE).getBoolean(key, defValue);
	}

	public static String get(Context context, String key, String defValue) {
		return context.getSharedPreferences(SETTING, Context.MODE_PRIVATE).getString(key, defValue);
	}

	public static void remove(Context context, String key) {
		context.getSharedPreferences(SETTING, Context.MODE_PRIVATE).edit().remove(key).commit();
	}
}
