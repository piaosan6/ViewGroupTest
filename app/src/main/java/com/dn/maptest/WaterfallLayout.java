package com.dn.maptest;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.Arrays;

/**
 *  瀑布流布局
 */

public class WaterfallLayout extends ViewGroup {

    private static final int DEFAULT_COLUMNS = 1;
    private int numColumns ;
    private int hSpacing;
    private int vSpacing;
    //    private int currentColumn;
    private int[] heights;

    public WaterfallLayout(Context context) {
        this(context, null);
    }

    public WaterfallLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }


    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.WaterfallLayout);
        numColumns = typedArray.getInt(R.styleable.WaterfallLayout_numColumns,DEFAULT_COLUMNS);
        setNumColumns(numColumns);
        hSpacing = typedArray.getDimensionPixelOffset(R.styleable.WaterfallLayout_horizontalSpacing,0);
        setHorizontalSpacing(hSpacing);
        vSpacing = typedArray.getDimensionPixelOffset(R.styleable.WaterfallLayout_verticalSpacing,0);
        setVerticalSpacing(vSpacing);
        typedArray.recycle();
        heights = new int[numColumns];
    }

    public int getNumColumns() {
        return numColumns;
    }

    public void setNumColumns(int numColumns) {
        this.numColumns = numColumns;
        heights = new int[numColumns];
    }

    public int getHorizontalSpacing() {
        return hSpacing;
    }

    public void setHorizontalSpacing(int hSpacing) {
        this.hSpacing = hSpacing;
    }

    public float getVerticalSpacing() {
        return vSpacing;
    }

    public void setVerticalSpacing(int vSpacing) {
        this.vSpacing = vSpacing;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        //int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        //得到总宽度
        int measureWidth = 0;
        int measureHeight = 0;

        clearHeights();
        int childWidth;
        int count = getChildCount() ;
        if(widthMode == MeasureSpec.EXACTLY){
            measureWidth = widthSize;
            measureHeight = heightSize;
            childWidth = (widthSize - (numColumns -1) * hSpacing) / numColumns;
            L.e("测量的宽度 = " + widthSize);
        }else {
            childWidth = (widthSize - (numColumns -1) * hSpacing) / numColumns;
            if(count < numColumns){
                measureWidth = count * childWidth + (count - 1) * hSpacing;
            }else {
                measureWidth = widthSize;
            }
        }
        // 这里需要以每行的宽度来测试child,每行的宽度是通过numColumns来计算
        measureChildren(childWidth,heightMeasureSpec);
        clearHeights();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            //计算子view的高度，因为这里的宽度是固定的，如果需要根据实际view的宽高比，重新计算出缩放（扩大）之后的view的高度。
            int childHeight = childWidth * child.getMeasuredHeight() / child.getMeasuredWidth();
            L.e("MeasuredWidth() = " + child.getMeasuredWidth() + " ,MeasuredHeight = " + child.getMeasuredHeight()
                    + ",calc childWidth = " +childWidth + ", calc childHeight = " + childHeight);
            // 获取当前高度最小的列
            int currentColumn = getCurrentMinHeightColumn();
            // 通过WaterfallLayoutParams 来设置view的具体位置
            WaterfallLayoutParams params = (WaterfallLayoutParams) child.getLayoutParams();
            params.left = currentColumn * (childWidth + hSpacing);
            params.right = params.left + childWidth ;
            int minHeight = heights[currentColumn];
            params.top = minHeight;
            params.bottom = params.top + childHeight;
            heights[currentColumn] = minHeight + vSpacing + childHeight;
            //L.e(currentColumn + ", left=" +  params.left + ", top=" + params.top + ", right=" + params.right + ", bottom=" + params.bottom + ",width = " + childWidth + ", height = " + childHeight );
            if(i == count -1){
                measureHeight = getMaxHeight();
            }
        }

        setMeasuredDimension(measureWidth, measureHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int count = getChildCount();
        clearHeights();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            WaterfallLayoutParams params = (WaterfallLayoutParams) child.getLayoutParams();
            child.layout(params.left, params.top, params.right, params.bottom);
            L.e("摆放：" + params.left + ", " + params.top + ", " + params.right  + ", " + params.bottom );
        }
    }

    private int getMaxHeight() {
        int maxHeight = heights[0];
        for(int i = 1; i < numColumns; i++){
            if(heights[i] > maxHeight){
                maxHeight = heights[i];
            }
        }
        return maxHeight;
    }

    /**
     *  获取当前列高度最低的列
     * @return
     */
    private int getCurrentMinHeightColumn() {
        int result = 0;
        int minHeight = heights[0];
        for(int i = 1; i< numColumns; i++){
            if(heights[i] < minHeight){
                minHeight = heights[i];
                result = i;
            }
        }
        return result;
    }

    public void clearHeights(){
        Arrays.fill(heights,0);
    }


    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new WaterfallLayoutParams(getContext(),attrs);
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new WaterfallLayoutParams(p);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new WaterfallLayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof WaterfallLayoutParams;
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int index);
    }

    public void setOnItemClickListener(final OnItemClickListener listener) {
        for (int i = 0; i < getChildCount(); i++) {
            final int index = i;
            View view = getChildAt(i);
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(v, index);
                }
            });
        }
    }


    public static class WaterfallLayoutParams extends ViewGroup.LayoutParams{

        private int left;
        private int top;
        private int right;
        private int bottom;

        public WaterfallLayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public WaterfallLayoutParams(int width, int height) {
            super(width, height);
        }

        public WaterfallLayoutParams(LayoutParams source) {
            super(source);
        }

    }



}
