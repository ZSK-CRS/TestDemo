package cn.example.testdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.RadioGroup;

import cn.example.testdemo.view.Bt1Activity;
import cn.example.testdemo.view.Bt2Activity;
import cn.example.testdemo.view.Bt3Activity;
import cn.example.testdemo.view.camera.MapView;
import cn.example.testdemo.view.matrix.MatrixDemoOne;


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
