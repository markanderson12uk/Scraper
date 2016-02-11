package uk.ac.cedar.screenscraper;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

/**
 * Class to scrape a defined webpage and retrieve products and associated date
 * 
 * @author Mark Anderson
 * 
 * 10/02/16 Initial version
 */
public class Scraper {

    // URL to retrieve
    private String pageUrl = "http://hiring-tests.s3-website-eu-west-1.amazonaws.com/2015_Developer_Scrape/5_products.html";
    
    public static void main(String[] args) {
        
        ArrayList<Product> products = new ArrayList<Product>();
        Scraper s = new Scraper();
        
        try {
            Document doc = Jsoup.connect(s.pageUrl).userAgent("Mozilla").timeout(6000).get();
                        
            products = s.addProductPrices(doc, 
                    s.createProductArray(doc,products)
            );
                        
        } catch (IOException ioe) {
            System.out.println("IOException: " + ioe.toString());
        }
        
        System.out.println(s.createJsonOutput(products));
    }
    
    /**
     * Method to convert the list of products to a JSON object
     * @param list The list of objects
     * @return A JSON formatted string
     * 
     * MA 10/02/16 Initial version
     */
    private String createJsonOutput(ArrayList<Product> list)
    {
        JSONObject obj = new JSONObject();
        JSONArray prodList = new JSONArray();
        double total = 0;
        
        // Create iterator to step through the list
        Iterator it = list.iterator();
        
        // Ensure list isn't empty
        assertTrue("The list has not been populated to create JSON", it.hasNext());
        
        // Loop through the list of products and create the JSON array
        while (it.hasNext())
        {
            // For each product, extract data and add to a JSON object
            Product p = (Product)it.next();
            JSONObject product = new JSONObject();
            product.put("title", p.getTitle());
            product.put("size", p.getSize());
            product.put("unit_price", p.getUnitPrice());
            product.put("description", p.getDescription());
            total += p.getUnitPriceDouble();
            
            prodList.add(product);
        }
        
        // The total unit price should be greater than 0
        assertTrue("Unit costs have not been added", total > 0);
        
        // Add all items from list and total of unit prices
        obj.put("results", prodList);
        
        DecimalFormat df = new DecimalFormat("##.00");
        obj.put("total", df.format(total));
        
        return obj.toJSONString();
    }
    
    /**
     * Method to populate an ArrayList with Product objects
     * that are instantiated from scraping a URL
     * 
     * @param doc The HTML document to scrape
     * @param list An empty ArrayList to populate
     * @return ArrayList of products retrieved
     * @throws IOException 
     * 
     * MA 10/2/16 Initial version
     */
    private ArrayList<Product> createProductArray(Document doc, ArrayList<Product> list) throws IOException
    {
        // String to use to find product description
        String searchFor = "<h3 class=\"productDataItemHeader\">Description</h3>\n" +
                                    "<div class=\"productText\">\n" +
                                    "<p>";
        if (null != list)
        {
            // Retrieve all div tags of class productInfo
            Elements productsInfo = doc.select("div.productInfo");
            
            // Test that there are products
            assertNotNull("Products have not been found", productsInfo);
            
            // Loop through all tags retrieved from above
            for(Element e : productsInfo)
            {
                // Create new product
                Product p = new Product();
                
                // Retrieve link to associated data as first a tag that 
                // is a child of product info tag
                Element detail = e.select("a").first();
                String href = detail.attr("href");
                
                // Set the product title
                String title = detail.ownText();
                p.setTitle(title);
                
                // Set the product description
                // Retrieve document, search for Description as text in a tag
                // Find first <p> tag and extract its text
                String linkedDoc = Jsoup.connect(href).execute().body();
                int index = linkedDoc.indexOf(searchFor);
                int start = index + searchFor.length();
                int end = linkedDoc.indexOf("<", start);
                String description = linkedDoc.substring(start,end);              
                p.setDescription(description);
                
                // Set the product page size
                // Count number of bytes in response
                // and divide by 1024 to get kb
                p.setSize(linkedDoc.getBytes().length / 1024);
                
                // Add to list of products
                list.add(p);
            } 
        }
        
        // Test that list has items in it
        assertTrue("No products are in the list", list.size() > 0);
        
        return list;
    }
    
    /**
     * Method to add item prices to Product objects 
     * stored in an ArrayList
     * 
     * @param doc The HTML document to scrape
     * @param list An empty ArrayList to populate
     * @return ArrayList of products retrieved
     * 
     * MA 10/2/16 Initial version
     **/
    private ArrayList<Product> addProductPrices(Document doc, ArrayList<Product> list)
    {
        // The list should have items in it
        assertNotNull("The list is not populated", list);
        
        // Ensure list isn't empty
        if (null != list)
        {
            // Retrieve all p tags of class pricePerUnit
            Elements productsPrices = doc.select("p.pricePerUnit");
            // Create iterator to loop through ArrayList
            Iterator it = list.iterator();
            
            // Test that prices have been found
            assertNotNull("There are no prices in the document", productsPrices);
            
            // Loop through all tags retrieved from above
            for(Element e : productsPrices)
            {
                // Retrieve product
                Product p = (Product)it.next();

                // Retrieve price and remove pound symbol encoding
                String price = e.ownText();
                if (price.startsWith("&pound"))
                {
                    price = price.substring(6);
                }
                
                // Convert to double and set as product price
                double d_price = Double.parseDouble(price);
                p.setUnitPrice(d_price);
                
                // Check that there are more products
                // and if not then stop iterating
                if (!it.hasNext())
                {
                    break;
                }
            }
        }
        return list;
    }
    
    /**
     * JUnit test runner
     */
    @Test
    public void executeScrape()
    {        
        ArrayList<Product> products = new ArrayList<Product>();
        Scraper s = new Scraper();
        
        try {
            Document doc = Jsoup.connect(s.pageUrl).userAgent("Mozilla").timeout(6000).get();
            
            // Unit test to ensure a document is received
            assertNotNull("A document has not been retrieved", doc);
            
            products = s.addProductPrices(doc, 
                    s.createProductArray(doc,products)
            );
            
            // Test to ensure products have been obtained
            assertTrue("0 products found", products.size() > 0);
            
        } catch (IOException ioe) {
            System.out.println("IOException: " + ioe.toString());
        }
        
        System.out.println(s.createJsonOutput(products));
    }
}
