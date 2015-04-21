package com.focosee.qingshow.util.sku;

/**
 * Created by Administrator on 2015/3/18.
 */
public class Prop {
    String propId;
    String propValue;
    String name;

    public String getPropId() {
        return propId;
    }

    public void setPropId(String propId) {
        this.propId = propId;
    }

    public String getPropValue() {
        return propValue;
    }

    public void setPropValue(String propValue) {
        this.propValue = propValue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Prop prop = (Prop) o;

        if (name != null ? !name.equals(prop.name) : prop.name != null) return false;
        if (propId != null ? !propId.equals(prop.propId) : prop.propId != null) return false;
        if (propValue != null ? !propValue.equals(prop.propValue) : prop.propValue != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = propId != null ? propId.hashCode() : 0;
        result = 31 * result + (propValue != null ? propValue.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}