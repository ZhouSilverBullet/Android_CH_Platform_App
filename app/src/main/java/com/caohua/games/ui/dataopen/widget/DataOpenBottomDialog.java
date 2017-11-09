package com.caohua.games.ui.dataopen.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.caohua.games.R;
import com.chsdk.ui.widget.CHToast;
import com.chsdk.ui.widget.LoadingDialog;
import com.chsdk.utils.ViewUtil;

import java.util.List;

/**
 * Created by admin on 2017/9/27.
 */

public class DataOpenBottomDialog {
    private String titleName;
    private Context context;
    private DialogItemClickListener listener;
    private int checkPosition = -1;
    private int checkedPosition = -1;


    public DataOpenBottomDialog(Context context, String titleName) {
        this.context = context;
        this.titleName = titleName;
    }

    public void setListener(DialogItemClickListener listener) {
        this.listener = listener;
    }

    public interface DialogItemClickListener {
        void onDialogClick(int position);
    }

    public void showContentPop(final List<String> popContent, final boolean quFu) {
        View view = View.inflate(context, R.layout.ch_data_open_bottom_dialog_list, null);
        ListView listView = (ListView) view.findViewById(R.id.ch_open_bottom_dialog_list_view);
        View back = view.findViewById(R.id.ch_open_bottom_dialog_list_back);
        TextView title = (TextView) view.findViewById(R.id.ch_open_bottom_dialog_list_title);
        title.setText(titleName);
        final ListAdapter adapter = new ListAdapter(popContent);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!adapter.isChecked(position)) {
                    adapter.setChecked(position);
                }
                checkPosition = position;
            }
        });
        listView.setAdapter(adapter);
        final Dialog dialog = new Dialog(context, R.style.ActionSheetDialogStyle);
        dialog.setContentView(view);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = ViewUtil.dp2px(context, 240);
        dialogWindow.setAttributes(lp);
        dialog.show();//显示对话框
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        View fBtn = view.findViewById(R.id.ch_data_open_botton_dialog_f);
        fBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        View tBtn = view.findViewById(R.id.ch_data_open_botton_dialog_t);
        tBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkedPosition == -1) {
                    if (quFu) {
                        CHToast.show(context, "请选择要绑定的区服");
                    } else {
                        CHToast.show(context, "请选择要绑定的角色");
                    }
                    return;
                }
                if (dialog != null) {
                    dialog.dismiss();
                }
                if (listener != null) {
                    listener.onDialogClick(checkPosition);
                }
            }
        });
    }

    private class ListAdapter extends BaseAdapter {
        private List<String> popContent;

        public ListAdapter(List<String> popContent) {
            this.popContent = popContent;
        }

        @Override
        public int getCount() {
            return popContent == null ? 0 : popContent.size();
        }

        @Override
        public String getItem(int position) {
            return popContent.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Context ctt = parent.getContext();
            if (convertView == null) {
                convertView = View.inflate(ctt, R.layout.ch_coupon_dialog_list_item, null);
            }
            TextView textView = (TextView) convertView;
            textView.setText(popContent.get(position));
            if (position == checkedPosition) {
                textView.setTextColor(context.getResources().getColor(R.color.green));
            } else {
                textView.setTextColor(context.getResources().getColor(R.color.ch_black));
            }
            return convertView;
        }

        public boolean isChecked(int position) {
            return checkedPosition == position;
        }

        public void setChecked(int position) {
            int prevChecked = checkedPosition;
            checkedPosition = position;

            if (prevChecked != -1) {
                notifyDataSetChanged();
            }
            notifyDataSetChanged();
        }
    }
}
