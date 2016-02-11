/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.cedar.screenscraper;

import java.text.DecimalFormat;

/**
 * Class to hold data for each product on a page
 * @author mark_
 */
public class Product {
    private String title;
    private double size;
    private double unitPrice;
    private String description;
    
    protected void setTitle(final String title)
    {
        this.title = title;
    }

    protected void setUnitPrice(final double unitPrice)
    {
        this.unitPrice = unitPrice;
    }

    protected void setSize(final double size)
    {
        this.size = size;
    }

    protected void setDescription(final String description)
    {
        this.description = description;
    }
    
    protected String getTitle()
    {
        return this.title;
    }
    
     protected String getDescription()
    {
        return this.description;
    }

    // Convert to correct number format and return as a string
    protected String getUnitPrice()
    {
        DecimalFormat df = new DecimalFormat("##.00");
        return df.format(this.unitPrice);
    }
    
    protected double getUnitPriceDouble()
    {
        return this.unitPrice;
    }

    // Return size as a String
    protected String getSize()
    {
        return new String(this.size + "kb");
    }

}
