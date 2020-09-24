import java.util.ArrayList;

public class Store {

    public ArrayList<RecipeBook> listOfRecipeBooks;

    public Store()
    {
        listOfRecipeBooks = new ArrayList<RecipeBook>();
    }

    public void addRecipeBook(RecipeBook recipeBook)
    {
        listOfRecipeBooks.add(recipeBook);
    }

    public boolean removeRecipeBook(String id)
    {
        for(var book : listOfRecipeBooks)
        {
            if(book.getId().equals(id))
            {
                listOfRecipeBooks.remove(book);
                return true;
            }
        }
        return false;
    }

    public ArrayList<RecipeBook> getListOfRecipeBooks() {
        return listOfRecipeBooks;
    }

    public void setListOfRecipeBooks(ArrayList<RecipeBook> listOfRecipeBooks) {
        this.listOfRecipeBooks = listOfRecipeBooks;
    }
}
