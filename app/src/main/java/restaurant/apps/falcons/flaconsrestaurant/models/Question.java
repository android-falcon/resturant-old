package restaurant.apps.falcons.flaconsrestaurant.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pure_ on 30/07/2016.
 */
public class Question implements Serializable{
    private String id;
    private String desc;
    private int answersCount;
    private List<Answer> answers;
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Question() {
        answers = new ArrayList<>();
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

    public int getAnswersCount() {
        return answersCount;
    }

    public void setAnswersCount(int answersCount) {
        this.answersCount = answersCount;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }
}
