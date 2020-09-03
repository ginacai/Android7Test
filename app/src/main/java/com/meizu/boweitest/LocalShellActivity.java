package com.meizu.boweitest;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LocalShellActivity extends AppCompatActivity {

    private boolean isSdCardExist;
    private String sdpath;
    private List<String> shPath = new ArrayList<String>();//sh文件的路径
    private static String[] shFormatSet = new String[]{"sh"};//合法的sh文件格式
    private String repeatTimes;
    //判断是否为sh文件
    private  static boolean isShFile(String path){
        for(String format:shFormatSet){//遍历数组
            if(path.contains(format))//判断是否为sh文件
            {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_shell);


        isSdCardExist = Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);// 判断sdcard是否存在
        sdpath = Environment.getExternalStorageDirectory()+"/Shell"; //获得SD卡的路径
        getFiles(sdpath);//调用getFiles方法获取路径下的全部sh文件
        if(shPath.size()<1){//如果不存在sh文件
            return;
        }

    }
    public void getFiles(String url){
        File files = new File(url);
        File[] file = files.listFiles();
        try {
            for (File f: file){
                if(f.isDirectory()){//如果是目录
                    getFiles(f.getAbsolutePath());//递归调用
                }else {
                    if(isShFile(f.getPath())){//如果是图片文件
                        shPath.add(f.getPath());//将文件加入list集合中

                        //填充listview
                        final ListView listView = findViewById(R.id.MyListView);//在视图中找到ListView
                        int sum = shPath.size();
                        //生成动态数组，用于adapter中转载数据
                        final ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
                        for (int i = 0; i < sum; i++) {
                            //装载脚本名称
                            HashMap<String, String> map = new HashMap<String, String>();
                            map.put("ItemTitle", shPath.get(i));
                            mylist.add(map);

                            //创建listview适配器adapter，并在其中添加按钮监控
                            final SimpleAdapter mSchedule = new SimpleAdapter(LocalShellActivity.this,
                                    mylist,//数据来源
                                    R.layout.my_listitem,//ListItem的XML实现
                                    //动态数组与ListItem对应的子项
                                    new String[]{"ItemTitle"},
                                    //ListItem的XML文件里面的两个TextView ID

                                    new int[]{R.id.ItemTitle}){
                                public View getView(int position, View convertView, ViewGroup parent) {
                                    View view = super.getView(position, convertView, parent);
                                    TextView itemScriptTitle = view.findViewById(R.id.ItemTitle);
                                    Button itemExecute = view.findViewById(R.id.CameraScriptExecute);
                                    itemScriptTitle.setOnClickListener(new TitleOnClickListener(shPath.get(position)));
                                    itemExecute.setOnClickListener(new ExecuteOnClickListener(shPath.get(position)));
                                    return view;
                                }
                            };
                            LocalShellActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //添加并且显示
                                    listView.setAdapter(mSchedule);
                                }
                            });

                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void Readme(View view) {
        new AlertDialog.Builder(LocalShellActivity.this).setTitle("功能说明")
                .setMessage("将.sh文件放到sdard/Shell目录中就可以执行")
                .setPositiveButton("确认",null)
                .show();




    }


    public class TitleOnClickListener implements View.OnClickListener{
        String Variable;
        public TitleOnClickListener(String Variable) {
            this.Variable = Variable;
        }
        @Override
        public void onClick(View v)
        {
            // TODO read your lovely variable
            Toast.makeText(LocalShellActivity.this, Variable, Toast.LENGTH_SHORT).show();
        }
    }

    public class ExecuteOnClickListener implements View.OnClickListener{
        String Variable;
        public ExecuteOnClickListener(String Variable) {
            this.Variable = Variable;
        }
        @Override
        public void onClick(View v)
        {
            try {
                EditText editText=findViewById(R.id.et);
                repeatTimes = editText.getText().toString();
                Toast.makeText(LocalShellActivity.this,"执行"+Variable+"脚本"+repeatTimes+"次",Toast.LENGTH_SHORT).show();

                //执行脚本
                Runtime.getRuntime().exec("chmod 777 " + Variable);
                Runtime.getRuntime().exec("/system/bin/sh " + Variable + " " + repeatTimes);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
