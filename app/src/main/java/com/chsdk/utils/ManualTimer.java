package com.chsdk.utils;

/** 
* @author  ZengLei <p>
* @version 2016年8月19日 <p>
*/

/**
 * 人工计时器 ：任务执行时间不超过指定时间则延长,反之则不做处理
 */
public class ManualTimer {
	private long startTime;
	private long durationS;

	public ManualTimer(long durationMs) {
		this.durationS = durationMs;
	}

	public void start() {
		startTime = System.currentTimeMillis();
	}

	public void end() {
		long countTime = System.currentTimeMillis() - startTime;

		if (countTime > durationS) {
			return;
		}

		delay(durationS - countTime);
	}

	private void delay(long delayTime) {
		try {
			Thread.sleep(delayTime);
		} catch (InterruptedException e) {
		}
	}
}