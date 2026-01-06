package com.realtorsuccessiq.data.dao

import androidx.room.*
import com.realtorsuccessiq.data.model.Contact
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {
    @Query("SELECT * FROM contacts ORDER BY score DESC, name ASC")
    fun getAllContacts(): Flow<List<Contact>>
    
    @Query("SELECT * FROM contacts WHERE id = :id")
    suspend fun getContactById(id: String): Contact?
    
    @Query("SELECT * FROM contacts WHERE name LIKE '%' || :query || '%' OR phone LIKE '%' || :query || '%' OR email LIKE '%' || :query || '%' ORDER BY score DESC")
    suspend fun searchContacts(query: String): List<Contact>
    
    @Query("SELECT * FROM contacts WHERE segment = :segment ORDER BY score DESC")
    suspend fun getContactsBySegment(segment: String): List<Contact>
    
    @Query("SELECT * FROM contacts WHERE tags LIKE '%' || :tag || '%' ORDER BY score DESC")
    suspend fun getContactsByTag(tag: String): List<Contact>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: Contact)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContacts(contacts: List<Contact>)
    
    @Update
    suspend fun updateContact(contact: Contact)
    
    @Delete
    suspend fun deleteContact(contact: Contact)

    @Query("DELETE FROM contacts WHERE id LIKE 'demo-%'")
    suspend fun deleteDemoContacts()
}

