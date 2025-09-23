package com.example.firstcursorapp.data.repository

import com.example.firstcursorapp.FavoriteNews
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoritesRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    
    companion object {
        private const val COLLECTION_FAVORITES = "favorites"
    }
    
    /**
     * Get the current user's favorites as a Flow
     */
    fun getFavorites(): Flow<List<FavoriteNews>> = callbackFlow {
        val user = auth.currentUser
        if (user == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        
        val listener = firestore
            .collection(COLLECTION_FAVORITES)
            .document(user.uid)
            .collection("user_favorites")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                val favorites = snapshot?.documents?.mapNotNull { document ->
                    try {
                        FavoriteNews(
                            id = document.getString("id") ?: return@mapNotNull null,
                            title = document.getString("title") ?: "",
                            description = document.getString("description") ?: "",
                            imageUrl = document.getString("imageUrl"),
                            url = document.getString("url"),
                            isFavorite = document.getBoolean("isFavorite") ?: true
                        )
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()
                
                trySend(favorites)
            }
        
        awaitClose { listener.remove() }
    }
    
    /**
     * Add a news article to favorites
     */
    suspend fun addFavorite(news: FavoriteNews): Result<Unit> {
        return try {
            val user = auth.currentUser
            if (user == null) {
                return Result.failure(Exception("User not authenticated"))
            }
            
            val favoriteData = mapOf(
                "id" to news.id,
                "title" to news.title,
                "description" to news.description,
                "imageUrl" to news.imageUrl,
                "url" to news.url,
                "isFavorite" to true,
                "addedAt" to System.currentTimeMillis()
            )
            
            firestore
                .collection(COLLECTION_FAVORITES)
                .document(user.uid)
                .collection("user_favorites")
                .document(news.id)
                .set(favoriteData)
                .await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Remove a news article from favorites
     */
    suspend fun removeFavorite(newsId: String): Result<Unit> {
        return try {
            val user = auth.currentUser
            if (user == null) {
                return Result.failure(Exception("User not authenticated"))
            }
            
            firestore
                .collection(COLLECTION_FAVORITES)
                .document(user.uid)
                .collection("user_favorites")
                .document(newsId)
                .delete()
                .await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Check if a news article is favorited
     */
    suspend fun isFavorite(newsId: String): Boolean {
        return try {
            val user = auth.currentUser
            if (user == null) return false
            
            val document = firestore
                .collection(COLLECTION_FAVORITES)
                .document(user.uid)
                .collection("user_favorites")
                .document(newsId)
                .get()
                .await()
            
            document.exists() && (document.getBoolean("isFavorite") ?: false)
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Toggle favorite status for a news article
     */
    suspend fun toggleFavorite(news: FavoriteNews): Result<Boolean> {
        return try {
            val isCurrentlyFavorite = isFavorite(news.id)
            
            if (isCurrentlyFavorite) {
                removeFavorite(news.id)
                Result.success(false)
            } else {
                addFavorite(news)
                Result.success(true)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Clear all favorites for the current user
     */
    suspend fun clearAllFavorites(): Result<Unit> {
        return try {
            val user = auth.currentUser
            if (user == null) {
                return Result.failure(Exception("User not authenticated"))
            }
            
            val batch = firestore.batch()
            val favoritesSnapshot = firestore
                .collection(COLLECTION_FAVORITES)
                .document(user.uid)
                .collection("user_favorites")
                .get()
                .await()
            
            favoritesSnapshot.documents.forEach { document ->
                batch.delete(document.reference)
            }
            
            batch.commit().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
