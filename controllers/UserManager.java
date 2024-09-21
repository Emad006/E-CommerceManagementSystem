package controllers;

import java.util.ArrayList;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

import interfaces.controllers.IUserManager;
import core.entities.User;
import core.entities.Admin;
import core.entities.Customer;

public class UserManager implements IUserManager {
    private ArrayList<User> userList;

    public UserManager() {
        userList = new ArrayList<User>();
        loadUserToMemory();
    }

    private void loadUserToMemory() {

        // Try to create a file if it doesn't exist
        File file = new File("../database/userData.txt");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Read from file
        try {
            BufferedReader reader = new BufferedReader(new FileReader("../database/userData.txt"));
            String l;
            while ((l = reader.readLine()) != null) {
                String[] parts = l.split("\\^~\\^"); // Use StringBuffer / StringBuilder
                if (parts[3].equals("Admin")) {
                    Admin a = new Admin(parts[0], parts[1], parts[2], parts[3]);
                    userList.add(a);
                } else if (parts[3].equals("Customer")) {
                    Customer c = new Customer(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5], parts[6]);
                    userList.add(c);
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Check if user already exist
    public boolean userExists(String email) {
        for (User u : userList) {
            // Return "true" if user exist.
            if (u.getEmail().equals(email)) {
                return true;
            }
        } 
        // If no user exists, it'll exit the for loop and return "false".
        return false;
    }

    // Check if credentials are valid
    public boolean validCredentials(String email, String password) {
        for (User u : userList) {
            // Return "true" if valid credentials.
            if (u.getEmail().equals(email) && u.getPassword().equals(password)) {
                return true;
            }
        }
        // Return "false" if invalid credentials.
        return false;
    }

    // Add user. Method overloading cause of two types of User (this one is for Admin)
    // TODO: Validate data in front-end
    public void addUser(String name, String email, String password, String role) {
        Admin a = new Admin(name, email, password, role);
        userList.add(a);
        dumpDataToFile();
    }

    // Add user. Method overloading cause of two types of User (this one is for Customer)
    // TODO: Validate data in front-end
    public void addUser(String name, String email, String password, String role, String gender, String contactNo, String address) {
        Customer c = new Customer(name, email, password, role, gender, contactNo, address);
        userList.add(c);
        dumpDataToFile();
    }

    // Delete user
    // TODO: Validate email in front-end || Just check if the userExists
    public void deleteUser(String email) {
        for (User u : userList) {
            if (u.getEmail().equals(email)) {
                userList.remove(u);
                dumpDataToFile();
                break;
            }
        }
    }

    // Search user (return user object)
    // MUST create a new object cause otherwise, if the object reference is returned, that object can be modified, compromising the user data.
    // TODO: Catch NullPointerException in front-end and notify user that the user does not exist.
    public User searchUser(String email) {
        for (User u : userList) {
            if (u.getEmail().equals(email)) {
                if (u instanceof Customer) {
                    Customer c = (Customer) u;
                    User customer = new Customer(c.getName(), c.getEmail(), c.getPassword(), c.getRole(), c.getGender(), c.getContactNo(), c.getAddress());
                    return customer;
                } else if (u instanceof Admin) {
                    Admin a = (Admin) u;
                    User admin = new Admin(a.getName(), a.getEmail(), a.getPassword(), a.getRole());
                    return admin;
                }
            }
        }
        return null;
    }

    // TODO: Catch NullPointerException in front-end and notify user that the user does not exist.
    public String getUserRole(String email) {
        for (User u : userList) {
            if (u.getEmail().equals(email)) {
                return u.getRole();
            }
        }
        return null;
    }

    // Update user (Customer)
    public boolean updateUser(String name, String email, String password, String role, String gender, String contactNo, String address) {
        for (User u : userList) {
            if (u.getEmail().equals(email)) {
                Customer c = (Customer) u;
                c.setName(name);
                c.setEmail(email);
                c.setPassword(password);
                c.setRole(role);
                c.setAddress(address);
                dumpDataToFile();
                return true;
            }
        }
        return false;
    }

    // Re-write the entire array list
    // Currently, everytime there's a change in userList, the contents are dumped to the txt file, prefer to do this only before app shutdown for reduced IO operation
    // However, isn't a big deal for such a small project at the moment.
    // TODO: Reduce IO Operations (Low Priority)
    private void dumpDataToFile() {

        try {
            // Do not pass "true" to FileWriter as I want to overwrite the entire file. Passing "true" would just append to the file.
            BufferedWriter writer = new BufferedWriter(new FileWriter("../database/userData.txt"));
            for (User u : userList) {
                if (u instanceof Admin) {
                    // Type cast back to original object to run object specific methods
                    Admin a = (Admin) u;
                    String t = a.getName() + "^~^" + a.getEmail() + "^~^" + a.getPassword() + "^~^" + a.getRole();
                    writer.write(t);
                    writer.newLine();
                } else if (u instanceof User) {
                    // Type cast back to original object to run object specific methods
                    Customer c = (Customer) u;
                    String t = c.getName() + "^~^" + c.getEmail() + "^~^" + c.getPassword() + "^~^" + c.getRole() + "^~^" + c.getGender() + "^~^" + c.getContactNo() + "^~^" + c.getAddress();
                    writer.write(t);
                    writer.newLine();
                }
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
