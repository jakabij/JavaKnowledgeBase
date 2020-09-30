import java.sql.Time;
import java.util.ArrayList;

public class Dessert extends Food {
    private boolean needToCook;
    private Time timeToPrepare;

    public Dessert() { }
    public Dessert(String id, String nameOfFood, boolean serveCold,
     ArrayList<String> listOfingredients,boolean needToCook,Time timeToPrepare)
    {
        super(id, nameOfFood, serveCold, listOfingredients);
        this.needToCook = needToCook;
        this.timeToPrepare = timeToPrepare;
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
}
