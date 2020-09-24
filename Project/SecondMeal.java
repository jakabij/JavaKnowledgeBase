package JavaKnowledgeBase.Project;

import java.sql.Time;
import java.util.ArrayList;

public class SecondMeal extends Food {
    public boolean needToCook;
    public Time timeToPrepare;
    public ArrayList<String> listOfSpices;

    public SecondMeal() { }
    public SecondMeal(String id, String nameOfFood, boolean serveCold, ArrayList<String> listOfingredients,
    boolean needToCook, Time timeToPrepare, ArrayList<String> listOfSpices)
    {
        super(id, nameOfFood, serveCold, listOfingredients);
        this.needToCook = needToCook;
        this.timeToPrepare = timeToPrepare;
        this.listOfSpices = listOfSpices;
    }

    public boolean isNeedToCook() {
        return needToCook;
    }

    public void setNeedToCook(boolean needToCook) {
        this.needToCook = needToCook;
    }

    public Time getTimeToPrepare() {
        return timeToPrepare;
    }

    public void setTimeToPrepare(Time timeToPrepare) {
        this.timeToPrepare = timeToPrepare;
    }

    public ArrayList<String> getListOfSpices() {
        return listOfSpices;
    }

    public void setListOfSpices(ArrayList<String> listOfSpices) {
        this.listOfSpices = listOfSpices;
    }
}
