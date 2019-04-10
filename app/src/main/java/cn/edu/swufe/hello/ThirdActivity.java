package cn.edu.swufe.hello;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ThirdActivity extends AppCompatActivity implements View.OnClickListener {
    TextView out;
    TextView outb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);
        out = findViewById(R.id.txtout4);
        Button btn1 = findViewById(R.id.btn1);
        Button btn2 = findViewById(R.id.btn2);
        Button btn3 = findViewById(R.id.btn3);
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        outb = findViewById(R.id.txtout4b);
        Button btn1b = findViewById(R.id.btn1b);
        Button btn2b = findViewById(R.id.btn2b);
        Button btn3b = findViewById(R.id.btn3b);
        Button btn4b = findViewById(R.id.btn4b);
        btn1b.setOnClickListener(this);
        btn2b.setOnClickListener(this);
        btn3b.setOnClickListener(this);
        btn4b.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int p=Integer.parseInt(out.getText().toString());
        int q=Integer.parseInt(outb.getText().toString());

        switch(v.getId()){
            case R.id.btn1: out.setText(String.valueOf(p+3));break;
            case R.id.btn2: out.setText(String.valueOf(p+2));break;
            case R.id.btn3: out.setText(String.valueOf(p+1));break;
            case R.id.btn1b: outb.setText(String.valueOf(q+3));break;
            case R.id.btn2b: outb.setText(String.valueOf(q+2));break;
            case R.id.btn3b: outb.setText(String.valueOf(q+1));break;
            case R.id.btn4b: outb.setText(String.valueOf(0));out.setText(String.valueOf(0));break;

        }


    }
}
