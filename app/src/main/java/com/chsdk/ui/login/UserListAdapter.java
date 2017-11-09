package com.chsdk.ui.login;

import java.util.List;

import com.caohua.games.R;
import com.chsdk.configure.DataStorage;
import com.chsdk.configure.UserDBHelper;
import com.chsdk.model.login.LoginUserInfo;
import com.chsdk.ui.widget.CHAlertDialog;
import com.chsdk.ui.widget.CHToast;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class UserListAdapter extends BaseAdapter {
	private Activity activity;
	private List<LoginUserInfo> items;
	private LayoutInflater infalter;
	private DeleteListener delCallback;

	public UserListAdapter(Activity activity, List<LoginUserInfo> items, DeleteListener delCallback) {
		super();
		this.activity = activity;
		this.items = items;
		this.delCallback = delCallback;
		infalter = LayoutInflater.from(activity);
		
	}

	@Override
	public int getCount() {
		if (items == null) {
			return 0;
		}
		return items.size();
	}

	@Override
	public LoginUserInfo getItem(int position) {
		if (items == null)
			return null;

		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;

		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = (RelativeLayout) infalter.inflate(R.layout.ch_dialog_login_pop_item, null);
			viewHolder.delete = (ImageView) convertView.findViewById(R.id.ch_list_delete);
			viewHolder.uesrname = (TextView) convertView.findViewById(R.id.ch_list_username);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.uesrname.setText(getItem(position).userName);
		viewHolder.delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final CHAlertDialog dialog = new CHAlertDialog(activity);
				dialog.show();
				dialog.setTitle("提示");
				dialog.setContent("确定删除该账号吗?");
				dialog.setCancelButton("取消删除", null);
				dialog.setOkButton("确定删除", new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
						LoginUserInfo info = getItem(position);
						CHToast.show(activity, "删除账号" + info.userName);
						items.remove(position);
						notifyDataSetChanged();

						UserDBHelper.deleteUser(activity, info.userName);

						if (delCallback != null) {
							delCallback.delete(info.userName, getCount() == 0);
						}
					}
				});
				
			}
		});
		return convertView;
	}

	private class ViewHolder {
		ImageView delete;
		TextView uesrname;
	}

	public interface DeleteListener {
		void delete(String userName, boolean noMoreUser);
	}
}
