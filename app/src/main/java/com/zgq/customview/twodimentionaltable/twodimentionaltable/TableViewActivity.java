package com.zgq.customview.twodimentionaltable.twodimentionaltable;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.AppCompatSeekBar;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
    private List<String> mDateLists;

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
                seekBar.setProgress(mProgress);
                txtProces.setText(mProgress+" min");
                mAdapter.notifyDataSetChanged();
            }
        }));

        seekBar.setMax(72*24*60);
        initDate(1);

        tex.setText(mDateLists.get(mTable.getFirstColunm()));
        mTable.setOnScrollListener(new RecycleTable.OnScrollListener() {
            @Override
            public void onScrollStateChanged(int oldRow, int oldColumn, int latestRow, int latestColumn) {
                tex.setText(mDateLists.get(latestColumn));
            }
        });
        mTable.setmAdapter(mAdapter);


    }

    private void initDate(int type) {
        startTime = new DateTime(new Date());
        endTime = startTime.plusDays(3);
        DateTimeFormatter format = DateTimeFormat.forPattern("yy/MM/dd HH:mm");
        Period period = new Period(startTime,endTime,PeriodType.minutes());
        int minutes = period.getMinutes();
        mDateLists = new ArrayList<>();
        switch (type){
            case 1:
                for(int i = 1;i<= minutes; i+=1){
                    mDateLists.add(startTime.plusMinutes(i).toString(format));

                }
                break;
            case 5:
                for(int i = 5;i<= minutes; i+=5){
                    mDateLists.add(startTime.plusMinutes(i).toString(format));

                }
                break;
            case 10:
                for(int i = 10;i<= minutes; i+=10){
                    mDateLists.add(startTime.plusMinutes(i).toString(format));

                }
                break;
            case 30:
                for(int i = 30;i<= minutes; i+=30){
                    mDateLists.add(startTime.plusMinutes(i).toString(format));

                }
                break;
            case 60:
                for(int i = 60;i<= minutes; i+=60){
                    mDateLists.add(startTime.plusMinutes(i).toString(format));

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
                text1.setText(mDateLists.get(colunm));
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
