package restaurant.apps.falcons.flaconsrestaurant.models;

public class TableInfoModel {
    private  String tableNo;
    private  String seats;
    private  String dateOrder;
    private  String doneOrder;

    public TableInfoModel() {
    }

    public String getTableNo() {
        return tableNo;
    }

    public void setTableNo(String tableNo) {
        this.tableNo = tableNo;
    }

    public String getSeats() {
        return seats;
    }

    public void setSeats(String seats) {
        this.seats = seats;
    }

    public String getDateOrder() {
        return dateOrder;
    }

    public void setDateOrder(String dateOrder) {
        this.dateOrder = dateOrder;
    }

    public String getDoneOrder() {
        return doneOrder;
    }

    public void setDoneOrder(String doneOrder) {
        this.doneOrder = doneOrder;
    }
}
