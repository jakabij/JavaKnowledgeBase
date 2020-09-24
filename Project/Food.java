package JavaKnowledgeBase.Project;

import java.util.ArrayList;

public class Food {
    public String id;
    public String nameOfFood;
    public boolean serveCold;
    public ArrayList<String> listOfIngredients;

    public Food() { }
    public Food(String id, String nameOfFood, boolean serveCold, ArrayList<String> listOfingredients)
    {
        this.id = id;
        this.nameOfFood = nameOfFood;
        this.serveCold = serveCold;
        this.listOfIngredients = listOfingredients;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNameOfFood() {
        return nameOfFood;
    }

    public void setNameOfFood(String nameOfFood) {
        this.nameOfFood = nameOfFood;
    }

    public boolean isServeCold() {
        return serveCold;
    }

    public void setServeCold(boolean serveCold) {
        this.serveCold = serveCold;
    }

    public ArrayList<String> getListOfIngredients() {
        return listOfIngredients;
    }

    public void setListOfIngredients(ArrayList<String> listOfIngredients) {
        this.listOfIngredients = listOfIngredients;
    }

    
}
