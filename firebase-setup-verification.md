# Firebase Setup Verification Checklist

## ✅ Steps to Complete:

### 1. Firebase Project Creation
- [ ] Created Firebase project at https://console.firebase.google.com
- [ ] Project name: `news-app` (or your choice)
- [ ] Project created successfully

### 2. Android App Registration
- [ ] Added Android app to Firebase project
- [ ] Package name: `com.example.firstcursorapp`
- [ ] App registered successfully

### 3. Configuration File
- [ ] Downloaded `google-services.json` from Firebase
- [ ] Replaced placeholder file in `app/google-services.json`
- [ ] File contains real project data (not placeholder values)

### 4. Services Enabled
- [ ] Authentication → Email/Password enabled
- [ ] Cloud Messaging → Enabled
- [ ] Firestore Database → Created in test mode

### 5. Verification Commands
Run these commands to verify setup:

```bash
# Build the app to check for Firebase integration
./gradlew.bat assembleDebug

# Check if google-services.json is valid
# (Should not show placeholder values in build output)
```

## 🔍 How to Verify Your Setup:

1. **Check google-services.json**:
   - Open `app/google-services.json`
   - Verify it contains real project data (not "123456789012" or "news-app-example")
   - Should have your actual project_id and api_key

2. **Test Authentication**:
   - Run the app
   - Try to register/login (will work once Firebase is properly configured)

3. **Check Firebase Console**:
   - Go to Authentication → Users (should be empty initially)
   - Go to Firestore → Data (should be empty initially)

## 🚨 Common Issues:

- **Wrong package name**: Must match exactly `com.example.firstcursorapp`
- **Invalid google-services.json**: Make sure you downloaded the correct file
- **Services not enabled**: Enable Authentication and Firestore in Firebase console
- **Build errors**: Usually means google-services.json is invalid or missing

## 📱 Next Steps After Setup:

1. Test user registration/login
2. Test push notifications
3. Test offline article storage
4. Configure notification topics in Firebase console
