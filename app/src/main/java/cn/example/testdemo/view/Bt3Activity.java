package cn.example.testdemo.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.example.testdemo.MainActivity;
import cn.example.testdemo.R;
import cn.example.testdemo.view.circle.CircleMenuAdapter;
import cn.example.testdemo.view.circle.CircleMenuItem;
import cn.example.testdemo.view.circle.CircleNavication;

public class Bt3Activity extends AppCompatActivity {

    private CircleNavication circleMenuGroup;
    private List<CircleMenuItem> mMenuItems = new ArrayList<CircleMenuItem>();
    private String[] mItemTexts = new String[] { "安全中心 ", "特色服务", "投资理财",
            "转账汇款", "我的账户", "信用卡" };
    private int[] mItemImgs = new int[] { R.drawable.home_mbank_1_normal,
            R.drawable.home_mbank_2_normal, R.drawable.home_mbank_3_normal,
            R.drawable.home_mbank_4_normal, R.drawable.home_mbank_5_normal,
            R.drawable.home_mbank_6_normal };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bt3);
        initData(mItemTexts, mItemImgs);
        //中心视图
        View centerView = LayoutInflater.from(this).inflate(R.layout.circle_menu_item_center,null,false);
        centerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Bt3Activity.this,
                        "you click centerView",
                        Toast.LENGTH_SHORT).show();
            }
        });
        circleMenuGroup = (CircleNavication)findViewById(R.id.circlemenu);
        circleMenuGroup.setAdapter(new CircleMenuAdapter(mMenuItems));
        circleMenuGroup.setCenterView(centerView);
        circleMenuGroup.setOnNavigationItemClickListener((view, pos) -> Toast.makeText(Bt3Activity.this, mItemTexts[pos],
                Toast.LENGTH_SHORT).show());
    }

    private void initData(String[] mItemTexts, int[] mItemImgs) {
        if (mItemImgs==null && mItemTexts==null){
            throw new IllegalArgumentException("文本和图片不能为空");
        }
        int count = mItemImgs==null ? mItemTexts.length: mItemImgs.length;
        if (mItemImgs!=null && mItemTexts!=null){
            count = Math.min(mItemImgs.length,mItemTexts.length);
        }

        for (int i=0;i<count;i++){
            mMenuItems.add(new CircleMenuItem(mItemImgs[i],mItemTexts[i]));
        }
    }
}
