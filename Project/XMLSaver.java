package JavaKnowledgeBase.Project;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XMLSaver implements ISaver {

    @Override
    public void saveToFile(String path, Store store) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            Document doc = docBuilder.newDocument();
            //Make Store node (root in this case)
            Element storeElement = doc.createElement("Store");
            doc.appendChild(storeElement);

            //Make book nodes
            for(var recepeBook : store.getListOfRecipeBooks())
            {
                Element book = doc.createElement("RecepeBook");
                storeElement.appendChild(book);

                //Make the book attributes
                Attr bookid = doc.createAttribute("id");
                Attr bookName = doc.createAttribute("name");
                bookid.setValue(recepeBook.getId());
                bookName.setValue(recepeBook.getName());

                book.setAttributeNode(bookid);
                book.setAttributeNode(bookName);

                //Make a node in book too for listing the foods
                for(var booksFood : recepeBook.getListOfFoods())
                {
                    Element food = doc.createElement("Food");
                    book.appendChild(food);

                    Attr foodId = doc.createAttribute("id");
                    Attr foodName = doc.createAttribute("name");
                    Attr foodServeCold = doc.createAttribute("toServeCold");
                    
                    foodId.setValue(booksFood.getId());
                    foodName.setValue(booksFood.getNameOfFood());
                    foodServeCold.setValue(Boolean.toString(booksFood.isServeCold()));

                    food.setAttributeNode(foodId);
                    food.setAttributeNode(foodName);
                    food.setAttributeNode(foodServeCold);

                    //add the ingredients as elements
                    for(var ingredient : booksFood.getListOfIngredients())
                    {
                        Element foodIngredient = doc.createElement("Ingredient");
                        food.appendChild(foodIngredient);

                        foodIngredient.setTextContent(ingredient);
                    }


                    if(booksFood instanceof Appetizer)
                    {
                        Appetizer appetizer = (Appetizer) booksFood;

                        Attr foodType = doc.createAttribute("foodType");
                        Attr foodTimeToPrepare = doc.createAttribute("timeToPrepare");

                        foodType.setValue("Appetizer");
                        foodTimeToPrepare.setValue(appetizer.getTimeToPrepare().toString());

                        food.setAttributeNode(foodTimeToPrepare);
                        food.setAttributeNode(foodType);
                    }
                    else if(booksFood instanceof Dessert)
                    {
                        Dessert dessert = (Dessert) booksFood;

                        Attr foodType = doc.createAttribute("foodType");
                        Attr foodNeedToCook = doc.createAttribute("needToCook");
                        Attr foodTimeToPrepare = doc.createAttribute("timeToPrepare");
                        
                        foodType.setValue("Dessert");
                        foodNeedToCook.setValue(Boolean.toString(dessert.isNeedToCook()));
                        foodTimeToPrepare.setValue(dessert.getTimeToPrepare().toString());

                        food.setAttributeNode(foodType);
                        food.setAttributeNode(foodNeedToCook);
                        food.setAttributeNode(foodTimeToPrepare);
                    }
                    else
                    {
                        SecondMeal sMeal = (SecondMeal) booksFood;

                        Attr foodType = doc.createAttribute("foodType");
                        Attr foodNeedToCook = doc.createAttribute("needToCook");
                        Attr foodTimeToPrepare = doc.createAttribute("timeToPrepare");

                        foodType.setValue("SecondMeal");
                        foodNeedToCook.setValue(Boolean.toString(sMeal.isNeedToCook()));
                        foodTimeToPrepare.setValue(sMeal.getTimeToPrepare().toString());

                        food.setAttributeNode(foodType);
                        food.setAttributeNode(foodNeedToCook);
                        food.setAttributeNode(foodTimeToPrepare);

                        //create spice element for this food type
                        for(var spice : sMeal.getListOfSpices())
                        {
                            Element foodsSpice = doc.createElement("Spice");
                            food.appendChild(foodsSpice);

                            foodsSpice.setTextContent(spice);
                        }
                    }
                }
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(path));

            transformer.transform(source, result);

        } catch (ParserConfigurationException | TransformerException pce) {
            System.out.println("UsersXML: Error trying to instantiate DocumentBuilder " + pce);
        }
    }
}
