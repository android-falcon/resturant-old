package restaurant.apps.falcons.flaconsrestaurant.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by pure_ on 30/07/2016.
 */
public class Item extends DummyItem implements Serializable{
    private String id;
    private String name;
    private String Category;
    private float price;
    private float qty;
    private float tax;
    private float discount;
    private List<Modifier> orgModifiers;
    private List<Modifier> modifiers;
    private List<Question> questions;
    private List<Answer> answers;
    private RefundReason refundReason;
    private String note;
    private String notes;
    private String name2;
    private boolean forceQ;
    private Date addTime;
    private boolean old;
    private float oldQty;
    private String kind;
    private boolean refundOrCancel;
    private boolean parent;
    private int seatNumber;
    private boolean isSubItem;
    private Date openTime;
    private Date endTime;
    private int isFinishedTime;
    private String imagePathItem;

    private  String itemDescription;

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public String getimagePathItem() {
        return imagePathItem;
    }

    public void setimagePathItem(String imagePath) {
        this.imagePathItem = imagePath;
    }

    public int getIsFinishedTime() {
        return isFinishedTime;
    }

    public void setIsFinishedTime(int isFinishedTime) {
        this.isFinishedTime = isFinishedTime;
    }



    public Date getOpenTime() {
        return openTime;
    }

    public void setOpenTime(Date openTime) {
        this.openTime = openTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }



    public boolean isTimeItem() {
        return isTimeItem;
    }

    public void setTimeItem(boolean timeItem) {
        isTimeItem = timeItem;
    }

    private boolean isTimeItem;

    public boolean isSubItem() {
        return isSubItem;
    }

    public void setSubItem(boolean subItem) {
        isSubItem = subItem;
    }



    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public boolean isParent() {
        return parent;
    }

    public void setParent(boolean parent) {
        this.parent = parent;
    }

    public List<Modifier> getModifiers() {
        return modifiers;
    }

    public void setModifiers(List<Modifier> modifiers) {
        this.modifiers = modifiers;
    }

    public boolean isRefundOrCancel() {
        return refundOrCancel;
    }

    public void setRefundOrCancel(boolean refundOrCancel) {
        this.refundOrCancel = refundOrCancel;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public float getOldQty() {
        return oldQty;
    }

    public void setOldQty(float oldQty) {
        this.oldQty = oldQty;
    }

    public boolean isOld() {
        return old;
    }

    public void setOld(boolean old) {
        this.old = old;
    }

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    public boolean isForceQ() {
        return forceQ;
    }

    public void setForceQ(boolean forceQ) {
        this.forceQ = forceQ;
    }

    public String getName2() {
        return name2;
    }

    public void setName2(String name2) {
        this.name2 = name2;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public float getDiscount() {
        return discount;
    }

    public void setDiscount(float discount) {
        this.discount = discount;
    }

    public RefundReason getRefundReason() {
        return refundReason;
    }

    public void setRefundReason(RefundReason refundReason) {
        this.refundReason = refundReason;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }

    public Item() {
        addTime = new Date();
        orgModifiers = new ArrayList<>();
        questions = new ArrayList<>();
        answers = new ArrayList<>();
        modifiers = new ArrayList<>();
        note = "";
        kind = "0";
    }

    public float getTax() {
        return tax;
    }

    public void setTax(float tax) {
        this.tax = tax;
    }

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

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public float getQty() {
        return qty;
    }

    public void setQty(float qty) {
        this.qty = qty;
    }

    public List<Modifier> getOrgModifiers() {
        return orgModifiers;
    }

    public void setOrgModifiers(List<Modifier> orgModifiers) {
        this.orgModifiers = orgModifiers;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(int seatNumber) {
        this.seatNumber = seatNumber;
    }
}
