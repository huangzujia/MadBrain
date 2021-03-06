package com.unipad.brain.number;

import android.view.View;
import android.widget.ScrollView;

import com.unipad.brain.R;
import com.unipad.brain.number.view.KeyboardDialog;
import com.unipad.brain.number.view.NumberRememoryLayout;
import com.unipad.common.Constant;
import com.unipad.utils.LogUtil;

/**
 * Created by gongkan on 2016/7/4.
 */
public class LongNumFragment extends NumberRightFragment{
    private KeyboardDialog mKeyboardDialog;

    @Override
    public void pauseGame() {
        super.pauseGame();
        if (mKeyboardDialog != null && mKeyboardDialog.isShowing()){
            mKeyboardDialog.dismiss();;
        }
    }

    @Override
    public void reStartGame() {
        super.reStartGame();
        if (mKeyboardDialog != null && !mKeyboardDialog.isShowing()){
            mKeyboardDialog.show();
        }
    }

    @Override
    public boolean isNeedShowCurrent() {
        return true;
    }


    @Override
    public void memoryTimeToEnd(int memoryTime) {
        super.memoryTimeToEnd(memoryTime);
    }

    @Override
    public String getCompeteItem() {
        return Constant.getProjectName(mActivity.getProjectId());
    }

    @Override
    public void initAnswerView() {
        mKeyboardDialog = new KeyboardDialog(mActivity);
        mKeyboardDialog.show();
        mKeyboardDialog.setKeyboardClickListener(this);
    }

    @Override
    public void rememoryTimeToEnd(int answerTime) {
        super.rememoryTimeToEnd(answerTime);
        if (mKeyboardDialog != null && mKeyboardDialog.isShowing()) {
            mKeyboardDialog.dismiss();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mKeyboardDialog != null) {
            if (mKeyboardDialog.isShowing()) {
                mKeyboardDialog.dismiss();
            }
            mKeyboardDialog = null;
        }
    }
}
