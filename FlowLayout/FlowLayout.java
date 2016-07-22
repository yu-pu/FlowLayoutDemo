package com.xytxw.viewstudy.viewgroup;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.orhanobut.logger.Logger;
import com.xytxw.viewstudy.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yupu on 2016/7/6-9:57.
 * Email:459112332@qq.com
 */
public class FlowLayout extends ViewGroup {
    //存储所有的子View
    private List<List<View>> allViews = new ArrayList<List<View>>();
    //存储每一行的最高值
    private List<Integer> lineHeights = new ArrayList<>();

    public FlowLayout(Context context) {
        this(context, null);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //获取他得测量模式和大小
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

        //为了支持wrap_content，计算宽高
        int width = 0;
        int height = 0;

        int lineWidth = 0;
        int lineHeight = 0;

        int cCount = getChildCount();
        for (int i = 0; i < cCount; i++) {
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            int childWidth = lp.leftMargin + lp.rightMargin + child.getMeasuredWidth();
            int childHeight = lp.topMargin + lp.bottomMargin + child.getMeasuredHeight();

            if (lineWidth + childWidth > sizeWidth) {
                width = Math.max(lineWidth, childWidth);
                lineWidth = childWidth;
                height += lineHeight;
                lineHeight = childHeight;
            } else {
                lineWidth += childWidth;
                lineHeight = Math.max(lineHeight, childHeight);
            }

            if (i == cCount - 1) {
                width = Math.max(width, lineWidth);
                height += lineHeight;
                lineHeights.add(lineHeight);
            }
        }
        setMeasuredDimension((modeWidth == MeasureSpec.EXACTLY) ? sizeWidth : width, (modeHeight == MeasureSpec.EXACTLY) ? sizeHeight : height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        allViews.clear();
        lineHeights.clear();
        List<View> lineViews = new ArrayList<View>();
        int left = 0;
        int top = 0;
        int lineHeight = 0;
        int width = getWidth();
        int cCount = getChildCount();
        int lineWidth = 0;
        for (int i = 0; i < cCount; i++) {
            View child = getChildAt(i);
            child.setTag(false);
            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

            if (childWidth + lp.leftMargin + lp.rightMargin + lineWidth > width) {
                lineHeights.add(lineHeight);
                allViews.add(lineViews);
                lineWidth = 0;
                lineViews = new ArrayList<View>();
            }

            lineWidth += childWidth + lp.leftMargin + lp.rightMargin;
            lineHeight = Math.max(lineHeight, childHeight + lp.topMargin + lp.bottomMargin);
            lineViews.add(child);
            child.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean tag = !(boolean) v.getTag();
                    if (tag) {
                        v.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_bg_select));
                    } else {
                        v.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_bg));
                    }
                    v.setTag(tag);
                }
            });
        }

        lineHeights.add(lineHeight);
        allViews.add(lineViews);

        Log.e("xxx", "" + lineHeights.size() + "," + allViews.size());
        int lines = allViews.size();
        for (int i = 0; i < allViews.size(); i++) {
            lineViews = allViews.get(i);
            Log.e("xx", "-----" + lineViews.size());
            //遍历每一行子View
            lineHeight = lineHeights.get(i);
            for (int j = 0; j < lineViews.size(); j++) {
                View child = lineViews.get(j);
                if (child.getVisibility() == View.GONE) {
                    continue;
                }
                MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
                int lc = left + lp.leftMargin;
                int tc = top + lp.topMargin;
                int rc = lc + child.getMeasuredWidth();
                int bc = tc + child.getMeasuredHeight();

                Logger.i("lines:" + lines + ",layout:" + lc + "," + tc + "," + rc + "," + bc);
                child.layout(lc, tc, rc, bc);

                left += child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            }
            left = 0;
            top += lineHeight;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.e("xx", "onDraw");
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        Log.e("xx", "dispatchDraw");
    }
}
