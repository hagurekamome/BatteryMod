package com.hagurekamome.batterymod;

import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import android.content.res.XResources;

public class BatteryMod implements IXposedHookZygoteInit
{
	private static XSharedPreferences pref;
	private int[] colorArray = {0xffffffff,0xffff0000,0xff00ff00,0xff0000ff,0xffffff00,0xffff00ff,0xff00ffff,0xff000000};
	private String dateFormat = "HŽžmm•ª";
	
	@Override
	public void initZygote(IXposedHookZygoteInit.StartupParam startupParam) throws Throwable {
		pref = new XSharedPreferences(BatteryMod.class.getPackage().getName());
		final int lowBattery = Integer.parseInt(pref.getString("low", "1"));
		final int middleBattery = Integer.parseInt(pref.getString("middle", "5"));
		final int fullBattery = Integer.parseInt(pref.getString("full", "2"));
		final int warnningBattery = pref.getInt("lowbattery",15);
		final int criticalBattery = pref.getInt("criticalbattery",4);
		final int clearWarnning = pref.getInt("clearwarnning",20);
		final boolean led = pref.getBoolean("config_led_off_when_battery_fully_charged",false);
		
		try{
			XResources.setSystemWideReplacement("android", "bool", "config_led_off_when_battery_fully_charged", led);
			XResources.setSystemWideReplacement("android", "integer", "config_lowBatteryWarningLevel", warnningBattery);
			XResources.setSystemWideReplacement("android", "integer", "config_criticalBatteryWarningLevel", criticalBattery);
			XResources.setSystemWideReplacement("android", "integer", "config_lowBatteryCloseWarningLevel", clearWarnning);
			XResources.setSystemWideReplacement("android", "integer", "config_notificationsBatteryLowARGB", colorArray[lowBattery]);
			XResources.setSystemWideReplacement("android", "integer", "config_notificationsBatteryMediumARGB", colorArray[middleBattery]);
			XResources.setSystemWideReplacement("android", "integer", "config_notificationsBatteryFullARGB", colorArray[fullBattery]);
			XResources.setSystemWideReplacement("android", "integer", "config_MaxConcurrentDownloadsAllowed", 10);
			XResources.setSystemWideReplacement("android", "array", "config_tether_bluetooth_regexs", new String[] { "bnep\\d" });
			XResources.setSystemWideReplacement("android", "string", "twenty_four_hour_time_format", dateFormat);
		} catch (Throwable t) {
			XposedBridge.log(t);
		}
	}
}
