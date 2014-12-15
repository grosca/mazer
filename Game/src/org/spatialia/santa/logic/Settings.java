package org.spatialia.santa.logic;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.spatialia.santa.Sprite;
import org.spatialia.santa.GameInput.Movement;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Re-usable settings component. 9/9/2013
 */
public class Settings {

	public static final String SETTINGS_FILE = "app.settings";

	public static final String EULA_ACCEPTED = "eula.accepted";
	public static final String AUR_ACCEPTED = "aur.accepted";

	public static final String LEVEL = "level";
	public static final String GIFTS = "gifts";

	public Settings(Context context) {
		m_context = context.getApplicationContext();
		m_prefs = m_context.getSharedPreferences(SETTINGS_FILE, 0);
		setDefaultValue(LEVEL, 0);
	}

	public boolean getBoolean(String name) {
		boolean defValue = false;
		if (m_mDefValues.containsKey(name))
			defValue = (Boolean) m_mDefValues.get(name);
		return m_prefs == null ? defValue : m_prefs.getBoolean(name, defValue);
	}

	public Long getLong(String name) {
		long defValue = 0;
		if (m_mDefValues.containsKey(name))
			defValue = (Long) m_mDefValues.get(name);
		return m_prefs == null ? defValue : m_prefs.getLong(name, defValue);
	}

	public int getInt(String name) {
		int defValue = -1;
		if (m_mDefValues.containsKey(name))
			defValue = (Integer) m_mDefValues.get(name);

		return m_prefs == null ? defValue : m_prefs.getInt(name, defValue);
	}

	public String getString(String name) {
		String defValue = "";
		if (m_mDefValues.containsKey("name"))
			defValue = (String) m_mDefValues.get(name);
		return m_prefs == null ? defValue : m_prefs.getString(name, defValue);
	}

	public void setBoolean(String name, boolean value) {
		if (m_prefs == null) {
			return;
		}
		SharedPreferences.Editor edit = m_prefs.edit();
		edit.putBoolean(name, value);
		edit.commit();
	}

	public void setLong(String name, Long value) {
		if (m_prefs == null) {
			return;
		}
		SharedPreferences.Editor edit = m_prefs.edit();
		edit.putLong(name, value);
		edit.commit();
	}

	public void setInt(String name, int value) {
		if (m_prefs == null) {
			return;
		}
		SharedPreferences.Editor edit = m_prefs.edit();
		edit.putInt(name, value);
		edit.commit();
	}

	public void setString(String name, String value) {
		if (m_prefs == null) {
			return;
		}
		SharedPreferences.Editor edit = m_prefs.edit();
		edit.putString(name, value);
		edit.commit();
	}

	public void getSprite(String name, Sprite sprite) {
		try {
			JSONObject obj = new JSONObject(m_prefs.getString(name, ""));

			if (obj.getInt(name + "deltaX") != -1) {
				sprite.setX(obj.getInt(name + "x"));
				sprite.setY(obj.getInt(name + "y"));
				sprite.setDx(obj.getInt(name + "dx"));
				sprite.setDy(obj.getInt(name + "dy"));
				sprite.setDeltaX(obj.getInt(name + "deltaX"));
				sprite.setDeltaY(obj.getInt(name + "deltaY"));

				String dir = obj.getString(name + "direction");
				if (dir.length() > 0) {
					sprite.setDirection(Movement.valueOf(dir));
				}

				sprite.setVisible(obj.getBoolean(name + "visible"));
			}
		} catch (Exception ex) {
		}
	}

	public void setSprite(String name, Sprite sprite) {
		if (m_prefs == null) {
			return;
		}
		SharedPreferences.Editor edit = m_prefs.edit();

		JSONObject obj = new JSONObject();
		try {
			obj.put(name + "x", sprite.getX());
			obj.put(name + "y", sprite.getY());
			obj.put(name + "dx", sprite.getDx());
			obj.put(name + "dy", sprite.getDy());
			obj.put(name + "deltaX", sprite.getDeltaX());
			obj.put(name + "deltaY", sprite.getDeltaY());
			obj.put(name + "direction", sprite.getDirection().name());
			obj.put(name + "visible", sprite.isVisible());
		} catch (Exception ex) {
		}

		edit.putString(name, obj.toString());
		edit.commit();
	}

	private SharedPreferences m_prefs;
	private Context m_context;

	public static void setDefaultValue(String s, Object o) {
		m_mDefValues.put(s, o);
	}

	private static Map<String, Object> m_mDefValues = new HashMap<String, Object>();
}
