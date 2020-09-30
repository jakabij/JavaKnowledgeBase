import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;

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

        //Make connection with DB
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

        //Make statement for more procedure.
        Statement st = null;
        try {
            st = conn.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ResultSet resultSet = null;
        
        //Load in the books.
        resultSet = tryToReadRecepe(st);
    
        if(resultSet != null)
        {
            try
            {
                while (resultSet.next())
                {
                    // to read out the books
                    String bookName = resultSet.getString("book_name");
                    String bookID = resultSet.getString("book_id");
                    ArrayList<Food> booksFoods = new ArrayList<>();

                    RecipeBook book = new RecipeBook(bookName, bookID, booksFoods);

                    // to read out the books' foods
                    /******************************/

                        // read out appetizers
                        /*********************/
                    ResultSet resultSet2 = tryToReadAppetizer(st, bookID);      //HIBAAAAA
                    
                    if(resultSet2 != null)
                    {
                        while (resultSet2.next())
                        {
                            String foodID = resultSet2.getString("a_id");
                            String foodName = resultSet2.getString("appetizer_name");
                            boolean toServeCold = Boolean.parseBoolean(resultSet2.getString("serve_cold"));
                            Time toPepare = Time.valueOf(resultSet2.getString("time_to_prepare"));

                            String[] arrayOfIngredients = resultSet2.getString("ingredients").split(",");
                            ArrayList<String> foodIngredients = new ArrayList<>(Arrays.asList(arrayOfIngredients));

                            Appetizer appetizer =
                                new Appetizer(foodID, foodName, toServeCold, foodIngredients, toPepare);

                            book.addFood(appetizer);
                        }
                    }
                        /*********************/

                        //read out second meals
                        /**********************/
                    resultSet2 = tryToReadSecondMeal(st, bookID);

                    if(resultSet2 != null)
                    {
                        while (resultSet2.next())
                        {
                            String foodID = resultSet2.getString("s_id");
                            String foodName = resultSet2.getString("second_meal_name");
                            boolean toServeCold = Boolean.parseBoolean(resultSet2.getString("serve_cold"));
                            boolean needToCook = Boolean.parseBoolean(resultSet2.getString("need_to_cook"));
                            Time toPepare = Time.valueOf(resultSet2.getString("time_to_prepare"));
                            
                            String[] arrayOfIngredients = resultSet2.getString("ingredients").split(",");
                            ArrayList<String> foodIngredients = new ArrayList<>(Arrays.asList(arrayOfIngredients));

                            String[] arrayOfSpices = resultSet2.getString("spices").split(",");
                            ArrayList<String> foodSpices = new ArrayList<>(Arrays.asList(arrayOfSpices));

                            SecondMeal secondMeal = 
                                new SecondMeal(foodID, foodName, toServeCold, foodIngredients,
                                 needToCook, toPepare, foodSpices);

                            book.addFood(secondMeal);
                        }
                    }
                        /**********************/

                        //read out desserts.
                        /*******************/
                    resultSet2 = tryToReadDessert(st, bookID);

                    if(resultSet2 != null)
                    {
                        while (resultSet2.next())
                        {
                            String foodID = resultSet2.getString("d_id");
                            String foodName = resultSet2.getString("dessert_name");
                            boolean toServeCold = Boolean.parseBoolean(resultSet2.getString("serve_cold"));
                            boolean needToCook = Boolean.parseBoolean(resultSet2.getString("need_to_cook"));
                            Time toPepare = Time.valueOf(resultSet2.getString("time_to_prepare"));
                           
                            String[] arrayOfIngredients = resultSet2.getString("ingredients").split(",");
                            ArrayList<String> foodIngredients = new ArrayList<>(Arrays.asList(arrayOfIngredients));

                            Dessert dessert = 
                                new Dessert(foodID, foodName, toServeCold, foodIngredients,
                                needToCook, toPepare);

                            book.addFood(dessert);
                        }
                    }
                        /*******************/

                    /******************************/

                    store.addRecipeBook(new RecipeBook(bookName, bookID, booksFoods));
                }
            }
            catch (SQLException e) 
            {
                return null;
            }
        }
        try {
            conn.close();
        } catch (SQLException e) {
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
            return st.executeQuery("SELECT * FROM appetizers " + "WHERE book_id LIKE '" + bookID + "'");
        } catch (Exception e) {
            System.out.println("There is no appetizer to read.");
            return null;
        }
    }

    public ResultSet tryToReadSecondMeal(Statement st, String bookID)
    {
        try {
            return st.executeQuery("SELECT * FROM second_meals " + "WHERE book_id LIKE '" + bookID + "'");
        } catch (Exception e) {
            System.out.println("There is no second meal to read.");
            return null;
        }
    }

    public ResultSet tryToReadDessert(Statement st, String bookID)
    {
        try {
            return st.executeQuery("SELECT * FROM desserts " + "WHERE book_id LIKE '" + bookID + "'");
        } catch (Exception e) {
            System.out.println("There is no dessert to read.");
            return null;
        }
    }
}
