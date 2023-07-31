package restaurant.apps.falcons.flaconsrestaurant.models;

import java.io.Serializable;

/**
 * Created by pure_ on 31/07/2016.
 */
public class DummyItem implements Serializable {
    private String dummyDesc;
    private int dummyColor;
    private String urlImage;

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public String getDummyDesc() {
        return dummyDesc;
    }

    public void setDummyDesc(String dummyDesc) {
        this.dummyDesc = dummyDesc;
    }

    public int getDummyColor() {
        return dummyColor;
    }

    public void setDummyColor(int dummyColor) {
        this.dummyColor = dummyColor;
    }
}
