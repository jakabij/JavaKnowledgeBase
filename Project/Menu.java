package JavaKnowledgeBase.Project;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class Menu {
    UI ui = new UI();
    String filePath = ".\\RecepeStoreJava\\ProjectData\\data.xml";
    File file = new File(filePath);
    Store store;
    public Menu() throws Exception
    {
        if (file.exists())
        {
            XMLLoader loader = new XMLLoader();
            store = loader.loadFromXml(filePath);
            ui.getInfo("Loading from database was successful.");
        }
        else
        {
            store = new Store();
            ui.getInfo("No database found.");
        }
    }

    public void menuStart() throws Exception
    {
        ui.start();
        String choice = ui.getInputFromUser("\nYour choice: ");

        if (choice.equals("1"))
        {
            ui.clearScreen();
            readAllBooks();
        }

        else if (choice.equals("2"))
        {
            try
            { 
                createRecepeBook();
            }
            catch(Exception ex)
            {
                ui.getInfo("Wrong attributes! Recepe book not created.");
            }
        }

        else if (choice.equals("3"))
        {
            while (true)
            {
                readAllBooks();
                String bookId = ui.getInputFromUser("The book's ID that you are searching for or press 0 to EXIT: ");
                if (bookId.equals("0"))
                    break;

                boolean foundIt = false;
                RecipeBook recipeBook = null;
                for(var book : store.getListOfRecipeBooks())
                {
                    if (book.getId().equals(bookId))
                    {
                        foundIt = true;
                        recipeBook = book;
                        break;
                    }
                }

                if (foundIt)
                {
                    while (true)
                    {
                        ui.getBookFoods(recipeBook);
                        ui.printUpdateMenu(recipeBook.getName());
                        String choice3 = ui.getInputFromUser("\nYou chose: ");

                        if (choice3.equals("1"))
                        {
                            deleteFood(recipeBook);
                        }
                        else if (choice3.equals("2"))
                        {
                            try
                            {
                                Food food = createFoodForRecipeBook(recipeBook);
                                recipeBook.addFood(food);
                                ui.clearScreen();
                                ui.getInfo("Food successfully added to the book.");
                            }
                            catch(Exception ex)
                            {
                                ui.getInfo("Not valid attributes added!");
                                ui.getInfo("Food does not created.");
                            }
                        }
                        else if (choice3.equals("3"))
                        {
                            break;
                        }
                        else
                        {
                            ui.getInfo("Invalid attribute!");
                        }
                    }
                }
                else
                {
                    ui.getInfo("Invalid ID");
                }
            }
        }

        else if (choice.equals("4"))
        {
            ui.clearScreen();
            readAllBooks();
            String id = ui.getInputFromUser("\nRecepe book's ID to delete: ");

            if (removeRecepeById(id, store))
                ui.getInfo("Recepe book successfully deleted.");

            else
                ui.getInfo("Recepe book not found.");
        }

        else if (choice.equals("5"))
        {
            String bookName = ui.getInputFromUser("\nFood name: ");
            ui.clearScreen();
            findBookByFoodName(bookName, store);
        }
        else if (choice.equals("6"))
        {
            ui.clearScreen();
            readAllBooks();
            String bookId = ui.getInputFromUser("\nBook ID: ");
            
            showRecepeByBookId(bookId, store);
            ui.clearScreen();
        }
        else if (choice.equals("7"))
        {
            XMLSaver saver = new XMLSaver();
            saver.saveToXml(filePath, store);
            ui.clearScreen();
            ui.getInfo("Save was successfull.");
        }
        else if (choice.equals("0"))
        {
            System.exit(0);
        }
        else
        {
            ui.clearScreen();
            ui.getInfo("Invalid attribute!");
        }
    }



    public void printAllBooks(ArrayList<RecipeBook> listOfBooks)
    {
        int nameCellWidth = getMaxBookName(listOfBooks) + 4;  
        int idCellWidth = 9;

        ui.tableCloser(true, nameCellWidth, idCellWidth);
        ui.tableDatas(listOfBooks, nameCellWidth, idCellWidth);
        ui.tableCloser(false, nameCellWidth, idCellWidth);
    }


    private int getMaxBookName(ArrayList<RecipeBook> listOfBooks)
    {
        int maxLenght = 0;
        for(var book : listOfBooks)
        {
            if(book.getName().length() > maxLenght)
            {
                maxLenght = book.getName().length();
            }
        }

        return maxLenght;
    }


    public void readAllBooks() throws Exception
    {
        if (store.getListOfRecipeBooks().size() < 1)
        {
            ui.clearScreen();
            ui.getInfo("There is no recepe in the store.");
        }
        else
        {
            ui.clearScreen();
            printAllBooks(store.getListOfRecipeBooks());
        }
    }


    public void createRecepeBook() throws Exception
    {
        String bookName = ui.getInputFromUser("Name of the book: ");

        RecipeBook recipeBook = new RecipeBook(bookName);
        ui.getInfo("Recipe book successfully created.");

        while (true)
        {
            ui.clearScreen();
            if (ui.questionForFoodAdding())
            {
                Food food = createFoodForRecipeBook(recipeBook);

                recipeBook.addFood(food);
                ui.getInfo("Food successfully added to recepe book.");
            }
            else
                break;
        }
        store.addRecipeBook(recipeBook);
        ui.getInfo("Recipe book successfully added to store.");
    }

    public void deleteFood(RecipeBook recipeBook) throws Exception
    {
        String foodId = ui.getInputFromUser("Food id to delete: ");
        boolean foundTheFood = false;
        for (int count = 0; count < recipeBook.getListOfFoods().size(); count++)
        {
            if (recipeBook.getListOfFoods().get(count).getId().equals(foodId))
            {
                foundTheFood = true;
                recipeBook.getListOfFoods().remove(count);
                ui.clearScreen();
                ui.getInfo("Food successfully removed.");
                break;
            }
        }
        if (!foundTheFood)
        {
            ui.clearScreen();
            ui.getInfo("Not valid ID!");
            ui.getInfo("Nothing changed.");
        }
    }


    public Food createFoodForRecipeBook(RecipeBook book) throws Exception
    {
        String typeOfFood = ui.getInputFromUser("[1: Appetizer, 2: Second Meal, 3: Dessert]\n"
        +"Type of the food: ");

        if (!(typeOfFood.equals("1") || typeOfFood.equals("2") || typeOfFood.equals("3")))
        {
            throw new Exception("NotValidAttribute!");
        }
        
        String nameOfFood = ui.getInputFromUser("\nName of food: ");

        boolean serveCold;
        
        String toConvert = ui.getInputFromUser("\n[yes or no]\nBest to serve cold: ");
        if(toConvert.toLowerCase().equals("yes") || toConvert.toLowerCase().equals("y"))
        {
            serveCold = true;
        }
        else if(toConvert.toLowerCase().equals("no") || toConvert.toLowerCase().equals("n"))
        {
            serveCold = false;
        }
        else
        {
            throw new Exception("ParseError");
        }


        String[] ingredients = ui.getInputFromUser("\nThe ingredients separated by ',': ").split(",");
        ArrayList<String> listOfIngredients = new ArrayList<String>(Arrays.asList(ingredients));

        String toCheck = "'~ˇ+^!˘%°/˛=`´˝¨\\|€÷×łŁ$ß#&@<?;.:*";
        for(var ingredient : listOfIngredients)
        {
            for (int i = 0; i < toCheck.length(); i++)
            {
                if (ingredient.contains(""+toCheck.charAt(i)))
                    throw new Exception("InvalidAttribute");
            }
        }

        try
        {
            Food food = book.createFood(typeOfFood, nameOfFood, serveCold, listOfIngredients);
            ui.getInfo("Creating food was successfull!");

            return food;
        }
        catch(Exception ex)
        {
            throw new Exception("InvalidCreation");
        }
    }

    public void findBookByFoodName(String name, Store store) throws Exception
    {
        ArrayList<RecipeBook> searchedBooks = new ArrayList<RecipeBook>();
        for(var book : store.getListOfRecipeBooks())
        {
            for(var food : book.getListOfFoods())
            {
                if (food.getNameOfFood().equals(name))
                {
                    if (!searchedBooks.contains(book))
                        searchedBooks.add(book);
                }
            }
        }
        ui.clearScreen();

        if (searchedBooks.size() == 0)
        {
            ui.getInfo("No food like that!");
        }
        else
        {
            ArrayList<String> messages = new ArrayList<>();
            for(var book : searchedBooks)
            {
                messages.add(book.getName()+"  ( "+ book.getId() + " )");
            }
            ui.getInfo("Searching was successfull!");
            ui.getResult(messages, false);
        }
    }

    public boolean removeRecepeById(String id, Store store)
    {
        return store.removeRecipeBook(id);
    }

    public void showRecepeByBookId(String id, Store store)
    {
        RecipeBook searchedBook = null;
        boolean foundIt = false;
        for(var book : store.getListOfRecipeBooks())
        {
            if (book.getId().equals(id))
            {
                searchedBook = book;
                foundIt = true;
                break;
            }
        }
        if (foundIt)
        { 
            ui.getBookFoods(searchedBook);
        }
        else
        {
            ui.getInfo("Book not found!");
        }
    }
}
