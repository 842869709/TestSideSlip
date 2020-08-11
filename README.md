# SideSlipLinearLayout
自定义仿QQ列表侧滑item View


## 示例图片
图片如果不展示请出国即可
图片做了帧率压缩，所以有卡顿，实际效果顺滑

![](https://github.com/842869709/TestSideSlip/blob/master/test.gif)


## 1.用法
使用前，对于Android Studio的用户，可以选择添加:

方法一：Gradle： 在dependencies中添加引用：
```
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
	
	dependencies {
	        implementation 'com.github.842869709:TestSideSlip:Tag'
	}
```
方法二：Maven仓库
```
<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>
```
```
<dependency>
	    <groupId>com.github.842869709</groupId>
	    <artifactId>TestSideSlip</artifactId>
	    <version>Tag</version>
	</dependency>
```

## 2.功能参数与含义
配置参数|参数含义|描述
-|-|-|-
open|	打开item|	无
close|	关闭item|	无
setOnSideSlipStateChangedListening|	设置状态监听|	展开，关闭，滑动的百分比，点击事件



## 3.代码参考
布局文件item
SideSlipLinearLayout内包裹两个子view 上面的放主布局，下面的放需要滑动展示出来的布局

```
<?xml version="1.0" encoding="utf-8"?>
<com.yxd.sidesliplinearlayout.SideSlipLinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:orientation="horizontal" android:layout_width="match_parent"
    android:layout_height="70dp"
    android:paddingBottom="1dp"
    android:id="@+id/ssll"
    android:gravity="center_vertical">
        <TextView
            android:id="@+id/tv_title"
            android:layout_width="match_parent"
            android:layout_height="70dp"
            android:text="A"
            android:textColor="#fff"
            android:textSize="45sp"
            android:gravity="center_vertical"
            android:paddingLeft="10dp"
            android:visibility="visible"
            android:background="@color/colorAccent"/>

        <Button
            android:id="@+id/bt_delete"
            android:layout_width="90dp"
            android:layout_height="70dp"
            android:background="#f00"
            android:gravity="center"
            android:text="删除"
            android:textColor="#fff"
            android:textSize="25sp"
            android:visibility="visible"/>

</com.yxd.sidesliplinearlayout.SideSlipLinearLayout>
```
示例
```
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:orientation="vertical"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <include layout="@layout/item"/>

    <Button
        android:id="@+id/bt_open"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="打开单item"
        android:visibility="visible"/>



    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/rcv"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:visibility="visible"/>

</LinearLayout>
```

配置及初始化
```
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

```
## v1.0.0 初始化提交
