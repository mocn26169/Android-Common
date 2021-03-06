package com.bourne.common_library.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;
import java.util.Set;

/**
 * 轻量级数据存储工具
 */
public class PreferencesUtils {
	static PreferencesUtils singleton = null;
	static SharedPreferences preferences;
	static SharedPreferences.Editor editor;

	private PreferencesUtils(Context context, String fileName) {
		preferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		editor = preferences.edit();
	}

	public static PreferencesUtils getPreferences(Context context, String fileName) {
		if (singleton == null) {
			singleton = new Builder(context, fileName).build();
		}
		return singleton;
	}

	public void put(String key, String value) {
		editor.putString(key, value);
		editor.commit();
	}

	public void put(String key, boolean value) {
		editor.putBoolean(key, value);
		editor.commit();
	}

	public void put(String key, float value) {
		editor.putFloat(key, value);
		editor.commit();
	}

	public void put(String key, int value) {
		editor.putInt(key, value);
		editor.commit();
	}

	public void put(String key, Long value) {
		editor.putLong(key, value);
		editor.commit();
	}

	public void put(String key, Set<String> values) {
		editor.putStringSet(key, values);
		editor.commit();
	}

	public String getString(String key, String defValue) {
		return preferences.getString(key, defValue);
	}

	public int getInt(String key, int defValue) {
		return preferences.getInt(key, defValue);
	}

	public boolean getBoolean(String key, boolean defValue) {
		return preferences.getBoolean(key, defValue);
	}

	public long getLong(String key, long defValue) {
		return preferences.getLong(key, defValue);
	}

	public float getFloat(String key, float defValue) {
		return preferences.getFloat(key, defValue);
	}

	public Set<String> getStringSet(String key, Set<String> defValues) {
		return preferences.getStringSet(key, defValues);
	}

	public Map<String, ?> getAll() {
		return preferences.getAll();
	}

	public void remove(String key) {
		editor.remove(key).apply();
	}

	public void clear() {
		editor.clear().apply();
	}

	private static class Builder {
		private final Context context;
		private final String fileName;

		public Builder(Context context, String fileName) {
			if (context == null)
				throw new IllegalArgumentException("Context must not be null.");
			this.context = context.getApplicationContext();
			this.fileName = fileName;
		}

		/**
		 * Method that creates an instance of Prefs
		 *
		 * @return an instance of Prefs
		 */
		public PreferencesUtils build() {
			return new PreferencesUtils(context, fileName);
		}
	}

}
