package com.payment.rho

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import androidx.security.crypto.MasterKeys
import java.math.BigInteger

class MasterDb(context: Context) : SQLiteOpenHelper(context,"cardStorage.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase?) {

        val createTableSql = """
            CREATE TABLE IF NOT EXISTS main (
                CARD_ID TEXT NOT NULL,
                CARD_NAME TEXT NOT NULL,
                BANK_NAME TEXT NOT NULL,
                CARD_NUMBER TEXT NOT NULL,
                EXP_DATE_M TEXT NOT NULL,
                EXP_DATE_Y TEXT NOT NULL,
                CCV TEXT NOT NULL,
                CARD_HOLDER TEXT NOT NULL,
                CIP_TYPE TEXT NOT NULL,
                IBAN TEXT,
                BG_COLOR TEXT NOT NULL,
                CARD_ATC TEXT NOT NULL
            );
        """

        db?.execSQL(createTableSql)
        Log.d("CREATE MAIN TABLE" , "TABLE CREATED!")

    }
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) { /*TODO("Not yet implemented")*/ }

    fun insertCard(cardName: String , bankName: String, cardNumber: String , expMonth: String , expYear: String, ccv: String, cardHolder: String, cipType: String, iban: String?) {

        try {

            val db = writableDatabase

            val content = ContentValues().apply {

                put("CARD_ID", getRandomId())
                put("CARD_NAME", cardName)
                put("BANK_NAME", bankName)
                put("CARD_NUMBER", cardNumber)
                put("EXP_DATE_M", expMonth)
                put("EXP_DATE_Y", expYear)
                put("CCV", ccv)
                put("CARD_HOLDER", cardHolder)
                put("CIP_TYPE", cipType)
                put("IBAN", iban)
                put("BG_COLOR", getRandomColor())
                put("CARD_ATC", "0")

            }

            db.insert("main", null , content)

            db.close()

        } catch (e: Exception) {

            Log.e("CHECK PASSWORD", "ERROR: " + e.message)

        }

    }

    fun removeCard(cardId: String) {

        try {

            val db = writableDatabase

            db.delete("main" , "CARD_ID = ?" , arrayOf(cardId)); db.close()

        } catch (e: Exception) {

            Log.e("REMOVE CARDS" , "ERROR WITH MESSAGE" + e.message);

        }

    }

    @SuppressLint("Range")
    fun retrieveAllCards(): MutableList<MutableList<String?>> {

        var returnList: MutableList<MutableList<String?>> = MutableList(0) { MutableList(0) { null } }

        try {

            val db = readableDatabase

            val columns = arrayOf("CARD_ID","CARD_NAME","BANK_NAME", "CARD_NUMBER", "EXP_DATE_M", "EXP_DATE_Y", "CCV","CARD_HOLDER","CIP_TYPE","IBAN", "BG_COLOR" , "CARD_ATC")
            val cursor: Cursor? = db.query("main" , columns, null , null , null , null , null)

            cursor?.use {

                cursor.moveToFirst()
                val count = cursor.count
                returnList = MutableList(count){ MutableList(12) { null } }

                for(i in 0 until count step 1) {

                    returnList[i][0] = cursor.getString(0)
                    returnList[i][1] = cursor.getString(1)
                    returnList[i][2] = cursor.getString(2)
                    returnList[i][3] = cursor.getString(3)
                    returnList[i][4] = cursor.getString(4)
                    returnList[i][5] = cursor.getString(5)
                    returnList[i][6] = cursor.getString(6)
                    returnList[i][7] = cursor.getString(7)
                    returnList[i][8] = cursor.getString(8)
                    returnList[i][9] = cursor.getString(9)
                    returnList[i][10]= cursor.getString(10)
                    returnList[i][11]= cursor.getString(11)

                    cursor.moveToNext()
                }

            }; cursor?.close(); db.close()

            return returnList

        } catch (e: Exception) {

            Log.e("RETRIEVE CARDS" , "ERROR WITH MESSAGE" + e.message);

        }

        return returnList
    }

    @SuppressLint("Range")
    fun retrieveCard(cardId: String?): MutableList<String?> {

        var returnList: MutableList<String?> = MutableList(0) { null }

        try {

            val db = readableDatabase

            val columns = arrayOf("CARD_ID","CARD_NAME","BANK_NAME", "CARD_NUMBER", "EXP_DATE_M", "EXP_DATE_Y", "CCV","CARD_HOLDER","CIP_TYPE","IBAN", "BG_COLOR" , "CARD_ATC")
            val cursor: Cursor? = db.query("main" , columns, "CARD_ID = ?" , arrayOf(cardId) , null , null , null)

            cursor?.use {

                cursor.moveToFirst()
                returnList = MutableList(12) { null }

                returnList[0] = cursor.getString(0)
                returnList[1] = cursor.getString(1)
                returnList[2] = cursor.getString(2)
                returnList[3] = cursor.getString(3)
                returnList[4] = cursor.getString(4)
                returnList[5] = cursor.getString(5)
                returnList[6] = cursor.getString(6)
                returnList[7] = cursor.getString(7)
                returnList[8] = cursor.getString(8)
                returnList[9] = cursor.getString(9)
                returnList[10]= cursor.getString(10)
                returnList[11]= cursor.getString(11)

            }; cursor?.close(); db.close()

            return returnList

        } catch (e: Exception) {

            Log.e("RETRIEVE CARDS" , "ERROR WITH MESSAGE" + e.message);

        }

        return returnList
    }


    //TODO: DB AS FOR 1.0 25/06/2024

}

class AndroidLocalStorage(private val context: Context) {

    private val masterKeyAlias = MasterKey.Builder(context).setKeyGenParameterSpec(MasterKeys.AES256_GCM_SPEC).build()

    fun saveAppState(appState: Int) {

        Log.d("LOCAL STORAGE","APP STATE SAVED")

        val sharedPreferences = EncryptedSharedPreferences.create(
            context,
            "appPrefs",
            masterKeyAlias,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        sharedPreferences.edit()
            .putInt("appState", appState)
            .apply()

    }

    fun savePassword(password: String) {

        Log.d("LOCAL STORAGE","PASSWORD SAVED")

        val sharedPreferences = EncryptedSharedPreferences.create(
            context,
            "appPassword",
            masterKeyAlias,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        sharedPreferences.edit()
            .putString("password", password)
            .apply()

    }

    fun getAppState(): Int {

        val sharedPreferences = EncryptedSharedPreferences.create(
            context,
            "appPrefs",
            masterKeyAlias,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        return sharedPreferences.getInt("appState", 0)

    }

    fun getPassword(): String? {

        val sharedPreferences = EncryptedSharedPreferences.create(
            context,
            "appPassword",
            masterKeyAlias,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        return sharedPreferences.getString("password", null)

    }

}



object StorageColors {

    val colorList: List<String> = listOf(
        //TODO: BRO IMPROVE THE COLORS 💀
        "#C5A3FF", "#D4A1FF", "#E5A1FF", "#F5A1FF", "#C5A1D5",
        "#C6A3FF", "#D5A1FF", "#E6A1FF", "#F6A1FF", "#C6A1D5",
        "#C7A3FF", "#D6A1FF", "#E7A1FF", "#F7A1FF", "#C7A1D5",
        "#C8A3FF", "#D7A1FF", "#E8A1FF", "#F8A1FF", "#C8A1D5",
        "#C9A3FF", "#D8A1FF", "#E9A1FF", "#F9A1FF", "#C9A1D5",
        "#CAA3FF", "#D9A1FF", "#EAA1FF", "#FAA1FF", "#CAA1D5",
        "#CBA3FF", "#DAA1FF", "#EBA1FF", "#FBA1FF", "#CBA1D5",
        "#CCA3FF", "#DBA1FF", "#ECA1FF", "#FCA1FF", "#CCA1D5",
        "#CDA3FF", "#DCA1FF", "#EDA1FF", "#FDA1FF", "#CDA1D5",
        "#CEA3FF", "#DDA1FF", "#EEA1FF", "#FEA1FF", "#CEA1D5",
        "#CFA3FF", "#DEA1FF", "#EFA1FF", "#FFA1FF", "#CFA1D5",
        "#C5A4FF", "#D4A2FF", "#E5A2FF", "#F5A2FF", "#C5A2D5",
        "#C6A4FF", "#D5A2FF", "#E6A2FF", "#F6A2FF", "#C6A2D5",
        "#C7A4FF", "#D6A2FF", "#E7A2FF", "#F7A2FF", "#C7A2D5",
        "#C8A4FF", "#D7A2FF", "#E8A2FF", "#F8A2FF", "#C8A2D5",
        "#C9A4FF", "#D8A2FF", "#E9A2FF", "#F9A2FF", "#C9A2D5",
        "#CAA4FF", "#D9A2FF", "#EAA2FF", "#FAA2FF", "#CAA2D5",
        "#CBA4FF", "#DAA2FF", "#EBA2FF", "#FBA2FF", "#CBA2D5",
        "#CCA4FF", "#DBA2FF", "#ECA2FF", "#FCA2FF", "#CCA2D5",
        "#CDA4FF", "#DCA2FF", "#EDA2FF", "#FDA2FF", "#CDA2D5",
        "#CEA4FF", "#DDA2FF", "#EEA2FF", "#FEA2FF", "#CEA2D5",
        "#CFA4FF", "#DEA2FF", "#EFA2FF", "#FFA2FF", "#CFA2D5",
        "#C5A5FF", "#D4A3FF", "#E5A3FF", "#F5A3FF", "#C5A3D5",
        "#C6A5FF", "#D5A3FF", "#E6A3FF", "#F6A3FF", "#C6A3D5",
        "#C7A5FF", "#D6A3FF", "#E7A3FF", "#F7A3FF", "#C7A3D5",
        "#C8A5FF", "#D7A3FF", "#E8A3FF", "#F8A3FF", "#C8A3D5",
        "#C9A5FF", "#D8A3FF", "#E9A3FF", "#F9A3FF", "#C9A3D5",
        "#CAA5FF", "#D9A3FF", "#EAA3FF", "#FAA3FF", "#CAA3D5",
        "#CBA5FF", "#DAA3FF", "#EBA3FF", "#FBA3FF", "#CBA3D5",
        "#CCA5FF", "#DBA3FF", "#ECA3FF", "#FCA3FF", "#CCA3D5",
        "#CDA5FF", "#DCA3FF", "#EDA3FF", "#FDA3FF", "#CDA3D5",
        "#CEA5FF", "#DDA3FF", "#EEA3FF", "#FEA3FF", "#CEA3D5",
        "#CFA5FF", "#DEA3FF", "#EFA3FF", "#FFA3FF", "#CFA3D5",
        "#C5A6FF", "#D4A4FF", "#E5A4FF", "#F5A4FF", "#C5A4D5",
        "#C6A6FF", "#D5A4FF", "#E6A4FF", "#F6A4FF", "#C6A4D5",
        "#C7A6FF", "#D6A4FF", "#E7A4FF", "#F7A4FF", "#C7A4D5",
        "#C8A6FF", "#D7A4FF", "#E8A4FF", "#F8A4FF", "#C8A4D5",
        "#C9A6FF", "#D8A4FF", "#E9A4FF", "#F9A4FF", "#C9A4D5",
        "#CAA6FF", "#D9A4FF", "#EAA4FF", "#FAA4FF", "#CAA4D5",
        "#CBA6FF", "#DAA4FF", "#EBA4FF", "#FBA4FF", "#CBA4D5",
        "#CCA6FF", "#DBA4FF", "#ECA4FF", "#FCA4FF", "#CCA4D5",
        "#CDA6FF", "#DCA4FF", "#EDA4FF", "#FDA4FF", "#CDA4D5",
        "#CEA6FF", "#DDA4FF", "#EEA4FF", "#FEA4FF", "#CEA4D5",
        "#CFA6FF", "#DEA4FF", "#EFA4FF", "#FFA4FF", "#CFA4D5",
        "#C5A7FF", "#D4A5FF", "#E5A5FF", "#F5A5FF", "#C5A5D5",
        "#C6A7FF", "#D5A5FF", "#E6A5FF", "#F6A5FF", "#C6A5D5",
        "#C7A7FF", "#D6A5FF", "#E7A5FF", "#F7A5FF", "#C7A5D5",
        "#C8A7FF", "#D7A5FF", "#E8A5FF", "#F8A5FF", "#C8A5D5",
        "#C9A7FF", "#D8A5FF", "#E9A5FF", "#F9A5FF", "#C9A5D5",
        "#CAA7FF", "#D9A5FF", "#EAA5FF", "#FAA5FF", "#CAA5D5",
        "#CBA7FF", "#DAA5FF", "#EBA5FF", "#FBA5FF", "#CBA5D5",
        "#CCA7FF", "#DBA5FF", "#ECA5FF", "#FCA5FF", "#CCA5D5",
        "#CDA7FF", "#DCA5FF", "#EDA5FF", "#FDA5FF", "#CDA5D5",
        "#CEA7FF", "#DDA5FF", "#EEA5FF", "#FEA5FF", "#CEA5D5",
        "#CFA7FF", "#DEA5FF", "#EFA5FF", "#FFA5FF", "#CFA5D5",
        "#C5A8FF", "#D4A6FF", "#E5A6FF", "#F5A6FF", "#C5A6D5",
        "#C6A8FF", "#D5A6FF", "#E6A6FF", "#F6A6FF", "#C6A6D5",
        "#C7A8FF", "#D6A6FF", "#E7A6FF", "#F7A6FF", "#C7A6D5",
        "#C8A8FF", "#D7A6FF", "#E8A6FF", "#F8A6FF", "#C8A6D5",
        "#C9A8FF", "#D8A6FF", "#E9A6FF", "#F9A6FF", "#C9A6D5",
        "#CAA8FF", "#D9A6FF", "#EAA6FF", "#FAA6FF", "#CAA6D5",
        "#CBA8FF", "#DAA6FF", "#EBA6FF", "#FBA6FF", "#CBA6D5",
        "#CCA8FF", "#DBA7AF"
    )

}


fun getRandomColor(): String {

    return StorageColors.colorList.random()

}


fun getRandomId(): String {

    val allChars  = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"

    val id = StringBuilder()

    repeat(12) {

        val randomIndex = (allChars.indices).random()
        val randomChar = allChars[randomIndex]
        id.append(randomChar)

    }

    return id.toString()

}
