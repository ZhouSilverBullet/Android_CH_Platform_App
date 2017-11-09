package com.chsdk.utils;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class ShakeEvent implements SensorEventListener {
	private static final int UPTATE_INTERVAL_TIME = 70; // 两次检测时间间隔
	private static final int SPEED_SHRESHOLD = 3000;
	private SensorManager sensorMgr;
	private Context context;
	private OnShakeListener ShakeListener;

	private long lastClickTime = System.currentTimeMillis();
	private long lastUpdateTime;
	private float lastX;
	private float lastY;
	private float lastZ;

	public ShakeEvent(Context context, OnShakeListener listener) {
		this.context = context;
		this.ShakeListener = listener;
	}

	public void registerShake() {
		sensorMgr = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		if (sensorMgr == null) {
			if (ShakeListener != null)
				ShakeListener.onUnsuppot();
		}

		Sensor sensor = sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		boolean supported = sensorMgr.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
		if (!supported) {
			if (ShakeListener != null)
				ShakeListener.onUnsuppot();
		}
	}

	public void unRegisterShake() {
		if (sensorMgr != null) {
			sensorMgr.unregisterListener(this);
			sensorMgr = null;
		}
	}

	private boolean allowShake() {
		long time = System.currentTimeMillis();
		long timeD = time - lastClickTime;
		if (0 < timeD && timeD <= 1000) {// 10秒内不可
			return false;
		} else {
			lastClickTime = time;
			return true;
		}
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// 现在检测时间
		long currentUpdateTime = System.currentTimeMillis();
		// 两次检测间隔时间
		long timeInterVal = currentUpdateTime - lastUpdateTime;
		// 是否达到检测时间间隔
		if (timeInterVal < UPTATE_INTERVAL_TIME) {
			return;
		} else {
			// 现在的时间变成last时间
			lastUpdateTime = currentUpdateTime;

			// 获得x,y,z坐标
			float x = event.values[0];
			float y = event.values[1];
			float z = event.values[2];

			// 变化值
			float deltaX = x - lastX;
			float deltaY = y - lastY;
			float deltaZ = z - lastZ;

			// 把现在的坐标变成last坐标
			lastX = x;
			lastY = y;
			lastZ = z;

			double speed = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) / timeInterVal * 10000;

			if (speed >= SPEED_SHRESHOLD && allowShake()) {
				LogUtil.debugLog("onShake:timeInterVal_" + timeInterVal + ", speed_" + speed);
				if (ShakeListener != null)
					ShakeListener.onShake();
			}
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

	public interface OnShakeListener {
		public void onShake();

		public void onUnsuppot();
	}
}
