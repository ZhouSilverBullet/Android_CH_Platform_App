package com.caohua.games.biz.task;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.view.View;

import com.caohua.games.app.AppContext;
import com.chsdk.biz.BaseLogic;
import com.chsdk.biz.app.AppLogic;
import com.chsdk.configure.DataStorage;
import com.chsdk.configure.SdkSession;
import com.chsdk.http.IRequestListener;
import com.chsdk.http.RequestExe;
import com.chsdk.model.BaseModel;
import com.chsdk.model.login.LoginUserInfo;
import com.chsdk.utils.LogUtil;
import com.chsdk.utils.NetworkUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by zhouzhou on 2017/3/17.
 */

public class TaskLogic extends BaseLogic {
    private static final String TASK_URL = "task/index";
    private static final long UPDATE_INTERVAL = 5 * 1000;
    private static long resumeTime;
    private boolean isRefresh;

    public TaskLogic() {
    }

    public TaskLogic(boolean isRefresh) {
        this.isRefresh = isRefresh;
    }

    public void getTaskEntry(final AppLogic.AppLogicListner listener) {
        final AppContext context = AppContext.getAppContext();
        if (!NetworkUtils.isNetworkConnected(context)) {
            if (listener != null) {
                listener.failed("网络连接失败，请重试！");
            }
            return;
        }
        if (System.currentTimeMillis() - resumeTime > 0 &&
                System.currentTimeMillis() - resumeTime < UPDATE_INTERVAL && !isRefresh) {
            LogUtil.errorLog("TaskFragment onResume  updateTime 2: " + resumeTime);
            if (listener != null) {
                listener.failed("delay");
            }
            return;
        }
        BaseModel baseModel = new BaseModel() {
            String getVersion(Context context) {
                PackageManager pm = context.getPackageManager();
                try {
                    PackageInfo pi = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS);
                    return pi.versionCode + "";
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                return "";
            }

            @Override
            public void putDataInMap() {
                SdkSession session = SdkSession.getInstance();
                String userId = session.getUserId();
                String token = session.getToken();
                put(BaseModel.PARAMS_USER_ID, userId == null ? "0" : userId);
                put(BaseModel.PARAMS_TOKEN, token == null ? "0" : token);
                put(BaseModel.PARAMS_SDK_VERSION, getVersion(context));
                put(BaseModel.PARAMS_DEVICE_NO, session.getDeviceNo());
            }
        };
        RequestExe.post(HOST_APP + TASK_URL, baseModel, new IRequestListener() {
            @Override
            public void success(HashMap<String, String> map) {
                LogUtil.errorLog("TaskLogic map: " + map);
                resumeTime = System.currentTimeMillis();
                if (listener != null && map != null) {
                    boolean hasStatus = false;
                    boolean isGameReward = false;
                    List<Object> objectList = new ArrayList<Object>();
                    List<TaskDailyEntry> dailyEntries = null;
                    List<TaskGameEntry> gameEntries = null;
                    TaskTitleEntry titleEntry = null;
                    String game_reward = map.get("game_reward");
                    if (!TextUtils.isEmpty(game_reward)) {
                        isGameReward = true;
                        DataStorage.saveTaskNotGave(context, game_reward);
                    } else {
                        if (!TextUtils.isEmpty(DataStorage.getTaskNotGave(context))) {
                            DataStorage.saveTaskNotGave(context, "");
                        }
                    }
                    String daily = map.get("daily");
                    String game = map.get("game");
                    String growth = map.get("develop");
                    if (!TextUtils.isEmpty(daily)) {
                        titleEntry = new TaskTitleEntry();
                        titleEntry.setPosition(1);
                        dailyEntries = fromJsonArray(daily, TaskDailyEntry.class);
                        if (dailyEntries != null) {
                            objectList.add(objectList.size(), titleEntry);
                            if (dailyEntries.size() == 0) {
                                objectList.add(objectList.size(), new TaskDailyEntry());
                            } else {
                                for (TaskDailyEntry dailyEntry : dailyEntries) {
                                    String task_status = dailyEntry.getTask_status();
                                    if (!TextUtils.isEmpty(task_status)) {
                                        int status = Integer.parseInt(task_status);
                                        if (status == 2) {
                                            TaskNotifyDotEntry dotEntry = new TaskNotifyDotEntry();
                                            dotEntry.setStatus(View.VISIBLE);
                                            EventBus.getDefault().post(dotEntry);
                                            hasStatus = true;
                                            break;
                                        }
                                    }
                                }
                                objectList.addAll(objectList.size(), dailyEntries);
                            }
                        }
                    }

                    if (!TextUtils.isEmpty(game)) {
                        titleEntry = new TaskTitleEntry();
                        titleEntry.setPosition(2);
                        gameEntries = fromJsonArray(game, TaskGameEntry.class);
                        if (gameEntries != null) {
                            objectList.add(objectList.size(), titleEntry);
                            if (gameEntries.size() == 0) {
                                objectList.add(objectList.size(), new TaskGameEntry());
                            } else {
                                if (!hasStatus) {
                                    for (TaskGameEntry gameEntry : gameEntries) {
                                        String task_status = gameEntry.getTask_status();
                                        if (!TextUtils.isEmpty(task_status)) {
                                            int status = Integer.parseInt(task_status);
                                            if (status == 2) {
                                                TaskNotifyDotEntry dotEntry = new TaskNotifyDotEntry();
                                                dotEntry.setStatus(View.VISIBLE);
                                                EventBus.getDefault().post(dotEntry);
                                                hasStatus = true;
                                                break;
                                            }
                                        }
                                    }
                                }

                                for (TaskGameEntry gameEntry : gameEntries) {
                                    String task_status = gameEntry.getTask_status();
                                    if (!TextUtils.isEmpty(task_status)) {
                                        int status = Integer.parseInt(task_status);
                                        if (status == 2) {
                                            if (isGameReward) {
                                                gameEntry.setTask_status("5");
                                            }
                                        }
                                    }
                                }

                                objectList.addAll(objectList.size(), gameEntries);
                            }
                        }
                    }

                    List<TaskGrowthEntry> growthEntries;
                    if (!TextUtils.isEmpty(growth)) {
                        titleEntry = new TaskTitleEntry();
                        titleEntry.setPosition(3);
                        growthEntries = fromJsonArray(growth, TaskGrowthEntry.class);
                        if (growthEntries != null) {
                            objectList.add(objectList.size(), titleEntry);
                            if (growthEntries.size() == 0) {
                                objectList.add(objectList.size(), new TaskGrowthEntry());
                            } else {
                                if (!hasStatus) {
                                    for (TaskGrowthEntry growthEntry : growthEntries) {
                                        String task_status = growthEntry.getTask_status();
                                        if (!TextUtils.isEmpty(task_status)) {
                                            int status = Integer.parseInt(task_status);
                                            if (status == 2) {
                                                TaskNotifyDotEntry dotEntry = new TaskNotifyDotEntry();
                                                dotEntry.setStatus(View.VISIBLE);
                                                EventBus.getDefault().post(dotEntry);
                                                hasStatus = true;
                                                break;
                                            }
                                        }

                                    }
                                }
                                objectList.addAll(objectList.size(), growthEntries);
                            }
                        }
                    }
                    if (!hasStatus) {
                        TaskNotifyDotEntry dotEntry = new TaskNotifyDotEntry();
                        dotEntry.setStatus(View.INVISIBLE);
                        EventBus.getDefault().post(dotEntry);
                    }
                    listener.success(objectList);
                }
            }

            @Override
            public void failed(String error, int errorCode) {
                resumeTime = System.currentTimeMillis();
                if (listener != null) {
                    listener.failed(error);
                }
            }
        });
    }
}
