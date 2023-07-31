package restaurant.apps.falcons.flaconsrestaurant.models;

import java.io.Serializable;

/**
 * Created by Salah on 7/5/17.
 */

public class Review implements Serializable{

    private String tableId;
    private String note;
    private String date;

    public String getTableId() {
        return tableId;
    }

    public void setTableId(String tableId) {
        this.tableId = tableId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
