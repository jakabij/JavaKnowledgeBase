import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class PostgreSQLSave implements ISaver{
    String url;
    String user;
    String password;
    Store store;
    Connection conn = null;
    DatabaseMetaData metaData = null;
    
    public PostgreSQLSave(String url, String user, String password, Store store)
    {
        this.url = url;
        this.user = user;
        this.password = password;
        this.store = store;
    }

    @Override
	public void saveToFile(String path, Store store)
    {
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

        try {
            Statement st = conn.createStatement();
            ResultSet resultSet = metaData.getTables(null, null, "recepe_books", null);

            if(!resultSet.next())
            {
                System.out.println("Table didn't exists (yet).");
                st.execute("CREATE TABLE recepe_books(book_id text, book_name text)");
                System.out.println("Table created!");
            }

            for(var book : store.getListOfRecipeBooks())
            {
                st.execute("INSERT INTO recepe_books(book_id, book_name) "+
                "VALUES('" + book.getId() + "', '" + book.getName() + "')");

                Statement stFood = conn.createStatement();
                ResultSet resultSetFood = null;

                for(var food : book.getListOfFoods())
                {
                    Statement stSpice = conn.createStatement();
                    ResultSet resultSetSpice = null;

                    if(food instanceof Appetizer)
                    {
                        Appetizer appetizer = (Appetizer) food;
                        resultSetFood = metaData.getTables(null, null, "appetizers", null);
                        if(!resultSetFood.next())
                        {
                            stFood.execute("CREATE TABLE appetizers(book_id text, id text, appetizer_name text, "+ 
                                ", serve_cold boolean,time_to_prepare time)");
                        }

                        stFood.execute("INSERT INTO appetizers(book_id, id, appetizer_name, "+
                        "serve_cold,time_to_prepare) VALUES('" + book.getId() + "', '" +
                        appetizer.getId() + "', '" + appetizer.getNameOfFood() + "', " +
                        appetizer.isServeCold() + ", " + appetizer.getTimeToPrepare() + ")");
                    }
                    else if(food instanceof Dessert)
                    {
                        Dessert dessert = (Dessert) food;
                        resultSetFood = metaData.getTables(null, null, "desserts", null);

                        if(!resultSetFood.next())
                        {
                            stFood.execute("CREATE TABLE desserts(book_id text, id text, dessert_name text, "+ 
                                "serve_cold boolean, need_to_cook boolean, time_to_prepare time)");
                        }

                        stFood.execute("INSERT INTO desserts(book_id, id, dessert_name, " +
                        "serve_cold, need_to_cook, time_to_prepare) "+ 
                        "VALUES('" + book.getId() + "', '" + dessert.getId() + "', '" + dessert.getNameOfFood() + "', " +
                        dessert.isServeCold() + ", " +dessert.isNeedToCook() + ", " + dessert.getTimeToPrepare() +
                        ")"); 
                    }
                    else
                    {
                        SecondMeal secondMeal = (SecondMeal) food;
                        resultSetFood = metaData.getTables(null, null, "second_meals", null);

                        if(!resultSetFood.next())
                        {
                            stFood.execute("CREATE TABLE second_meals(book_id text, id text, second_meal_name text, "+ 
                                ", serve_cold boolean, need_to_cook boolean, time_to_prepare time)");
                        }

                        stFood.execute("INSERT INTO second_meals(book_id, id, second_meal_name, " +
                            "serve_cold, need_to_cook, time_to_prepare) " +
                            "VALUES('" + book.getId() + "', '" + secondMeal.getId() + "', '" + secondMeal.getNameOfFood() + "', " +
                            secondMeal.isServeCold() + ", " + secondMeal.isNeedToCook() + ", " +
                            secondMeal.getTimeToPrepare() + ")");

                        for(var spice : secondMeal.getListOfSpices())
                        {
                            resultSetSpice = metaData.getTables(null, null, "spices", null);
                            if(!resultSetSpice.next())
                            {
                                stSpice.execute("CREATE TABLE spices(food_id text, spice_name text");
                            }

                            stSpice.execute("INSERT INTO spices(food_id, spice_name) " +
                            "VALUES('" + secondMeal.getId() + 
                            ", " + spice + "')");
                        }
                    }
                }
            }
            conn.close();
           
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
