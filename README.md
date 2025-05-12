#  Memorise Photo Gallery

A **Java-based photo gallery management system** with **role-based user authentication** and comprehensive photo organization features.

---

##  Overview

**Memorise Photo Gallery** is a **console-based application** that allows users to **efficiently manage digital photo collections**. It implements a **role-based access control system** with distinct features for **admins** and **regular users**, secure authentication, and photo management capabilities like **searching**, **sorting**, and **organizing**.

---

##  Features

###  User Authentication

- **Role-based Access**: Separate login for **administrators** and **regular users**
- **Secure Registration**: Enforced **username and password complexity**
- **Login System**: Secure **credential validation**

###  Admin Features

- **View All Photos** (excluding hidden ones)
- **Create Collages**: Combine multiple photos into named collages
- **Edit Photo Details**: Modify title, date, and file type
- **Hide Photos**: Make photos private (password-protected)
- **View Hidden Photos**: Requires password verification

###  User Features

- **Add Photos**: Upload new photos with metadata
- **Delete Photos**: Remove unwanted photos
- **View Gallery**: Browse visible photos
- **Manage Photo Properties**: Change folder and file type
- **Favorite Management**: Mark/unmark photos as favorites
- **View Favorites**: View favorite photo collection
- **Search Functionality**: Find photos by name or folder
- **Sort Photos**: Sort by ID, name, or date

---

##  Technologies Used

- Java SE
- File-based storage system
- Object-Oriented Programming (OOP)
- Linked Lists (for dynamic photo storage)
- ArrayLists (for temporary photo handling)
- Regular Expressions (for input validation)

---

##  Admin Workflow

As an **administrator**, you can:

- View all **non-hidden** photos
- **Create collages** from selected photos
- **Edit** photo metadata
- **Hide photos** from regular users
- **View hidden photos** with password verification

---

##  User Workflow

As a **regular user**, you can:

- **Add** and manage photos
- **Mark** photos as favorites
- **View** favorites
- **Search** photos by name or folder
- **Sort** photos by ID, name, or date

---

##  Project Structure

- `User` Class: Handles user credentials and roles
- `Photo` Class: Implements linked list node for storing photo information
- `Photogallery` Class: Main application class with all features and functionalities

---

##  Core Data Structures

###  Linked List

- Stores and manages photo objects
- Supports **dynamic size** and **efficient insertion**
- Used for **searching**, **sorting**, and **displaying**

###  ArrayList

- Used for temporary collections like:
  - Hidden photos
  - Collages

###  File-based Persistence

- Custom serialization format
- Text files with **delimited values**
- Lightweight and effective for this application

---

##  Data Model

###  File Structure

- `users.txt`: Stores user credentials and roles
- `Photos.txt`: Stores all photo metadata
- `hidden_images.txt`: Tracks hidden photos
- `collage.txt`: Stores created collages

### Data Storage Format

- **User Records**:  
  `username,password,role`

- **Photo Records**:  
  `id;name;type;folder;dateTime;isFavorite`

- **Hidden Photos**:  
  Simple list of photo names

- **Collages**:  
  `Collage Title → photo1, photo2, ...`

---

##  Security Features

- **Password Requirements**:  
  - Minimum 8 characters  
  - At least 1 digit, 1 lowercase, 1 uppercase, and 1 special character

- **Input Validation**:  
  - Strong validation for all user input

- **Hidden Photo Protection**:  
  - Password verification required to view hidden photos

---
