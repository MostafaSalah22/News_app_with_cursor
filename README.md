# 📰 News App

**22 hours. Zero manual coding. Fully working app.** 🚀  

This is my first **fully AI-built application**, created entirely using **Cursor** without writing any manual code.  
I simply described the features step-by-step, and Cursor handled all the implementation.

The goal was to test if a complete app could be built **just by describing requirements**, like a total beginner.  
The code may not be perfectly optimized yet — this was purely an experiment to focus on features and flow.

---

## ✨ Features

### 🔐 Authentication
- Sign in or create a new account using **Firebase Authentication**.
- Personalized experience based on the logged-in user.

### 📰 News Feed
- Browse curated news with **smooth pull-to-refresh** and **loading shimmer**.
- Supports **Egypt** 🇪🇬 and **United States** 🇺🇸 — more countries can be added easily.
- **Category filters** to quickly show topics you care about.

### 📖 Article Reader
- Clean, distraction-free **in-app WebView** to read full articles.

### ⭐ Favorites
- Save or unsave any article with a single tap.
- View saved articles in a dedicated screen.
- **Real-time sync** via Firebase — favorites persist even after restarting the app.

### ⚙ Settings Screen
- 🌙 **Dark Theme Toggle:** Instantly switch between light and dark modes.  
- ⏱ **Reading Tracker:** Track total reading time (minutes/seconds).  
- 👤 **Profile Info:** Display name, email, and short user ID.  
- 🚪 **Logout:** Full-width sign out button.  
- 📊 **Usage Analytics:**  
  - Bar chart of top in-app actions.  
  - Message when no events are tracked yet.  
  - **Clear Data** button to reset tracked actions.  
- ℹ **App Info:** Name, version, and a “Built with…” note.

### ⚡ Smart Splash Flow
- Automatically detects login status and navigates the user to the correct screen.

---

## 🛠 Tech Stack

- **Kotlin** + **Jetpack Compose** (UI)
- **Firebase Authentication** (Sign-in / Sign-up)
- **Firebase Firestore** (Real-time sync for favorites & analytics)
- **Material 3** for polished UI and dark theme
- **Cursor AI** for code generation
