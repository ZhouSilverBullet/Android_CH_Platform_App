package com.caohua.games.ui.search;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.caohua.games.R;
import com.caohua.games.app.AppContext;
import com.caohua.games.biz.TransmitDataInterface;
import com.caohua.games.biz.search.AssigCatEntry;
import com.caohua.games.biz.search.AssigCatModel;
import com.caohua.games.biz.search.AssignCatLogic;
import com.caohua.games.biz.search.HotGameEntry;
import com.caohua.games.biz.search.SearchCountEntry;
import com.caohua.games.biz.search.SearchGameLogic;
import com.caohua.games.biz.search.SearchGameModel;
import com.caohua.games.ui.BaseActivity;
import com.caohua.games.ui.BaseFragment;
import com.caohua.games.ui.widget.NoNetworkView;
import com.chsdk.biz.BaseLogic;
import com.chsdk.biz.app.AnalyticsHome;
import com.chsdk.http.HttpConsts;
import com.chsdk.ui.widget.CHToast;
import com.chsdk.ui.widget.LoadingDialog;
import com.chsdk.utils.LogUtil;
import com.chsdk.utils.ViewUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by CXK on 2016/10/28.
 */

public class SearchActivity extends BaseActivity {
    private ImageView goback;
    private EditText edit;
    private TextView searchIcon;
    private ListView searchList;
    private ResultListAdapter adapter;
    private PopupWindow popWindow;
    private List<AssigCatEntry> lastSearchList;
    private SearchFragment fragment;
    private String editValue;
    private boolean clickitem;
    private String lastSearchRequestText;
    private View popWidth;
    private NoNetworkView noNetworkView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ch_activity_search);
        init();
    }

    private void init() {
        popWidth = findViewById(R.id.ch_activity_search_pop_width);
        fragment = (SearchFragment) getSupportFragmentManager().findFragmentById(R.id.ch_activity_search_fragment);
        goback = (ImageView) findViewById(R.id.ch_activity_search_game_goback);
        edit = (EditText) findViewById(R.id.ch_activity_search_game_user_input);
        searchIcon = (TextView) findViewById(R.id.ch_activity_search_search_text);
        noNetworkView = getView(R.id.ch_activity_search_not_network);
        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        edit.addTextChangedListener(watcher);
        edit.setOnEditorActionListener(onEditor);

        List<String> strings = fragment.readHistoryRecord();
        if (strings == null || strings.size() == 0) {
            fragment.hideHistoryGridView();
        } else {
            fragment.showHistoryGridView();
        }

        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                String searchGame = edit.getText().toString().trim();
                searchClick(searchGame);
            }

        });

        Intent intent = getIntent();
        String load = intent.getStringExtra("task_load");
        if (!TextUtils.isEmpty(load) && load.equalsIgnoreCase("load")) {
            fragment.loadData(false);
        }
    }

    protected void showNoNetworkView(boolean showNoNetwork) {
        if (showNoNetwork) {
            noNetworkView.setVisibility(View.VISIBLE);
            noNetworkView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    noNetworkView.setVisibility(View.GONE);
                    searchLogic(gameGlobName);
                }
            });
        } else {
            noNetworkView.setVisibility(View.GONE);
        }
    }

    private void searchClick(final String searchGame) {
        if (TextUtils.isEmpty(searchGame)) {
            CHToast.show(SearchActivity.this, "请输入游戏名称");
            return;
        }

        if (!TextUtils.isEmpty(gameGlobName) && gameGlobName.equals(searchGame)) {
            CHToast.show(SearchActivity.this, "请勿重复搜索");
            return;
        }

        if (popWindow != null) {
            popWindow.dismiss();
        }

        final LoadingDialog dialog = new LoadingDialog(SearchActivity.this, "");
        dialog.show();

        SearchGameModel model = new SearchGameModel();
        model.setGameName(searchGame);
        SearchGameLogic logic = new SearchGameLogic(SearchActivity.this);
        logic.getAssignGame(searchGame, new BaseLogic.DataLogicListner<SearchCountEntry>() {

            @Override
            public void failed(String errorMsg, int code) {
                gameGlobName = searchGame;
                dialog.dismiss();
                fragment.setData(null);
                if (errorMsg.equals(HttpConsts.ERROR_CODE_PARAMS_VALID)) {
                    showNoNetworkView(false);
                    CHToast.show(SearchActivity.this, "没有搜索到指定游戏");
                } else {
                    if (!AppContext.getAppContext().isNetworkConnected()||
                            (!TextUtils.isEmpty(errorMsg) && errorMsg.contains("请确认连接上有效网络后重试"))) {
                        showNoNetworkView(true);
                        return;
                    }
                    showNoNetworkView(false);
                    CHToast.show(SearchActivity.this, errorMsg);
                }
            }

            @Override
            public void success(SearchCountEntry entryResult) {
                gameGlobName = searchGame;
                showNoNetworkView(false);
                dialog.dismiss();
                if (entryResult == null) {
                    fragment.setData(null);
                    CHToast.show(SearchActivity.this, "没有搜索到指定游戏");
                } else {
                    hideKeyBorad();
                    fragment.hideHotAndHistoryLayout();
                    fragment.writeHistoryRecord(searchGame);
                    fragment.setGameName(searchGame);
                    fragment.setData(entryResult);
                }
                AnalyticsHome.umOnEvent(AnalyticsHome.HOME_SEARCH_CLICK_ANALYTICS, "收索被点击");
            }
        });
    }

    private void hideKeyBorad() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            boolean isOpen = imm.isActive();//isOpen若返回true，则表示输入法打开
            if (isOpen) {
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    private String gameGlobName;

    public String getGameName() {
        return gameGlobName;
    }

    @Override
    protected void onDestroy() {
        gameGlobName = null;
        super.onDestroy();
    }

    public void setGameName(String gameName) {
        this.gameGlobName = gameName;
    }

    private void hideSoftInput(View v) {
        InputMethodManager imm = (InputMethodManager) v
                .getContext().getSystemService(
                        Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(
                    v.getApplicationWindowToken(), 0);
        }
    }

    /**
     * 软键盘回车键监听
     */
    private TextView.OnEditorActionListener onEditor = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideSoftInput(v);

                String gameName = edit.getText().toString().trim();
                if (TextUtils.isEmpty(gameName)) {
                    CHToast.show(SearchActivity.this, "请输入游戏名称");
                } else {
                    searchClick(gameName);
                    fragment.writeHistoryRecord(gameName);
                    return true;
                }
            }
            return false;
        }
    };

    private TextWatcher watcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            if (s.equals("")) {
//                fragment.refreshHotAndHistoryLayout();
//                fragment.setData(null);
//            }
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            final String changeText = String.valueOf(s).trim();
            if (TextUtils.isEmpty(changeText)) {
                fragment.refreshHotAndHistoryLayout();
                fragment.setData(null);
                return;
            }

            if (!TextUtils.isEmpty(lastSearchRequestText) && changeText.contains(lastSearchRequestText)) {
                if (!clickitem) {
                    ArrayList<AssigCatEntry> newList = new ArrayList<>();
                    for (int i = 0; i < lastSearchList.size(); i++) {
                        AssigCatEntry assigCatEntry = lastSearchList.get(i);
                        String game_name = assigCatEntry.getGame_name();
                        if (game_name.contains(changeText)) {
                            newList.add(assigCatEntry);
                        }
                    }
                    showGamePop(newList);
                } else {
                    clickitem = false;
                    popWindow.dismiss();
                }
                return;
            }

//            AssignCatLogic logic = new AssignCatLogic(SearchActivity.this);
//            AssigCatModel model = new AssigCatModel();
//            model.setGameName(changeText);
//            logic.getAssigCatGame(model, new BaseLogic.AppLogicListner() {
//                @Override
//                public void failed(String errorMsg) {
//                    if (popWindow != null && popWindow.isShowing()) {
//                        popWindow.dismiss();
//                    }
//                }
//
//                @Override
//                public void success(Object entryResult) {
//                    if (isFinishing()) {
//                        return;
//                    }
//                    if (entryResult instanceof List) {
//                        lastSearchRequestText = changeText;
//                        lastSearchList = (List<AssigCatEntry>) entryResult;
//                    }
//
//                    if (lastSearchList == null || lastSearchList.size() == 0) {
//                        CHToast.show(SearchActivity.this, "没有搜索到指定游戏");
//                    }
//
//                    if (!clickitem) {
//                        showGamePop(lastSearchList);
//                    } else {
//                        clickitem = false;
//                        popWindow.dismiss();
//                    }
//                }
//            });
        }
    };

    private void showGamePop(final List<AssigCatEntry> list) {
        if (list == null || list.size() == 0) {
            return;
        }

        if (popWindow == null) {
            View view = LayoutInflater.from(this).inflate(R.layout.ch_activity_game_pop, null);
            searchList = (ListView) view.findViewById(R.id.ch_search_pop_list);
            adapter = new ResultListAdapter(this, list, new TransmitDataInterface() {
                @Override
                public void transmit(Object o) {
                    int position = (int) o;
                    if (popWindow != null) {
                        popWindow.dismiss();
                    }
                    clickitem = true;
                    edit.clearFocus();
                    fragment.hideHotAndHistoryLayout();
                    AssigCatEntry entry = adapter.getItemData(position);
                    if (entry == null) {
                        return;
                    }
                    final String gameName = entry.getGame_name();
                    edit.setText(gameName);
                    edit.setSelection(edit.getText().length());

                    SearchGameLogic logic = new SearchGameLogic(SearchActivity.this);
                    SearchGameModel model = new SearchGameModel();
                    model.setGameName(gameName);
                    fragment.writeHistoryRecord(gameName);
                    final LoadingDialog dialog = new LoadingDialog(SearchActivity.this, "");
                    dialog.show();
                    logic.getAssignGame(gameName, new BaseLogic.DataLogicListner<SearchCountEntry>() {
                        @Override
                        public void failed(String errorMsg, int code) {
                            dialog.dismiss();
                        }

                        @Override
                        public void success(SearchCountEntry entryResult) {
                            dialog.dismiss();
                            fragment.setGameName(gameName);
                            fragment.setData(entryResult);
                        }
                    });
                }
            });
            searchList.setAdapter(adapter);
            int widthPixels = popWidth.getWidth();
            popWindow = new PopupWindow(view, widthPixels, ViewGroup.LayoutParams.WRAP_CONTENT);
            popWindow.setOutsideTouchable(true);
            Drawable drawable = getResources().getDrawable(R.drawable.ch_shape_search_lines);
            popWindow.setBackgroundDrawable(drawable);
        } else {
            adapter.setData(list);
        }
        if (list.size() >= 6) {
            popWindow.setHeight(ViewUtil.dp2px(this, 260));
        } else {
            popWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        try {
//            popWindow.showAsDropDown(popWidth, 0, ViewUtil.dp2px(this,5));
            popWindow.update();
        } catch (WindowManager.BadTokenException e) {
            popWindow = null;
            LogUtil.errorLog("SearchActivity showAsDropDown error:" + e.getMessage());
        }
    }

    public static void start(Context context, List<HotGameEntry> data) {
        Intent intent = new Intent(context, SearchActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(BaseFragment.KEY_INTENT_DATA, (Serializable) data);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    public void setHistoryRecord(String s) {
        editValue = s;
        edit.setText(editValue);
        edit.setSelection(edit.getText().length());
        searchLogic(s);
    }

    public void setEdit(String editValue) {
        edit.setText(editValue);
    }

    public void searchLogic(final String entryGameName) {
        final LoadingDialog dialog = new LoadingDialog(this, "");
        dialog.show();
        new SearchGameLogic(this).getAssignGame(entryGameName, new BaseLogic.DataLogicListner<SearchCountEntry>() {
            @Override
            public void failed(String errorMsg, int code) {
                dialog.dismiss();
                fragment.setData(null);
                if (errorMsg.equals(HttpConsts.ERROR_CODE_PARAMS_VALID)) {
                    gameGlobName = entryGameName;
                    showNoNetworkView(false);
                    CHToast.show(SearchActivity.this, "没有搜索到指定游戏");
                } else {
                    if (!AppContext.getAppContext().isNetworkConnected() ||
                            (!TextUtils.isEmpty(errorMsg) && errorMsg.contains("请确认连接上有效网络后重试"))) {
                        showNoNetworkView(true);
                        return;
                    }
                    gameGlobName = entryGameName;
                    showNoNetworkView(false);
                    CHToast.show(SearchActivity.this, errorMsg);
                }
            }

            @Override
            public void success(SearchCountEntry entryResult) {
                gameGlobName = entryGameName;
                showNoNetworkView(false);
                dialog.dismiss();
                fragment.hideHotAndHistoryLayout();
                fragment.setGameName(entryGameName);
                fragment.setData(entryResult);
            }
        });
    }
}
