package restaurant.apps.falcons.flaconsrestaurant.models;

import java.io.Serializable;

/**
 * Created by pure_ on 05/09/2016.
 */
public class User implements Serializable {
    String id;
    String name;
    String master;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMaster() {
        return master;
    }

    public void setMaster(String master) {
        this.master = master;
    }
}
