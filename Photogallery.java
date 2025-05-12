import java.io.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * User class representing system users with authentication credentials and role
 */
class User {
    String username;
    String password;
    String role; // "admin" or "user"

    /**
     * Constructor for creating a user with specified credentials and role
     *
     * @param username The user's unique identifier
     * @param password The user's password
     * @param role     The user's role ("admin" or "user")
     */
    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }
}

/**
 * Photo class representing image entries in the gallery
 * Implemented as a node in a linked list
 */
class Photo {
    int id;
    String name;
    String type;
    String folder;
    String dateTime;
    boolean isFavourite;
    Photo next; // Reference to next photo in linked list

    /**
     * Constructor for creating a photo with all attributes
     *
     * @param id          Unique identifier for the photo
     * @param name        Name/title of the photo
     * @param type        File type (jpg, png)
     * @param folder      Storage folder path
     * @param dateTime    Date and time when photo was added
     * @param isFavourite Whether photo is marked as favorite
     */
    public Photo(int id, String name, String type, String folder, String dateTime, boolean isFavourite) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.folder = folder;
        this.dateTime = dateTime;
        this.isFavourite = isFavourite;
        this.next = null;
    }

    /**
     * Get photo title/name
     *
     * @return The photo's title/name
     */
    public String getTitle() {
        return name;
    }

    /**
     * Set photo title/name
     *
     * @param title New title for the photo
     */
    public void setTitle(String title) {
        this.name = title;
    }

    /**
     * Set photo date
     *
     * @param date New date for the photo
     */
    public void setDate(String date) {
        this.dateTime = date;
    }

    /**
     * Set photo type
     *
     * @param type New type for the photo
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Display formatted photo details to console
     */
    public void display() {
        System.out.println("--------------------------------------------------");
        System.out.println(" ID       : " + id);
        System.out.println(" Name     : " + name);
        System.out.println(" Type     : " + type);
        System.out.println(" Folder   : " + folder);
        System.out.println(" DateTime : " + dateTime);
        System.out.println(" Favourite: " + (isFavourite ? "Yes" : "No"));
        System.out.println("--------------------------------------------------");
    }
}

/**
 * Main Photogallery class containing application logic and execution
 */
public class Photogallery {

    // Static variables for maintaining application state
    static Photo head = null; // Head of the linked list of photos
    static int idCounter = 1; // Counter for generating unique IDs
    static User currentUser = null; // Store the current logged in user globally

    // Constants for password validation
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$");

    // Constants for username validation
    private static final int MIN_USERNAME_LENGTH = 4;
    private static final Pattern USERNAME_PATTERN =
            Pattern.compile("^[a-zA-Z0-9_]{4,20}$");

    // Constants for photo name validation
    private static final int MAX_PHOTO_NAME_LENGTH = 50;
    private static final Pattern PHOTO_NAME_PATTERN =
            Pattern.compile("^[a-zA-Z0-9\\s_-]{1,50}$");

    // Constants for folder name validation
    private static final Pattern FOLDER_NAME_PATTERN =
            Pattern.compile("^[a-zA-Z0-9\\s-/]{1,100}$");


    /**
     * Validates username according to rules and checks for uniqueness
     *
     * @param username Username to validate
     * @return true if username is valid, false otherwise
     */
    static boolean isValidUsername(String username) {
        // Check for null or too short username
        if (username == null || username.length() < MIN_USERNAME_LENGTH) {
            System.out.println("Username must be at least " + MIN_USERNAME_LENGTH + " characters long.");
            return false;
        }

        // Check username against pattern requirements
        Matcher matcher = USERNAME_PATTERN.matcher(username);
        if (!matcher.matches()) {
            System.out.println("Username can only contain letters, numbers, and underscores, and must be 4-20 characters long.");
            return false;
        }

        // Check if username already exists
        try (BufferedReader br = new BufferedReader(new FileReader("users.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 1 && parts[0].equals(username)) {
                    System.out.println("Username already exists. Please choose another one.");
                    return false;
                }
            }
        } catch (IOException e) {
            // If file doesn't exist, username is available
            if (!new File("users.txt").exists()) {
                return true;
            }
            System.out.println("Error checking username: " + e.getMessage());
            return false;
        }

        return true;
    }

    /**
     * Validates password according to security requirements
     *
     * @param password Password to validate
     * @return true if password is valid, false otherwise
     */
    static boolean isValidPassword(String password) {
        // Check for null or too short password
        if (password == null || password.length() < MIN_PASSWORD_LENGTH) {
            System.out.println("Password must be at least " + MIN_PASSWORD_LENGTH + " characters long.");
            return false;
        }

        // Check password against pattern requirements
        Matcher matcher = PASSWORD_PATTERN.matcher(password);
        if (!matcher.matches()) {
            System.out.println("Password must contain at least one digit, one lowercase letter, " +
                    "one uppercase letter, one special character (@#$%^&+=!), and no whitespace.");
            return false;
        }

        return true;
    }

    /**
     * Validates a photo name according to requirements
     *
     * @param name Photo name to validate
     * @return true if name is valid, false otherwise
     */
    static boolean isValidPhotoName(String name) {
        if (name == null || name.isEmpty()) {
            System.out.println("Photo name cannot be empty.");
            return false;
        }

        if (name.length() > MAX_PHOTO_NAME_LENGTH) {
            System.out.println("Photo name cannot exceed " + MAX_PHOTO_NAME_LENGTH + " characters.");
            return false;
        }

        // Check name against pattern requirements
        Matcher matcher = PHOTO_NAME_PATTERN.matcher(name);
        if (!matcher.matches()) {
            System.out.println("Photo name can only contain letters, numbers, spaces, underscores, and hyphens.");
            return false;
        }

        return true;
    }

    /**
     * Validates a folder name according to requirements
     *
     * @param folder Folder name to validate
     * @return true if folder name is valid, false otherwise
     */
    static boolean isValidFolderName(String folder) {
        if (folder == null || folder.isEmpty()) {
            System.out.println("Folder name cannot be empty.");
            return false;
        }

        // Check folder name against pattern requirements
        Matcher matcher = FOLDER_NAME_PATTERN.matcher(folder);
        if (!matcher.matches()) {
            System.out.println("Folder name can only contain letters, numbers, spaces, underscores, hyphens, and forward slashes.");
            return false;
        }

        return true;
    }

    /**
     * Validates that the input is a valid photo type
     *
     * @param type Photo type to validate
     * @return true if type is valid, false otherwise
     */
    static boolean isValidPhotoType(String type) {
        if (type == null || type.isEmpty()) {
            System.out.println("Photo type cannot be empty.");
            return false;
        }

        // Convert to lowercase for case-insensitive comparison
        type = type.toLowerCase();
        if (!type.equals("png") && !type.equals("jpg")) {
            System.out.println("Only 'png' and 'jpg' types are allowed.");
            return false;
        }

        return true;
    }

    /**
     * User registration function - creates new user accounts
     *
     * @param sc Scanner for input
     * @return true if registration was successful, false otherwise
     */
    static boolean registerUser(Scanner sc, String roleType) {
        System.out.println("======= " + roleType.toUpperCase() + " Registration =======");

        // Get and validate username
        String username;
        do {
            System.out.print("Enter username (4-20 characters, letters, numbers, underscore only): ");
            username = sc.nextLine().trim();
        } while (!isValidUsername(username));

        // Get and validate password
        String password;
        do {
            System.out.print("Enter password (min 8 chars, must include digit, uppercase, lowercase, special char): ");
            password = sc.nextLine();
        } while (!isValidPassword(password));

        // Role is fixed based on initial selection - no need to prompt
        String role = roleType;
        System.out.println("Registering as: " + role);

        // Save to users.txt
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("users.txt", true))) {
            writer.write(username + "," + password + "," + role);
            writer.newLine();
            System.out.println("Registration successful as " + role + "!");
            return true;
        } catch (IOException e) {
            System.out.println("Error saving user: " + e.getMessage());
            return false;
        }
    }

    /**
     * User login function - authenticates users against stored credentials
     *
     * @param sc Scanner for input
     * @param roleType The role type selected at start ("admin" or "user")
     * @return User object if authentication successful, null otherwise
     */
    static User loginUser(Scanner sc, String roleType) {
        System.out.println("\n======= " + roleType.toUpperCase() + " Login =======");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.print("Choose option: ");

        // Get login choice with validation
        int loginChoice;
        try {
            loginChoice = Integer.parseInt(sc.nextLine().trim());
            if (loginChoice != 1 && loginChoice != 2) {
                System.out.println("Invalid option. Please enter 1 or 2.");
                return null;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return null;
        }

        if (loginChoice == 2) {
            registerUser(sc, roleType); // Pass the role type to registration
            System.out.println("Please login with your new credentials.");
        }

        // Get login credentials
        System.out.print("Username: ");
        String username = sc.nextLine().trim();
        System.out.print("Password: ");
        String password = sc.nextLine();

        // Validate credentials against stored users - with role check
        try (BufferedReader br = new BufferedReader(new FileReader("users.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String fileUser = parts[0];
                    String filePass = parts[1];
                    String fileRole = parts[2];

                    // Check credentials AND that the role matches the selected role type
                    if (fileUser.equals(username) && filePass.equals(password)) {
                        if (fileRole.equals(roleType)) {
                            System.out.println("Login successful as " + roleType.toUpperCase() + "!");
                            return new User(username, password, fileRole);
                        } else {
                            System.out.println("This account is registered as a " + fileRole +
                                    ", but you selected " + roleType + ".");
                            return null;
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading users.txt: " + e.getMessage());
        }

        System.out.println("Invalid credentials or account not found for role: " + roleType);
        return null;
    }

    /**
     * Main application entry point
     *
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // First select role - this is the key addition
        System.out.println("\n======================================");
        System.out.println("   Welcome to Memorise Photo Gallery ");
        System.out.println("======================================\n");
        System.out.println("Are you an admin or a user?");
        System.out.println("1. Admin");
        System.out.println("2. User");
        System.out.print("Select your role (1-2): ");

        // Get role choice with validation
        int roleChoice;
        String roleType;
        try {
            roleChoice = Integer.parseInt(sc.nextLine().trim());
            if (roleChoice != 1 && roleChoice != 2) {
                System.out.println("Invalid option. Please restart the application.");
                sc.close();
                return;
            }
            roleType = (roleChoice == 1) ? "admin" : "user";
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please restart the application.");
            sc.close();
            return;
        }

        // Login loop - continue until successful login under the selected role
        User loggedInUser = null;
        while (loggedInUser == null) {
            loggedInUser = loginUser(sc, roleType);
        }
        currentUser = loggedInUser; // Store the user globally

        loadPhotos(); // Load photos from file

        int choice;

        // Main program loop - show menu based on user role
        do {
            System.out.println("\n╔════════════════════════════════════╗");
            System.out.println("║          PHOTO GALLERY MENU        ║");
            System.out.println("╠════════════════════════════════════╣");

            if (loggedInUser.role.equals("admin")) {
                // Admin menu options
                System.out.println("║ 1. View All Photos                 ║");
                System.out.println("║ 2. Create Collage                  ║");
                System.out.println("║ 3. Edit Image Details              ║");
                System.out.println("║ 4. Hide Image                      ║");
                System.out.println("║ 5. View Hidden Photos              ║");
                System.out.println("║ 6. Exit                            ║");
            } else {
                // Regular user menu options
                System.out.println("║ 1. Add Photo                       ║");
                System.out.println("║ 2. Delete Photo                    ║");
                System.out.println("║ 3. View All Photos                 ║");
                System.out.println("║ 4. Change Folder/Type              ║");
                System.out.println("║ 5. Manage Favourite                ║");
                System.out.println("║ 6. View Favourites                 ║");
                System.out.println("║ 7. Search Photo                    ║");
                System.out.println("║ 8. Sort Photos                     ║");
                System.out.println("║ 9. Exit                            ║");
            }

            System.out.println("╚════════════════════════════════════╝");
            System.out.print("Enter your choice: ");

            // Get and validate menu choice
            try {
                choice = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                choice = 0; // Invalid choice will show menu again
                continue;
            }

            if (loggedInUser.role.equals("admin")) {
                // Admin menu options
                switch (choice) {
                    case 1:
                        viewAllPhotos();
                        break;
                    case 2:
                        createCollage(sc);
                        break;
                    case 3:
                        editPhoto(sc);
                        break;
                    case 4:
                        hidePhoto(sc);
                        break;
                    case 5:
                        viewHiddenPhotos(sc);
                        break;
                    case 6:
                        savePhotos();
                        System.out.println("\nThank you for using Memorise Gallery. Goodbye!");
                        break;
                    default:
                        System.out.println("Invalid choice. Please enter a number between 1 and 6.");
                }
            } else {
                // Regular user menu options
                switch (choice) {
                    case 1:
                        addPhoto(sc);
                        break;
                    case 2:
                        deletePhoto(sc);
                        break;
                    case 3:
                        viewAllPhotos();
                        break;
                    case 4:
                        changeTypeOrFolder(sc);
                        break;
                    case 5:
                        manageFavourite(sc);
                        break;
                    case 6:
                        viewFavourites();
                        break;
                    case 7:
                        searchPhoto(sc);
                        break;
                    case 8:
                        sortPhotos(sc);
                        break;
                    case 9:
                        savePhotos();
                        System.out.println("\nThank you for using Memorise Gallery. Goodbye!");
                        break;
                    default:
                        System.out.println("Invalid choice. Please enter a number between 1 and 9.");
                }
            }
        } while ((loggedInUser.role.equals("admin") && choice != 6) ||
                (loggedInUser.role.equals("user") && choice != 9));

        sc.close();
    }

    /**
     * View hidden photos after password verification
     *
     * @param sc Scanner for input
     */
    static void viewHiddenPhotos(Scanner sc) {
        System.out.println("\nView Hidden Photos - Password Required");
        System.out.print("Enter your password to view hidden photos: ");
        String password = sc.nextLine();

        // Verify password matches current user's password
        if (!password.equals(currentUser.password)) {
            System.out.println("Incorrect password. Access denied.");
            return;
        }

        // Password correct, proceed to show hidden photos
        ArrayList<String> hidden = loadHiddenPhotos();

        if (hidden.isEmpty()) {
            System.out.println("There are no hidden photos in hidden_images.txt file.");
            return;
        }

        System.out.println("\nHidden Photos:");
        Photo temp = head;

        // Create a list of hidden photos that exist in the gallery
        List<Photo> hiddenPhotos = new ArrayList<>();
        while (temp != null) {
            if (hidden.contains(temp.getTitle().toLowerCase())) {
                hiddenPhotos.add(temp);
            }
            temp = temp.next;
        }

        // Display the hidden photos that exist in the gallery
        if (hiddenPhotos.isEmpty()) {
            System.out.println("No hidden photos found that match existing photos in the gallery.");
        } else {
            System.out.println("Found " + hiddenPhotos.size() + " hidden photos:");
            for (Photo photo : hiddenPhotos) {
                System.out.println("(HIDDEN) ");
                displayPhoto(photo);
            }
        }
    }

    /**
     * Create a collage from selected photos (admin only)
     *
     * @param sc Scanner for input
     */
    static void createCollage(Scanner sc) {
        System.out.println("\nCreate Collage");

        // Validate there are photos to create a collage from
        if (head == null) {
            System.out.println("No photos available to create collage.");
            return;
        }

        // Get number of photos for collage with validation
        int n;
        try {
            System.out.print("Enter number of photos to include in collage (1-10): ");
            n = Integer.parseInt(sc.nextLine().trim());
            if (n <= 0 || n > 10) {
                System.out.println("Please enter a number between 1 and 10.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return;
        }

        List<String> collagePhotos = new ArrayList<>();

        // Collect photo titles for the collage
        for (int i = 0; i < n; i++) {
            System.out.print("Enter title of photo " + (i + 1) + ": ");
            String title = sc.nextLine().trim();

            if (title.isEmpty()) {
                System.out.println("Title cannot be empty. Try again.");
                i--; // Retry this iteration
                continue;
            }

            boolean found = false;
            Photo temp = head;
            while (temp != null) {
                if (temp.getTitle().equalsIgnoreCase(title)) {
                    collagePhotos.add(title);
                    found = true;
                    break;
                }
                temp = temp.next;
            }

            if (!found) {
                System.out.println("Photo titled '" + title + "' not found. Try again.");
                i--; // Retry this iteration
            }
        }

        // Get collage title with validation
        String collageTitle;
        do {
            System.out.print("Enter collage title (1-50 characters): ");
            collageTitle = sc.nextLine().trim();
            if (collageTitle.isEmpty()) {
                System.out.println("Collage title cannot be empty.");
            } else if (collageTitle.length() > 50) {
                System.out.println("Collage title cannot exceed 50 characters.");
            }
        } while (collageTitle.isEmpty() || collageTitle.length() > 50);

        // Save collage to file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("collage.txt", true))) {
            writer.write("Collage: " + collageTitle + " → " + String.join(", ", collagePhotos));
            writer.newLine();
            System.out.println("Collage saved successfully.");
        } catch (IOException e) {
            System.out.println("Error saving collage: " + e.getMessage());
        }
    }

    /**
     * Edit photo details (admin only)
     *
     * @param sc Scanner for input
     */
    static void editPhoto(Scanner sc) {
        // Check if there are photos to edit
        if (head == null) {
            System.out.println("Gallery is empty. No photos to edit.");
            return;
        }

        System.out.print("Enter the title of the photo to edit: ");
        String title = sc.nextLine().trim();

        if (title.isEmpty()) {
            System.out.println("Title cannot be empty.");
            return;
        }

        Photo temp = head;
        boolean found = false;

        // Find the photo to edit
        while (temp != null) {
            if (temp.getTitle().equalsIgnoreCase(title)) {
                found = true;
                System.out.println("Editing photo: " + temp.getTitle());

                // Get new title (optional)
                System.out.print("New title (leave blank to keep current): ");
                String newTitle = sc.nextLine().trim();

                // Validate new title if provided
                if (!newTitle.isEmpty() && !isValidPhotoName(newTitle)) {
                    System.out.println("Invalid title format. Edit canceled.");
                    return;
                }

                // Get new date (optional)
                System.out.print("New date (leave blank to keep current, format YYYY-MM-DD HH:MM:SS): ");
                String newDate = sc.nextLine().trim();

                // Validate date format if provided
                if (!newDate.isEmpty()) {
                    // Simple date format validation
                    if (!newDate.matches("^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$")) {
                        System.out.println("Invalid date format. Should be YYYY-MM-DD HH:MM:SS. Edit canceled.");
                        return;
                    }
                }

                // Get new type (optional)
                System.out.print("New type (leave blank to keep current, png/jpg): ");
                String newType = sc.nextLine().trim().toLowerCase();

                // Validate type if provided
                if (!newType.isEmpty() && !isValidPhotoType(newType)) {
                    System.out.println("Invalid photo type. Edit canceled.");
                    return;
                }

                // Apply changes if provided
                if (!newTitle.isEmpty()) temp.setTitle(newTitle);
                if (!newDate.isEmpty()) temp.setDate(newDate);
                if (!newType.isEmpty()) temp.setType(newType);

                savePhotos();
                System.out.println("Photo details updated successfully.");
                break;
            }
            temp = temp.next;
        }

        if (!found) {
            System.out.println("Photo titled '" + title + "' not found.");
        }
    }

    /**
     * Hide a photo by adding it to hidden_images.txt
     *
     * @param sc Scanner for input
     */
    static void hidePhoto(Scanner sc) {
        // Check if there are photos to hide
        if (head == null) {
            System.out.println("Gallery is empty. No photos to hide.");
            return;
        }

        System.out.print("Enter title of photo to hide: ");
        String title = sc.nextLine().trim();

        if (title.isEmpty()) {
            System.out.println("Title cannot be empty.");
            return;
        }

        boolean found = false;
        Photo temp = head;

        // Find the photo to hide
        while (temp != null) {
            if (temp.getTitle().equalsIgnoreCase(title)) {
                found = true;

                // Check if photo is already hidden
                ArrayList<String> hidden = loadHiddenPhotos();
                if (hidden.contains(title.toLowerCase())) {
                    System.out.println("Photo '" + title + "' is already hidden.");
                    return;
                }

                // Add to hidden_images.txt
                try (BufferedWriter writer = new BufferedWriter(new FileWriter("hidden_images.txt", true))) {
                    writer.write(title.toLowerCase()); // Store in lowercase for consistent matching
                    writer.newLine();
                    System.out.println("Photo '" + title + "' marked as hidden.");
                } catch (IOException e) {
                    System.out.println("Error writing to hidden_images.txt: " + e.getMessage());
                }
                return;
            }
            temp = temp.next;
        }

        if (!found) {
            System.out.println("Photo titled '" + title + "' not found.");
        }
    }

    /**
     * Load the list of hidden photos from file
     *
     * @return ArrayList of hidden photo names (lowercase)
     */
    static ArrayList<String> loadHiddenPhotos() {
        ArrayList<String> hidden = new ArrayList<>();
        File hiddenFile = new File("hidden_images.txt");

        // Create file if it doesn't exist
        if (!hiddenFile.exists()) {
            try {
                hiddenFile.createNewFile();
                System.out.println("Created new hidden_images.txt file.");
            } catch (IOException e) {
                System.out.println("Error creating hidden_images.txt file: " + e.getMessage());
            }
            return hidden;
        }

        // Read hidden photo names from file
        try (BufferedReader reader = new BufferedReader(new FileReader(hiddenFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    hidden.add(line.trim().toLowerCase());
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading hidden_images.txt: " + e.getMessage());
        }

        return hidden;
    }

    /**
     * Get current date and time formatted as string
     *
     * @return Formatted date and time string
     */
    static String getCurrentDateTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.now().format(formatter);
    }

    /**
     * Add a new photo to the gallery
     *
     * @param sc Scanner for input
     */
    static void addPhoto(Scanner sc) {
        // Get and validate photo name
        String name;
        do {
            System.out.print("Enter photo name: ");
            name = sc.nextLine().trim();
        } while (!isValidPhotoName(name));

        // Get and validate photo type
        String type;
        do {
            System.out.print("Enter photo type (png/jpg): ");
            type = sc.nextLine().toLowerCase().trim();
        } while (!isValidPhotoType(type));

        // Get and validate folder name
        String folder;
        do {
            System.out.print("Enter folder name: ");
            folder = sc.nextLine().trim();
        } while (!isValidFolderName(folder));

        // Check for duplicate photo in same folder
        Photo temp = head;
        while (temp != null) {
            if (temp.name.equalsIgnoreCase(name) && temp.folder.equalsIgnoreCase(folder)) {
                System.out.println("Photo with the same name already exists in this folder.");
                return;
            }
            temp = temp.next;
        }

        // Create and add new photo
        String dateTime = getCurrentDateTime();
        Photo newPhoto = new Photo(idCounter, name, type, folder, dateTime, false);
        idCounter++;

        if (head == null) {
            head = newPhoto;
        } else {
            temp = head;
            while (temp.next != null) temp = temp.next;
            temp.next = newPhoto;
        }

        savePhotos(); // Save after adding

        System.out.println("\nPhoto added successfully! Details:");
        displayPhoto(newPhoto);
    }

    /**
     * View all photos excluding hidden ones
     */
    static void viewAllPhotos() {
        ArrayList<String> hidden = loadHiddenPhotos();
        System.out.println("\n All Photos (Excluding Hidden):");

        // Check if there are photos to display
        if (head == null) {
            System.out.println("No photos available in the gallery.");
            return;
        }

        int count = 0;
        Photo temp = head;
        while (temp != null) {
            if (!hidden.contains(temp.getTitle().toLowerCase())) {
                displayPhoto(temp);
                count++;
            }
            temp = temp.next;
        }

        if (count == 0) {
            System.out.println("No visible photos to display. All photos might be hidden.");
        } else {
            System.out.println("Total visible photos: " + count);
        }
    }

    /**
     * Method to delete a photo from the gallery based on ID or name
     *
     * @param sc Scanner object for user input
     */
    static void deletePhoto(Scanner sc) {
        // Check if gallery is empty
        if (head == null) {
            System.out.println("Gallery is empty.");
            return;
        }

        try {
            // Get deletion method choice from user
            System.out.print("Delete by 1.ID or 2.Name? Enter choice (1-2): ");
            int opt = getValidIntInput(sc, 1, 2);
            boolean found = false;

            if (opt == 1) {
                // Delete by ID
                System.out.print("Enter ID to delete: ");

                int did = getValidPositiveIntInput(sc);

                // Handle deletion of head node
                if (head.id == did) {
                    head = head.next;
                    found = true;
                } else {
                    // Traverse the list to find the photo
                    Photo prev = head;
                    Photo curr = head.next;
                    while (curr != null) {
                        if (curr.id == did) {
                            prev.next = curr.next;
                            found = true;
                            break;
                        }
                        prev = curr;
                        curr = curr.next;
                    }
                }
            } else {
                // Delete by Name
                System.out.print("Enter Name to delete: ");
                String dname = sc.nextLine().trim();

                if (dname.isEmpty()) {
                    System.out.println("Name cannot be empty.");
                    return;
                }

                // Store the name for potential hidden list removal
                String photoNameToRemove = dname;

                // Handle deletion of head node
                if (head.name.equalsIgnoreCase(dname)) {
                    head = head.next;
                    found = true;
                } else {
                    // Traverse the list to find the photo
                    Photo prev = head;
                    Photo curr = head.next;
                    while (curr != null) {
                        if (curr.name.equalsIgnoreCase(dname)) {
                            prev.next = curr.next;
                            found = true;
                            break;
                        }
                        prev = curr;
                        curr = curr.next;
                    }
                }

                // Also remove from hidden list if found
                if (found) {
                    removeFromHiddenList(photoNameToRemove);
                }
            }

            // Update IDs and save to file if a photo was deleted
            if (found) {
                reassignIds();
                savePhotos();
                System.out.println("Photo deleted and IDs reassigned.");
            } else {
                System.out.println("Photo not found.");
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a number.");
            sc.nextLine(); // Clear the buffer
        } catch (Exception e) {
            System.out.println("Error deleting photo: " + e.getMessage());
        }
    }

    /**
     * Helper method to remove a photo name from the hidden photos list
     *
     * @param photoName Name of the photo to remove from hidden list
     */
    static void removeFromHiddenList(String photoName) {
        if (photoName == null || photoName.trim().isEmpty()) {
            return; // Skip if name is null or empty
        }

        ArrayList<String> hidden = loadHiddenPhotos();
        if (hidden.contains(photoName.toLowerCase())) {
            hidden.remove(photoName.toLowerCase());

            // Rewrite the hidden_images.txt file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("hidden_images.txt"))) {
                for (String name : hidden) {
                    writer.write(name);
                    writer.newLine();
                }
            } catch (IOException e) {
                System.out.println("Error updating hidden_images.txt: " + e.getMessage());
            }
        }
    }

    /**
     * Reassigns sequential IDs to all photos in the gallery
     */
    static void reassignIds() {
        Photo temp = head;
        int i = 1;
        while (temp != null) {
            temp.id = i++;
            temp = temp.next;
        }
        idCounter = i; // Update the counter for new photos
    }

    /**
     * Displays details of a photo in a formatted manner
     *
     * @param p Photo object to display
     */
    static void displayPhoto(Photo p) {
        if (p == null) {
            System.out.println("Cannot display: Photo is null");
            return;
        }

        System.out.println("--------------------------------------------------");
        System.out.println(" ID       : " + p.id);
        System.out.println(" Name     : " + p.name);
        System.out.println(" Type     : " + p.type);
        System.out.println(" Folder   : " + p.folder);
        System.out.println(" DateTime : " + p.dateTime);
        System.out.println(" Favourite: " + (p.isFavourite ? "Yes" : "No"));
        System.out.println("--------------------------------------------------");
    }

    /**
     * Manages the favorite status of a photo
     *
     * @param sc Scanner object for user input
     */
    static void manageFavourite(Scanner sc) {
        // Check if gallery is empty
        if (head == null) {
            System.out.println("Gallery is empty.");
            return;
        }

        try {
            System.out.print("Enter ID of the photo: ");
            int id = getValidPositiveIntInput(sc);

            // Search for the photo with matching ID
            Photo temp = head;
            boolean found = false;
            while (temp != null) {
                if (temp.id == id) {
                    found = true;
                    System.out.println("1. Mark as Favourite");
                    System.out.println("2. Unmark as Favourite");
                    System.out.print("Enter your choice (1-2): ");
                    int choice = getValidIntInput(sc, 1, 2);

                    if (choice == 1) {
                        if (!temp.isFavourite) {
                            temp.isFavourite = true;
                            savePhotos();
                            System.out.println("Photo marked as favourite.");
                        } else {
                            System.out.println("Photo is already marked as favourite.");
                        }
                    } else { // choice == 2
                        if (temp.isFavourite) {
                            temp.isFavourite = false;
                            savePhotos();
                            System.out.println("Photo unmarked as favourite.");
                        } else {
                            System.out.println("Photo is already not a favourite.");
                        }
                    }
                    break;
                }
                temp = temp.next;
            }

            if (!found) {
                System.out.println("Photo not found.");
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a number.");
            sc.nextLine(); // Clear the buffer
        } catch (Exception e) {
            System.out.println("Error managing favourites: " + e.getMessage());
        }
    }

    /**
     * Displays all photos marked as favorites (excluding hidden photos)
     */
    static void viewFavourites() {
        try {
            ArrayList<String> hidden = loadHiddenPhotos(); // Also exclude hidden from favorites view
            Photo temp = head;
            boolean found = false;

            while (temp != null) {
                if (temp.isFavourite && !hidden.contains(temp.getTitle().toLowerCase())) {
                    displayPhoto(temp);
                    found = true;
                }
                temp = temp.next;
            }

            if (!found) {
                System.out.println("No favourite photos to display.");
            }
        } catch (Exception e) {
            System.out.println("Error viewing favourites: " + e.getMessage());
        }
    }

    /**
     * Changes the type or folder of a photo
     *
     * @param sc Scanner object for user input
     */
    static void changeTypeOrFolder(Scanner sc) {
        // Check if gallery is empty
        if (head == null) {
            System.out.println("Gallery is empty.");
            return;
        }

        try {
            System.out.print("Enter ID of the photo to modify: ");
            int id = getValidPositiveIntInput(sc);

            // Search for the photo with matching ID
            Photo temp = head;
            boolean found = false;
            while (temp != null) {
                if (temp.id == id) {
                    found = true;
                    System.out.println("What do you want to change?");
                    System.out.println("1. Change Folder");
                    System.out.println("2. Change Type");
                    System.out.println("3. Change Both");
                    System.out.print("Enter your choice (1-3): ");
                    int choice = getValidIntInput(sc, 1, 3);

                    boolean updated = false;

                    // Change folder if option 1 or 3 selected
                    if (choice == 1 || choice == 3) {
                        System.out.print("Enter new folder name: ");
                        String newFolder = sc.nextLine().trim();

                        if (newFolder.isEmpty()) {
                            System.out.println("Folder name cannot be empty.");
                        } else if (newFolder.equalsIgnoreCase(temp.folder)) {
                            System.out.println("Photo exists in the same folder.");
                        } else {
                            temp.folder = newFolder;
                            updated = true;
                        }
                    }

                    // Change type if option 2 or 3 selected
                    if (choice == 2 || choice == 3) {
                        System.out.print("Enter new type: ");
                        String newType = sc.nextLine().trim();

                        if (newType.isEmpty()) {
                            System.out.println("Type cannot be empty.");
                        } else if (newType.equalsIgnoreCase(temp.type)) {
                            System.out.println("Photo exists in the same type.");
                        } else {
                            temp.type = newType;
                            updated = true;
                        }
                    }

                    if (updated) {
                        savePhotos();
                        System.out.println("Photo updated successfully.");
                    } else {
                        System.out.println("No changes were made.");
                    }
                    break;
                }
                temp = temp.next;
            }

            if (!found) {
                System.out.println("Photo not found.");
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a number.");
            sc.nextLine(); // Clear the buffer
        } catch (Exception e) {
            System.out.println("Error changing photo details: " + e.getMessage());
        }
    }

    /**
     * Searches for photos by name or folder
     *
     * @param sc Scanner object for user input
     */
    static void searchPhoto(Scanner sc) {
        try {
            System.out.print("Enter name or folder to search: ");
            String query = sc.nextLine().trim().toLowerCase();

            if (query.isEmpty()) {
                System.out.println("Search query cannot be empty.");
                return;
            }

            ArrayList<String> hidden = loadHiddenPhotos(); // Exclude hidden photos from search results
            Photo temp = head;
            boolean found = false;

            while (temp != null) {
                if ((temp.name.toLowerCase().contains(query) || temp.folder.toLowerCase().contains(query))
                        && !hidden.contains(temp.getTitle().toLowerCase())) {
                    displayPhoto(temp);
                    found = true;
                }
                temp = temp.next;
            }

            if (!found) {
                System.out.println("No matching visible photo found.");
            }
        } catch (Exception e) {
            System.out.println("Error searching photos: " + e.getMessage());
        }
    }

    /**
     * Sorts photos by ID, Name, or DateTime
     *
     * @param sc Scanner object for user input
     */
    static void sortPhotos(Scanner sc) {
        try {
            // Check if there are enough photos to sort
            if (head == null || head.next == null) {
                System.out.println("Not enough photos to sort.");
                return;
            }

            System.out.println("Sort by:\n1. ID\n2. Name\n3. DateTime");
            System.out.print("Enter your choice (1-3): ");
            int sortChoice = getValidIntInput(sc, 1, 3);

            // Use bubble sort to sort the linked list
            for (Photo i = head; i != null; i = i.next) {
                for (Photo j = i.next; j != null; j = j.next) {
                    boolean swap = false;

                    switch (sortChoice) {
                        case 1: // Sort by ID
                            if (i.id > j.id) swap = true;
                            break;

                        case 2: // Sort by Name using String's compareToIgnoreCase
                            if (i.name.compareToIgnoreCase(j.name) > 0) swap = true;
                            break;

                        case 3: // Sort by DateTime
                            if (i.dateTime.compareTo(j.dateTime) > 0) swap = true;
                            break;
                    }

                    if (swap) {
                        // Swap all fields except next pointer
                        int tempId = i.id;
                        String tempName = i.name;
                        String tempType = i.type;
                        String tempFolder = i.folder;
                        String tempDateTime = i.dateTime;
                        boolean tempFav = i.isFavourite;

                        i.id = j.id;
                        i.name = j.name;
                        i.type = j.type;
                        i.folder = j.folder;
                        i.dateTime = j.dateTime;
                        i.isFavourite = j.isFavourite;

                        j.id = tempId;
                        j.name = tempName;
                        j.type = tempType;
                        j.folder = tempFolder;
                        j.dateTime = tempDateTime;
                        j.isFavourite = tempFav;
                    }
                }
            }

            System.out.println("Photos sorted successfully.");
            savePhotos(); // Save the sorted list to file
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a number.");
            sc.nextLine(); // Clear the buffer
        } catch (Exception e) {
            System.out.println("Error sorting photos: " + e.getMessage());
        }
    }

    /**
     * Saves the photo gallery to a file
     */
    static void savePhotos() {
        try (PrintWriter pw = new PrintWriter(new FileWriter("Photos.txt"))) {
            Photo temp = head;
            while (temp != null) {
                pw.println(temp.id + ";" + temp.name + ";" + temp.type + ";" + temp.folder + ";" + temp.dateTime + ";" + temp.isFavourite);
                temp = temp.next;
            }
            // Success message removed to avoid cluttering console during internal operations
        } catch (IOException e) {
            System.out.println("Error saving gallery: " + e.getMessage());
        }
    }

    /**
     * Loads the photo gallery from a file
     */
    static void loadPhotos() {
        File file = new File("Photos.txt");
        if (!file.exists()) {
            System.out.println("Photos.txt not found. Starting with empty gallery.");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 6) {
                    try {
                        int id = Integer.parseInt(parts[0]);
                        String name = parts[1];
                        String type = parts[2];
                        String folder = parts[3];
                        String dateTime = parts[4];
                        boolean isFavourite = Boolean.parseBoolean(parts[5]);

                        Photo newPhoto = new Photo(id, name, type, folder, dateTime, isFavourite);
                        if (head == null) {
                            head = newPhoto;
                        } else {
                            Photo temp = head;
                            while (temp.next != null) temp = temp.next;
                            temp.next = newPhoto;
                        }
                        idCounter = Math.max(idCounter, id + 1);
                    } catch (NumberFormatException e) {
                        System.out.println("Skipping invalid entry in Photos.txt: " + line);
                    }
                } else {
                    System.out.println("Skipping malformed entry in Photos.txt: " + line);
                }
            }
            System.out.println("Gallery loaded successfully.");
        } catch (IOException e) {
            System.out.println("Error loading gallery: " + e.getMessage());
        }
    }

    /**
     * Utility method to get a valid integer input within a specified range
     *
     * @param sc  Scanner object for input
     * @param min Minimum acceptable value
     * @param max Maximum acceptable value
     * @return Valid integer input within range
     */
    static int getValidIntInput(Scanner sc, int min, int max) {
        int input;
        while (true) {
            try {
                input = sc.nextInt();
                sc.nextLine(); // Clear the buffer

                if (input >= min && input <= max) {
                    return input;
                } else {
                    System.out.print("Please enter a number between " + min + " and " + max + ": ");
                }
            } catch (InputMismatchException e) {
                System.out.print("Invalid input. Please enter a number between " + min + " and " + max + ": ");
                sc.nextLine(); // Clear the buffer
            }
        }
    }

    /**
     * Utility method to get a valid positive integer input
     *
     * @param sc Scanner object for input
     * @return Valid positive integer
     */
    static int getValidPositiveIntInput(Scanner sc) {
        int input;
        while (true) {
            try {
                input = sc.nextInt();
                sc.nextLine(); // Clear the buffer

                if (input > 0) {
                    return input;
                } else {
                    System.out.print("Please enter a positive number: ");
                }
            } catch (InputMismatchException e) {
                System.out.print("Invalid input. Please enter a positive number: ");
                sc.nextLine(); // Clear the buffer
            }
        }
    }

    /**
     * Loads the list of hidden photos from file
     *
     * @return Set containing names of hidden photos (lowercase)
     */

}