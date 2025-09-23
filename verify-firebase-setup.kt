// Firebase Setup Verification Script
// This file helps you verify your Firebase configuration

package com.example.firstcursorapp.utils

import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging

object FirebaseVerification {
    
    fun verifyFirebaseSetup(context: Context): Boolean {
        return try {
            // Check if Firebase is initialized
            val firebaseApp = FirebaseApp.getInstance()
            Log.d("FirebaseVerification", "Firebase initialized: ${firebaseApp.name}")
            
            // Check Authentication
            val auth = FirebaseAuth.getInstance()
            Log.d("FirebaseVerification", "Auth instance created: ${auth.app.name}")
            
            // Check Firestore
            val firestore = FirebaseFirestore.getInstance()
            Log.d("FirebaseVerification", "Firestore instance created: ${firestore.app.name}")
            
            // Check Cloud Messaging
            val messaging = FirebaseMessaging.getInstance()
            Log.d("FirebaseVerification", "Messaging instance created: ${messaging.app.name}")
            
            // Get FCM token
            messaging.token.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result
                    Log.d("FirebaseVerification", "FCM Token: ${token?.take(20)}...")
                } else {
                    Log.e("FirebaseVerification", "Failed to get FCM token", task.exception)
                }
            }
            
            true
        } catch (e: Exception) {
            Log.e("FirebaseVerification", "Firebase setup verification failed", e)
            false
        }
    }
    
    fun logFirebaseInfo() {
        Log.d("FirebaseVerification", "=== Firebase Configuration Info ===")
        Log.d("FirebaseVerification", "Project ID: ${FirebaseApp.getInstance().options.projectId}")
        Log.d("FirebaseVerification", "App ID: ${FirebaseApp.getInstance().options.applicationId}")
        Log.d("FirebaseVerification", "API Key: ${FirebaseApp.getInstance().options.apiKey?.take(10)}...")
        Log.d("FirebaseVerification", "=====================================")
    }
}
