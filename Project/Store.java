import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class Store {

    private ArrayList<RecipeBook> listOfRecipeBooks;
    private String idStoreFile = ".\\ProjectData\\ids.txt";

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
                removeIDsFromIDStore(book);
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

    private void removeIDsFromIDStore(RecipeBook book){
        try
        {
            File file = new File(idStoreFile);
            ArrayList<String> idsToDelete = new ArrayList<>();

            for(var food : book.getListOfFoods()){
                idsToDelete.add(food.getId());
            }

            Scanner fileScanner = new Scanner(file);
            String newFileData = "";

            //get existing data from the text file
            while(fileScanner.hasNextLine())
            {
                String data = fileScanner.nextLine();
                
                if(!idsToDelete.contains(data) && !data.equals(book.getId()) ){
                    newFileData += (data  + "\n");
                }
            }
            fileScanner.close();

            //create the text file without the deleted food's id
            FileWriter fw = new FileWriter(file);
            fw.write(newFileData);
            fw.close();
        }
        catch(Exception ex)
        {
            System.err.println(ex);
        }
    }
}
