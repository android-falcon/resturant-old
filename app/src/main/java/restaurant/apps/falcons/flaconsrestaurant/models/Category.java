package restaurant.apps.falcons.flaconsrestaurant.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pure_ on 31/07/2016.
 */
public class Category extends DummyItem implements Serializable{
    private String id;
    private List<Modifier> modifiers;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Modifier> getModifiers() {
        return modifiers;
    }

    public void setModifiers(List<Modifier> modifiers) {
        this.modifiers = modifiers;
    }

    public Category() {
        modifiers = new ArrayList<>();
    }
}
