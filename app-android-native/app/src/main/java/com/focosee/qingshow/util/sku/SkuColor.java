package com.focosee.qingshow.util.sku;

/**
 * Created by Administrator on 2015/3/18.
 */
public class SkuColor {
    String url;
    public Prop prop;

    public SkuColor(Prop prop) {
        this.prop = prop;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SkuColor skuColor = (SkuColor) o;

        if (!prop.equals(skuColor.prop)) return false;
        if (url != null ? !url.equals(skuColor.url) : skuColor.url != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = prop.hashCode();
        result = 31 * result + (url != null ? url.hashCode() : 0);
        return result;
    }
}