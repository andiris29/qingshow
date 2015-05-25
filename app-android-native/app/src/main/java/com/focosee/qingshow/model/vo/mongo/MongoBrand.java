package com.focosee.qingshow.model.vo.mongo;

import com.focosee.qingshow.model.vo.context.BrandContext;
import com.focosee.qingshow.model.vo.metadata.ImageMetadata;

import java.io.Serializable;

public class MongoBrand implements Serializable {
    public static final String DEBUG_TAG = "MongoBrand";

    public String _id;

    public String name;
    public Number type;
    public String logo;
    public String background;
    public String cover;

}
