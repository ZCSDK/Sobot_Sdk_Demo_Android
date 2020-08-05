package com.sobot.demo;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * 数据保存
 */
public class SobotSPUtil {
	private static SharedPreferences sharedPreferences;
	private static String CONFIG = "sobot_demo_config";
	
	public static void saveStringData(Context context,String key,String value){
		if(sharedPreferences == null){
			sharedPreferences = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
		}
		sharedPreferences.edit().putString(key, value).commit();
	}

	public static void saveBooleanData(Context context,String key,boolean value){
		if(sharedPreferences == null){
			sharedPreferences = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
		}
		sharedPreferences.edit().putBoolean(key, value).commit();
	}

	public static boolean getBooleanData(Context context,String key,boolean defValue){
		if(sharedPreferences == null){
			sharedPreferences = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
		}
		return sharedPreferences.getBoolean(key, defValue);
	}

	public static String getStringData(Context context,String key,String defValue){
		if(sharedPreferences == null){
			sharedPreferences = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
		}
		return sharedPreferences.getString(key, defValue);
	}

	public static void saveIntData(Context context,String key,int value){
		if(sharedPreferences == null){
			sharedPreferences = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
		}
		sharedPreferences.edit().putInt(key, value).commit();
	}

	public static void saveLongData(Context context,String key,long value){
		if(sharedPreferences == null){
			sharedPreferences = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
		}
		sharedPreferences.edit().putLong(key, value).commit();
	}

	public static int getIntData(Context context,String key,int defValue){
		if(sharedPreferences == null){
			sharedPreferences = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
		}
		return sharedPreferences.getInt(key, defValue);
	}

	public static long getLongData(Context context,String key,long defValue){
		if(sharedPreferences == null){
			sharedPreferences = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
		}
		return sharedPreferences.getLong(key, defValue);
	}

	public static void removeKey(Context context,String key){
		if(sharedPreferences == null){
			sharedPreferences = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
		}
		sharedPreferences.edit().remove(key).commit();
	}


	/**
	 * 使用SharedPreference保存对象
	 *
	 * @param key        储存对象的key
	 * @param saveObject 储存的对象
	 */
	public static void saveObject(Context context, String key, Object saveObject) {
		if(sharedPreferences == null){
			sharedPreferences = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
		}
		SharedPreferences.Editor editor = sharedPreferences.edit();
		String string = Object2String(saveObject);
		editor.putString(key, string);
		editor.commit();
	}

	/**
	 * 获取SharedPreference保存的对象
	 *
	 * @param key 储存对象的key
	 * @return object 返回根据key得到的对象
	 */
	public static Object getObject(Context context, String key) {
		if(sharedPreferences == null){
			sharedPreferences = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
		}
		String string = sharedPreferences.getString(key, null);
		if (string != null) {
			Object object = String2Object(string);
			return object;
		} else {
			return null;
		}
	}

	/**
	 * writeObject 方法负责写入特定类的对象的状态，以便相应的 readObject 方法可以还原它
	 * 最后，用Base64.encode将字节文件转换成Base64编码保存在String中
	 *
	 * @param object 待加密的转换为String的对象
	 * @return String   加密后的String
	 */
	private static String Object2String(Object object) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ObjectOutputStream objectOutputStream = null;
		try {
			objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
			objectOutputStream.writeObject(object);
			String string = new String(Base64.encode(byteArrayOutputStream.toByteArray(), Base64.DEFAULT));
			objectOutputStream.close();
			return string;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 使用Base64解密String，返回Object对象
	 *
	 * @param objectString 待解密的String
	 * @return object      解密后的object
	 */
	private static Object String2Object(String objectString) {
		byte[] mobileBytes = Base64.decode(objectString.getBytes(), Base64.DEFAULT);
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(mobileBytes);
		ObjectInputStream objectInputStream = null;
		try {
			objectInputStream = new ObjectInputStream(byteArrayInputStream);
			Object object = objectInputStream.readObject();
			objectInputStream.close();
			return object;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}
}