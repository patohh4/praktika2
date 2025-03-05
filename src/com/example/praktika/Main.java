package com.example.praktika;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

  private static final Logger logger = Logger.getLogger(Main.class.getName());
  // Path to the JSON file for users
  private static final String USERS_FILE = "C:\\Users\\Admin\\IdeaProjects\\praktika\\src\\com\\example\\praktika\\users.json";
  // Path to the JSON file for plants
  private static final String PLANTS_FILE = "C:\\Users\\Admin\\IdeaProjects\\praktika\\src\\com\\example\\praktika\\plants.json";
  private static final ObjectMapper objectMapper = new ObjectMapper(); // Jackson ObjectMapper
  private static Map<String, User> users = new HashMap<>();
  private static Map<String, Plant> plants = new HashMap<>();
  private static User currentUser = null;

  static {
    ConsoleHandler handler = new ConsoleHandler();
    handler.setFormatter(new CustomFormatter());
    logger.addHandler(handler);
    logger.setUseParentHandlers(false);
  }

  public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);
    boolean running = true;

    // Load data
    loadUsers();
    loadPlants();

    while (running) {
      if (currentUser == null) {
        logger.info("\n=== Main Menu ===");
        logger.info("1. Registration");
        logger.info("2. Login");
        logger.info("3. Exit Program");

        System.out.print("Choose an action: ");
        String choice = scanner.nextLine();

        switch (choice) {
          case "1" -> register(scanner);
          case "2" -> login(scanner);
          case "3" -> {
            running = false;
            logger.info("Program terminated.");
          }
          default -> logger.warning("Invalid choice. Please try again.");
        }
      } else {
        logger.info("\n=== Main Menu ===");
        logger.info("1. View Plants");
        logger.info("2. Find Plants by Category");
        logger.info("3. Search Plant by Name");
        if (isAdmin()) {
          logger.info("4. Add Plant (admin)");
          logger.info("5. Edit Plant (admin)");
          logger.info("6. Delete Plant (admin)");
          logger.info("7. View Plant Care Tips");
          logger.info("8. Update Profile");
          logger.info("9. Logout");
          logger.info("10. Exit Program");
        } else {
          logger.info("4. View Plant Care Tips");
          logger.info("5. Update Profile");
          logger.info("6. Logout");
          logger.info("7. Exit Program");
        }

        System.out.print("Choose an action: ");
        String choice = scanner.nextLine();

        if (isAdmin()) {
          switch (choice) {
            case "1" -> viewPlants();
            case "2" -> findPlantsByCategory(scanner);
            case "3" -> searchPlantByName(scanner);
            case "4" -> addPlant(scanner);
            case "5" -> editPlant(scanner);
            case "6" -> deletePlant(scanner);
            case "7" -> viewPlantCareTips();
            case "8" -> updateProfile(scanner);
            case "9" -> logout();
            case "10" -> {
              saveUsers();
              savePlants();
              running = false;
              logger.info("Program terminated.");
            }
            default -> logger.warning("Invalid choice. Please try again.");
          }
        } else {
          switch (choice) {
            case "1" -> viewPlants();
            case "2" -> findPlantsByCategory(scanner);
            case "3" -> searchPlantByName(scanner);
            case "4" -> viewPlantCareTips();
            case "5" -> updateProfile(scanner);
            case "6" -> logout();
            case "7" -> {
              saveUsers();
              savePlants();
              running = false;
              logger.info("Program terminated.");
            }
            default -> logger.warning("Invalid choice. Please try again.");
          }
        }
      }
    }
  }

  private static boolean isAdmin() {
    return currentUser != null && "admin".equals(currentUser.getRole());
  }

  private static void logout() {
    if (currentUser == null) {
      logger.warning("You are not logged in.");
    } else {
      logger.info("You have logged out: " + currentUser.getUsername());
      currentUser = null;
    }
  }

  private static void register(Scanner scanner) {
    logger.info("Enter username:");
    String username = scanner.nextLine();
    logger.info("Enter password:");
    String password = scanner.nextLine();

    if (users.containsKey(username)) {
      logger.warning("A user with this username already exists.");
      return;
    }

    String role = users.isEmpty() ? "admin" : "user";
    User newUser = new User(username, password, role);
    users.put(username, newUser);
    logger.info("Registration successful! Your role: " + role);
    saveUsers();
  }

  private static void login(Scanner scanner) {
    logger.info("Enter username:");
    String username = scanner.nextLine();
    logger.info("Enter password:");
    String password = scanner.nextLine();

    User user = users.get(username);
    if (user != null && password.equals(user.getPassword())) {
      currentUser = user;
      logger.info("Login successful! You are logged in as: " + user.getRole());
    } else {
      logger.warning("Incorrect username or password.");
    }
  }

  private static void viewPlants() {
    if (plants.isEmpty()) {
      logger.info("The plant list is empty.");
    } else {
      logger.info("\n=== Plant List ===");
      for (Plant plant : plants.values()) {
        logger.info("Name: " + plant.getName());
        logger.info("Description: " + plant.getDescription());
        logger.info("Scientific Name: " + plant.getScientificName());
        logger.info("Watering Frequency: " + plant.getWateringFrequency());
        logger.info("Sunlight Requirement: " + plant.getSunlightRequirement());
        logger.info("Soil Type: " + plant.getSoilType());
        logger.info("Category: " + plant.getCategory());
        logger.info("Image URL: " + plant.getImageUrl());
        logger.info("--------------------------");
      }
    }
  }

  private static void addPlant(Scanner scanner) {
    if (!isAdmin()) {
      logger.warning("You do not have permission to add plants! Only an admin can do this.");
      return;
    }

    logger.info("Enter plant name:");
    String name = scanner.nextLine();
    logger.info("Enter plant description:");
    String description = scanner.nextLine();
    logger.info("Enter scientific name:");
    String scientificName = scanner.nextLine();
    logger.info("Enter watering frequency:");
    String wateringFrequency = scanner.nextLine();
    logger.info("Enter sunlight requirement:");
    String sunlightRequirement = scanner.nextLine();
    logger.info("Enter soil type:");
    String soilType = scanner.nextLine();
    logger.info("Enter category (e.g., Flower, Tree, Herb):");
    String category = scanner.nextLine();
    logger.info("Enter image URL:");
    String imageUrl = scanner.nextLine();

    Plant newPlant = new Plant(name, description, scientificName, wateringFrequency,
        sunlightRequirement, soilType, category, imageUrl);
    plants.put(name, newPlant);
    savePlants();
    logger.info("Plant added successfully.");
  }

  private static void editPlant(Scanner scanner) {
    if (!isAdmin()) {
      logger.warning("You do not have permission to edit plants! Only an admin can do this.");
      return;
    }

    logger.info("Enter the name of the plant to edit:");
    String name = scanner.nextLine();

    Plant plant = plants.get(name);
    if (plant == null) {
      logger.warning("Plant not found.");
      return;
    }

    logger.info("Enter new description for the plant:");
    String description = scanner.nextLine();
    logger.info("Enter new scientific name:");
    String scientificName = scanner.nextLine();
    logger.info("Enter new watering frequency:");
    String wateringFrequency = scanner.nextLine();
    logger.info("Enter new sunlight requirement:");
    String sunlightRequirement = scanner.nextLine();
    logger.info("Enter new soil type:");
    String soilType = scanner.nextLine();
    logger.info("Enter new category (e.g., Flower, Tree, Herb):");
    String category = scanner.nextLine();
    logger.info("Enter new image URL:");
    String imageUrl = scanner.nextLine();

    plant.setDescription(description);
    plant.setScientificName(scientificName);
    plant.setWateringFrequency(wateringFrequency);
    plant.setSunlightRequirement(sunlightRequirement);
    plant.setSoilType(soilType);
    plant.setCategory(category);
    plant.setImageUrl(imageUrl);
    savePlants();
    logger.info("Plant edited successfully.");
  }

  private static void deletePlant(Scanner scanner) {
    if (!isAdmin()) {
      logger.warning("You do not have permission to delete plants! Only an admin can do this.");
      return;
    }

    logger.info("Enter the name of the plant to delete:");
    String name = scanner.nextLine();

    Plant plant = plants.remove(name);
    if (plant != null) {
      savePlants();
      logger.info("Plant deleted successfully.");
    } else {
      logger.warning("Plant not found.");
    }
  }

  private static void searchPlantByName(Scanner scanner) {
    logger.info("Enter plant name to search:");
    String name = scanner.nextLine();

    Plant plant = plants.get(name);
    if (plant != null) {
      logger.info("Name: " + plant.getName());
      logger.info("Description: " + plant.getDescription());
      logger.info("Scientific Name: " + plant.getScientificName());
      logger.info("Watering Frequency: " + plant.getWateringFrequency());
      logger.info("Sunlight Requirement: " + plant.getSunlightRequirement());
      logger.info("Soil Type: " + plant.getSoilType());
      logger.info("Category: " + plant.getCategory());
      logger.info("Image URL: " + plant.getImageUrl());
    } else {
      logger.warning("Plant not found.");
    }
  }

  private static void findPlantsByCategory(Scanner scanner) {
    logger.info("Enter category to search (e.g., Flower, Tree, Herb):");
    String category = scanner.nextLine();

    boolean found = false;
    for (Plant plant : plants.values()) {
      if (category.equalsIgnoreCase(plant.getCategory())) {
        logger.info("Name: " + plant.getName());
        logger.info("Description: " + plant.getDescription());
        logger.info("Scientific Name: " + plant.getScientificName());
        logger.info("Watering Frequency: " + plant.getWateringFrequency());
        logger.info("Sunlight Requirement: " + plant.getSunlightRequirement());
        logger.info("Soil Type: " + plant.getSoilType());
        logger.info("Category: " + plant.getCategory());
        logger.info("Image URL: " + plant.getImageUrl());
        logger.info("--------------------------");
        found = true;
      }
    }

    if (!found) {
      logger.warning("No plants found in the category: " + category);
    }
  }

  private static void viewPlantCareTips() {
    logger.info("\n=== Plant Care Tips ===");
    logger.info("1. Water your plants regularly.");
    logger.info("2. Ensure they get enough sunlight.");
    logger.info("3. Use the right type of soil.");
    logger.info("4. Prune your plants to encourage growth.");
    logger.info("5. Keep an eye out for pests.");
  }

  private static void updateProfile(Scanner scanner) {
    if (currentUser == null) {
      logger.warning("You are not logged in.");
      return;
    }

    logger.info("Enter new password:");
    String newPassword = scanner.nextLine();

    currentUser.setPassword(newPassword);
    users.put(currentUser.getUsername(), currentUser);
    saveUsers();
    logger.info("Profile updated successfully.");
  }

  private static void loadPlants() {
    try {
      plants = objectMapper.readValue(new File(PLANTS_FILE),
          new TypeReference<Map<String, Plant>>() {
          });
      logger.info("Plants loaded successfully.");
    } catch (IOException e) {
      logger.log(Level.SEVERE, "Failed to load plants: " + e.getMessage(), e);
    }
  }

  private static void savePlants() {
    try {
      objectMapper.writeValue(new File(PLANTS_FILE), plants);
      logger.info("Plants saved successfully.");
    } catch (IOException e) {
      logger.log(Level.SEVERE, "Error saving plants: " + e.getMessage(), e);
    }
  }

  private static void loadUsers() {
    try {
      users = objectMapper.readValue(new File(USERS_FILE), new TypeReference<Map<String, User>>() {
      });
      logger.info("Users loaded successfully.");
    } catch (IOException e) {
      logger.log(Level.SEVERE, "Failed to load users: " + e.getMessage(), e);
    }
  }

  private static void saveUsers() {
    try {
      objectMapper.writeValue(new File(USERS_FILE), users);
      logger.info("Users saved successfully.");
    } catch (IOException e) {
      logger.log(Level.SEVERE, "Error saving users: " + e.getMessage(), e);
    }
  }

  static class Plant {

    private String name;
    private String description;
    private String scientificName;
    private String wateringFrequency;
    private String sunlightRequirement;
    private String soilType;
    private String category;
    private String imageUrl;

    public Plant() {
    }

    public Plant(String name, String description, String scientificName, String wateringFrequency,
        String sunlightRequirement, String soilType, String category, String imageUrl) {
      this.name = name;
      this.description = description;
      this.scientificName = scientificName;
      this.wateringFrequency = wateringFrequency;
      this.sunlightRequirement = sunlightRequirement;
      this.soilType = soilType;
      this.category = category;
      this.imageUrl = imageUrl;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getDescription() {
      return description;
    }

    public void setDescription(String description) {
      this.description = description;
    }

    public String getScientificName() {
      return scientificName;
    }

    public void setScientificName(String scientificName) {
      this.scientificName = scientificName;
    }

    public String getWateringFrequency() {
      return wateringFrequency;
    }

    public void setWateringFrequency(String wateringFrequency) {
      this.wateringFrequency = wateringFrequency;
    }

    public String getSunlightRequirement() {
      return sunlightRequirement;
    }

    public void setSunlightRequirement(String sunlightRequirement) {
      this.sunlightRequirement = sunlightRequirement;
    }

    public String getSoilType() {
      return soilType;
    }

    public void setSoilType(String soilType) {
      this.soilType = soilType;
    }

    public String getCategory() {
      return category;
    }

    public void setCategory(String category) {
      this.category = category;
    }

    public String getImageUrl() {
      return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
      this.imageUrl = imageUrl;
    }
  }

  static class User {

    private String username;
    private String password;
    private String role;

    public User() {
    }

    public User(String username, String password, String role) {
      this.username = username;
      this.password = password;
      this.role = role;
    }

    public String getUsername() {
      return username;
    }

    public void setUsername(String username) {
      this.username = username;
    }

    public String getPassword() {
      return password;
    }

    public void setPassword(String password) {
      this.password = password;
    }

    public String getRole() {
      return role;
    }

    public void setRole(String role) {
      this.role = role;
    }
  }
}