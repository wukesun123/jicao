package com.example.design1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {
    Button button1;
    Button button2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#283593")));
        init();
    }
    public void init(){
        button1 = (Button) findViewById(R.id.process);
        button2 = (Button) findViewById(R.id.bank);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 创建意图（Intent）来跳转到 SecondActivity
                Intent intent = new Intent(MainActivity.this, Progress.class);
                // 启动跳转
                startActivity(intent);
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 创建意图（Intent）来跳转到 SecondActivity
                Intent intent = new Intent(MainActivity.this,Bank.class);
                // 启动跳转
                startActivity(intent);
            }
        });
    }
}