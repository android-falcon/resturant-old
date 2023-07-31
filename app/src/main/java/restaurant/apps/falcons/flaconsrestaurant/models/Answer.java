package restaurant.apps.falcons.flaconsrestaurant.models;

import java.io.Serializable;

/**
 * Created by pure_ on 01/08/2016.
 */
public class Answer implements Serializable {
    private String qId;
    private String desc;
    private String mId;
    private SubItem Subitem;

    public SubItem getItem() {
        return Subitem;
    }

    public void setSubItem(SubItem item) {
        this.Subitem = item;
    }

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public String getqId() {
        return qId;
    }

    public void setqId(String qId) {
        this.qId = qId;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
