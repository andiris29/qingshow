package com.focosee.qingshow.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import com.focosee.qingshow.R;
import com.focosee.qingshow.adapter.CityListAdapter;
import com.focosee.qingshow.model.Cityinfo;
import com.focosee.qingshow.util.FileUtil;
import com.focosee.qingshow.widget.QSTextView;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

public class CityActivity extends Activity {

    @InjectView(R.id.left_btn)
    ImageButton leftBtn;
    @InjectView(R.id.title)
    QSTextView title;
    @InjectView(R.id.city_list_provice)
    ListView cityListProvice;

    private List<Cityinfo> province_list = new ArrayList<>();
    private HashMap<String, List<Cityinfo>> city_map = new HashMap<>();
    private HashMap<String, List<Cityinfo>> couny_map = new HashMap<>();

    private StringBuilder result = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city);
        ButterKnife.inject(this);
        title.setText("选择地址");
        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getaddressinfo();

        final CityListAdapter adapter = new CityListAdapter(province_list, this);
        cityListProvice.setAdapter(adapter);
        cityListProvice.setMinimumHeight(100);

        cityListProvice.setOnItemClickListener(new AdapterView.OnItemClickListener() {//省
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                result.append(adapter.getDatas().get(position).getCity_name());
                title.setText(adapter.getDatas().get(position).getCity_name());
                adapter.setDatas(city_map.get(adapter.getDatas().get(position).getId()));
                adapter.notifyDataSetChanged();
                cityListProvice.setOnItemClickListener(new AdapterView.OnItemClickListener() {//市
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        result.append(adapter.getDatas().get(position).getCity_name());
                        title.setText(adapter.getDatas().get(position).getCity_name());
                        adapter.setDatas(couny_map.get(adapter.getDatas().get(position).getId()));
                        adapter.notifyDataSetChanged();
                        cityListProvice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                result.append(adapter.getDatas().get(position).getCity_name());
                                EventBus.getDefault().post(new CityEvent(result.toString()));
                                finish();
                            }
                        });
                    }
                });
            }
        });
    }

    // 获取城市信息
    private void getaddressinfo() {
        // 读取城市信息string
        JSONParser parser = new JSONParser();
        String area_str = FileUtil.readAssets(CityActivity.this, "area.json");
        province_list = parser.getJSONParserResult(area_str, "area0");
        city_map = parser.getJSONParserResultArray(area_str, "area1");
        couny_map = parser.getJSONParserResultArray(area_str, "area2");
    }

    public static class JSONParser {
        public ArrayList<String> province_list_code = new ArrayList<>();
        public ArrayList<String> city_list_code = new ArrayList<>();

        public List<Cityinfo> getJSONParserResult(String JSONString, String key) {
            List<Cityinfo> list = new ArrayList<>();
            JsonObject result = new JsonParser().parse(JSONString)
                    .getAsJsonObject().getAsJsonObject(key);

            Iterator iterator = result.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, JsonElement> entry = (Map.Entry<String, JsonElement>) iterator
                        .next();
                Cityinfo cityinfo = new Cityinfo();

                cityinfo.setCity_name(entry.getValue().getAsString());
                cityinfo.setId(entry.getKey());
                province_list_code.add(entry.getKey());
                list.add(cityinfo);
            }
            return list;
        }

        public HashMap<String, List<Cityinfo>> getJSONParserResultArray(
                String JSONString, String key) {
            HashMap<String, List<Cityinfo>> hashMap = new HashMap<>();
            JsonObject result = new JsonParser().parse(JSONString)
                    .getAsJsonObject().getAsJsonObject(key);

            Iterator iterator = result.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, JsonElement> entry = (Map.Entry<String, JsonElement>) iterator
                        .next();
                List<Cityinfo> list = new ArrayList<>();
                JsonArray array = entry.getValue().getAsJsonArray();
                for (int i = 0; i < array.size(); i++) {
                    Cityinfo cityinfo = new Cityinfo();
                    cityinfo.setCity_name(array.get(i).getAsJsonArray().get(0)
                            .getAsString());
                    cityinfo.setId(array.get(i).getAsJsonArray().get(1)
                            .getAsString());
                    city_list_code.add(array.get(i).getAsJsonArray().get(1)
                            .getAsString());
                    list.add(cityinfo);
                }
                hashMap.put(entry.getKey(), list);
            }
            return hashMap;
        }
    }

}
