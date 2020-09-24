import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;

public class PostgreSQLLoader implements ILoader{
    String url;
    String user;
    String password;
    Connection conn = null;
    DatabaseMetaData metaData = null;
    
    public PostgreSQLLoader(String url, String user, String password)
    {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    @Override
    public Store loadFromFile(String path)
    {
        Store store = new Store();
        ArrayList<RecipeBook> listOfRecipeBooks = new ArrayList<>();

        try{
            conn = DriverManager.getConnection(url, user, password);

            if (conn != null) {
                System.out.println("Connected to the database!");
            } else {
                System.out.println("Failed to make connection!");
            }

        } catch (SQLException e) {
            System.err.printf("SQL State: %s\n%s" ,e.getSQLState() , e.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        DatabaseMetaData metaData = null;
        try {
            metaData = conn.getMetaData();
            System.out.println("Meta datas loaded from database.");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Statement st = null;
        try {
            st = conn.createStatement();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        ResultSet resultSet = null;
        
        resultSet = tryToReadRecepe(st);
    
        if(resultSet != null)
        {
            try
            {
                while (resultSet.next())
                {
                    // to read out the books
                    String bookName = resultSet.getString("book_id");
                    String bookID = resultSet.getString("book_name");
                    ArrayList<Food> booksFoods = new ArrayList<>();

                    // to read out the books' foods
                    ResultSet resultSet2 = tryToReadAppetizer(st, bookID);

                    if(resultSet2 != null)
                    {
                        while (resultSet2.next())
                        {
                            String foodID = resultSet2.getString("id");
                            String foodName = resultSet2.getString("appetizer_name");
                            boolean toServeCold = Boolean.parseBoolean(resultSet2.getString("serve_cold"));
                            Time toPepare = Time.valueOf(resultSet2.getString("time_to_prepare"));
                            ArrayList<String> foodIngredients = new ArrayList<>();

                            ResultSet resultSet3 = tryToReadIngredients(st, foodID);

                            // to read out the books' foods' ingredients
                            if(resultSet3 != null)
                            {
                                while (resultSet3.next())
                                {
                                    foodIngredients.add(resultSet3.getString("ingredient_name"));
                                }

                                booksFoods.add(new Appetizer(foodID, foodName, toServeCold, foodIngredients, toPepare));
                            }
                        }
                    }
                    resultSet2 = tryToReadSecondMeal(st, bookID);

                    if(resultSet2 != null)
                    {
                        while (resultSet2.next())
                        {
                            String foodID = resultSet2.getString("id");
                            String foodName = resultSet2.getString("second_meal_name");
                            boolean toServeCold = Boolean.parseBoolean(resultSet2.getString("serve_cold"));
                            boolean needToCook = Boolean.parseBoolean(resultSet2.getString("need_to_cook"));
                            Time toPepare = Time.valueOf(resultSet2.getString("time_to_prepare"));
                            ArrayList<String> foodIngredients = new ArrayList<>();
                            ArrayList<String> foodSpices = new ArrayList<>();

                            ResultSet resultSet3 = tryToReadIngredients(st, foodID);

                            // to read out the books' foods' ingredients
                            if(resultSet3 != null)
                            {
                                while (resultSet3.next())
                                {
                                    foodIngredients.add(resultSet3.getString("ingredient_name"));
                                }

                                ResultSet resultSet4 = tryToReadSpices(st, foodID);

                                if(resultSet4 != null)
                                {
                                    // to read out the books' second meals' spices
                                    while (resultSet3.next())
                                    {
                                        foodSpices.add(resultSet3.getString("spice_name"));
                                    }

                                    booksFoods.add(new SecondMeal(foodID, foodName, toServeCold, foodIngredients, needToCook,
                                            toPepare, foodSpices));
                                }
                            }
                        }
                    }

                    resultSet2 = tryToReadDessert(st, bookID);

                    if(resultSet2 != null)
                    {
                        while (resultSet2.next())
                        {
                            String foodID = resultSet2.getString("id");
                            String foodName = resultSet2.getString("dessert_name");
                            boolean toServeCold = Boolean.parseBoolean(resultSet2.getString("serve_cold"));
                            boolean needToCook = Boolean.parseBoolean(resultSet2.getString("need_to_cook"));
                            Time toPepare = Time.valueOf(resultSet2.getString("time_to_prepare"));
                            ArrayList<String> foodIngredients = new ArrayList<>();

                            ResultSet resultSet3 = tryToReadIngredients(st, foodID);

                            // to read out the books' foods' ingredients
                            if(resultSet3 != null)
                            {
                                while (resultSet3.next())
                                {
                                    foodIngredients.add(resultSet3.getString("ingredient_name"));
                                }

                                booksFoods.add(new Dessert(foodID, foodName, toServeCold,
                                 foodIngredients, needToCook, toPepare));
                            }
                        }
                    }

                    store.addRecipeBook(new RecipeBook(bookName, bookID, booksFoods));
                }
            }
            catch (SQLException e) 
            {
                // TODO Auto-generated catch block
                return null;
            }
        }
        try {
            conn.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return store;
    }

    public ResultSet tryToReadRecepe(Statement st)
    {
        try {
            return st.executeQuery("SELECT * FROM recepe_books");
        } catch (SQLException e) {
            System.out.println("There is no recepe to read.");
            return null;
        }
    }

    public ResultSet tryToReadAppetizer(Statement st, String bookID)
    {
        try {
            return st.executeQuery("SELECT * FROM appetizers " + "WHERE book_id='" + bookID + "'");
        } catch (Exception e) {
            System.out.println("There is no appetizer to read.");
            return null;
        }
    }

    public ResultSet tryToReadSecondMeal(Statement st, String bookID)
    {
        try {
            return st.executeQuery("SELECT * FROM second_meals " + "WHERE book_id='" + bookID + "'");
        } catch (Exception e) {
            System.out.println("There is no second meal to read.");
            return null;
        }
    }

    public ResultSet tryToReadDessert(Statement st, String bookID)
    {
        try {
            return st.executeQuery("SELECT * FROM desserts " + "WHERE book_id='" + bookID + "'");
        } catch (Exception e) {
            System.out.println("There is no dessert to read.");
            return null;
        }
    }

    public ResultSet tryToReadIngredients(Statement st, String foodID)
    {
        try {
            return st.executeQuery("SELECT * FROM food_ingredients " + "WHERE food_id='" + foodID + "'");
        } catch (Exception e) {
            System.out.println("There is no ingredient to read.");
            return null;
        }
    }

    public ResultSet tryToReadSpices(Statement st, String foodID)
    {
        try {
            return st.executeQuery("SELECT * FROM spices " + "WHERE food_id='" + foodID + "'");
        } catch (Exception e) {
            System.out.println("There is no spice to read.");
            return null;
        }
    }
}
