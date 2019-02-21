package com.zgq.customview.twodimentionaltable.twodimentionaltable;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.zgq.customview.twodimentionaltable.twodimentionaltable.RecycleTable.RecycleTableAdpter;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TableViewActivity extends Activity {

    private RecycleTable mTable;
    private List<DateTime> mDateLists;

    private DateTime startTime;
    private DateTime endTime;
    private AppCompatSeekBar seekBar;
    private LinearLayout ll;
    private TableAdapter mAdapter;
    private int mProgress = 1;

    private String rowTitles[] = {"HR","VPC/M","ST(I)","ST(II","ST(III",
                                  "ST(aVR)","ST(aVL)","ST(aVF)","ST(V1)","ST(V2)",
                                "ST(V3)","ST(V4)","ST(V5)", "ST(V6)","RR(IMP)",
                                "SpO2","T1","T2","Tb","ART SYS",
                                "ART DIV","ART MEAN","PAP SYS","PAP DIV","PAP MEAN",
                                "CVP MAX","CVP MIN","CVP MEAN","ICP MAX","ICP MIN",
                                "ICP MEAN"};

    private int rowColors[] = {Color.GREEN,Color.GREEN,Color.GREEN,Color.GREEN,Color.GREEN,
            Color.GREEN,Color.GREEN,Color.GREEN,Color.GREEN,Color.GREEN,
            Color.GREEN,Color.GREEN,Color.GREEN,Color.GREEN,Color.WHITE,
            Color.parseColor("#04C5Fb"),Color.parseColor("#F88D24"),Color.YELLOW,Color.parseColor("#F88D24"), Color.RED,
            Color.RED,Color.RED,Color.YELLOW,Color.YELLOW,Color.YELLOW,
            Color.parseColor("#8A8AEC"),Color.parseColor("#8A8AEC"),Color.parseColor("#8A8AEC"),Color.YELLOW,Color.YELLOW,
            Color.YELLOW};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycletable);
        mAdapter = new TableAdapter();
        final TextView tex = findViewById(R.id.txt_time);
        seekBar = findViewById(R.id.seekBar);
        ll = findViewById(R.id.ll_container);
        mTable = findViewById(R.id.recycletable);
       final TextView txtProces = findViewById(R.id.txt_process);
       TextView startTime = findViewById(R.id.txt_startTime);
        TextView endTime = findViewById(R.id.txt_endTime);
        final DateTimeFormatter format = DateTimeFormat.forPattern("MM/dd HH:mm");

       txtProces.setText(1+" min");
        ll.setOnTouchListener(new DoubleClickLinstenner(new DoubleClickLinstenner.DoubleClickCallback() {
            @Override
            public void onDoubleClick() {
                switch (mProgress){
                    case 1:
                        mProgress = 5;
                        break;
                    case 5:
                        mProgress = 10;
                        break;
                    case 10:
                        mProgress = 30;
                        break;
                    case 30:
                        mProgress = 60;
                        break;
                    case 60:
                        mProgress = 1;
                        break;
                    default:
                            mProgress = 1;
                            break;
                }
                initDate(mProgress);
                txtProces.setText(mProgress+" min");
                mAdapter.notifyDataSetChanged();
            }
        }));
        seekBar.setMin(1);
        seekBar.setMax(72*60);
        initDate(1);
        startTime.setText(mDateLists.get(0).toString(format));
        endTime.setText(mDateLists.get(mDateLists.size()-1).toString(format));
        final DateTimeFormatter format1 = DateTimeFormat.forPattern("yyyy/MM/dd HH:mm");
        tex.setText(mDateLists.get(mTable.getFirstColunm()).toString(format1));
        mTable.setOnScrollListener(new RecycleTable.OnScrollListener() {
            @Override
            public void onScrollStateChanged(int oldRow, int oldColumn, int latestRow, int latestColumn) {
                tex.setText(mDateLists.get(latestColumn).toString(format1));
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int process = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(i%mProgress == 0){
                    process = i;
//                    Log.e("Tag",i+"");
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mTable.setRowAndColumn(mTable.getFirstRow(),process/mProgress-1);
                tex.setText(mDateLists.get(process/mProgress-1).toString(format1));
            }
        });
        mTable.setmAdapter(mAdapter);


    }

    private void initDate(int type) {
        startTime = new DateTime(new Date());
        endTime = startTime.plusDays(3);
        DateTimeFormatter format = DateTimeFormat.forPattern("yyyy/MM/dd HH:mm");
        Period period = new Period(startTime,endTime,PeriodType.minutes());
        int minutes = period.getMinutes();
        mDateLists = new ArrayList<>();
        switch (type){
            case 1:
                for(int i = 1;i<= minutes; i+=1){
                    mDateLists.add(startTime.plusMinutes(i));
                }
                break;
            case 5:
                for(int i = 5;i<= minutes; i+=5){
                    mDateLists.add(startTime.plusMinutes(i));
                }
                break;
            case 10:
                for(int i = 10;i<= minutes; i+=10){
                    mDateLists.add(startTime.plusMinutes(i));
                }
                break;
            case 30:
                for(int i = 30;i<= minutes; i+=30){
                    mDateLists.add(startTime.plusMinutes(i));
                }
                break;
            case 60:
                for(int i = 60;i<= minutes; i+=60){
                    mDateLists.add(startTime.plusMinutes(i));
                }
                break;
            default:
                break;

        }





    }

    private String getTitle(int row){

        return rowTitles[row];

    }
public class  TableAdapter extends RecycleTableAdpter{
    @Override
    public int getRowCounts() {
        return rowTitles.length;
    }

    @Override
    public int getColunmCounts() {
        return mDateLists.size();
    }

    @Override
    public int getRowHeight(int row) {
        return 100;
    }

    @Override
    public int getColunmWidth(int colunm) {
        return 210;
    }

    @Override
    public View getView(int row, int colunm, View convertView, final ViewGroup parent) {
        switch (getViewType(row,colunm)){
            case 0:
                if(convertView == null){
                    convertView = getLayoutInflater().inflate(R.layout.item_table_header_column,parent,false);
                }
                TextView text = convertView.findViewById(R.id.text_column);
                text.setText("Time");
                text.setTextColor(Color.WHITE);

                break;
            case 1:
                if(convertView == null){
                    convertView = getLayoutInflater().inflate(R.layout.item_table_header_column,parent,false);
                }
                TextView text1 = convertView.findViewById(R.id.text_column);
                DateTimeFormatter format = DateTimeFormat.forPattern("yy/MM/dd HH:mm");
                text1.setText(mDateLists.get(colunm).toString(format));
                text1.setTextColor(Color.WHITE);


                break;
            case 2:
                if(convertView == null){
                    convertView = getLayoutInflater().inflate(R.layout.item_table_header_row,parent,false);
                }
                TextView text3 = convertView.findViewById(R.id.text_row);
                text3.setText(getTitle(row));
                text3.setTextColor(rowColors[row]);

                break;
            case 3:
                if(convertView == null){
                    convertView = getLayoutInflater().inflate(R.layout.item_table,parent,false);
                }
                TextView text2 = convertView.findViewById(R.id.text_content);
                text2.setText("("+row+"，"+colunm+")");
                final String str = "("+row+"，"+colunm+")";
                text2.setTextColor(rowColors[row]);
//                        text2.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                Toast.makeText(parent.getContext(),str,Toast.LENGTH_SHORT).show();
//                            }
//                        });
                break;
        }
        return convertView;
    }

    @Override
    public int getViewType(int row, int colunm) {
        int viewType;
        if(row == -1&&colunm==-1)
            viewType = 0;
        else if(row == -1)
            viewType = 1;
        else if(colunm == -1)
            viewType = 2;
        else
            viewType = 3;

        return viewType;
    }

    @Override
    public int getViewTypeCounts() {
        return 4;
    }



}


}
