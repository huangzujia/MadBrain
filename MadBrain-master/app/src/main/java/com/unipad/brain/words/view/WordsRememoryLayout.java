package com.unipad.brain.words.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.unipad.brain.number.bean.BinaryNumberEntity;

/**
 * 随机词语回忆界面
 */
public class WordsRememoryLayout extends LinearLayout {
    private static final String TAG = WordsRememoryLayout.class.getSimpleName();
    public static final int ID = 0x3333333;
    private Context mContext;
    private LayoutInflater mInflater;
    private int mRows = BinaryNumberEntity.rows;
    private int mLines = BinaryNumberEntity.lines;

    public WordsRememoryLayout(Context context) {
        super(context);
    }

    public WordsRememoryLayout(Context context, int competeType) {
        super(context);
        mContext = context;

    }

}
