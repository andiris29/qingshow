package com.focosee.qingshow.model;

import android.support.v4.util.ArrayMap;
import com.focosee.qingshow.model.vo.remix.RemixByItem;

/**
 * Created by dylanjiang on 16/1/3.
 */
public class S11Cache {
  private static S11Cache instance;
  private static ArrayMap<String, RemixByItem> map;

  private S11Cache(){
    map = new ArrayMap<>();
  }

  public static S11Cache getInstance() {
    if (instance == null) {
      instance = new S11Cache();
    }
    return instance;
  }

  public void put(String id, RemixByItem remixByItem) {
    map.put(id, remixByItem);
  }

  public RemixByItem get(String id) {
    if (map.containsKey(id)) {
      return map.get(id);
    }else {
      return null;
    }
  }

}
