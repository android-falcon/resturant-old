package restaurant.apps.falcons.flaconsrestaurant.models;

import java.io.Serializable;

/**
 * Created by pure_ on 19/08/2016.
 */
public class RefundReason implements Serializable{
    private String id;
    private String desc;
    private String printOnReport;
    private String reduceInv;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPrintOnReport() {
        return printOnReport;
    }

    public void setPrintOnReport(String printOnReport) {
        this.printOnReport = printOnReport;
    }

    public String getReduceInv() {
        return reduceInv;
    }

    public void setReduceInv(String reduceInv) {
        this.reduceInv = reduceInv;
    }
}
