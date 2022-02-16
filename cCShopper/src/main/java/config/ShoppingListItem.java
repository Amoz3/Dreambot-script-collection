package config;
import org.dreambot.api.methods.grandexchange.LivePrices;
import org.dreambot.api.wrappers.items.Item;


import java.awt.*;

import static org.dreambot.api.methods.MethodProvider.log;

public class ShoppingListItem {
    private int itemID;
    private String itemName;
    private int itemPrice;
    private int itemQuantity;

    // boolean for once you place the order
    private boolean ordered;

    // Constructors
    public ShoppingListItem(String itemName) {
        // checks if itemName contains atleast 1 a-z char, if not we will assume an id has been entered
        if (itemName.matches(".*[a-z].*")) {
            this.itemName = itemName;
        } else {
            this.itemID = Integer.parseInt(itemName);
        }

        if (this.itemName == null) {
            this.itemPrice = LivePrices.get(itemID);
        } else {
            this.itemPrice = LivePrices.get(itemName);
        }

        // negative Quantities will represent ALL
        itemQuantity = -1;
    }

    public ShoppingListItem(String itemName, int quantity) {
        // checks if itemName contains atleast 1 a-z char, if not we will assume an id has been entered
        if (itemName.matches(".*[a-z].*")) {
            this.itemName = itemName;
        } else {
            this.itemID = Integer.parseInt(itemName);
        }

        // if name hasnt been set use id
        if (this.itemName == null) {
            this.itemPrice = LivePrices.get(itemID);
        } else {
            this.itemPrice = LivePrices.get(itemName);
        }

        if (quantity > 0) this.itemQuantity = quantity;
    }


    public ShoppingListItem(String itemName, int quantity, int itemPrice) {
        // checks if itemName contains atleast 1 a-z char, if not we will assume an id has been entered
        if (itemName.matches(".*[a-z].*")) {
            this.itemName = itemName;
        } else {
            this.itemID = Integer.parseInt(itemName);
        }
        if (quantity > 0) this.itemQuantity = quantity;
        this.itemPrice = itemPrice;
    }


    // Accessors
    public int getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(int itemPrice) {
        this.itemPrice = itemPrice;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getItemQuantity() {
        return itemQuantity;
    }

    public void setItemQuantity(int itemQuantity) {
        this.itemQuantity = itemQuantity;
    }

    public boolean isOrdered() {
        return ordered;
    }

    public void setOrdered(boolean ordered) {
        this.ordered = ordered;
    }

    // Methods
    public void print() {
        log("-------------camalCaseShopper-------------");
        if (itemName != null) {
            log( "Item: " + this.itemName);
        } else {
            // i dont think stack matters as this is just to grab the name
            log("Item: " + new Item(itemID, 1).getName());
        }
        log("Quantity: " + this.itemQuantity);
        log("Buy Offer: " + this.itemPrice);
        log("-----------------------------------------------------------");
        log("");
    }


    public void updatePrice() {
        this.itemPrice = LivePrices.get(this.itemName);
    }


}
