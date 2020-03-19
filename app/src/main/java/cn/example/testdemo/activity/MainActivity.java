package cn.example.testdemo.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import cn.example.testdemo.R;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        findViewById(R.id.bt1).setOnClickListener(view ->{
            Intent intent = new Intent(MainActivity.this, Bt1Activity.class);
            startActivity(intent);
        });

        findViewById(R.id.bt2).setOnClickListener(view ->{
            Intent intent = new Intent(MainActivity.this, Bt2Activity.class);
            startActivity(intent);
        });


        findViewById(R.id.bt3).setOnClickListener(view ->{
            Intent intent = new Intent(MainActivity.this, Bt3Activity.class);
            startActivity(intent);
        });



    }

}
