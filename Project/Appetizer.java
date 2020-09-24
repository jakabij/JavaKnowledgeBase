package JavaKnowledgeBase.Project;

import java.sql.Time;
import java.util.ArrayList;

public class Appetizer extends Food {
    public Time timeToPrepare;

    public Appetizer() { }
    public Appetizer(String id, String nameOfFood, boolean serveCold,
     ArrayList<String> listOfingredients, Time timeToPrepare)
    {
        super(id, nameOfFood, serveCold, listOfingredients);
        this.timeToPrepare = timeToPrepare;
    }

    public Time getTimeToPrepare() {
        return timeToPrepare;
    }

    public void setTimeToPrepare(Time timeToPrepare) {
        this.timeToPrepare = timeToPrepare;
    }
}
