package com.example.palina.lr1

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream


val DATABASE_NAME = "MyDb"

class DataBaseHandler(context: Context) :SQLiteOpenHelper(context, DATABASE_NAME, null, 1) {
    private val TABLE_NAME = "USERS"
    private val COL_NAME = "name"
    private val COL_SURNAME = "surname"
    private val COL_PHONE = "phone"
    private val COL_EMAIL = "email"
    private val COL_ID = "id"
    private val COL_PHOTO = "photo"

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_NAME  + " VARCHAR(256)," +
                COL_SURNAME + " VARCHAR(256)," +
                COL_PHONE + " VARCHAR(256)," +
                COL_EMAIL + " VARCHAR(256)," +
                COL_PHOTO + " BLOB)"
        db?.execSQL(createTable)
    }

    fun insertUser(name: String, surname: String, phone: String, email:String) {
        val db = this.writableDatabase
        val content = getContentValues(name, surname, phone, email)
        db.insert(TABLE_NAME, null, content)
        db.close()
    }

    fun updateUser(name: String, surname: String, phone: String, email:String) {
        val content = getContentValues(name, surname, phone, email)
        insertNewData(content)
    }

    fun updatePhoto(photo: Bitmap){
        val outputStream = ByteArrayOutputStream()
        photo.compress(CompressFormat.PNG, 0, outputStream)
        val data = outputStream.toByteArray()

        val content = ContentValues()
        content.put("photo", data)
        insertNewData(content)
    }

    private fun getContentValues(name: String, surname: String, phone: String, email: String): ContentValues {
        val content = ContentValues()
        content.put(COL_NAME, name)
        content.put(COL_SURNAME, surname)
        content.put(COL_PHONE, phone)
        content.put(COL_EMAIL, email)
        return  content
    }

    private fun insertNewData(content: ContentValues){
        val db = this.writableDatabase
        val query = "SELECT * FROM $TABLE_NAME"
        val result = db.rawQuery(query, null)

        result.moveToNext()
        db.update(TABLE_NAME, content, "id = ?", arrayOf(result.getString(result.getColumnIndex(COL_ID))))
        result.close()
        db.close()
    }

    fun clearDatabase(){
        val db = this.writableDatabase
        db.execSQL("DELETE FROM $TABLE_NAME")
        //db.execSQL("DROP TABLE $TABLE_NAME")
        db.close()
    }

    fun readData(): MutableMap<String, String>?{
        val map: MutableMap<String, String>? = mutableMapOf()
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_NAME"
        val result = db.rawQuery(query, null)

        result.moveToNext()
        if (result.count != 0){
            map?.put("Name", result.getString(result.getColumnIndex(COL_NAME)))
            map?.put("Surname", result.getString(result.getColumnIndex(COL_SURNAME)))
            map?.put("Phone", result.getString(result.getColumnIndex(COL_PHONE)))
            map?.put("Email", result.getString(result.getColumnIndex(COL_EMAIL)))
            result.close()
            db.close()
            return map
        }
        result.close()
        db.close()
        return null
    }

    fun getPhoto(): Bitmap?{
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_NAME"
        val result = db.rawQuery(query, null)

        result.moveToNext()
        if (result.count != 0){
            val photoByte = result.getBlob(result.getColumnIndex(COL_PHOTO))
            if (photoByte == null){
                result.close()
                db.close()
                return null
            }
            result.close()
            db.close()
            return BitmapFactory.decodeByteArray(photoByte, 0, photoByte.size)
        }
        result.close()
        db.close()
        return null
    }
}
