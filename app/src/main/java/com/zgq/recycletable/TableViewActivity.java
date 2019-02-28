package com.zgq.recycletable;

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


import com.zgq.recycletable.RecycleTable;

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
    private ArrayList<DateTime> mDateLists;

    private DateTime startTime;
    private DateTime endTime;
    private AppCompatSeekBar seekBar;
    private LinearLayout ll;
    private TableAdapter mAdapter;
    private int mProgress = 1;
    private int currentRow = 0;
    private int currentColumn = 0;
    private DateTime currentTime;
    private TextView tex;
    private DateTimeFormatter format;
    private DateTimeFormatter format1;
    private TextView txtProces;
    private TextView txtStartTime;

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
        initData();
        initView();
        initAdapter();
        initLisenter();
    }

    private void initLisenter() {

        mTable.setOnScrollListener(new RecycleTable.OnScrollListener() {
            @Override
            public void onScrollStateChanged(int oldRow, int oldColumn, int latestRow, int latestColumn) {
                tex.setText(mDateLists.get(latestColumn).toString(format1));
                Log.e("tag",latestRow*mProgress+"");
                seekBar.setProgress(latestColumn);
                currentTime = mDateLists.get(latestColumn);

            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int process = -1;
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                Log.e("Tag","i:"+i+"");
                process = i;

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                    mTable.setRowAndColumn(mTable.getFirstRow(),process);
                    tex.setText(mDateLists.get(mTable.getFirstColunm()).toString(format1));
                    currentTime = mDateLists.get(process);
                    Log.e("Tag","stop:process:"+process);

            }
        });
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

                initDate(mProgress,startTime);
                mAdapter.setDateTimeArrayList(mDateLists);
                int index = 0;
                for(int i = 0;i<mDateLists.size();i++){
                    Period period = new Period(mDateLists.get(i),currentTime,PeriodType.minutes());
                    int diff = period.getMinutes();
                    if(mProgress == 1){
                       index = 0;
                        break;

                    }else {
                        if(diff == 0){
                            index = i;
                            break;
                        }else if(diff > 0 && diff <= mProgress){
                            index = i;
                            continue;
                        }else if(diff > 0 && diff > mProgress){
                            continue;
                        }else {
                            break;
                        }
                    }

                }
//                int afterColumn = mDateLists.
                seekBar.setMax(mDateLists.size()-1);
                txtProces.setText(mProgress+" min");

                seekBar.setProgress(index);
                Log.e("Index","column is :"+index);
                mTable.setRowAndColumn(mTable.getFirstRow(),index);
                tex.setText(mDateLists.get(mTable.getFirstColunm()).toString(format1));
                currentTime = mDateLists.get(mTable.getFirstColunm());
            }
        }));
    }

    private void initAdapter() {
        mAdapter = new TableAdapter(mDateLists);
        mTable.setmAdapter(mAdapter);

    }

    private void initView() {
        tex = findViewById(R.id.txt_time);
        seekBar = findViewById(R.id.seekBar);
        ll = findViewById(R.id.ll_container);
        mTable = findViewById(R.id.recycletable);

        txtProces = findViewById(R.id.txt_process);
        txtStartTime = findViewById(R.id.txt_startTime);
        TextView endTime = findViewById(R.id.txt_endTime);
        format = DateTimeFormat.forPattern("MM/dd HH:mm");
        txtProces.setText(1+" min");
        currentRow = mTable.getFirstRow();
        currentColumn = mTable.getFirstColunm();

        seekBar.setMin(0);
        seekBar.setMax(mDateLists.size()-1);

        txtStartTime.setText(mDateLists.get(0).toString(format));
        endTime.setText(mDateLists.get(mDateLists.size()-1).toString(format));
        format1 = DateTimeFormat.forPattern("yyyy/MM/dd HH:mm");
        tex.setText(mDateLists.get(mTable.getFirstColunm()).toString(format1));
    }

    private void initData() {
        startTime = new DateTime(new Date());
        initDate(1,startTime);
        currentTime = mDateLists.get(currentColumn);
    }

    private void initDate(int type,DateTime startTime) {

        endTime = startTime.plusDays(1);
        format = DateTimeFormat.forPattern("yyyy/MM/dd HH:mm");
        Period period = new Period(startTime,endTime,PeriodType.minutes());
        int minutes = period.getMinutes();
        mDateLists = null;
        mDateLists = new ArrayList<>();
        switch (type){
            case 1:
                for(int i = 0;i< minutes; i+=1){
                    mDateLists.add(startTime.plusMinutes(i));
                }
                break;
            case 5:
                for(int i = 0;i< minutes; i+=5){
                    mDateLists.add(startTime.plusMinutes(i));
                }
                break;
            case 10:
                for(int i = 0;i< minutes; i+=10){
                    mDateLists.add(startTime.plusMinutes(i));
                }
                break;
            case 30:
                for(int i = 0;i< minutes; i+=30){
                    mDateLists.add(startTime.plusMinutes(i));
                }
                break;
            case 60:
                for(int i = 0;i< minutes; i+=60){
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
public class  TableAdapter extends RecycleTable.RecycleTableAdpter {
    public void setDateTimeArrayList(ArrayList<DateTime> dateTimeArrayList) {
        this.dateTimeArrayList = dateTimeArrayList;
    }

    private ArrayList<DateTime> dateTimeArrayList;

    public TableAdapter(ArrayList<DateTime> list) {
        dateTimeArrayList = list;
    }

    @Override
    public int getRowCounts() {
        return rowTitles.length;
    }

    @Override
    public int getColunmCounts() {
        return dateTimeArrayList.size();
    }

    @Override
    public int getRowHeight(int row) {
        return 150;
    }

    @Override
    public int getColunmWidth(int colunm) {
        return 250;
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
