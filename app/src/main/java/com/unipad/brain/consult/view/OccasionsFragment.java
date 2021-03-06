package com.unipad.brain.consult.view;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.BitmapUtils;
import com.unipad.AppContext;
import com.unipad.brain.R;
import com.unipad.brain.home.MainBasicFragment;
import com.unipad.brain.home.bean.NewEntity;
import com.unipad.brain.home.dao.NewsService;
import com.unipad.common.Constant;
import com.unipad.common.adapter.CommonAdapter;
import com.unipad.http.HttpConstant;
import com.unipad.observer.IDataObserver;

import org.xutils.common.Callback;

import java.util.ArrayList;
import java.util.List;

/**
 * 赛事
 * Created by Administrator on 2016/6/20.
 */
public class OccasionsFragment extends MainBasicFragment implements IDataObserver {
    private String TAG;

    private ListView mListViewTab;
    private List<NewEntity> occasionData = new ArrayList<NewEntity>();
    private NewsService service;
    private MyNewsListAdapter mNewsAdapter;
    //用来记录第一次获取数据
    private  boolean isGetData;
    private PopupWindow mPopupWindows;
    private ScaleAnimation sa;
    private View mPopupView;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }


    //对于用户不可见 与 不可见  会被调用；该方法可能会多次调用
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if ((isVisibleToUser && isResumed())) {

            if(!isGetData){
                service.getNews("00002", null, 1, 10);
                Log.d(getTag(), "获取消息 界面可见");
                isGetData = true;
            }


        } else if (!isVisibleToUser) {
            super.onPause();
        }
    }

    private void initData() {

        mListViewTab = (ListView) getView().findViewById(R.id.lv_occasions_listview);

        service = (NewsService) AppContext.instance().getService(Constant.NEWS_SERVICE);
        service.registerObserver(HttpConstant.NOTIFY_GET_COMPETITION, this);

        mNewsAdapter = new MyNewsListAdapter(mActivity, occasionData, R.layout.item_listview_occasions);
        mListViewTab.setAdapter(mNewsAdapter);
    }

    private void initPopupWindows(final NewEntity newEntity ){
        mPopupView = View.inflate(mActivity, R.layout.comment_commit_popup, null);
        //评论内容
        final EditText et_commment = (EditText) mPopupView.findViewById(R.id.et_popup_comment_input);
        //提交评论按钮
        Button btn_commit = (Button) mPopupView.findViewById(R.id.btn_comment_commit);

        btn_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击提交关闭窗体  用户评论内容
                final String user_comment = et_commment.getText().toString().trim();
                if(TextUtils.isEmpty(user_comment)){
                    Toast.makeText(mActivity, "内容为空 请重新输入", Toast.LENGTH_SHORT).show();
                    return;
                }

                //提交评论内容到服务器
                service.getNewsOperate(newEntity.getId(), "2", null, user_comment, 0, new Callback.CommonCallback<String>(){

                    @Override
                    public void onSuccess(String s) {

                    }

                    @Override
                    public void onError(Throwable throwable, boolean b) {
                        Toast.makeText(mActivity, "网络原因 提交失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(CancelledException e) {

                    }

                    @Override
                    public void onFinished() {

                    }
                });
                //清空输入的内容
                et_commment.setText("");
                //关闭弹出窗体
                closePopup();
            }
        });

        mPopupWindows = new PopupWindow(mPopupView, -1, 100, true);
        mPopupWindows.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        mPopupWindows.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mPopupWindows.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //动画效果;
        sa = new ScaleAnimation(0f, 1f, 0.5f, 1f,
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0.5f);
        sa.setDuration(300);

    }

    private void showPopupWindows(View parent , int x, int y){
        closePopup();

        popupInputMethodWindow();
        mPopupView.startAnimation(sa);

        mPopupWindows.showAtLocation(parent, Gravity.BOTTOM, 0, 0);

    }

    //关闭弹出窗体
    private void closePopup(){

        if(mPopupWindows != null && mPopupWindows.isShowing()){
            mPopupWindows.dismiss();
        }
    }
    //同时弹出 隐藏键盘 弹出框
    private void popupInputMethodWindow() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) getActivity()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm.isActive()) {
                    imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
                            InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        }, 0);
    }

    private void clear(){
        service.unRegisterObserve(HttpConstant.NOTIFY_GET_COMPETITION, this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        clear();
    }

    @Override
    public int getLayoutId() {

        return  R.layout.fragment_occasions;
    }

    @Override
    public void onClick(View v) {

    }

    class MyNewsListAdapter extends CommonAdapter<NewEntity> {

        public MyNewsListAdapter(Context context, List<NewEntity> datas, int layoutId) {
            super(context, datas, layoutId);
        }

        @Override
        public void convert(final com.unipad.common.ViewHolder holder, final NewEntity newEntity) {
            //设置  缩略图
            final ImageView iv_picture = (ImageView) holder.getView(R.id.iv_item_occasion_icon);

            new BitmapUtils(mActivity).display(iv_picture, newEntity.getThumbUrl());
            //设置标题
            ((TextView) holder.getView(R.id.tv_item_occasion_news_title)).setText(newEntity.getTitle());
            //设置更新时间
            ((TextView) holder.getView(R.id.tv_item_occasion_updatetime)).setText(newEntity.getPublishDate());
            //分割线
            View view_line_split = (View) holder.getView(R.id.view_line_item_occasion);


            //点赞的imagebutton
            final ImageView iv_pager_zan = (ImageView) holder.getView(R.id.iv_item_occasion_zan);
            final ImageView iv_pager_comment = (ImageView) holder.getView(R.id.iv_item_occasion_comment);
//            //查看详情
//            TextView tv_checkDetail  = (TextView) holder.getView(R.id.tv_item_occasion_detail);
            //查看详情的 relative
            RelativeLayout rl_checkDetail = holder.getView(R.id.rl_item_occasion_detail);


            if (newEntity.getIsLike()) {
                iv_pager_zan.setImageResource(R.drawable.favorite_introduction_check);
            } else {
                //默认情况下
                iv_pager_zan.setImageResource(R.drawable.favorite_introduction_normal);
            }


            //点赞  点击事件
            iv_pager_zan.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    Log.e("", "dianzao kai shi !!!!");
                    service.getNewsOperate(newEntity.getId(), "1", String.valueOf(!newEntity.getIsLike()), "0", 0,
                            new Callback.CommonCallback<String>() {
                                @Override
                                public void onSuccess(String s) {
                                    newEntity.setIsLike(!newEntity.getIsLike());
                                    if (newEntity.getIsLike()) {
                                        //点击之后 变为check
                                        iv_pager_zan.setImageResource(R.drawable.favorite_introduction_check);
                                    } else {
                                        iv_pager_zan.setImageResource(R.drawable.favorite_introduction_normal);
                                    }
                                }

                                @Override
                                public void onError(Throwable throwable, boolean b) {

                                }

                                @Override
                                public void onCancelled(CancelledException e) {

                                }

                                @Override
                                public void onFinished() {

                                }

                            });
                }
            });


            //评论的点击事件
            iv_pager_comment.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    //初始化弹出窗体
                    initPopupWindows(newEntity);
                    //先做弹出窗体  然后 输入文本信息;
                    int[] location = new int[2];
                    iv_pager_comment.getLocationInWindow(location);
                    //弹出窗体位置
                    showPopupWindows(iv_pager_comment, location[0] + 30, location[1] + 30);

                }
            });
            //查看详情点击
            rl_checkDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //查看详情的界面
                    Intent intent = new Intent(mActivity, PagerDetailActivity.class);
                    intent.putExtra("pagerId", newEntity.getId());
                    startActivity(intent);
                }
            });


        }
    }

        //用于网络请求数据 key 是网页的id   o是json数据
        @Override
        public void update(int key, Object o) {
            switch (key) {
                case HttpConstant.NOTIFY_GET_COMPETITION:
                    //发送网络请求 获取赛事页面数据
                    occasionData.addAll((List<NewEntity>) o);
                    mNewsAdapter.notifyDataSetChanged();
                    break;

                case HttpConstant.NOTIFY_GET_OPERATE:

                    break;

            }
        }

        private String tag() {
            if (TextUtils.isEmpty(TAG)) {
                TAG = this.getClass().getSimpleName();
            }
            return TAG;
        }

}
