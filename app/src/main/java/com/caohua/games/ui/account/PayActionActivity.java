package com.caohua.games.ui.account;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.caohua.games.R;
import com.caohua.games.app.AppContext;
import com.caohua.games.biz.pay.PayLogic;
import com.caohua.games.biz.pay.PayOrderLogic;
import com.caohua.games.biz.pay.PayOrderModel;
import com.caohua.games.biz.pay.PayRebate;
import com.caohua.games.biz.pay.Rebate;
import com.caohua.games.ui.BaseActivity;
import com.caohua.games.ui.widget.SubActivityTitleView;
import com.chsdk.api.CHSdk;
import com.chsdk.biz.BaseLogic;
import com.chsdk.biz.pay.AlipayMgr;
import com.chsdk.biz.pay.IpayNowMgr;
import com.chsdk.biz.pay.UnionPayMgr;
import com.chsdk.biz.pay.WechatPayMgr;
import com.chsdk.configure.DataStorage;
import com.chsdk.model.pay.GameMoneyInfo;
import com.chsdk.ui.widget.CHToast;
import com.chsdk.ui.widget.LoadingDialog;
import com.chsdk.utils.LogUtil;
import com.chsdk.utils.ViewUtil;

import org.json.JSONArray;
import org.json.JSONException;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import static com.caohua.games.R.id.pay_now_money;

/**
 * Created by CXK on 2016/11/15.
 */

public class PayActionActivity extends BaseActivity implements View.OnClickListener {
    public static final int PAY_TYPE_ALIPAY = 2;
    public static final int PAY_TYPE_WETCHAT = 23;
    public static final int PAY_TYPE_UNION = 22;
    public static final int PAY_TYPE_IPAY = 25;

    private EditText inputAccountEditText;
    private TextView paySumbit, payRealMoney, getRebateMoney;
    private int payType;
    private int selectedMoney;
    private View coverTop, coverBottom, coverLeft, coverRight;
    private String lastUserName;
    private TextView tvFocus;
    private PayRebate payRebate;

    private GridView gridView;
    private SparseArray<String> drawableArray;
    private SparseArray<String> titleArray;
    private SparseIntArray sortArray;
    private TextView[] payViewArray;
    private boolean editAccountMode;
    private SubActivityTitleView subTitleView;
    private int benefitType = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ch_activity_pay_action);
        initData();
        initView();
    }

    private void initData() {
        handlePaySort();
        payType = PAY_TYPE_WETCHAT;
        lastUserName = AppContext.getAppContext().getUser().userName;
        getRebateMsg(lastUserName, null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 必须在支付界面的Activity中onActivityResult方法调用,用于处理支付回调
        if (payRebate != null) {
            Rebate rebate = payRebate.getPayType(payType, selectedMoney);
            if (rebate != null) {
                CHSdk.handleCHPayStatus(this, data, rebate.getShow());
            } else {
                CHSdk.handleCHPayStatus(this, data, "");
            }
        } else {
            CHSdk.handleCHPayStatus(this, data, "");
        }
    }

    private void handlePaySort() {
        sortArray = new SparseIntArray();
        titleArray = new SparseArray<String>();
        drawableArray = new SparseArray<String>();

        String paySort = DataStorage.getAppPaySort(this);
        if (!TextUtils.isEmpty(paySort)) {
            try {
                JSONArray jsonSort = new JSONArray(paySort);
                for (int i = 0; i < jsonSort.length(); i++) {
                    sortArray.put(i, jsonSort.getInt(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (sortArray.size() == 0) {

            sortArray.put(0, PAY_TYPE_ALIPAY);
            sortArray.put(1, PAY_TYPE_IPAY);
            sortArray.put(2, PAY_TYPE_UNION);
        }

        titleArray.put(PAY_TYPE_ALIPAY, "支付宝");
        titleArray.put(PAY_TYPE_WETCHAT, "微信");
        titleArray.put(PAY_TYPE_UNION, "银联");
        titleArray.put(PAY_TYPE_IPAY, "微信");

        drawableArray.put(PAY_TYPE_ALIPAY, "alipay");
        drawableArray.put(PAY_TYPE_WETCHAT, "wechat");
        drawableArray.put(PAY_TYPE_IPAY, "wechat");
        drawableArray.put(PAY_TYPE_UNION, "unionpay");
    }

    private void initView() {
        gridView = getView(R.id.ch_pay_gridview);
        gridView.setAdapter(new PayListGridAdapter());
        tvFocus = getView(R.id.ZF_textview);
        coverTop = getView(R.id.ch_activity_pay_cover_top);
        coverBottom = getView(R.id.ch_activity_pay_cover_bottom);
        coverBottom.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        coverLeft = getView(R.id.ch_view_one);
        coverRight = getView(R.id.ch_view_two);
        inputAccountEditText = getView(R.id.et_pay_tcl);
        inputAccountEditText.setText(lastUserName);
        getRebateMoney = getView(R.id.pay_now_money1);
        payRealMoney = getView(pay_now_money);
        paySumbit = getView(R.id.ch_pay_sumbit);
        subTitleView = getView(R.id.ch_pay_layout);
        subTitleView.showPlay(SubActivityTitleView.SHOW_PAY_CENTER);
        paySumbit.setOnClickListener(this);


        inputAccountEditText.addTextChangedListener(new TextChangeListener());
        inputAccountEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Drawable drawable = inputAccountEditText.getCompoundDrawables()[2];
                if (drawable == null) {
                    return false;
                }

                if (event.getAction() != MotionEvent.ACTION_UP) {
                    return false;
                }

                if (event.getX() > inputAccountEditText.getWidth() - inputAccountEditText.getPaddingRight() - drawable.getIntrinsicWidth()) {
                    final String tmpUserName = inputAccountEditText.getText().toString();
                    getRebateMsg(tmpUserName, new Runnable() {
                        @Override
                        public void run() {
                            lastUserName = tmpUserName;
                            tvFocus.setFocusable(true);
                            tvFocus.setFocusableInTouchMode(true);
                            tvFocus.requestFocus();

                            editAccountMode = false;
                            inputAccountEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                            coverBottom.setVisibility(View.GONE);
                            coverTop.setVisibility(View.GONE);
                            coverLeft.setVisibility(View.GONE);
                            coverRight.setVisibility(View.GONE);
                            CHToast.show(PayActionActivity.this, "账号校验成功");
                        }
                    });
                }
                return false;
            }
        });


        showPayType();
        for (TextView view : payViewArray) {
            view.setOnClickListener(this);
        }
        payViewArray[0].performClick();
    }

    private void showPayType() {
        int payCount = sortArray.size();
        for (int index = 0; index < payCount; index++) {
            if (payViewArray == null) {
                payViewArray = new TextView[payCount];
            }

            int type = sortArray.get(index);
            String drawableDes = drawableArray.get(type);
            String titleDes = titleArray.get(type);

            TextView indexView = getView(ViewUtil.getIdRs(this, "ch_dialog_pay_type_" + index));
            indexView.setTag(type);
            indexView.setText(titleDes);
            indexView.setVisibility(View.VISIBLE);

            payViewArray[index] = indexView;

            int drawableId = ViewUtil.getDrawableRs(this, "ch_dialog_pay_" + drawableDes + "_icon");
            drawableChange(drawableId, indexView);
        }
    }

    private void drawableChange(int resId, TextView view) {
        Drawable drawable = getResources().getDrawable(resId);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        view.setCompoundDrawables(null, drawable, null, null);
    }

    private void handleDrawable(int index) {
        for (int i = 0; i < payViewArray.length; i++) {
            TextView view = payViewArray[i];
            int payType = (Integer) view.getTag();
            String drawableDes = "ch_dialog_pay_" + drawableArray.get(payType) + "_icon";
            if (index == i) {
                drawableDes += "_p";
            }
            int drawableId = ViewUtil.getDrawableRs(this, drawableDes);
            drawableChange(drawableId, view);
        }
    }

    private void getRebateMsg(String userName, final Runnable run) {
        final LoadingDialog dialog = new LoadingDialog(this, "");
        dialog.show();

        PayLogic logic = new PayLogic(userName);
        logic.getRebat(new BaseLogic.AppLogicListner() {
            @Override
            public void failed(String errorMsg) {
                dialog.dismiss();
                CHToast.show(getApplicationContext(), "获取账号信息失败:" + errorMsg);
            }

            @Override
            public void success(Object entryResult) {
                dialog.dismiss();
                if (entryResult instanceof PayRebate) {
                    payRebate = (PayRebate) entryResult;
                } else {
                    payRebate = null;
                }
                if (run != null) {
                    run.run();

                    if (payRebate != null) {
                        float rebateMoney = payRebate.getRebate(payType, selectedMoney);
                        updateMoney(selectedMoney, rebateMoney);
                    } else {
                        updateMoney(selectedMoney, 100f);
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ch_pay_sumbit:
                if (TextUtils.isEmpty(inputAccountEditText.getText().toString())) {
                    CHToast.show(PayActionActivity.this, "用户名不能为空");
                    return;
                }
                if (selectedMoney <= 0) {
                    CHToast.show(PayActionActivity.this, "请选择金额");
                    return;
                }

                if (editAccountMode) {
                    CHToast.show(PayActionActivity.this, "请先验证账号");
                    return;
                }
                paySubmit();
                break;
            case R.id.ch_dialog_pay_type_0:
            case R.id.ch_dialog_pay_type_1:
            case R.id.ch_dialog_pay_type_2:
                for (int i = 0; i < payViewArray.length; i++) {
                    TextView view = payViewArray[i];
                    if (v == view) {
                        payType = (Integer) view.getTag();
                        handleDrawable(i);
                        if (payRebate != null) {
                            float rebateMoney = payRebate.getRebate(payType, selectedMoney);
                            updateMoney(selectedMoney, rebateMoney);
                        }
                    }
                }
                break;
        }
    }

    private void paySubmit() {
        final String userName = inputAccountEditText.getText().toString();
        String orderNumber = String.valueOf(selectedMoney * 100);
        String gainUserMoney = String.valueOf(selectedMoney * 10);

        final Float payRebateMoney;
        if (payRebate != null) {
            payRebateMoney = Float.valueOf(payRebate.getRebate(payType, selectedMoney));
        } else {
            payRebateMoney = Float.valueOf(100);
        }
        String gainGiveMoney = "0";
        if (payRebateMoney != 0) {
            int intResult = computeValue(selectedMoney, payRebateMoney);
            gainGiveMoney = String.valueOf(intResult);
        }
        LogUtil.errorLog("赠送币" + gainGiveMoney);
        final LoadingDialog loadingDialog = new LoadingDialog(this, "");
        loadingDialog.show();

        PayOrderLogic logic = new PayOrderLogic();
        PayOrderModel payOrderModel = new PayOrderModel(userName, orderNumber, gainUserMoney, gainGiveMoney, String.valueOf(payType));
        logic.getOrder(payOrderModel, payType, new BaseLogic.LogicListener() {
            @Override
            public void failed(String errorMsg) {
                loadingDialog.dismiss();
                CHToast.show(PayActionActivity.this, errorMsg);
            }

            @Override
            public void success(String... result) {
                loadingDialog.dismiss();
                String[] arr = result;
                if (arr == null) {
                    return;
                }
                int value = 0;
                if (payRebateMoney != 0) {
                    value = computeValue(selectedMoney, payRebateMoney);
                }
                LogUtil.errorLog("v+赠送币" + value + "");
                String sdk_orderno = arr[0];
                String token_id = arr[1];
                GameMoneyInfo info = new GameMoneyInfo();
                info.gameOrderNo = sdk_orderno;
                info.money = selectedMoney;
                info.gameMoneyName = "草花币";
                info.gameMoney = selectedMoney * 10 + value;
                if (payType == PAY_TYPE_WETCHAT) {  //微信支付
                    WechatPayMgr.pay(PayActionActivity.this, token_id);
                } else if (payType == PAY_TYPE_ALIPAY) {   //支付宝支付
                    AlipayMgr mgr = new AlipayMgr(PayActionActivity.this, info, new BaseLogic.LogicListener() {
                        @Override
                        public void success(String... result) {
                            if (payRebate != null) {
                                Rebate rebate = payRebate.getPayType(payType, selectedMoney);
                                if (rebate != null) {
                                    String show = rebate.getShow();
                                    if (TextUtils.isEmpty(show)) {
                                        CHToast.show(PayActionActivity.this, result[0]);
                                    } else {
                                        CHToast.show(PayActionActivity.this, result[0] + "," + show);
                                    }
                                } else {
                                    CHToast.show(PayActionActivity.this, result[0]);
                                }
                            } else {
                                CHToast.show(PayActionActivity.this, result[0]);
                            }
                        }

                        @Override
                        public void failed(String errorMsg) {
                            CHToast.show(PayActionActivity.this, errorMsg);
                        }
                    }, result);
                    mgr.pay();
                } else if (payType == PAY_TYPE_UNION) {  //银联支付
                    UnionPayMgr.getInstance().pay(PayActionActivity.this, token_id);
                } else if (payType == PAY_TYPE_IPAY) {
                    if (payRebate != null) {
                        Rebate rebate = payRebate.getPayType(payType, selectedMoney);
                        if (rebate != null) {
                            String show = rebate.getShow();
                            if (TextUtils.isEmpty(show)) {
                                IpayNowMgr.pay(PayActionActivity.this, token_id, "");
                            } else {
                                IpayNowMgr.pay(PayActionActivity.this, token_id, show);
                            }
                        } else {
                            IpayNowMgr.pay(PayActionActivity.this, token_id, "");
                        }
                    } else {
                        IpayNowMgr.pay(PayActionActivity.this, token_id, "");
                    }
                }
            }
        });
    }

    private void updateMoney(int payMoney, Float realGetMoney) {
        if (payRebate != null) {
            benefitType = payRebate.getCouponForType(payType, payMoney);
        }
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(2);
        switch (benefitType) {
            case 1:
                if (realGetMoney != null) {
                    if (realGetMoney == 100) {
//                      payRealMoney.setText("应付 : ￥" + payMoney + "(" + "无折扣)");
                        payRealMoney.setText("应付 : ￥" + payMoney + "(" + "元)");
                        getRebateMoney.setText("获得:" + (payMoney * 10) + "草花币");
                    } else {
//                      payRealMoney.setText("应付 : ￥" + payMoney + "(" + realGetMoney + "折扣)");
                        payRealMoney.setText("应付 : ￥" + payMoney + "(" + "元)");
                        int value = computeValue(payMoney, realGetMoney);
                        LogUtil.errorLog("折扣值" + realGetMoney + "获得的显示金额:实际金额" + payMoney * 10 + "赠送的草花币" + "|||" + value + "");
                        if (value < 1) {
                            getRebateMoney.setText("获得:" + (payMoney * 10) + "草花币");
                        } else {
                            getRebateMoney.setText("获得:" + (payMoney * 10 + value) + "(包含返利" + value + "草花币)");
                        }
                    }
                }
                break;
            case 2:
                Rebate rebate = payRebate.getPayType(payType, selectedMoney);
                if (rebate != null) {
                    payRealMoney.setText("应付 : ￥" + payMoney + "(" + "元)");
                    String show = rebate.getShow();
                    if (TextUtils.isEmpty(show)) {
                        getRebateMoney.setText("获得:" + (payMoney * 10) + "草花币");
                    } else {
                        getRebateMoney.setText("获得:" + (payMoney * 10) + "草花币" + "+" + show);
                    }
                } else { //什么也没获取
                    payRealMoney.setText("应付 : ￥" + payMoney + "(" + "元)");
                    getRebateMoney.setText("获得:" + (payMoney * 10) + "草花币");
                }
                break;
        }
    }

    /**
     * @param payMoney
     * @param realGetMoney
     * @return 计算赠送得到的草花币个数
     */
    private int computeValue(int payMoney, Float realGetMoney) {
        BigDecimal decimal = new BigDecimal(realGetMoney - 100);
        BigDecimal decimal2 = new BigDecimal(100f);
        BigDecimal decimal3 = new BigDecimal(payMoney);
        BigDecimal decimal4 = new BigDecimal(10);
        BigDecimal divide = decimal.divide(decimal2);
        LogUtil.errorLog("123   " + divide);
        BigDecimal multiply = decimal3.multiply(divide);
        BigDecimal multiply1 = multiply.multiply(decimal4);
        LogUtil.errorLog("multiply    " + multiply + "multiply1    " + multiply1);
        int integer = multiply1.intValue();
        LogUtil.errorLog("integer" + integer);
        return integer;
    }

    class TextChangeListener implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.toString().equals(lastUserName)) {
                inputAccountEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                coverBottom.setVisibility(View.GONE);
                coverTop.setVisibility(View.GONE);
                coverLeft.setVisibility(View.GONE);
                coverRight.setVisibility(View.GONE);
                editAccountMode = false;
            } else {
                inputAccountEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ch_edit_pressd), null);
                coverBottom.setVisibility(View.VISIBLE);
                coverTop.setVisibility(View.VISIBLE);
                coverLeft.setVisibility(View.VISIBLE);
                coverRight.setVisibility(View.VISIBLE);
                editAccountMode = true;
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }

    }

    class PayListGridAdapter extends BaseAdapter {
        private List<Integer> list;

        public PayListGridAdapter() {
            list = new ArrayList<Integer>();
            list.add(10);
            list.add(20);
            list.add(50);
            list.add(100);
            list.add(200);
            list.add(500);
            list.add(1000);
            list.add(2000);
            list.add(5000);
            list.add(-1);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Integer getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (position == list.size() - 1) {
                if (convertView == null) {
                    convertView = new PayGridViewEditItem(PayActionActivity.this);
                    ((PayGridViewEditItem) convertView).setCallBack(new PayGridViewEditItem.CallBack() {
                        @Override
                        public void work(int value) {
                            cancelSelected(-1);
                            moneySelect(value);
                        }
                    });
                }
            } else {
                if (convertView == null) {
                    convertView = new PayGridViewItem(PayActionActivity.this);
                    ((PayGridViewItem) convertView).setCallBack(new PayGridViewItem.CallBack() {
                        @Override
                        public void work(int value) {
                            tvFocus.requestFocus();
                            clearEdit();
                            cancelSelected(position);
                            moneySelect(value);
                        }
                    });
                }
                ((PayGridViewItem) convertView).setData(getItem(position));
            }
            return convertView;
        }


        private void cancelSelected(int index) {
            for (int i = 0; i < gridView.getCount() - 1; i++) {
                if (i != index) {
                    ((PayGridViewItem) gridView.getChildAt(i)).cancelSelect();
                }
            }
        }

        private void clearEdit() {
            ((PayGridViewEditItem) gridView.getChildAt(gridView.getCount() - 1)).clearText();
        }
    }

    private void moneySelect(int value) {
        selectedMoney = value;
        if (payRebate != null) {
            float rebateMoney = payRebate.getRebate(payType, selectedMoney);
            updateMoney(selectedMoney, rebateMoney);
        } else {
            updateMoney(selectedMoney, 100f);
        }
    }
}
