package com.unipad.http;

import com.unipad.AppContext;
import com.unipad.UserDetailEntity;
import com.unipad.brain.home.bean.RuleGame;
import com.unipad.brain.home.dao.HomeGameHandService;
import com.unipad.common.Constant;
import com.unipad.observer.GlobleObserService;

import org.json.JSONObject;

/**
 * Created by gongkan on 2016/6/17.
 */
public class HitopGetRule extends HitopRequest<RuleGame> {

    private GlobleObserService sevice;

    public HitopGetRule(String path) {
        super(path);
    }

    public HitopGetRule(String matchId, String path,GlobleObserService service) {
        super(HttpConstant.GET_RULE);
        this.sevice = service;
        mParams.addQueryStringParameter("matchId", matchId);
    }

    @Override
    public String buildRequestURL() {
        return null;
    }

    @Override
    public RuleGame handleJsonData(String json) {
        RuleGame rule = null;
        JSONObject jsObj = null;
        try {
            jsObj = new JSONObject(json);
            if (jsObj != null && jsObj.toString().length() != 0) {
                if (jsObj.getInt("ret_code") == 0) {
                    JSONObject dataJson = new JSONObject(jsObj.getString("data"));
                    if (dataJson != null) {
                        rule = new RuleGame();
                        rule.setId(dataJson.getString("id"));
                        rule.setGradeId(dataJson.getString("gradeId"));
                        rule.setProjectId(dataJson.getString("projectId"));
                        rule.setTiltle(dataJson.getString("title"));
                        rule.setRuleNo(dataJson.getString("ruleNo"));
                        rule.setMemeryTime1(dataJson.getInt("memoryTime"));
                        rule.setRecallTime1(dataJson.getInt("recallTime"));
                        if ("00009".equals(rule.getProjectId())) {
                            rule.setMemeryTime2(dataJson.getInt("memoryTime2"));
                            rule.setRecallTime2(dataJson.getInt("recallTime2"));
                            rule.setMemeryTime3(dataJson.getInt("memoryTime3"));
                            rule.setRecallTime3(dataJson.getInt("recallTime3"));
                        }
                        rule.setMemeryTip(dataJson.getString("memoryText"));
                        rule.setReCallTip(dataJson.getString("recallText"));
                        rule.setCountRule(dataJson.getString("scoreText"));
                        rule.setCountRecall(dataJson.getInt("RECALL_COUNT"));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (rule != null) {
            sevice.noticeDataChange(HttpConstant.GET_RULE_NOTIFY, rule);
        }
        return rule;
    }

    @Override
    public void buildRequestParams() {

    }
}
