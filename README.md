# Food Ordering App - ICT3214 Group Project 🍔📱

## 📌 Project Overview

This is an Android mobile application developed for the **ICT3214 - Mobile Application Development** module. The application allows users to browse a food menu, view item details, add items to a cart, and place orders seamlessly.

The system is built using **Java** and **SQLite** for local data storage, ensuring a smooth user experience without requiring an external payment gateway. We followed a **Vertical Slicing** approach to ensure all team members contributed to the UI, Logic, and Database tiers.

---

## 🚀 Core Features

* **User Authentication:** Secure Login and Registration system with **SHA-256 password encryption** (Strict Guideline Requirement).
* **Session Management:** Auto-login and secure session handling using `SharedPreferences`.
* **Modern UI/UX:** Premium design implementing **Material Design Components**, `CardViews`, and a `BottomNavigationView` for seamless app traversal.
* **Menu Management:** Dynamic food catalog fetched from SQLite and displayed using customized `RecyclerView` adapters.
* **Cart & Order System:** Functional UI for adding items to the cart, modifying quantities, and proceeding to checkout.
* **Data Privacy & Integrity:** User-specific order filtering using Foreign Keys mapping Orders to User IDs.

---

## 📂 Project Architecture & Structure

```
com.example.foodorderingapp
│
├── activities/       # Contains all UI controllers (LoginActivity, MainActivity, FoodDetailActivity, etc.)
├── adapters/         # Contains RecyclerView adapters (FoodAdapter)
├── database/         # SQLite database management (DBHelper)
├── models/           # Data structures and POJO classes (FoodModel, User)
└── utils/            # Helper classes and shared tools (SessionManager)
```

---

## 🛠️ Technologies Used

* **Language:** Java (Android SDK)
* **Database:** SQLite (Local Mobile Database)
* **IDE:** Android Studio
* **Version Control:** Git & GitHub
* **UI Components:** AndroidX, Material Design Components (MDC)

---

## 👥 Team Details & Work Breakdown

| Index No | Registration No | Name                 | Role & Core Responsibilities                                                                     | GitHub Profile         |
| -------- | --------------- | -------------------- | ------------------------------------------------------------------------------------------------ | ---------------------- |
| 5707     | ICT/2022/104    | T.H.M. Thilakarathna | Auth & Integration: Login/Register, SHA-256 Encryption, Session Management, App Integrator       | @Mihiran-Thilakarathna |
| 5708     | ICT/2022/105    | M.I Afthal Ahamad    | Menu & Dashboard: Home UI, Food Detail View, RecyclerView setup, SQLite Food Seeding             | @afthal-ahamad01       |
| 5709     | ICT/2022/106    | M.A.F Nuha           | Cart & Orders: Cart UI, Order Confirmation, Order History tracking, User-specific data filtering | @nuha-akil             |

---

## ⚙️ Installation & Setup Instructions

1. Clone this repository

   ```
   git clone https://github.com/Mihiran-Thilakarathna/FoodOrderingApp.git
   ```

2. Open the project in **Android Studio**

3. Allow **Gradle** to sync and download the required dependencies

4. Build and Run the application on:

   * Android Emulator OR
   * Physical Android device

---

📘 *This project is submitted in partial fulfillment of the requirements for the ICT3214 module.*
