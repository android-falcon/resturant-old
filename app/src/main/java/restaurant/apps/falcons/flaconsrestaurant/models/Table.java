package restaurant.apps.falcons.flaconsrestaurant.models;

import android.view.View;

import restaurant.apps.falcons.flaconsrestaurant.util.DataManager;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by pure_ on 30/07/2016.
 */
public class Table implements Serializable {
    private String id;
    private String desc;
    private int color;
    private String status;
    private String orderID;
    private String section;
    private float total;
    private float tax;
    private int seatCount;
    private String user;
    private Date date;
    private String note;
    private List<Item> items;
    private View view;
    private float x;
    private float y;
    private List<Item> refundItems;
    private String transNo;
    private String transKind;
    private Date startTime;
    private String tempVHFNO;
    private float discount;
    private int requestId;
    private double cash;
    private double payed;
    private double change;
    private double visa;
    private double master;
    private double other;
    private float service;

    public float getDiscount() {
        return discount;
    }

    public void setDiscount(float discount) {
        this.discount = discount;
    }

    public String getTempVHFNO() {
        return tempVHFNO;
    }

    public void setTempVHFNO(String tempVHFNO) {
        this.tempVHFNO = tempVHFNO;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public String getTransNo() {
        return transNo;
    }

    public void setTransNo(String transNo) {
        this.transNo = transNo;
    }

    public String getTransKind() {
        return transKind;
    }

    public void setTransKind(String transKind) {
        this.transKind = transKind;
    }

    public Table() {
        orderID = "";
        id = "";
        desc = "";
        status = "";
        section = "";
        note = "";
        date = new Date();
        startTime = new Date();
        items = new ArrayList<>();
        refundItems = new ArrayList<>();
    }

    public void calcTotal() {
        boolean calcBeforeTax = DataManager.getRegisters().get("TAXCALCKIND").equals("1");

        total = 0;
        tax = 0;
        for (Item item : items) {
            for (Answer answer : item.getAnswers()) {
                if (answer.getItem() != null) {
                    float price = answer.getItem().useNewPrice() ? answer.getItem().getNewPrice() : answer.getItem().getPrice();
                    if (!calcBeforeTax) {
                        price = price / (1 + answer.getItem().getTax() / 100f);
                    }
                    float itemTotal = item.getQty() * price;
                    total += itemTotal;
                    tax += itemTotal * answer.getItem().getTax() / 100f;
                }
            }
            float price = item.getPrice();
            if (!calcBeforeTax) {
                price = price / (1 + item.getTax() / 100f);
            }

            float itemTotal;

            if (item.isTimeItem())
            {
                itemTotal = item.getQty() * price;
            }
            else
            {
                itemTotal = item.getQty() * price;
            }

            total += itemTotal;
            tax += itemTotal * item.getTax() / 100f;
        }
        float discPer = discount / total;
        total -= discount;
        tax = tax * (1 - discPer);

        String fullService = DataManager.getRegisters().get("FULLSERVICE");
        if (fullService.equals("1")) {
            String taxPer = DataManager.getRegisters().get("SERVICE_TAX");
            String taxValue = DataManager.getRegisters().get("SERVICE_VALUE");

            float TAXPER = Float.valueOf(taxPer);
            float TAXVALUE = Float.valueOf(taxValue);

            float serviceTotal = total * TAXVALUE / 100f;
            float serviceTax = serviceTotal * TAXPER / 100f;

            total += serviceTotal;
            tax += serviceTax;

            service = serviceTotal;
        }

    }

    public float getService() {
        return service;
    }

    public List<Item> getRefundItems() {
        return refundItems;
    }

    public void setRefundItems(List<Item> reasons) {
        this.refundItems = reasons;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    public float getTax() {
        return tax;
    }

    public void setTax(float tax) {
        this.tax = tax;
    }

    public int getSeatCount() {
        return seatCount;
    }

    public void setSeatCount(int seatCount) {
        this.seatCount = seatCount;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

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

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public double getCash() {
        return cash;
    }

    public void setCash(double cash) {
        this.cash = cash;
    }

    public double getPayed() {
        return payed;
    }

    public void setPayed(double payed) {
        this.payed = payed;
    }

    public double getChange() {
        return change;
    }

    public void setChange(double change) {
        this.change = change;
    }

    public double getVisa() {
        return visa;
    }

    public void setVisa(double visa) {
        this.visa = visa;
    }

    public double getMaster() {
        return master;
    }

    public void setMaster(double master) {
        this.master = master;
    }

    public double getOther() {
        return other;
    }

    public void setOther(double other) {
        this.other = other;
    }
}
