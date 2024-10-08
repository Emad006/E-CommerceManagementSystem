package controllers;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Iterator;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

import core.entities.Product;
import interfaces.controllers.IProductManager;

public class ProductManager implements IProductManager {
    private ArrayList<Product> productList;

    public ProductManager() {
        productList = new ArrayList<Product>();
        loadProductToMemory();
    }

    private void loadProductToMemory() {
        // Try to create file if it doesn't exist
        File file = new File("../database/productData.txt");
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Read from file
        try {
            BufferedReader reader = new BufferedReader(new FileReader("../database/productData.txt"));
            String l;
            while ((l = reader.readLine()) != null) {
                String[] parts = l.split("\\^~\\^");
                Product p = new Product(Integer.parseInt(parts[0]), parts[1], Double.parseDouble(parts[2]), Integer.parseInt(parts[3]), parts[4], parts[5], parts[6]);
                productList.add(p);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }    
    }

    // TODO: Validate ID in front-end
    public boolean productExists(int id) {
        for (Product p : productList) {
            if (p.getID() == id) {
                return true;
            }
        }
        return false;
    }

    // Add product with default image
    // TODO: Validate data in front-end
    public void addProduct(int id, String name, double price, int stock, String category, String desc) {
        Product p = new Product(id, name, price, stock, category, desc);
        productList.add(p);
        dumpDataToFile();
    }

    // Add product with custom image
    // TODO: Validate data in front-end
    public void addProduct(int id, String name, double price, int stock, String category, String desc, String imagePath) {
        Product p = new Product(id, name, price, stock, category, desc, imagePath);
        productList.add(p);
        dumpDataToFile();
    }

    // Edit product with default image
    public void editProduct(int id, String name, double price, int stock, String category, String desc) {
        for (Product p : productList) {
            if (p.getID() == id) {
                p.setName(name);
                p.setPrice(price);
                p.setStock(stock);
                p.setCategory(category);
                p.setDescription(desc);
                p.setImagePath();
                dumpDataToFile();
                break;
            }
        }
    }

    // Edit product with custom image
    public void editProduct(int id, String name, double price, int stock, String category, String desc, String imagePath) {
        for (Product p : productList) {
            if (p.getID() == id) {
                p.setName(name);
                p.setPrice(price);
                p.setStock(stock);
                p.setCategory(category);
                p.setDescription(desc);
                p.setImagePath(imagePath);
                dumpDataToFile();
                break;
            }
        }
    }

    // Delete product
    // TODO: Validate ID in front-end
    public void deleteProduct(int id) {
        Iterator<Product> iterator = productList.iterator();
        while (iterator.hasNext()) {
            Product p = iterator.next();
            if (p.getID() == id) {
                iterator.remove();
                dumpDataToFile();
                return;
            }
        }
    }

    // TODO: Validate ID in front-end
    // Catch NullPointerException and notify user that the product does not exist.
    public Product searchProduct(int id) {
        for (Product p : productList) {
            if (p.getID() == id) {
                Product prod = new Product(p.getID(), p.getName(), p.getPrice(), p.getStock(), p.getCategory(), p.getDescription(), p.getImagePath());
                return prod;
            }
        }
        return null;
    }

    public ArrayList<Product> getAllProducts() {
        ArrayList<Product> duplicateProductList = new ArrayList<Product>();
        for (Product p : productList) {
            Product prod = new Product(p.getID(), p.getName(), p.getPrice(), p.getStock(), p.getCategory(), p.getDescription(), p.getImagePath());
            duplicateProductList.add(prod);
        }
        return duplicateProductList;
    }

    public String[] getAllCategories() {
        LinkedHashSet<String> categories = new LinkedHashSet<String>();
        categories.add("Category");
        for (Product p : productList) {
            categories.add(p.getCategory());
        }
        return categories.toArray(new String[0]);
    }

    // Get 2D array for table
    public String[][] getDataForTable() {
        String[][] data = new String[productList.size()][7];
        int i = 0;
        for (Product p : productList) {
            data[i][0] = Integer.toString(p.getID());
            data[i][1] = p.getName();
            data[i][2] = Double.toString(p.getPrice());
            data[i][3] = Integer.toString(p.getStock());
            data[i][4] = p.getCategory();
            data[i][5] = p.getDescription();
            data[i][6] = p.getImagePath();
            i++;
        }
        return data;
    }

    // Update product stock after checkout
    public void deductStock(int id, int quantity) {
        for (Product p : productList) {
            if (p.getID() == id) {
                p.setStock(p.getStock() - quantity);
                dumpDataToFile();
                break;
            }
        }
    }

    // Copying entire array list to text file
    private void dumpDataToFile() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("../database/productData.txt"));
            for (Product p : productList) {
                String t = p.getID() + "^~^" + p.getName() + "^~^" + p.getPrice() + "^~^" + p.getStock() + "^~^" + p.getCategory() + "^~^" + p.getDescription() + "^~^" + p.getImagePath();
                writer.write(t);
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
