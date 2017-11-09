package com.chsdk.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

public class ViewUtil {
	@SuppressWarnings("unchecked")
	public static <T extends View> T getView(View view, int id) {
		return (T) view.findViewById(id);
	}

	public static int dp2px(Context ctx, int dp) {
		final float scale = ctx.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}

	public static boolean setClickLimit(View view, int limitTimeMs, String tip) {
		if (view == null)
			return true;
		
		final View clickView = view;
		Object object = clickView.getTag(clickView.getId());
		if (object == null) {
			view.setEnabled(false);
			postDelay(clickView, limitTimeMs);
			return false;
		} else {
			boolean bBoolean = object instanceof Boolean;
			if (bBoolean) {
				boolean tag = (Boolean) object;
				if (tag) {
//					CHToast.show(clickView.getContext(), tip);
					return true;
				} else {
					view.setEnabled(false);
					postDelay(clickView, limitTimeMs);
					return false;
				}
			}
			return false;
		}
	}
	
	private static void postDelay(final View view, int limitTimeMs) {
		view.setTag(view.getId(), true);
		view.postDelayed(new Runnable() {
			@Override
			public void run() {
				view.setEnabled(true);
				view.setTag(view.getId(), false);
			}
		}, limitTimeMs);
	}
	
	/**
	 * 设置圆角背景
	 * 
	 * @param color
	 * @param flag
	 * @param radius
	 * @return GradientDrawable
	 */
	public static GradientDrawable addBtnBackgroundRound(String color, boolean flag, int radius) {
		// true标识设置为圆角，color为背景颜色,radius为圆角弧度
		int fillColor;
		GradientDrawable gd = null;
		if (flag == true) {
			fillColor = Color.parseColor(color);
			gd = new GradientDrawable();
			gd.setColor(fillColor);
			gd.setCornerRadius(radius);
		} else {
			fillColor = Color.parseColor(color);
			gd = new GradientDrawable();
			gd.setColor(fillColor);
		}
		return gd;
	}
	
	@SuppressWarnings("deprecation")
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public static void setBackground(View view, Drawable drawable) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) { 
			view.setBackground(drawable);
		} else {
			view.setBackgroundDrawable(drawable);
		}
	}
	
	public static int getDrawableRs(Context context, String resName){
//		try {
//			Field field = R.drawable.class.getField(resName);
//			return field.getInt(new R.drawable());
//		} catch (NoSuchFieldException e) {
//			e.printStackTrace();
//		} catch (IllegalAccessException e) {
//			e.printStackTrace();
//		} catch (IllegalArgumentException e) {
//			e.printStackTrace();
//		}
		return context.getResources().getIdentifier(resName, "drawable", context.getPackageName());
	}
	
	public static int getIdRs(Context context, String resName){
//		try {
//			Field field = R.id.class.getField(resName);
//			return field.getInt(new R.id());
//		} catch (NoSuchFieldException e) {
//			e.printStackTrace();
//		} catch (IllegalAccessException e) {
//			e.printStackTrace();
//		} catch (IllegalArgumentException e) {
//			e.printStackTrace();
//		}
		return context.getResources().getIdentifier(resName, "id", context.getPackageName());
	}

	public static int getScreenWidth(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		manager.getDefaultDisplay().getMetrics(dm);
		int width = dm.widthPixels;
		return width;
	}

	public static int getRealHeight(Context context, int width, int height, int padding, boolean flag) {
		int screenW = getScreenWidth(context);
		if (flag) {
			screenW = screenW / 2;
		}
		int w = screenW - (flag ? 1 : 2 )* dp2px(context, padding);
		int h = (int) (((float) w ) * height / width);
		return h;
	}

	public static int getRealHeight(Context context, int width, int height, int padding, boolean flag, int w) {
		int h = (int) (((float) w ) * height / width);
		return h;
	}

	public static void setImageViewColorFilter(ImageView view) {
		final ImageView imageView = view;
		imageView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getActionMasked()) {
					case MotionEvent.ACTION_DOWN:
						//在按下事件中设置滤镜
						imageView.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
						break;
					case MotionEvent.ACTION_UP:
						//由于捕获了Touch事件，需要手动触发Click事件
						imageView.performClick();
					case MotionEvent.ACTION_CANCEL:
						//在CANCEL和UP事件中清除滤镜
						imageView.clearColorFilter();
						break;
					default:
						break;
				}
				return true;
			}
		});
	}

	/**
	 * 获取状态栏高度
	 * @param context
	 * @return
     */
	public static int getStatusHeight(Context context) {
		int statusHeight = -1;
		try {
			Class<?> clazz = Class.forName("com.android.internal.R$dimen");
			Object object = clazz.newInstance();
			int height = Integer.parseInt(clazz.getField("status_bar_height")
					.get(object).toString());
			statusHeight = context.getResources().getDimensionPixelSize(height);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return statusHeight;
	}
}
