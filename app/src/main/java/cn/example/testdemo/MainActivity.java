package cn.example.testdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.widget.RadioGroup;

import cn.example.testdemo.view.camera.MapView;
import cn.example.testdemo.view.matrix.MatrixDemoOne;


public class MainActivity extends AppCompatActivity {

    private RadioGroup radioGroup;
    private MatrixDemoOne matrix_view;
    private MapView mapView;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        radioGroup = findViewById(R.id.radioGroup);
        matrix_view = findViewById(R.id.matrix_view);
        mapView = findViewById(R.id.map_view);

        ObjectAnimator animator1 = ObjectAnimator.ofFloat(mapView, "degreeY", 0, -45);
        animator1.setDuration(1000);
        animator1.setStartDelay(500);

        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mapView, "degreeZ", 0, 270);
        animator2.setDuration(800);
        animator2.setStartDelay(500);

        ObjectAnimator animator3 = ObjectAnimator.ofFloat(mapView, "fixDegreeY", 0, 30);
        animator3.setDuration(500);
        animator3.setStartDelay(500);

        final AnimatorSet set = new AnimatorSet();
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mapView.reset();
                                set.start();
                            }
                        });
                    }
                }, 500);
            }
        });
        set.playSequentially(animator1, animator2, animator3);
        set.start();

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
