package com.yxd.testsideslip;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.yxd.sidesliplinearlayout.SideSlipLinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * 创建时间：2020/7/23
 * 编写人：czy_yangxudong
 * 功能描述：测试自定义SideSlipLinearLayout仿QQ侧滑item控件
 */
public class MainActivity extends AppCompatActivity {

    private List<String> list=new ArrayList<>();
    private SideSlipLinearLayout ssll;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_sideslip_linearlayout);

        RecyclerView rcv=findViewById(R.id.rcv);
        ssll = findViewById(R.id.ssll);
        Button bt_open=findViewById(R.id.bt_open);
        Button bt_delete=findViewById(R.id.bt_delete);

        ssll.setOnSideSlipStateChangedListening(new SideSlipLinearLayout.OnSideSlipStateChangedListening() {
            @Override
            public void onSideSlipStateChanged(boolean isExpand) {
                Toast.makeText(MainActivity.this,isExpand?"打开":"关闭",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSideSlip(float percent) {
                //Log.i("test","percent="+percent);
            }

            @Override
            public void onClickContent() {
                Toast.makeText(MainActivity.this,"点击",Toast.LENGTH_SHORT).show();
            }

        });

        bt_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ssll.open();
            }
        });

        bt_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"删除",Toast.LENGTH_SHORT).show();
                ssll.close();
            }
        });

        for (int i = 0; i < 20; i++) {
            list.add("条目="+i);
        }
        rcv.setLayoutManager(new LinearLayoutManager(this));
        rcv.setAdapter(new MyAdapter(this));

    }

    public class MyAdapter extends RecyclerView.Adapter  {

        private Context context;
        public MyAdapter(Context context) {
            this.context=context;
        }
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View inflate = LayoutInflater.from(context).inflate(R.layout.item, null);
            inflate.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            return new MyAdapter.ViewHolder(inflate);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            final MyAdapter.ViewHolder viewHolder = (MyAdapter.ViewHolder) holder;

            viewHolder.tv_title.setText(list.get(position));
            viewHolder.bt_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(MainActivity.this,"删除",Toast.LENGTH_SHORT).show();
                    viewHolder.ssll.close();
                }
            });
            viewHolder.ssll.setOnSideSlipStateChangedListening(new SideSlipLinearLayout.OnSideSlipStateChangedListening() {
                @Override
                public void onSideSlipStateChanged(boolean isExpand) {
                    Toast.makeText(MainActivity.this,isExpand?"打开":"关闭",Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSideSlip(float percent) {
                    //Log.i("test","percent="+percent);
                }

                @Override
                public void onClickContent() {
                    Toast.makeText(MainActivity.this,"点击",Toast.LENGTH_SHORT).show();
                }
            });
        }
        @Override
        public int getItemCount() {
            return list.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView tv_title;
            Button bt_delete;
            SideSlipLinearLayout ssll;

            public ViewHolder(View itemView) {
                super(itemView);
                tv_title=itemView.findViewById(R.id.tv_title);
                bt_delete=itemView.findViewById(R.id.bt_delete);
                ssll=itemView.findViewById(R.id.ssll);
            }
        }
    }

}