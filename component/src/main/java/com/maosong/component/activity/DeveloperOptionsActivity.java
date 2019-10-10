package com.maosong.component.activity;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.maosong.component.BaseActivity;
import com.maosong.component.R;
import com.maosong.component.net.AbsBuildAPI;
import com.maosong.component.view.BasePresenter;
import com.maosong.component.widget.SetAreaLayout;
import com.maosong.component.widget.SetItemLayout;
import com.maosong.tools.AbsStaticConstants;
import com.maosong.tools.AppLifeCircleUtil;
import com.maosong.tools.AppUtils;
import com.maosong.tools.ClearUtils;
import com.maosong.tools.Constants;
import com.maosong.tools.DialogController;
import com.maosong.tools.Environment;
import com.maosong.tools.SPUtils;
import com.maosong.tools.ToastUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.maosong.tools.Environment.ENV_S;
import static com.maosong.tools.Environment.ONLINE;

public class DeveloperOptionsActivity extends BaseActivity {
    private String toastMessage = "";
    private final static int EXIT_APP = 0x1001;
    String[] contentOpenClose = {"Open", "Close"};

    private Toast mToast;
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case EXIT_APP:
                    int index = msg.arg1;
                    if (index == 0) {
                        AppUtils.exitApp(false);
                        finish();
                    } else {
                        mToast.cancel();
                        mToast = Toast.makeText(getContext(), toastMessage + index + "s", Toast.LENGTH_SHORT);
                        mToast.show();

                        Message exitMsg = new Message();
                        exitMsg.what = EXIT_APP;
                        exitMsg.arg1 = --index;
                        mHandler.sendMessageDelayed(exitMsg, 1000);
                    }
                    break;
                default:
            }
            return false;
        }
    });

    @Override
    public void initView() {
        super.initView();
        mToast = Toast.makeText(getContext(), "", Toast.LENGTH_SHORT);
        LinearLayout setContainer = (LinearLayout) findViewById(R.id.set_container);
        SetAreaLayout areaLayout = new SetAreaLayout(this);
        setContainer.addView(areaLayout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        addReleaseItem(areaLayout);
        addEnvItem(areaLayout);
        addClearDataItem(areaLayout);
        addDebug(areaLayout);
        addPayChooseItem(areaLayout);
//        addRoomACRVoice(areaLayout);
        findViewById(R.id.back).setOnClickListener(v -> back());
    }

    public void back() {
        AppLifeCircleUtil.getInstance().finishActivity(this);
    }

    @Override
    public int getContentViewRes() {
        return R.layout.activity_developer_options;
    }

    @Override
    public BasePresenter createPresenter() {
        return null;
    }

    /**
     * 添加释放类型的问题
     **/
    private void addReleaseItem(SetAreaLayout areaLayout) {
        SetItemLayout itemLayout = new SetItemLayout(getContext());
        itemLayout.setTitle("Release type:");
        itemLayout.setOnClickListener(null);
        itemLayout.setSubTitle(AbsStaticConstants.BUILD_TYPE);
        areaLayout.addSetItem(itemLayout);
    }

    /**
     * 添加环境选择功能
     */
    private void addEnvItem(SetAreaLayout areaLayout) {
        String nowEnvironment = SPUtils.getInstance().getString(Environment.KEY_ENVIRONMENT, "");
        if (TextUtils.isEmpty(nowEnvironment)) {
            nowEnvironment = ONLINE;
        }
        SetItemLayout itemLayout = new SetItemLayout(getContext());
        itemLayout.setTitle("Background environment:");
        itemLayout.setOnClickListener(null);
        itemLayout.setClickEventListener(() -> showEnvDialog());
        itemLayout.setSubTitle(nowEnvironment);
        itemLayout.showArrowIcon();
        areaLayout.addSetItem(itemLayout);
    }

    /**
     * 是否保存acr录音文件
     */
    private void addRoomACRVoice(SetAreaLayout areaLayout) {
        Boolean b_save = SPUtils.getInstance().getBoolean("_acr_save", false);
        SetItemLayout itemLayout = new SetItemLayout(getContext());
        itemLayout.setTitle("Save ACR recording:");
        itemLayout.setOnClickListener(null);
        itemLayout.setClickEventListener(() -> showSaveAcrVoiceDialog());
        if (b_save) {
            itemLayout.setSubTitle(contentOpenClose[0]);
        } else {
            itemLayout.setSubTitle(contentOpenClose[1]);
        }
        itemLayout.showArrowIcon();
        areaLayout.addSetItem(itemLayout);
    }

    /**
     * 选择支付功能
     * 默认是googlePay支付
     */
    private void addPayChooseItem(SetAreaLayout areaLayout) {
        String payName = SPUtils.getInstance().getString(Constants.SP_LAST_PAY_TYPE, "");
        if (TextUtils.isEmpty(payName)) {
            payName = TextUtils.equals(AbsStaticConstants.BUILD_TYPE, "debug") ? Constants.PAY_TYPE_PINGPONG : Constants.PAY_TYPE_GOOGLEPAY;
            SPUtils.getInstance().put(Constants.SP_LAST_PAY_TYPE, payName);
        }
        SetItemLayout itemLayout = new SetItemLayout(getContext());
        itemLayout.setTitle("Payment environment:");
        itemLayout.setOnClickListener(null);
        itemLayout.setClickEventListener(() -> showPayItemDialog(itemLayout));
        itemLayout.setSubTitle(payName);
        itemLayout.showArrowIcon();
        areaLayout.addSetItem(itemLayout);
    }

    /**
     * 显示支付类型选择
     */
    private void showPayItemDialog(SetItemLayout item) {
        final String title = "Payment option";
        String[] payItems = {Constants.PAY_TYPE_GOOGLEPAY, Constants.PAY_TYPE_PINGPONG, Constants.PAY_TYPE_PAYPAL};
        DialogController.getInstance().showMoreDialog(getContext(), Arrays.asList(payItems), title, (parent, view, position, id) -> {
            String payName = payItems[position];
            SPUtils.getInstance().put(Constants.SP_LAST_PAY_TYPE, payName);
            item.setSubTitle(payName);
//            AppUtils.exitApp(false);
            DialogController.getInstance().dismissMoreDialog();
        });
    }

    /**
     * 切换环境
     */
    private void showEnvDialog() {
        final String title = "Environmental choice";
        DialogController.getInstance().showMoreDialog(getContext(), ENV_S, title, (parent, view, position, id) -> {
            String oldEnv =
                    SPUtils.getInstance().getString(Environment.KEY_ENVIRONMENT, "");
            String newEnv = ENV_S.get(position);
            if (!TextUtils.equals(oldEnv, newEnv)) {
                //登出账号
                //记录新的环境
                SPUtils.getInstance().put(Environment.KEY_ENVIRONMENT, newEnv);
                SPUtils.getInstance().put(Constants.SP_TOKEN, "");
//                    FriendDBModule fdb = new FriendDBModule();
//                    fdb.delete((List<UserDetailInfo>) null);
                Environment.chooseBaseHost();
                AbsBuildAPI.clear();
                //清除验签配置。如果项目中使用到了多个版本的验签,请注意在这里调用所有的验签清除方法!
                toastMessage = "The environment has changed and is exiting the app:";
                exitApp();
            }
            DialogController.getInstance().dismissMoreDialog();
        });
    }

    /**
     * 切换环境
     */
    private void showClearSureDialog() {
        String title = "Clear data";
        List<String> contents = new ArrayList<>();
        contents.add("Yes");
        contents.add("No");
        DialogController.getInstance().showMoreDialog(getContext(), contents, title, (parent, view, position, id) -> {
            if (position == 0) {
                ClearUtils.clearDatabases(getContext());
                SPUtils.getInstance().clear();
                ToastUtils.showShortToast("Clear success!");
            }
            DialogController.getInstance().dismissMoreDialog();
        });
    }

    /**
     * 添加调试功能
     */
    private void addDebug(SetAreaLayout areaLayout) {
        Boolean debug = AbsStaticConstants.IS_DEBUG;
        SetItemLayout itemLayout = new SetItemLayout(getContext());
        itemLayout.setTitle("Debugging function:");
        itemLayout.setOnClickListener(null);
        itemLayout.setClickEventListener(new SetItemLayout.OnClickEventListener() {
            @Override
            public void onClick() {
                showDebugDialog();
            }
        });
        if (debug) {
            itemLayout.setSubTitle(contentOpenClose[0]);
        } else {
            itemLayout.setSubTitle(contentOpenClose[1]);
        }
        itemLayout.showArrowIcon();
        areaLayout.addSetItem(itemLayout);
    }

    private void showDebugDialog() {
        final String title = "Debugging function";
        DialogController.getInstance().showMoreDialog(getContext(), Arrays.asList(contentOpenClose), title, new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    AbsStaticConstants.IS_DEBUG = true;
                    SPUtils.getInstance().put(Constants.KEY_DEBUG, true);
                    //清除验签配置。如果项目中使用到了多个版本的验签,请注意在这里调用所有的验签清除方法!
                } else if (position == 1) {
                    AbsStaticConstants.IS_DEBUG = false;
                    SPUtils.getInstance().put(Constants.KEY_DEBUG, false);
                }
                AppLifeCircleUtil.getInstance().finishActivity(DeveloperOptionsActivity.this);
                DialogController.getInstance().dismissMoreDialog();
                finish();
            }
        });
    }

    private void showSaveAcrVoiceDialog() {
        final String title = "Save ACR recording";
        DialogController.getInstance().showMoreDialog(getContext(), Arrays.asList(contentOpenClose), title, new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    SPUtils.getInstance().put("_acr_save", true);
                    //清除验签配置。如果项目中使用到了多个版本的验签,请注意在这里调用所有的验签清除方法!
                } else if (position == 1) {
                    SPUtils.getInstance().put("_acr_save", false);
                }
                AppLifeCircleUtil.getInstance().finishActivity(DeveloperOptionsActivity.this);
                DialogController.getInstance().dismissMoreDialog();
                finish();
            }
        });
    }

    /**
     * 清除用户数据库
     */
    private void addClearDataItem(SetAreaLayout areaLayout) {
        SetItemLayout itemLayout = new SetItemLayout(getContext());
        itemLayout.setTitle("Clear user data");
        itemLayout.setOnClickListener(null);
        itemLayout.setClickEventListener(this::showClearSureDialog);
        itemLayout.showArrowIcon();
        areaLayout.addSetItem(itemLayout);
    }

    /**
     * 退出app
     */
    private void exitApp() {
        Message exitMsg = new Message();
        exitMsg.what = EXIT_APP;
        exitMsg.arg1 = 5;
        mHandler.sendMessage(exitMsg);
    }
}
