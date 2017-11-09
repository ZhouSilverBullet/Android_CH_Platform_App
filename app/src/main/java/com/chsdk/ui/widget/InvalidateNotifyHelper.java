package com.chsdk.ui.widget;

public class InvalidateNotifyHelper {
	
	private static final int SEND_MSG_DELAY = 15;
	private long mLastNotifyTime = 0;
	
	public InvalidateNotifyHelper(){
	}
	
	public synchronized void add(boolean isEnforce){
		if(isEnforce){
			onNeedNotify();
			return;
		}
		if(System.currentTimeMillis() - mLastNotifyTime < SEND_MSG_DELAY){
			return;
		}
		mLastNotifyTime = System.currentTimeMillis();
		onNeedNotify();
	}
	
	protected void onNeedNotify(){

	}

}
