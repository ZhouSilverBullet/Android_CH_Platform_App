package com.caohua.games.biz.task;

import com.chsdk.model.BaseEntry;

/**
 * Created by zhouzhou on 2017/2/21.
 */

public class TaskEntry extends BaseEntry {
    private String id;//
    private String task_status;//
    private String task_name;//
    private String task_desc;//
    private String award_points;//
    private String award_money;//
    private String award_silver;//
    private String method;//
    private String value;//
    private String award_growexp;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTask_status() {
        return task_status;
    }

    public void setTask_status(String task_status) {
        this.task_status = task_status;
    }

    public String getTask_name() {
        return task_name;
    }

    public void setTask_name(String task_name) {
        this.task_name = task_name;
    }

    public String getTask_desc() {
        return task_desc;
    }

    public void setTask_desc(String task_desc) {
        this.task_desc = task_desc;
    }

    public String getAward_points() {
        return award_points;
    }

    public void setAward_points(String award_points) {
        this.award_points = award_points;
    }

    public String getAward_money() {
        return award_money;
    }

    public void setAward_money(String award_money) {
        this.award_money = award_money;
    }

    public String getAward_silver() {
        return award_silver;
    }

    public void setAward_silver(String award_silver) {
        this.award_silver = award_silver;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getAward_growexp() {
        return award_growexp;
    }

    public void setAward_growexp(String award_growexp) {
        this.award_growexp = award_growexp;
    }
}
