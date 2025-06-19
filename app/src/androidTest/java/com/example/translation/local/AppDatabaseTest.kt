package com.example.translation.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertNotNull

class AppDatabaseTest {
    private lateinit var appDatabase: AppDatabase

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        appDatabase = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        ).build()
    }

    @After
    fun tearDown() {
        appDatabase.close()
    }

    @Test
    fun testGetDatabaseInstance() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val instance = AppDatabase.getDatabase(context)
        assertNotNull(instance)
    }

    @Test
    fun testTranslationDao() {
        val translationDao = appDatabase.translationDao()
        assertNotNull(translationDao)
    }
}