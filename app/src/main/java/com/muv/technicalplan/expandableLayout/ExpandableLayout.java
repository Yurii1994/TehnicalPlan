package com.muv.technicalplan.expandableLayout;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.silencedut.expandablelayout.R.styleable;

public class ExpandableLayout extends LinearLayout {
    private static final String TAG = ExpandableLayout.class.getSimpleName();
    private Settings mSettings;
    private int mExpandState;
    private ValueAnimator mExpandAnimator;
    private ValueAnimator mParentAnimator;
    private AnimatorSet mExpandScrollAnimotorSet;
    private int mExpandedViewHeight;
    private boolean mIsInit = true;
    private ScrolledParent mScrolledParent;
    private ExpandableLayout.OnExpandListener mOnExpandListener;

    public int getExpandedViewHeight() {
        return mExpandedViewHeight;
    }

    public void setExpandedViewHeight(int mExpandedViewHeight)
    {
        this.mExpandedViewHeight = mExpandedViewHeight;
    }

    public ExpandableLayout(Context context) {
        super(context);
        this.init((AttributeSet)null);
    }

    public ExpandableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init(attrs);
    }

    public ExpandableLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs) {
        this.setClickable(true);
        this.setOrientation(VERTICAL);
        this.setClipChildren(false);
        this.setClipToPadding(false);
        this.mExpandState = -1;
        this.mSettings = new Settings();
        if(attrs != null) {
            TypedArray typedArray = this.getContext().obtainStyledAttributes(attrs, styleable.ExpandableLayout);
            this.mSettings.expandDuration = typedArray.getInt(styleable.ExpandableLayout_expDuration, 300);
            this.mSettings.expandWithParentScroll = typedArray.getBoolean(styleable.ExpandableLayout_expWithParentScroll, false);
            this.mSettings.expandScrollTogether = typedArray.getBoolean(styleable.ExpandableLayout_expExpandScrollTogether, true);
            typedArray.recycle();
        }

    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int childCount = this.getChildCount();
        if(childCount != 2) {
            throw new IllegalStateException("ExpandableLayout must has two child view !");
        } else {
            if(this.mIsInit) {
                ((MarginLayoutParams)this.getChildAt(0).getLayoutParams()).bottomMargin = 0;
                MarginLayoutParams marginLayoutParams = (MarginLayoutParams)this.getChildAt(1).getLayoutParams();
                marginLayoutParams.bottomMargin = 0;
                marginLayoutParams.topMargin = 0;
                marginLayoutParams.height = 0;
                this.mExpandedViewHeight = this.getChildAt(1).getMeasuredHeight();
                this.mIsInit = false;
                this.mExpandState = 0;
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }

        }
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        if(this.mSettings.expandWithParentScroll) {
            this.mScrolledParent = Utils.getScrolledParent(this);
        }

    }

    private int getParentScrollDistance() {
        byte distance = 0;
        if(this.mScrolledParent == null) {
            return distance;
        } else {
            int var4 = (int)(this.getY() + (float)this.getMeasuredHeight() + (float)this.mExpandedViewHeight - (float)this.mScrolledParent.scrolledView.getMeasuredHeight());

            for(int index = 0; index < this.mScrolledParent.childBetweenParentCount; ++index) {
                ViewGroup parent = (ViewGroup)this.getParent();
                var4 = (int)((float)var4 + parent.getY());
            }

            return var4;
        }
    }

    private void verticalAnimate(final int startHeight, final int endHeight) {
        int distance = this.getParentScrollDistance();
        final View target = this.getChildAt(1);
        this.mExpandAnimator = ValueAnimator.ofInt(new int[]{startHeight, endHeight});
        this.mExpandAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                target.getLayoutParams().height = ((Integer)animation.getAnimatedValue()).intValue();
                target.requestLayout();
            }
        });
        this.mExpandAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if(endHeight - startHeight < 0) {
                    ExpandableLayout.this.mExpandState = 0;
                    if(ExpandableLayout.this.mOnExpandListener != null) {
                        ExpandableLayout.this.mOnExpandListener.onExpand(false);
                    }
                } else {
                    ExpandableLayout.this.mExpandState = 1;
                    if(ExpandableLayout.this.mOnExpandListener != null) {
                        ExpandableLayout.this.mOnExpandListener.onExpand(true);
                    }
                }

            }
        });
        this.mExpandState = this.mExpandState == 1?3:2;
        this.mExpandAnimator.setDuration((long)this.mSettings.expandDuration);
        if(this.mExpandState == 2 && this.mSettings.expandWithParentScroll && distance > 0) {
            this.mParentAnimator = Utils.createParentAnimator(this.mScrolledParent.scrolledView, distance, (long)this.mSettings.expandDuration);
            this.mExpandScrollAnimotorSet = new AnimatorSet();
            if(this.mSettings.expandScrollTogether) {
                this.mExpandScrollAnimotorSet.playTogether(new Animator[]{this.mExpandAnimator, this.mParentAnimator});
            } else {
                this.mExpandScrollAnimotorSet.playSequentially(new Animator[]{this.mExpandAnimator, this.mParentAnimator});
            }

            this.mExpandScrollAnimotorSet.start();
        } else {
            this.mExpandAnimator.start();
        }

    }

    public void setExpand(boolean expand) {
        if(this.mExpandState != -1) {
            this.getChildAt(1).getLayoutParams().height = expand?this.mExpandedViewHeight:0;
            this.requestLayout();
            this.mExpandState = expand?1:0;
        }
    }

    public boolean isExpanded() {
        return this.mExpandState == 1;
    }

    public void toggle() {
        if(this.mExpandState == 1) {
            this.close();
        } else if(this.mExpandState == 0) {
            this.expand();
        }

    }

    public void expand() {
        this.verticalAnimate(0, this.mExpandedViewHeight);
    }

    public void close() {
        this.verticalAnimate(this.mExpandedViewHeight, 0);
    }

    public boolean performClick() {
        this.toggle();
        return super.performClick();
    }

    public void setOnExpandListener(ExpandableLayout.OnExpandListener onExpandListener) {
        this.mOnExpandListener = onExpandListener;
    }

    public void setExpandScrollTogether(boolean expandScrollTogether) {
        this.mSettings.expandScrollTogether = expandScrollTogether;
    }

    public void setExpandWithParentScroll(boolean expandWithParentScroll) {
        this.mSettings.expandWithParentScroll = expandWithParentScroll;
    }

    public void setExpandDuration(int expandDuration) {
        this.mSettings.expandDuration = expandDuration;
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(this.mExpandAnimator != null && this.mExpandAnimator.isRunning()) {
            this.mExpandAnimator.cancel();
        }

        if(this.mParentAnimator != null && this.mParentAnimator.isRunning()) {
            this.mParentAnimator.cancel();
        }

        if(this.mExpandScrollAnimotorSet != null) {
            this.mExpandScrollAnimotorSet.cancel();
        }

    }

    public interface OnExpandListener {
        void onExpand(boolean var1);
    }
}