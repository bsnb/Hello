package cn.edu.swufe.hello;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ThirdActivity extends AppCompatActivity implements View.OnClickListener {
    TextView out;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);
        out = findViewById(R.id.txtout4);
        Button btn1 = findViewById(R.id.btn1);
        Button btn2 = findViewById(R.id.btn2);
        Button btn3 = findViewById(R.id.btn3);
        Button btn4 = findViewById(R.id.btn4);
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int p=Integer.parseInt(out.getText().toString());
        switch(v.getId()){
            case R.id.btn1: p=p+3;break;
            case R.id.btn2: p=p+2;break;
            case R.id.btn3: p=p+1;break;
            case R.id.btn4: p=0;
            break;

        }
        out.setText(String.valueOf(p));

    }
}
