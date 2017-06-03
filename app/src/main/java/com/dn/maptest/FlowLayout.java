package com.dn.maptest;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/6/3 0003.
 */

public class FlowLayout extends ViewGroup {

    private ArrayList<ArrayList<View>> views;
    private int maxWidth;
    private int maxHeight;


    public FlowLayout(Context context) {
        this(context,null);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        views = new ArrayList<>();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int parentWidth = MeasureSpec.getSize(widthMeasureSpec) ;
        int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
        L.e("parentWidth = "+ parentWidth + " ,PaddingLeft =" + getPaddingLeft()+ " ,PaddingRight =" +getPaddingRight());
        int childCount = getChildCount();
        ArrayList<View> lineList = new ArrayList<>();
        //防止多次测量数据重复添加，所以先重新初始化下数据;
        views.clear();
        maxWidth = 0;
        maxHeight = 0;
        int currentWidth = 0;
        int currentLineMaxHeight = 0;
        int canUseWidth =  parentWidth - getPaddingLeft() - getPaddingRight();

        for (int i = 0; i < childCount; i++){
            View child = getChildAt(i);
            //计算子控件的大小
            //measureChild(child,widthMeasureSpec,heightMeasureSpec);
            int widthUsed = getPaddingLeft() + getPaddingRight() ;
            int heightUsed = getPaddingTop() + getPaddingBottom();
            //该方法主要哦用户测量父容易已经使用了padding
            measureChildWithMargins(child,widthMeasureSpec,widthUsed,heightMeasureSpec,heightUsed);
            // 该方法用户测量子view，不考虑父容器的padding
            //measureChild(child,widthMeasureSpec,heightMeasureSpec);
            //L.e(param + ", param.width = " + param.width + ", measureWidth = " + child.getMeasuredWidth());
            MarginLayoutParams params = (MarginLayoutParams) child.getLayoutParams();
            int width = child.getMeasuredWidth() + params.leftMargin + params.rightMargin ;
            int height = child.getMeasuredHeight() + params.topMargin + params.bottomMargin;
            if(currentWidth + width > canUseWidth){
                //L.e("currentWidth = " + currentWidth + " , child width = " + width + ", canUseWidth = " + canUseWidth);
                // 宽度不够，换一行
                //将行list添加到总views中，
                views.add(lineList);
                // 换行后记录下一行起始的宽度
                currentWidth = width;
                //创建下一行view的list
                lineList = new ArrayList<>();
                //将换行的第一个view添加到list中
                lineList.add(child);
                //将自定义控件的高度累加
                maxHeight += currentLineMaxHeight;
                //设置下一行的最大高度为当前view的高度
                currentLineMaxHeight = height;
                L.e("maxHeight = " + maxHeight);
            }else{
                // 当前宽度累加
                currentWidth += width;
                // 获取当前最高控件的高度
                currentLineMaxHeight = Math.max(currentLineMaxHeight,height);
                // 将view添加到行list中
                lineList.add(child);
                L.e("currentLineMaxHeight = " + currentLineMaxHeight);
            }
        }
        // 把最后一行的数据加上去
        maxWidth = Math.max(maxWidth,currentWidth);
        maxHeight += currentLineMaxHeight;
        views.add(lineList);
        L.e("last maxHeight = " + maxHeight);
//        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

//        switch (widthMode){
//            case MeasureSpec.EXACTLY:
//                break;
//            case MeasureSpec.UNSPECIFIED:
//            case MeasureSpec.AT_MOST:
//                parentWidth = maxWidth ;
//                break;
//        }
        switch (heightMode){
            case MeasureSpec.EXACTLY:
                break;
            case MeasureSpec.UNSPECIFIED:
            case MeasureSpec.AT_MOST:
                parentHeight = maxHeight + getPaddingTop() + getPaddingBottom();
                break;
        }
        L.e("parentWidth = " + parentWidth + " ,parentHeight = " + parentHeight);
        setMeasuredDimension(parentWidth,parentHeight);

    }



    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        //设置当前自定义控件的LayoutParams
        return new MarginLayoutParams(getContext(),attrs);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int count = views.size();
        int currentLineMaxHeight = 0;
        int left ;
        int top ;
        int right;
        int bottom ;
        int currentLineWidth ;
        int totalHeight = getPaddingTop();
        L.i("count = " + count);
        for (int i = 0 ; i < count ; i++ ){
            ArrayList<View> lineViews = views.get(i);
            int lineCount = lineViews.size();
            //获取每一行起始的宽度
            currentLineWidth = getPaddingLeft();
            //获取每一行起始的高度
            totalHeight += currentLineMaxHeight;
            // 假设当前最大高度为0
            currentLineMaxHeight = 0;
            for (int j = 0; j < lineCount; j ++){
                View v = lineViews.get(j);
                int width = v.getMeasuredWidth();
                int height = v.getMeasuredHeight();
                MarginLayoutParams params = (MarginLayoutParams) v.getLayoutParams();
                left = currentLineWidth + params.leftMargin;
                right = left + width + params.rightMargin;
                top = params.topMargin + totalHeight;
                bottom = top + height;// + params.bottomMargin;

                currentLineWidth +=  width +  params.leftMargin + params.rightMargin;
                currentLineMaxHeight = Math.max(currentLineMaxHeight,params.topMargin + height + params.bottomMargin);

//                L.e("left = " + left + ",top = " + top+ ",right = " + right+ ",bottom = " + bottom
//                        +",currentLineWidth =" + currentLineWidth +",currentLineMaxHeight =" + currentLineMaxHeight);
                v.layout(left, top, right, bottom);
            }
        }

    }


}
