package cn.example.testdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.RadioGroup;

import cn.example.testdemo.view.matrix.MatrixDemoOne;


public class MainActivity extends AppCompatActivity {

    private RadioGroup radioGroup;
    private MatrixDemoOne matrix_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        radioGroup = findViewById(R.id.radioGroup);
        matrix_view = findViewById(R.id.matrix_view);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.button0){
                    matrix_view.setTestPoint(0);
                } else if (checkedId == R.id.button1){
                    matrix_view.setTestPoint(1);
                }else if (checkedId == R.id.button2){
                    matrix_view.setTestPoint(2);
                }else if (checkedId == R.id.button3){
                    matrix_view.setTestPoint(3);
                }else if (checkedId == R.id.button4){
                    matrix_view.setTestPoint(4);
                }
            }
        });
    }

}
