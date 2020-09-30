import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

public class XMLLoader implements ILoader {

    private String filePath = ".\\ProjectData\\ids.txt";

    @Override
    public Store loadFromFile(String path) throws Exception {
        File file = new File(path);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            Document doc;

            doc = dBuilder.parse(file);
            doc.getDocumentElement().normalize();

            Store store = new Store();
            ArrayList<String> idsToStore = new ArrayList<>();

            //We collect all of the recepe books to a list.
            NodeList recepeBookList = doc.getElementsByTagName("RecepeBook");

            for(int count = 0; count < recepeBookList.getLength(); count++)
            {
                RecipeBook book;
                Node  node = recepeBookList.item(count);

                Element recepeBook = (Element) node;
                String bookName = recepeBook.getAttribute("name");
                String bookId = recepeBook.getAttribute("id");

                idsToStore.add(bookId);

                ArrayList<Food> listOfFoods = new ArrayList<>();

                //get all the foods of the current book
                NodeList foodList = node.getChildNodes();

                for(int count2 = 0; count2 < foodList.getLength(); count2++)
                {
                    Node node2 = foodList.item(count2);

                    if(node2.getNodeType() == Node.ELEMENT_NODE)
                    {
                        Element food = (Element) node2;
                        String foodName = food.getAttribute("name");
                        String foodId = food.getAttribute("id");
                        boolean foodToServeCold = Boolean.parseBoolean(food.getAttribute("toServeCold"));
                        Time foodTimeToPrepare = Time.valueOf(food.getAttribute("timeToPrepare"));
                        String foodType = food.getAttribute("foodType");
                       
                        idsToStore.add(foodId);

                        if(foodType.equals("SecondMeal"))
                        {
                            NodeList ingredientsAndSpices = food.getChildNodes();

                            //collect datas to separated lists
                            ArrayList<String> listOfIngredients = new ArrayList<>();
                            ArrayList<String> listOfSpices = new ArrayList<>();

                            for(int count3 = 0; count3 < ingredientsAndSpices.getLength(); count3++)
                            {
                                Node node3 = ingredientsAndSpices.item(count3);

                                if(node3.getNodeType() == Node.ELEMENT_NODE)
                                {
                                    Element ingredientOrSpice = (Element) node3;
                                    if(node3.getNodeName().equals("Ingredient"))
                                    {
                                        listOfIngredients.add(ingredientOrSpice.getTextContent());
                                    }
                                    else
                                    {
                                        listOfSpices.add(ingredientOrSpice.getTextContent());
                                    }
                                }
                            }
                            boolean foodNeedToCook = Boolean.parseBoolean(food.getAttribute("needToCook"));

                            SecondMeal secondMeal = new SecondMeal(foodId, foodName, foodToServeCold, listOfIngredients,
                            foodNeedToCook, foodTimeToPrepare, listOfSpices);

                            listOfFoods.add(secondMeal);
                        }
                        else
                        {
                            NodeList ingredients = food.getChildNodes();

                            ArrayList<String> listOfIngredients = new ArrayList<>();

                            for(int count3 = 0; count3 < ingredients.getLength(); count3++)
                            {
                                Node node3 = ingredients.item(count3);

                                if(node3.getNodeType() == Node.ELEMENT_NODE)
                                {
                                    Element ingredient = (Element) node3;
                                    listOfIngredients.add(ingredient.getTextContent());
                                }
                            }

                            if(foodType.equals("Dessert"))
                            {
                                boolean foodNeedToCook = Boolean.parseBoolean(food.getAttribute("needToCook"));

                                Dessert dessert = new Dessert(foodId, foodName, foodToServeCold, listOfIngredients,
                                foodNeedToCook, foodTimeToPrepare);

                                listOfFoods.add(dessert);
                            }
                            else
                            {
                                Appetizer appetizer = new Appetizer(foodId, foodName, foodToServeCold, listOfIngredients,
                                foodTimeToPrepare);

                                listOfFoods.add(appetizer);
                            }
                        }
                    }
                }
                book = new RecipeBook(bookName, bookId, listOfFoods);
                store.addRecipeBook(book);
            }

            saveIdToIDStore(filePath, idsToStore);

            return store;

            }
        catch (SAXException | IOException | ParserConfigurationException e)
        {
            throw new Exception(e);
        }
    }

    public void saveIdToIDStore(String filePath, ArrayList<String> ids){
        try
        {
            File file = new File(filePath);

            Scanner fileScanner = new Scanner(file);
            String newFileData = "";

            //get the loaded ids first
            for(var id : ids){
                newFileData += (id +"\n");
            }

            //get existing data from the text file
            while(fileScanner.hasNextLine())
            {
                String data = fileScanner.nextLine();
                
                if(!ids.contains(data)){
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