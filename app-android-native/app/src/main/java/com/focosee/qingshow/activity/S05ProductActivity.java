package com.focosee.qingshow.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.allthelucky.common.view.ProductImageIndicatorView;
import com.allthelucky.common.view.network.NetworkProductImageIndicatorView;
import com.focosee.qingshow.R;
import com.focosee.qingshow.entity.ShowDetailEntity;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

public class S05ProductActivity extends Activity {
    public static final String INPUT_ITEM_LIST = "S05ProductActivity_input_item_list";

    private NetworkProductImageIndicatorView _productView;

    private ArrayList<ShowDetailEntity.RefItem> _itemsData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s05_product);

        Intent intent = getIntent();
        _itemsData = (ArrayList<ShowDetailEntity.RefItem>)intent.getSerializableExtra(S05ProductActivity.INPUT_ITEM_LIST);

        _productView = (NetworkProductImageIndicatorView) findViewById(R.id.S05_product_view);
        _productView.setupData(getCoverUrlList(), getDescriptionList(), getBrandNameList(), ImageLoader.getInstance());

        _productView.getBrandButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("app_click", "click brand button, index:" + String.valueOf(_productView.getCurrentIndex()));
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(_itemsData.get(_productView.getCurrentIndex()).source));
                S05ProductActivity.this.startActivity(intent);
            }
        });

        _productView.getBuyButton().setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.i("app_click", "click buy button, index:" + String.valueOf(_productView.getCurrentIndex()));
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(_itemsData.get(_productView.getCurrentIndex()).source));
                S05ProductActivity.this.startActivity(intent);
            }
        });

        _productView.getCloseButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("app_click", "click close button");
                S05ProductActivity.this.finish();
            }
        });

        _productView.setOnItemClickListener(new ProductImageIndicatorView.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, int position) {
                Log.i("app_click", "click background");
                S05ProductActivity.this.finish();
            }
        });

        _productView.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_s05_product, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // get cover url list from items data
    public ArrayList<String> getCoverUrlList() {
        ArrayList<String> _coverUrlList = new ArrayList<String>();
        for (ShowDetailEntity.RefItem item : _itemsData){
            _coverUrlList.add(item.cover);
        }
        return _coverUrlList;
    }

    // get brand list from items data
    public ArrayList<String> getBrandNameList() {
        ArrayList<String> _brandList = new ArrayList<String>();
        for (ShowDetailEntity.RefItem item : _itemsData){
            _brandList.add(item.brandRef.name);
        }
        return _brandList;
    }

    // get description list from items data
    public ArrayList<String> getDescriptionList() {
        ArrayList<String> _descriptionList = new ArrayList<String>();
        for (ShowDetailEntity.RefItem item : _itemsData){
            _descriptionList.add(item.name);
        }
        return _descriptionList;
    }

    // get source url list from item data
    public ArrayList<String> getSourceUrlList() {
        ArrayList<String> _sourceUrlList = new ArrayList<String>();
        for (ShowDetailEntity.RefItem item : _itemsData)
            _sourceUrlList.add(item.source);
        return _sourceUrlList;
    }
}
