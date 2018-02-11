package com.example.daniel.cardriver;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by daniel on 2017/9/10.
 */

public class CaseQuickMap extends AppCompatActivity {

    private SQLiteQuickMap dbHelper = null;
    private ListView lv_main;
    private SimpleAdapter adapter = null;
    private List<Map<String, Object>> totalList = new ArrayList<Map<String, Object>>();
    private Toolbar mtoolbar;
    private TextView mCaseStyle, mclientlocation, mclientname;
    private Button mbtndelete;
    private String ClientName, ClientLocation, caseCid, MapCarClientDetination, CaseStyle;
    public final static String TAG = CaseQuickMap.class.getName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.casequickmap);

        dbHelper = new SQLiteQuickMap(this);
        lv_main = (ListView) findViewById(R.id.listView_main);
        totalList = getcontent();
        adapter = new SimpleAdapter(this, totalList, R.layout.item_quickmap, new String[]{"clientlocation", "clientname", "casestyle", "casecid", "clientDestination"},
                new int[]{R.id.txtitem_clientlocation, R.id.txtCaseClientname, R.id.txtcasestyle, R.id.txtitem_cid, R.id.txtitem_destination});
        lv_main.setAdapter(adapter);
        reloadView();
        lv_main.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ClientName=totalList.get(i).get("clientname").toString();
                ClientLocation=totalList.get(i).get("clientlocation").toString();
                caseCid=totalList.get(i).get("casecid").toString();
                MapCarClientDetination=totalList.get(i).get("clientDestination").toString();
                CaseStyle=totalList.get(i).get("casestyle").toString();
                if(CaseStyle.equals("快速叫車")){
                    Bundle toCaseDetail=new Bundle();
                    toCaseDetail.putString("ClientName", ClientName);
                    toCaseDetail.putString("ClientLocation", ClientLocation);
                    toCaseDetail.putString("caseCid", caseCid);
                    Intent intentDriverCaseDetail = new Intent();
                    intentDriverCaseDetail.setClass(CaseQuickMap.this, DriverCaseDetail.class);
                    intentDriverCaseDetail.putExtras(toCaseDetail);
                    startActivity(intentDriverCaseDetail);
                }
                if(CaseStyle.equals("地圖叫車")){
                    Bundle todriverMapCaseDetail=new Bundle();
                    todriverMapCaseDetail.putString("MapClientName", ClientName);
                    Log.d(TAG, ClientName);
                    todriverMapCaseDetail.putString("MapClientLocation", ClientLocation);
                    todriverMapCaseDetail.putString("MapcaseCid", caseCid);
                    todriverMapCaseDetail.putString("MapClientDestination", MapCarClientDetination);
                    todriverMapCaseDetail.putString("CaseStyle", CaseStyle );
                    Intent intentDriverMapCaseDetail = new Intent();
                    intentDriverMapCaseDetail.setClass(CaseQuickMap.this,DriverMapCarCaseDetail.class);
                    intentDriverMapCaseDetail.putExtras(todriverMapCaseDetail);
                    startActivity(intentDriverMapCaseDetail);
                }

            }
        });

        mbtndelete=(Button)findViewById(R.id.btndelete);
        mbtndelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbHelper.deleteAll();
                reloadView();
            }
        });
    }

    private List<Map<String, Object>> getcontent() {
        return dbHelper.selectList("select * from tb_quickmap", null);
    }

    protected void reloadView() {
        totalList.clear();
        totalList.addAll(getcontent());
        adapter.notifyDataSetChanged();
    }


}
