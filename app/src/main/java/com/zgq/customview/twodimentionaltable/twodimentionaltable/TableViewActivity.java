package com.zgq.customview.twodimentionaltable.twodimentionaltable;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.zgq.customview.twodimentionaltable.twodimentionaltable.RecycleTable.RecycleTableAdpter;

public class TableViewActivity extends Activity {

    private RecycleTable mTable;
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
        mTable = findViewById(R.id.recycletable);
        mTable.setmAdapter(new RecycleTableAdpter(){
            @Override
            public int getRowCounts() {
                return rowTitles.length;
            }

            @Override
            public int getColunmCounts() {
                return 20;
            }

            @Override
            public int getRowHeight(int row) {
                return 100;
            }

            @Override
            public int getColunmWidth(int colunm) {
                return 200;
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
                        text1.setText("第"+colunm+"列");
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
                        text2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(parent.getContext(),str,Toast.LENGTH_SHORT).show();
                            }
                        });
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
        } );


    }
    private String getTitle(int row){

        return rowTitles[row];

    }
}
