package com.chsdk.ui.login;

import com.caohua.games.R;
import com.chsdk.model.game.PushEntry;
import com.chsdk.ui.WebActivity;
import com.chsdk.utils.ViewUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class TextPushDialog implements OnClickListener {
	private Activity activity;
	private AlertDialog dialog;
	private PushEntry entry;
	private ImageView imgProgress, imgClose;
	private RelativeLayout layout;
	private WebView webView;
	private Button btnOk;
	
	public TextPushDialog(Activity activity, PushEntry entry) {
		this.activity = activity;
		this.entry = entry;
	}

	public void dismiss() {
		if (dialog != null) {
			dialog.dismiss();
		}
		destroyWebView();
	}
	
	private void destroyWebView() {
		if (webView != null) {
			webView.setVisibility(View.GONE);
			layout.removeView(webView);
			webView.clearHistory();
			webView.clearCache(true);
			webView.loadUrl("about:blank");
			webView.removeAllViews();
			webView.freeMemory();//
			webView.destroy();
			// mWebView.pauseTimers();
			webView = null;
		}
	}

	public void show() {
		dialog = new AlertDialog.Builder(activity, R.style.ch_base_style).create();
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
		View view = LayoutInflater.from(activity).inflate(
				R.layout.ch_dialog_text_push, null);
		dialog.setContentView(view);
		initView(view);
	}

	private void initView(View view) {
		imgClose = ViewUtil.getView(view, R.id.ch_dialog_push_close);
		layout = ViewUtil.getView(view, R.id.ch_dialog_push_webview_layout);
		imgProgress = ViewUtil.getView(view, R.id.ch_dialog_push_progress_img);
		btnOk = ViewUtil.getView(view, R.id.ch_dialog_push_btn_ok);
		
		Animation rotateAnim = AnimationUtils.loadAnimation(view.getContext(), R.anim.ch_anim_rotate_cycle);
		imgProgress.setVisibility(View.VISIBLE);
		imgProgress.startAnimation(rotateAnim);
		
		imgClose.setOnClickListener(this);
		btnOk.setOnClickListener(this);
		
		webView = new WebView(view.getContext().getApplicationContext());
		layout.addView(webView, 0,
				new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		setWebView(webView);
		
		webView.loadUrl(entry.imgUrl);
	}

	@Override
	public void onClick(View v) {
		if (v == imgClose) {
			dismiss();
		} else if (v == btnOk) {
			dismiss();
		}
	}
	
	@SuppressLint("SetJavaScriptEnabled")
	private void setWebView(WebView webView) {
		WebSettings settings = webView.getSettings();
		settings.setSupportZoom(false);
		settings.setBuiltInZoomControls(false);
		settings.setDisplayZoomControls(false);
		settings.setJavaScriptEnabled(true);
		
		webView.setVerticalScrollBarEnabled(false);
		webView.setScrollContainer(false);
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				Context context = view.getContext();
				if (url.startsWith("http")) {
					if (url.endsWith("apk")) {
						view.loadUrl(url);
					} else {
						WebActivity.startWebPage(activity, url);
					}
				} else {
					Intent baseIntent = new Intent(Intent.ACTION_VIEW);
					baseIntent.setData(Uri.parse(url));
					if (baseIntent.resolveActivity(context.getPackageManager()) != null) {
						Intent chooserIntent = Intent.createChooser(baseIntent, "Select Application");
						if (chooserIntent != null) {
							context.startActivity(chooserIntent);
						}
					} else {
						super.shouldOverrideUrlLoading(view, url);
					}
				}
				return true;
			}
		});
		webView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				super.onProgressChanged(view, newProgress);
				int visible = imgProgress.getVisibility();
				if (visible != View.VISIBLE && newProgress < 100) {
					imgProgress.setVisibility(View.VISIBLE);
					Animation rotateAnim = AnimationUtils.loadAnimation(view.getContext(), R.anim.ch_anim_rotate_cycle);
					imgProgress.startAnimation(rotateAnim);
				} else if (newProgress == 100) {
					imgProgress.setVisibility(View.GONE);
					imgProgress.clearAnimation();
				}
			}
		});
		
		webView.setDownloadListener(new DownloadListener() {
			@Override
			public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype,
					long contentLength) {
				Uri uri = Uri.parse(url);
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				if (intent.resolveActivity(activity.getPackageManager()) != null) {
					activity.startActivity(intent);
				}				
			}
		});
	}
}
