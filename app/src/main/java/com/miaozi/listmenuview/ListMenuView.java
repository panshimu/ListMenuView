package com.miaozi.listmenuview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

/**
 * created by panshimu
 * on 2019/8/23
 */
public class ListMenuView extends LinearLayout {
    //tab布局
    private LinearLayout mTabView;
    //内容布局
    private FrameLayout mContentView;
    //背景阴影
    private View mBottomView;
    private BaseListMenuAdapter mAdapter;
    private int mCurrentPosition = -1 ;
    private int mTabViewHeight = 80;
    //内容布局的高度 默认 高度的75%
    private int mContentViewHeight;
    //动画执行时间
    private long mAnimationDuration = 350;
    //菜单页面 包含 内容 和 阴影
    private FrameLayout mMenuView;
    //分割线的颜色
    private int mLineViewColor = 0xffCACACA;
    //动画是否在执行
    private boolean mAnimationExecute;
    //关闭动画
    private AnimatorSet mCloseAnimatorSet;
    //记录 当前点击的position 为了不用传递到 open 的方法中成为 final类型 会出现乱序的情况
    //除非每一次启动都是new 一个动画
    private int mOnClickPosition;
    //打开动画
    private AnimatorSet mOpenAnimatorSet;

    private ListMenuObserver mObserver;

    public ListMenuView(Context context) {
        this(context,null);
    }

    public ListMenuView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ListMenuView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initLayout();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //因为 设置View的 setVisibility 会重复调用这个方法
        if(mContentViewHeight == 0) {
            int height = MeasureSpec.getSize(heightMeasureSpec);
            mContentViewHeight = (int) (height * 0.76f);
            ViewGroup.LayoutParams layoutParams = mContentView.getLayoutParams();
            layoutParams.height = mContentViewHeight;
            mContentView.setLayoutParams(layoutParams);
            //往上移动这个布局 达到隐藏的功能
            mContentView.setTranslationY(-mContentViewHeight);
        }
    }


    private void initLayout() {

        //设置方向
        setOrientation(VERTICAL);
        mTabView = new LinearLayout(getContext());
        mTabView.setOrientation(HORIZONTAL);
        ViewGroup.LayoutParams mTabViewParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,mTabViewHeight);
        mTabView.setLayoutParams(mTabViewParams);
        addView(mTabView);

        View mLineView = new View(getContext());
        mLineView.setBackgroundColor(mLineViewColor);
        mLineView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
        addView(mLineView);

        mMenuView = new FrameLayout(getContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
        layoutParams.weight = 1;
        mMenuView.setLayoutParams(layoutParams);

        addView(mMenuView);

        mBottomView = new View(getContext());
        mBottomView.setBackgroundColor(Color.parseColor("#80000000"));
        mBottomView.setVisibility(GONE);
        mBottomView.setAlpha(0f);
        mMenuView.addView(mBottomView);

        mBottomView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                closeContentView();
            }
        });

        mContentView = new FrameLayout(getContext());
        mContentView.setBackgroundColor(Color.WHITE);
        mMenuView.addView(mContentView);

    }

    /**
     * 设置adapter
     * @param adapter
     */
    public void setAdapter(BaseListMenuAdapter adapter){


        if(adapter == null){
            throw new IllegalArgumentException("请设置->BaseListMenuAdapter");
        }

        //先反注册 防止多次注册
        if(this.mAdapter != null && mObserver!=null){
            mAdapter.unRegisterObserver(mObserver);
        }

        this.mAdapter = adapter;

        mObserver = new ListMenuObserver();
        mAdapter.registerObserver(mObserver);

        for (int i = 0; i < mAdapter.getCount(); i++) {
            View tabView = mAdapter.getTabView(i, mTabView);
            if(tabView != null){
                LayoutParams layoutParams = new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
                layoutParams.weight = 1;
                layoutParams.gravity = Gravity.CENTER;
                tabView.setLayoutParams(layoutParams);
                mTabView.addView(tabView);
                tabView.setTag(-100,i);
                setTabViewOnclickListener(tabView);
            }
            View contentView = mAdapter.getContentView(i, mContentView);
            if(contentView != null){
                contentView.setVisibility(GONE);
                mContentView.addView(contentView);
            }
        }
    }

    /**
     * 设置tab的点击事件
     * @param tabView
     */
    private void setTabViewOnclickListener( View tabView) {
        tabView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnClickPosition = (int) view.getTag(-100);
                //关闭状态
                if (mCurrentPosition == -1) {
                    openContentView();
                } else {
                    //不是同一个，先把上一个隐藏 切换显示
                    if (mCurrentPosition != mOnClickPosition) {
                        View currentView = mContentView.getChildAt(mCurrentPosition);
                        if (currentView != null) {
                            currentView.setVisibility(GONE);
                            mAdapter.contentViewClose(mTabView.getChildAt(mCurrentPosition));
                        }
                        mCurrentPosition = mOnClickPosition;
                        View showView = mContentView.getChildAt(mCurrentPosition);
                        if (showView != null) {
                            showView.setVisibility(VISIBLE);
                            mAdapter.contentViewOpen(mTabView.getChildAt(mCurrentPosition));
                        }
                    } else {
                        closeContentView();
                    }
                }
            }
        });
    }

    private void setPosition(int position){
        this.mCurrentPosition = position;
    }

    /**
     * 设置tabView的高度
     * @param height
     */
    public void setTabViewHeight(int height){
        this.mTabViewHeight = height;
        ViewGroup.LayoutParams layoutParams = mTabView.getLayoutParams();
        layoutParams.height = height;
        mTabView.setLayoutParams(layoutParams);
    }

    /**
     * 打开动画
     */
    private void openContentView(){
        if(mAnimationExecute){
           return;
        }
        if(mOpenAnimatorSet == null) {
            mOpenAnimatorSet = new AnimatorSet();
            ObjectAnimator contentViewAnimation = ObjectAnimator.ofFloat(mContentView, "translationY", -mContentViewHeight, 0);
            ObjectAnimator bottomViewAnimation = ObjectAnimator.ofFloat(mBottomView, "alpha", 0f, 1f);
            mOpenAnimatorSet.setDuration(mAnimationDuration);
            mOpenAnimatorSet.playTogether(contentViewAnimation, bottomViewAnimation);
            mOpenAnimatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    mAnimationExecute = true;
                    View view = mContentView.getChildAt(mOnClickPosition);
                    if (view != null) {
                        view.setVisibility(VISIBLE);
                    }
                    mBottomView.setVisibility(VISIBLE);
                    mAdapter.contentViewOpen(mTabView.getChildAt(mOnClickPosition));
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mAnimationExecute = false;
                    mCurrentPosition = mOnClickPosition;
                }
            });
        }
        if(!mAnimationExecute) {
            mOpenAnimatorSet.start();
        }

    }

    /**
     * 关闭动画
     */
    private void closeContentView(){
        if(mCloseAnimatorSet == null) {
            mCloseAnimatorSet = new AnimatorSet();
            ObjectAnimator contentViewAnimation = ObjectAnimator.ofFloat(mContentView, "translationY", 0, -mContentViewHeight);
            ObjectAnimator bottomViewAnimation = ObjectAnimator.ofFloat(mBottomView, "alpha", 1f, 0f);
            mCloseAnimatorSet.setDuration(mAnimationDuration);
            mCloseAnimatorSet.playTogether(contentViewAnimation, bottomViewAnimation);
            mCloseAnimatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    View view = mContentView.getChildAt(mCurrentPosition);
                    if (view != null) {
                        view.setVisibility(GONE);
                    }
                    mBottomView.setVisibility(GONE);
                    mCurrentPosition = -1;
                    mAnimationExecute = false;
                }

                @Override
                public void onAnimationStart(Animator animation) {
                    mAnimationExecute = true;
                    mAdapter.contentViewClose(mTabView.getChildAt(mCurrentPosition));
                }
            });
        }
         if(!mAnimationExecute) {
             mCloseAnimatorSet.start();
         }
    }


    public class ListMenuObserver extends BaseMenuObserver{
        @Override
        public void closeMenu() {
            ListMenuView.this.closeContentView();
        }
    }
}
