// import java.sql.Connection;
// import java.sql.DatabaseMetaData;
// import java.sql.DriverManager;
// import java.sql.ResultSet;
// import java.sql.SQLException;
// import java.sql.Statement;

// public class PostgreSQLLoader implements ILoader{
//     String url;
//     String user;
//     String password;
//     Connection conn = null;
//     DatabaseMetaData metaData = null;
    
//     public PostgreSQLLoader(String url, String user, String password)
//     {
//         this.url = url;
//         this.user = user;
//         this.password = password;
//     }

//     @Override
//     public Store loadFromFile(String path)
//     {
//         try{
//             conn = DriverManager.getConnection(url, user, password);

//             if (conn != null) {
//                 System.out.println("Connected to the database!");
//             } else {
//                 System.out.println("Failed to make connection!");
//             }

//         } catch (SQLException e) {
//             System.err.printf("SQL State: %s\n%s" ,e.getSQLState() , e.getMessage());
//         } catch (Exception ex) {
//             ex.printStackTrace();
//         }

//         DatabaseMetaData metaData = null;
//         try {
//             metaData = conn.getMetaData();
//             System.out.println("Meta datas loaded from database.");
//         } catch (SQLException e) {
//             e.printStackTrace();
//         }

//         try {
//             Statement st = conn.createStatement();
//             ResultSet resultSet = metaData.getTables(null, null, "recepe_books", null);

//             if(!resultSet.next())
//             {
//                 System.out.println("Table didn't exists (yet).");
//                 st.execute("CREATE TABLE recepe_books(book_id text, book_name text)");
//                 System.out.println("Table created!");
//                 st.execute("INSERT INTO recepe_books() VALUES('"+ time.format(formatter) +"')");
//             }
//             else
//             {
//                 System.out.println("Table already exists!");
//                 st.execute("INSERT INTO created_with_vscode(time) VALUES('"+ time.format(formatter) +"')");
//             }

//             conn.close();
           
//         } catch (SQLException e) {
//             // TODO Auto-generated catch block
//             e.printStackTrace();
//         }
//     }
// }
