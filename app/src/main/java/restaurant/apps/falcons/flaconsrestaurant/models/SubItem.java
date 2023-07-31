package restaurant.apps.falcons.flaconsrestaurant.models;

import java.io.Serializable;

/**
 * Created by pure_ on 20/08/2016.
 */
public class SubItem extends Item implements Serializable{
    private float newPrice;
    private boolean useNewPrice;
    private String caption;
    private boolean doSave;
    private boolean doPrint;

    public float getNewPrice() {
        return newPrice;
    }

    public void setNewPrice(float newPrice) {
        this.newPrice = newPrice;
    }

    public boolean useNewPrice() {
        return useNewPrice;
    }

    public void setUseNewPrice(boolean useNewPrice) {
        this.useNewPrice = useNewPrice;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public boolean doSave() {
        return doSave;
    }

    public void setDoSave(boolean doSave) {
        this.doSave = doSave;
    }

    public boolean doPrint() {
        return doPrint;
    }

    public void setDoPrint(boolean doPrint) {
        this.doPrint = doPrint;
    }
}
