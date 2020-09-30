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
    
    public PostgreSQLSave(String url, String user, String password, Store store){
        this.url = url;
        this.user = user;
        this.password = password;
        this.store = store;
    }

    @Override
	public void saveToFile(String path, Store store){
        //Try to make the connection to DB
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

        //Try to get the metaDatas which we'll use to get the existing tables.
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

            //Examine if the recepe_books table already exists.
            if(resultSet.next()){
                UI ui = new UI();
                ui.getInfo("Table already exists.");
                String choice = ui.getInputFromUser("Do you really want to drop those?");

                if(choice.toLowerCase().equals("yes") || choice.toLowerCase().equals("y")){       
                    st.execute("DELETE from recepe_books");
                }
            }else{
                st.execute("CREATE TABLE recepe_books(" +
                "book_id text PRIMARY KEY, " + 
                "book_name text, " +
                "UNIQUE (book_id))");
            }

            //Iterate through the book list (Store's).
            for(var book : store.getListOfRecipeBooks()){
                st.execute("INSERT INTO recepe_books(book_id, book_name) "+
                "VALUES('" + book.getId() + "', '" + book.getName() + "')");


                //Here we create the food tables if those didn't exist.
                /*******************************************************/
                resultSet = metaData.getTables(null, null, "second_meals", null);

                if(!resultSet.next()){
                    st.execute("CREATE TABLE second_meals(" + 
                        "book_id text REFERENCES recepe_books(book_id) ON DELETE CASCADE, "+
                        "s_id text PRIMARY KEY, " + 
                        "second_meal_name text, " + 
                        "serve_cold boolean, " + 
                        "need_to_cook boolean, " + 
                        "time_to_prepare time, " +
                        "ingredients text, " +
                        "spices text, " +
                        "UNIQUE (s_id))");
                }


                resultSet = metaData.getTables(null, null, "desserts", null);

                if(!resultSet.next()){
                    st.execute("CREATE TABLE desserts(" +
                        "book_id text REFERENCES recepe_books(book_id) ON DELETE CASCADE," +
                        "d_id text PRIMARY KEY, " +
                        "dessert_name text, " + 
                        "serve_cold boolean, " +
                        "need_to_cook boolean, " +
                        "time_to_prepare time, "+
                        "ingredients text, " +
                        "UNIQUE (d_id))");
                }


                resultSet = metaData.getTables(null, null, "appetizers", null);

                if(!resultSet.next()){
                    st.execute("CREATE TABLE appetizers(" + 
                        "book_id text," +
                        "a_id text PRIMARY KEY, " +
                        "appetizer_name text, "+ 
                        "serve_cold boolean, " +
                        "time_to_prepare time, " +
                        "ingredients text, " +
                        "UNIQUE (a_id))");

                    st.execute("ALTER TABLE appetizers ADD CONSTRAINT book_id FOREIGN KEY (book_id)" +
                    " REFERENCES recepe_books (book_id) ON DELETE CASCADE");
                }
                /*******************************************************/


                //Iterate through the book's foods.
                for(var food : book.getListOfFoods()){

                    //Iterate through the ingredients of food.
                    /*****************************************/
                    String ingredientsArray = "";

                    for(var ingredient : food.getListOfIngredients()){
                        ingredientsArray += ( ingredient + ", " );
                    }
                    ingredientsArray = ingredientsArray.substring(0, ingredientsArray.length() - 2);
                    /*****************************************/


                    //Check the food's type and create like that.
                    /*******************************************/
                    if(food instanceof Appetizer){
                        Appetizer appetizer = (Appetizer) food;
                        
                        st.execute("INSERT INTO appetizers(" +
                            "book_id, a_id, appetizer_name, serve_cold, time_to_prepare, ingredients) "+
                            "VALUES('" + 
                            book.getId() + "', '" +
                            appetizer.getId() + "', '" + 
                            appetizer.getNameOfFood() + "', " +
                            appetizer.isServeCold() + ", '" +
                            appetizer.getTimeToPrepare() + "', '" +
                            ingredientsArray +"')");
                    }else if(food instanceof Dessert){
                        Dessert dessert = (Dessert) food;

                        st.execute("INSERT INTO desserts(" +
                            "book_id, d_id, dessert_name, serve_cold, need_to_cook, time_to_prepare, ingredients) "+ 
                            "VALUES('" + 
                            book.getId() + "', '" + 
                            dessert.getId() + "', '" + 
                            dessert.getNameOfFood() + "', " +
                            dessert.isServeCold() + ", " +
                            dessert.isNeedToCook() + ", '" +
                            dessert.getTimeToPrepare() + "', '" +
                            ingredientsArray + "')"); 
                    }
                    else{
                        SecondMeal secondMeal = (SecondMeal) food;

                        String spicesArray = "";
                        for(var spice : secondMeal.getListOfSpices()){
                           spicesArray += ( spice + ", ");
                        }
                        spicesArray = spicesArray.substring(0, spicesArray.length() - 1);

                        st.execute("INSERT INTO second_meals(" +
                            "book_id, s_id, second_meal_name, serve_cold, need_to_cook, "+
                            "time_to_prepare, ingredients, spices ) " +
                            "VALUES('" + 
                            book.getId() + "', '" + 
                            secondMeal.getId() + "', '" + 
                            secondMeal.getNameOfFood() + "', " +
                            secondMeal.isServeCold() + ", " +
                            secondMeal.isNeedToCook() + ", '" +
                            secondMeal.getTimeToPrepare() + "', '" +
                            ingredientsArray + "', '" +
                            spicesArray + "')");
                    }
                }
                /*******************************************/
            }
            conn.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
}
