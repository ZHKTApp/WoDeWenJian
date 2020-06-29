package com.zwyl.myfile.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import com.orhanobut.logger.Logger;
import com.zwyl.myfile.util.CRCUtil;
import com.zwyl.myfile.util.SharedPrefsUtil;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

/**
 * 设备参数获取
 */
public class DeviceUtil {

	/** 缓存设备号 */
	private static String CACHED_DEVICE_ID = null;
	/** 缓存CPU序列号 */
	private static String CACHED_CPU_SERIAL = null;
	/** 键 */
	private static String K_DEVICE_ID = "d.0";

	private static final Object LOCKER = new Object();

	private DeviceUtil() {
	}

	/**
	 * 获取缓存中的 设备ID
	 *
	 * @param context
	 * @return
	 */
	public static String getDeviceID(Context context) {
		if (CACHED_DEVICE_ID == null) {
			String id = SharedPrefsUtil.get(context, K_DEVICE_ID, null);
			String idSD = SharedPrefsUtil.get(context, K_DEVICE_ID, null);

			if (id == null && idSD == null) {
				String rId = genDeviceID(context);
				SharedPrefsUtil.put(context, K_DEVICE_ID, rId);
				SharedPrefsUtil.put(context, K_DEVICE_ID, rId);
				id = rId;
			} else if (id != null && (idSD == null /* || id.equals(idSD) */)) {
				SharedPrefsUtil.put(context, K_DEVICE_ID, id);
			} else if (id == null && idSD != null) {
				SharedPrefsUtil.put(context, K_DEVICE_ID, idSD);
				id = idSD;
			}
			CACHED_DEVICE_ID = id;
		}

		return CACHED_DEVICE_ID;
	}

	/**
	 * 获取设备ID
	 *
	 * @param context 环境上下文
	 * @return 设备ID号
	 * @see <a href="http://blog.csdn.net/billpig/article/details/6728573">http://blog.csdn.net/billpig/article/details/6728573</a>
	 */
	private static String genDeviceID(Context context) {
		String deviceId;
		synchronized (LOCKER) {
			int type = 1;
			do {
				deviceId = getCPUSerial(context);
				if (deviceId != null) {
					// 防止 000000 的情况
					if (!deviceId.matches("0+"))
						break;
				}
				type++;

				deviceId = getAndroidDeviceId(context);
				if (deviceId != null)
					break;
				type++;

				deviceId = getLocalMacAddress(context);
				if (deviceId != null)
					break;
				type++;

				deviceId = getIMSI(context);
				if (deviceId != null)
					break;
				type++;

				deviceId = getMACAddress(context);
				if (deviceId != null)
					break;
				type++;

				deviceId = getAndroidID(context);
				if (deviceId != null)
					break;
				type++;

				deviceId = "RAND" + System.currentTimeMillis();
				type = 0;
			} while (false);
			Logger.e("D" + type + ":" + deviceId);
			deviceId = packDeviceNumber(type, deviceId);
		}
		return deviceId;
	}

	private static String packDeviceNumber(int type, String deviceId) {
		try {
			String crc16 = CRCUtil.getCRC16(deviceId.getBytes("UTF-8"));
			String eStr = encryptDeviceID(type, deviceId);
			StringBuilder sb = new StringBuilder();
			sb.append(type).append((char) ('Z' - (type % 26))).append(crc16).append(eStr);
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return deviceId;
	}

	private static int encryptChar2Int(char c) {
		int j = -1;
		if (c >= '0' && c <= '9')
			j = c - '0';
		else if (c >= 'a' && c <= 'z')
			j = 10 + (c - 'a');
		else if (c >= 'A' && c <= 'Z')
			j = 36 + (c - 'A');
		else
			j = -1;
		return j;
	}

	private static char encryptInt2Char(char c, int j, int k) {
		if (j < 0)
			return c;
		j = (j + k) % 62;
		if (j < 0)
			j += 62;
		if (j < 10)
			return (char) ('0' + j);
		if (j < 36)
			return (char) ('a' + (j - 10));
		if (j < 62)
			return (char) ('A' + (j - 36));
		return c;
	}

	private static String encryptDeviceID(int type, String id) {
		char[] chars = id.toCharArray();
		char[] ret = new char[chars.length];
		int pos = type * 3 + 1;
		for (int i = 0; i < chars.length; i++) {
			char c = chars[i];
			int j = encryptChar2Int(c);
			ret[i] = encryptInt2Char(c, j, pos);
			pos += ret[i];
		}
		return new String(ret);
	}

	/**
	 * 所有添加有谷歌账户的设备可以返回一个 ANDROID_ID
	 *
	 * @param context
	 * @return
	 */
	public static String getAndroidID(Context context) {
		try {
			final String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
			if (androidId != null && !"9774d56d682e549c".equals(androidId)) {
				return androidId;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 权限要求： uses-permission android:name="android.permission.READ_PHONE_STATE" GSM手机的 IMEI 和 CDMA手机的 MEID.
	 */
	public static String getIMEI(Context context) {
		return getAndroidDeviceId(context);
	}

	/** 所有的设备都可以返回一个 TelephonyManager.getDeviceId() */
	@SuppressLint("MissingPermission")
	public static String getAndroidDeviceId(Context context) {
		try {
			TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			if (tm != null)
				return tm.getDeviceId();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/** SIM卡的 IMSI 属性 */
	@SuppressLint("MissingPermission")
	public static String getIMSI(Context context) {
		try {
			TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			if (tm != null)
				return tm.getSubscriberId();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/** 返回手机号 */
	@SuppressLint("MissingPermission")
	public static String getPhoneNumber(Context context) {
		try {
			TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			if (tm != null)
				return tm.getLine1Number();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getLocalMacAddress(Context context) {
		try {
			WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			WifiInfo info = (null == wifiMgr ? null : wifiMgr.getConnectionInfo());
			if (info != null) {
				String macAddr = info.getMacAddress();
				if (macAddr != null)
					return macAddr.replace(":", "");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/** 返回 cpu 序列号 */
	public static String getCPUSerial(Context context) {
		String serial = CACHED_CPU_SERIAL;
		try {
			if (serial == null) {
				Process pp = Runtime.getRuntime().exec("cat /proc/cpuinfo ");
				InputStreamReader ir = new InputStreamReader(pp.getInputStream());
				LineNumberReader input = new LineNumberReader(ir);
				String str;
				while ((str = input.readLine()) != null) {
					// Serial : 8c856f3b42f07105
					if (str.indexOf("Serial") > -1) {
						String strCPU = str.substring(str.indexOf(":") + 1, str.length()); //提取序列号
						serial = strCPU.trim(); //去空格
						break;
					}
				}
				CACHED_CPU_SERIAL = serial;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return serial;
	}

	public static String getMACAddress(Context context) {
		String macAdd = null;
		try {
			Process pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address ");
			InputStreamReader ir = new InputStreamReader(pp.getInputStream());
			LineNumberReader input = new LineNumberReader(ir);
			String str = input.readLine();
			if (str != null) {
				macAdd = str.trim();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		if (macAdd != null)
			return macAdd.replace(":", "");
		return macAdd;
	}
	/** 获取设备类别 如:HUAWEI G610-U00 */
	public static String getPhoneModel() {
		return Build.MODEL;
	}
	/** 获取android版本 如:4.2.1 */
	public static String getVersionName(Context ctx) {
		PackageManager manager;
		PackageInfo info = null;
		manager = ctx.getPackageManager();
		try {
			info = manager.getPackageInfo(ctx.getPackageName(), 0);
			return info.versionName;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return "1.0";
	}

	public static int getVersionCode(Context ctx) {
		PackageManager manager;
		PackageInfo info = null;
		manager = ctx.getPackageManager();
		try {
			info = manager.getPackageInfo(ctx.getPackageName(), 0);
			return info.versionCode;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static boolean checkNetWork(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}

}
