package com.zgq.customview.twodimentionaltable.twodimentionaltable;

import android.content.Context;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class RecycleTable extends ViewGroup {

    private RecycleTableAdpter mAdapter;

    private int mRow;
    private int mColunm;

    private int[] widths;
    private int[] heights;

    private RecyclePool recyclerPool;
    private boolean needRelayout;
    private int touchSlop;

    private List<View> colunmTitleLists;
    private List<View> rowTitleLists;
    private List<List<View>> tableViewLists;

    private int firstRow;
    private int firstColunm;

    private View headView;
    private int width;
    private int height;

    private int scrollOffsetX;
    private int scrollOffsetY;

    private int ex;
    private int ey;


    public RecycleTable(Context context) {
        this(context,null);
    }

    public RecycleTable(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public RecycleTable(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        needRelayout = true;

        headView = null;
        colunmTitleLists = new ArrayList<>();
        rowTitleLists = new ArrayList<>();
        tableViewLists = new ArrayList<>();

        firstColunm = 0;
        firstRow = 0;
        scrollOffsetX = 0;
        scrollOffsetY = 0;
        final ViewConfiguration configuration = ViewConfiguration.get(context);
        touchSlop = configuration.getScaledTouchSlop();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean isIntercept = false;
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN: {
                ex = (int) ev.getRawX();
                ey = (int) ev.getRawY();
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                int moveX = (int) ev.getRawX();
                int moveY = (int) ev.getRawY();
                int diffX = Math.abs(moveX - ex);
                int diffY = Math.abs(moveY - ey);
                if (diffX > touchSlop || diffY > touchSlop) {

                    isIntercept = true;
                }


                break;
            }

        }
        return isIntercept;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                ex = (int) event.getRawX();
                ey = (int) event.getRawY();
                if(ex > widths[0] || ey > heights[0]){
                    return false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                final int moveX = (int) event.getRawX();
                final int moveY = (int) event.getRawY();
                final int diffX = ex - moveX;
                final int diffY = ey - moveY;
                ex = moveX;
                ey = moveY;
                scrollBy(diffX,diffY);
                break;
            case MotionEvent.ACTION_UP:
                break;
        }

        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int cellsWidth = getPaddingLeft();
        int cellsHeight = getPaddingBottom() + getPaddingTop();
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        if (mAdapter != null) {
            mRow = mAdapter.getRowCounts();
            mColunm = mAdapter.getColunmCounts();
            widths = new int[mColunm+1];
            heights = new int[mRow+1];
            //row and column title index equal -1, to distinguish with the content table index
            for (int i = -1; i < mColunm; i++) {
                widths[i+1] = mAdapter.getColunmWidth(i);
            }
            for (int j = -1; j < mRow; j++) {
                heights[j+1] = mAdapter.getRowHeight(j);
            }
            if (widthMode == MeasureSpec.AT_MOST) {
                cellsWidth += Math.min(width, sum(widths));
            } else if (widthMode == MeasureSpec.EXACTLY) {
                cellsWidth += width;
            } else {
                cellsWidth += sum(widths);
            }
            if (heightMode == MeasureSpec.AT_MOST) {
                cellsHeight += Math.min(height, sum(heights));
            } else if (heightMode == MeasureSpec.EXACTLY) {
                cellsHeight += height;

            } else {
                cellsHeight += sum(heights);
            }

            setMeasuredDimension(cellsWidth, cellsHeight);

        }

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed || needRelayout) {
            needRelayout = false;
            headView = null;
            colunmTitleLists.clear();
            rowTitleLists.clear();
            tableViewLists.clear();
            removeAllViews();
            if(mAdapter != null){
                int width = r-l;
                int height = b-t;
                int left, top, right, bottom;
                headView = createView(-1,-1,0,0,widths[0],heights[0]);
                left = widths[0];
                for(int i = firstColunm;i < mColunm && left < width;i++){
                    right = left + widths[i+1];
                    View view = createView(-1,i,left,0,right,heights[0]);
                    rowTitleLists.add(view);
                    left = right;
                }
                top = heights[0];
                for(int j = firstRow;j < mRow && top < height;j++){
                    bottom = top+heights[j+1];
                    View view = createView(j,-1,0,top,widths[0],bottom);
                    colunmTitleLists.add(view);
                    top = bottom;
                }
                top = heights[0];
                for(int i = firstRow; i < mRow && top < height;i++){
                    ArrayList<View> views = new ArrayList<>();
                    bottom = top + heights[i];
                    left = widths[0];
                    for(int j = firstColunm;j < mColunm && left < width;j++){
                        right = left + widths[j];
                        View view = createView(i,j,left,top,right,bottom);
                        views.add(view);
                        left = right;
                    }
                    tableViewLists.add(views);
                    top = bottom;

                }

            }

        }


    }

    private View createView(int row, int colunm,int left,int top,int right,int bottom) {
        int width = right - left;
        int height = bottom - top;
        View view = obtainView(row,colunm,width,height);
        view.layout(left,top,right,bottom);
        return view;
    }

    private View obtainView(int row,int column,int width,int height){
        int viewType = mAdapter.getViewType(row,column);
        View convertView = null;
        if(recyclerPool != null){
            convertView = recyclerPool.get(viewType);
        }
        View view = mAdapter.getView(row,column,convertView,this);
        view.measure(MeasureSpec.makeMeasureSpec(width,MeasureSpec.EXACTLY),MeasureSpec.makeMeasureSpec(height,MeasureSpec.EXACTLY));
        view.setTag(R.id.tag_view,viewType);
        view.setTag(R.id.tag_row,row);
        view.setTag(R.id.tag_column,column);
        addView(view);
        if(BuildConfig.DEBUG){
            Log.e("View",view.hashCode()+"");
        }
        return  view;
    }


    @Override
    public void removeView(View view) {
        super.removeView(view);
        int viewType = (int)view.getTag(R.id.tag_view);
        recyclerPool.put(view,viewType);

    }

    @Override
    public void scrollBy(int x, int y) {
        scrollOffsetX += x;
        scrollOffsetY += y;

        if(needRelayout)
            return;
        //according to the firstRow and firstColumn index to recompute the scrollOffset
        computeScrollOffset();

        if(scrollOffsetX == 0){

        }else if(scrollOffsetX > 0){//scroll from right to left
            while(widths[firstColunm+1] < scrollOffsetX){
                if(!rowTitleLists.isEmpty()){
                    //remove left
                    removeColumn(0);
                }
                scrollOffsetX -= widths[firstColunm+1];
                firstColunm++;
            }
            while(getFilledWidth() < width){
                //add right
                int rowSize = rowTitleLists.size();
                addColumn(rowSize+firstColunm,rowTitleLists.size());
            }

        }else {//scroll from left to right
            while((!rowTitleLists.isEmpty() && getFilledWidth() - widths[firstColunm+rowTitleLists.size()] > width)){
                //remove right
                removeColumn(rowTitleLists.size()-1);
            }
            while(scrollOffsetX < 0){
                //add left
                addColumn(firstColunm-1,0);
                firstColunm--;
                scrollOffsetX += widths[firstColunm + 1];

            }
        }

        if(scrollOffsetY == 0){

        }else if(scrollOffsetY > 0){//scroll from bottom to up
            while(heights[firstRow+1] < scrollOffsetY){
                if(!rowTitleLists.isEmpty()){
                    removeRow(0);
                }
                scrollOffsetY -= heights[firstRow+1];
                firstRow++;
            }

            while(getFilledHeight() < height){
                addRow(colunmTitleLists.size() + firstRow,colunmTitleLists.size());
            }

        } else{
            while(!colunmTitleLists.isEmpty() && getFilledHeight()-heights[firstRow + colunmTitleLists.size()]>height){
                removeRow(colunmTitleLists.size() - 1);
            }
            while(scrollOffsetY <0){
                addRow(firstRow-1,0);
                firstRow--;
                scrollOffsetY += heights[firstRow + 1];
            }
        }

        reLayoutView();
    }

    private void computeScrollOffset() {
        scrollOffsetX = computeScrollOffset(scrollOffsetX, firstColunm, widths, width);
        scrollOffsetY = computeScrollOffset(scrollOffsetY, firstRow, heights, height);
    }

    private int computeScrollOffset(int desiredScroll, int firstCell, int sizes[], int viewSize) {
        if (desiredScroll == 0) {
            //no offset
        } else if (desiredScroll < 0) {
            desiredScroll = Math.max(desiredScroll, -sum(sizes, 1, firstCell));
        } else {
            desiredScroll = Math.min(desiredScroll, Math.max(0, sum(sizes, firstCell + 1, sizes.length - 1 - firstCell) + sizes[0] - viewSize));
        }
        return desiredScroll;
    }

    private void reLayoutView() {
        int left , top, right, bottom,i;
        left = widths[0] - scrollOffsetX;
        int j = firstColunm;
        for(View view : rowTitleLists){
            right = left + widths[++j];
            view.layout(left,0,right,heights[0]);
            left = right;
        }
        i = firstRow;
        top = heights[0] - scrollOffsetY;
        for (View view : colunmTitleLists) {
            bottom = top + heights[++i];
            view.layout(0,top,widths[0],bottom);
            top = bottom;
        }
        top = heights[0] - scrollOffsetY;
        i = firstRow;
        for (List<View> tableViewList : tableViewLists) {
            bottom = top + heights[++i];
            j = firstColunm;
            left = widths[0] - scrollOffsetX;
            for (View view : tableViewList) {
                right = left + widths[++j];
                view.layout(left,top,right,bottom);
                left = right;
            }
            top = bottom;
        }
        invalidate();
    }

    private void addRow(int row, int index) {

        View view = obtainView(row,-1,widths[0],heights[row]);
        colunmTitleLists.add(index,view);
        ArrayList<View> list = new ArrayList<>();
        int size = firstColunm + rowTitleLists.size();
        for(int i = firstColunm;i < size;i++){
            Log.e("Tag","the add row is "+row+"the column is "+i);
            View view1 = obtainView(row,i,widths[i+1],heights[row+1]);
            list.add(view1);
        }
        tableViewLists.add(index,list);
    }

    private int getFilledHeight() {
        return heights[0] + sum(heights,firstRow + 1,colunmTitleLists.size()) - scrollOffsetY;
    }

    private void removeRow(int pos) {
        removeView(colunmTitleLists.remove(pos));
        List<View> removeView = tableViewLists.remove(pos);
        for(View view: removeView){
            removeView(view);
        }

    }

    private void addColumn(int column, int index) {
        View view = obtainView(-1,column,widths[column+1],heights[0]);
        rowTitleLists.add(index,view);
        int i = firstRow;
        for (List<View> viewList : tableViewLists) {
            View viewContent = obtainView(i,column,widths[column+1],heights[i+1]);
            viewList.add(index,viewContent);
            i++;
        }

    }

    private int getFilledWidth() {
        return  widths[0] + sum(widths,firstColunm + 1,rowTitleLists.size()) - scrollOffsetX;
    }

    private void removeColumn(int position) {
        removeView(rowTitleLists.remove(position));
        for (List<View> tableViewList : tableViewLists) {
            removeView(tableViewList.remove(position));
        }



    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        final boolean ret;

        final Integer row = (Integer) child.getTag(R.id.tag_row);
        final Integer column = (Integer) child.getTag(R.id.tag_column);
        // row == null => Shadow view
        if (row == null || (row == -1 && column == -1)) {
            ret = super.drawChild(canvas, child, drawingTime);
        } else {
            canvas.save();
            if (row == -1) {
                canvas.clipRect(widths[0], 0, canvas.getWidth(), canvas.getHeight());
            } else if (column == -1) {
                canvas.clipRect(0, heights[0], canvas.getWidth(), canvas.getHeight());
            } else {
                canvas.clipRect(widths[0], heights[0], canvas.getWidth(), canvas.getHeight());
            }

            ret = super.drawChild(canvas, child, drawingTime);
            canvas.restore();
        }
        return ret;
    }

    /**
     *
     * @param mAdapter
     */
    public void setmAdapter(RecycleTableAdpter mAdapter) {
        this.mAdapter = mAdapter;
        recyclerPool = new RecyclePool(mAdapter.getViewTypeCounts());
        firstRow = 0;
        firstColunm = 0;
        needRelayout = true;
        scrollOffsetX = 0;
        scrollOffsetY = 0;
        requestLayout();
    }

    public interface BaseAdpter {
        /**
         *
         */
        void registerDataSetObserver(RecyCleTableDataSetObsever obsever);

        /**
         *
         */
        void unRegisterDataSetObserver(RecyCleTableDataSetObsever obsever);

        /**
         *
         */
        void notifyDataSetChanged();


    }

    private int sum(int array[]) {
        return sum(array, 0, array.length);
    }

    private int sum(int array[], int start, int end) {
        int sum = 0;
        end += start;
        for (int i = start; i < end; i++) {
            sum += array[i];
        }
        return sum;
    }

    public abstract static class RecycleTableAdpter implements BaseAdpter{
        final DataSetObservable mObsever = new DataSetObservable();

        @Override
        public void unRegisterDataSetObserver(RecyCleTableDataSetObsever obsever) {
            mObsever.unregisterObserver(obsever);

        }

        @Override
        public void registerDataSetObserver(RecyCleTableDataSetObsever obsever) {
            mObsever.registerObserver(obsever);
        }

        @Override
        public void notifyDataSetChanged() {
            mObsever.notifyChanged();
        }

        /**
         * @return
         */
        public abstract int getRowCounts();

        /**
         * @return
         */
        public abstract int getColunmCounts();

        /**
         * @param row
         * @return
         */
        public abstract int getRowHeight(int row);

        /**
         * @param colunm
         * @return
         */
        public abstract int getColunmWidth(int colunm);

        /**
         *
         * @param row
         * @param colunm
         * @param parent
         * @return
         */
        public abstract View getView(int row, int colunm,View convertView, ViewGroup parent);

        /**
         *
         * @param row
         * @param colunm
         * @return
         */
        public abstract int getViewType(int row,int colunm);

        /**
         *
         * @return
         */
        public abstract int getViewTypeCounts();


    }

   private class RecyCleTableDataSetObsever extends DataSetObserver{
       @Override
       public void onChanged() {
           needRelayout = true;
           requestLayout();
       }

       @Override
       public void onInvalidated() {

       }
   }

   class RecyclePool {

        private Stack<View>[] poolViews;
        private int mSize;

        public RecyclePool(int length) {
            mSize = length;
            poolViews = new Stack[length];
            for(int i = 0; i<length; i++) {
                poolViews[i] = new Stack<>();
            }
        }

        public void put(View view, int type){
            poolViews[type].push(view);
        }

        public View get(int type){
            if(poolViews[type].size()<=0){
                return null;
            }
            return poolViews[type].pop();
        }
    }

}
