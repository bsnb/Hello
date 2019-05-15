package cn.edu.swufe.hello;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class RateActivity extends AppCompatActivity implements Runnable {
    EditText rmb;
    TextView show;
    Handler handler;
    private float dollarRate = 0.1f;
    float euroRate = 0.2f;
    float wonRate = 0.3f;
    private final String TAG = "Rate";
    private String update = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);
        rmb = findViewById(R.id.rmb);
        show = findViewById(R.id.showOut);
        SharedPreferences sharedPreferences = getSharedPreferences("myrate", Activity.MODE_PRIVATE);
        PreferenceManager.getDefaultSharedPreferences(this);
        dollarRate = sharedPreferences.getFloat("dollar_rate", 0.0f);
        euroRate = sharedPreferences.getFloat("euro_rate", 0.0f);
        wonRate = sharedPreferences.getFloat("won_rate", 0.0f);
        update = sharedPreferences.getString("update_date","");

        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        final String todaystr = sdf.format(today);

        if(!todaystr.equals(update)){
            Thread t = new Thread(this);
            t.start();
        }


        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what==5){
                    Bundle bdl = (Bundle) msg.obj;
                    dollarRate = bdl.getFloat("dollar-rate");
                    euroRate = bdl.getFloat("euro-rate");
                    wonRate = bdl.getFloat("won-rate");
                    SharedPreferences sharedPreferences = getSharedPreferences("myrate", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("update_date",todaystr);
                    editor.putFloat("dollar_rate", dollarRate);
                    editor.putFloat("euro_rate", euroRate);
                    editor.putFloat("won_rate", wonRate);
                    editor.commit();
                    Toast.makeText(RateActivity.this,"汇率已更新",Toast.LENGTH_SHORT).show();
                }
                super.handleMessage(msg);
            }
        };


    }

    public void onClick(View btn) {
        String str = rmb.getText().toString();
        float r = 0;
        if (str.length() > 0) {
            r = Float.parseFloat(str);
        } else {
            Toast.makeText(RateActivity.this, "请输入金额", Toast.LENGTH_SHORT).show();
        }
        if (btn.getId() == R.id.btn_dollar) {
            show.setText(String.format("%.2f", r * dollarRate));
        } else if (btn.getId() == R.id.btn_euro) {
            show.setText(String.format("%.2f", r * euroRate));
        } else {
            show.setText(String.format("%.2f", r * wonRate));
        }
    }

    public void openOne(View btn) {
        openConfig();

    }

    private void openConfig() {
        Intent config = new Intent(this, ConfigActivity.class);
        config.putExtra("dollar_rate_key", dollarRate);
        config.putExtra("euro_rate_key", euroRate);
        config.putExtra("won_rate_key", wonRate);
        //startActivity(config);
        startActivityForResult(config, 1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.rate, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_set) {
            openConfig();
        }else if(item.getItemId()==R.id.open_list){
            Intent list = new Intent(this,MylistActivity.class);
            startActivity(list);
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 2) {
            Bundle bundle = data.getExtras();
            dollarRate = bundle.getFloat("key_dollar", 0.1f);
            euroRate = bundle.getFloat("key_euro", 0.1f);
            wonRate = bundle.getFloat("key_won", 0.1f);
            SharedPreferences sharedPreferences = getSharedPreferences("myrate", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putFloat("dollar_rate", dollarRate);
            editor.putFloat("euro_rate", euroRate);
            editor.putFloat("won_rate", wonRate);
            editor.commit();
        }
    }

    @Override
    public void run() {
        for (int i = 1; i < 3; i++) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Object bundle = new Bundle();

    //获取网络数据
       /* URL url = null;
        try {
            url = new URL("http://www.used-cny.com/bankofchina.htm");
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            InputStream in = http.getInputStream();
            String html = inputStream2String(in);
            Document doc = Jsoup.parse(html);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/


        Document doc = null;
        bundle = getFromBOC();
        Message msg = handler.obtainMessage(5);
        //msg.what = 5;
        msg.obj = bundle;
        handler.sendMessage(msg);
    }

    private Bundle getFromUsdCny() {
        Bundle bundle= new Bundle();
        Document doc;
        try {
            doc = Jsoup.connect("http://www.usd-cny.com/bankofchina.htm").get();
            Elements tables = doc.getElementsByTag("table");

           /* for (Element table:tables){
                Log.i(TAG,"run:table["+i+"]="+table);
                i++;
            }*/
            Element table6 = tables.get(0);
            //获取td中的数据
            Elements tds = table6.getElementsByTag("td");
            for(int i = 0;i<tds.size();i+=6){
                Element td1 = tds.get(i);
                Element td2 = tds.get(i+5);
                String str1 = td1.text();
                String val = td2.text();
                if ("美元".equals(str1) ){
                    bundle.putFloat("dollar-rate",100f/Float.parseFloat(val));
                }
                else if ("欧元".equals(str1)) {
                    bundle.putFloat("euro-rate",100f/Float.parseFloat(val));
                }
                else if("韩元".equals(str1)) {
                    bundle.putFloat("won-rate",100f/Float.parseFloat(val));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bundle;
    }
    private Bundle getFromBOC() {
        Bundle bundle= new Bundle();
        Document doc;
        try {
            doc = Jsoup.connect("http://www.boc.cn/sourcedb/whpj/").get();
            Elements tables = doc.getElementsByTag("table");

           /* for (Element table:tables){
                Log.i(TAG,"run:table["+i+"]="+table);
                i++;
            }*/
            Element table6 = tables.get(1);
            //获取td中的数据
            Elements tds = table6.getElementsByTag("td");
            for(int i = 0;i<tds.size();i+=8){
                Element td1 = tds.get(i);
                Element td2 = tds.get(i+5);
                String str1 = td1.text();
                String val = td2.text();
                if ("美元".equals(str1) ){
                    bundle.putFloat("dollar-rate",100f/Float.parseFloat(val));
                }
                else if ("欧元".equals(str1)) {
                    bundle.putFloat("euro-rate",100f/Float.parseFloat(val));
                }
                else if("韩国元".equals(str1)) {
                    bundle.putFloat("won-rate",100f/Float.parseFloat(val));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bundle;
    }

    private String inputStream2String(InputStream inputStream) throws IOException {
        final  int bufferSize = 1024;
        final char[] buffer = new char[bufferSize];
        final StringBuilder out = new StringBuilder();
        Reader in = new InputStreamReader(inputStream,"gb2312");
        for ( ; ; ){
            int rsz = in.read(buffer,0,buffer.length);
            if (rsz<0)
                break;;
                out.append(buffer,0,rsz);
        }
        return out.toString();



    }

}
