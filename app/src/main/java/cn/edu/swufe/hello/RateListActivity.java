package cn.edu.swufe.hello;

import android.app.ListActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RateListActivity extends ListActivity implements Runnable{
    String data[] = {"wait..."};
    Handler handler;
    private String logDate = "";
    private final String DATE_SP_KEY = "lastRateDateStr";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sp = getSharedPreferences("myrate",Context.MODE_PRIVATE);
        logDate = sp.getString(DATE_SP_KEY," ");
        //setContentView(R.layout.activity_rate_list);
        Thread t =  new Thread(this);
        t.start();

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what==7){
                    List<String> list2 = (List<String>)msg.obj;
                    ListAdapter adapter = new ArrayAdapter<String>(RateListActivity.this,android.R.layout.simple_list_item_1,list2);
                    setListAdapter(adapter);
                }
                super.handleMessage(msg);
            }

        };

    }

    @Override
    public void run() {
       List<String> retList = new ArrayList<String>();
       String curDateStr = (new SimpleDateFormat("yyyy-MM-dd")).format(new Date());
       if(curDateStr.equals(logDate)){
           //如果相等，则不从网络获取数据
           Log.i("run","日期相等，从数据库获取数据");
           RateManager manager = new RateManager(this);
           for(RateItem item:manager.listAll()){
               retList.add(item.getCurName()+"-->"+item.getCurRate());
           }
       }else {
           //从网络获得数据
           Log.i("run","日期不等，从网络获取数据");
           Document doc;
           try {
               Thread.sleep(2000);
               doc = Jsoup.connect("http://www.boc.cn/sourcedb/whpj/").get();
               Elements tables = doc.getElementsByTag("table");
               Element table6 = tables.get(1);
               //获取td中的数据
               Elements tds = table6.getElementsByTag("td");
               List<RateItem> rateList = new ArrayList<RateItem>();
               for (int i = 0; i < tds.size(); i += 8) {
                   Element td1 = tds.get(i);
                   Element td2 = tds.get(i + 5);
                   String str1 = td1.text();
                   String val = td2.text();
                   retList.add(str1 + "==>" + val);
                   rateList.add(new RateItem(str1,val));
               }
               //把数据写入到数据库中
               RateManager manager = new RateManager(this);
               manager.deleteAll();
               manager.addAll(rateList);

               //更新记录日期
               SharedPreferences sp = getSharedPreferences("myrate",Context.MODE_PRIVATE);
               SharedPreferences.Editor edit = sp.edit();
               edit.putString(DATE_SP_KEY,curDateStr);
               edit.commit();
               Log.i("run","更新日期结束："+curDateStr);
           } catch (IOException e) {
               e.printStackTrace();
           } catch (InterruptedException e) {
               e.printStackTrace();
           }



       }
        Message msg = handler.obtainMessage(7);
        msg.obj = retList;
        handler.sendMessage(msg);

    }
}
