package cn.edu.swufe.hello;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Hello extends AppCompatActivity implements View.OnClickListener {
    TextView out;
    EditText inp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second);
        out = findViewById(R.id.txtout2);
        inp = findViewById(R.id.inp);
        Button btn = findViewById(R.id.btn);
        btn.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        inp = findViewById(R.id.inp);
        if (TextUtils.isEmpty(inp.getText().toString()))
            Toast.makeText(Hello.this, "数字不能为空", Toast.LENGTH_SHORT).show();
        else {
            double c = Double.parseDouble(inp.getText().toString());
            double d = (9.0 * c) / 5.0 + 32.0;
            out.setText("华氏度为" + String.valueOf(d) + "℉");
        }
    }
}