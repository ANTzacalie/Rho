package com.payment.rho

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.nfc.NfcAdapter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt


class InitActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appState: Int = AndroidLocalStorage(applicationContext).getAppState()

        if (appState == 0) {

            startActivity(Intent("MasterActivity"))

        } else {

            startActivity(Intent("AuthActivity"))

        }

    }

    override fun onStop() {
        super.onStop()

        finish()

    }


}

class MasterActivity: AppCompatActivity() {

    private var isExit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.master_activity)
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.black)

        val nextButton: Button = findViewById(R.id.next)

        nextButton.setOnClickListener {

            isExit = true;
            startActivity(Intent("SetupActivity"))

        }

    }

    override fun onStop() {
        super.onStop()

        if(isExit) { finish() }

    }

}

class SetupActivity: AppCompatActivity() {

    private var exitFlag: Boolean = false

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.setup_activity)
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.black)

        val nextButton: Button = findViewById(R.id.next)
        val inputPassword: EditText = findViewById(R.id.password)
        val errorText: TextView = findViewById(R.id.errorView)


        nextButton.setOnClickListener {

            if(inputPassword.length() >= 4 && " " !in inputPassword.text.toString()) {

                //INITIALIZE DB FOR FIRST TIME
                MasterDb(applicationContext)

                //APP STATE SET TO 1 AS USER ENTERED A PASSWORD(CONNECTED)
                AndroidLocalStorage(applicationContext).saveAppState(1)
                AndroidLocalStorage(applicationContext).savePassword(inputPassword.text.toString())

                exitFlag = true; startActivity(Intent("UserActivity"))

            } else if(inputPassword.length() < 4) {

                val errorTxt = "Should contain more or equal to four characters!"
                errorText.text = errorTxt

            } else if(" " in inputPassword.text.toString()) {

                val errorTxt = "There should be no whitespaces!"
                errorText.text = errorTxt

            }

        }

    }

    override fun onStop() {
        super.onStop()

        if(exitFlag) { finish() }

    }

}

class AuthActivity: AppCompatActivity() {

    private var exitFlag: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.auth_activity)
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.black)

        val nextButton: Button = findViewById(R.id.next)
        val inputPassword: EditText = findViewById(R.id.password)
        val errorText: TextView = findViewById(R.id.errorView)

        val pass: String? = AndroidLocalStorage(applicationContext).getPassword()
        nextButton.setOnClickListener {

            if(inputPassword.length() >= 4 && ' ' !in inputPassword.text.toString() && (inputPassword.text.toString() == pass)) {

                exitFlag = true
                startActivity(Intent("UserActivity"))

            } else if(inputPassword.length() < 4) {

                val errorTxt = "Should contain more or equal to four characters!"
                errorText.text = errorTxt

            } else if(' ' in inputPassword.text.toString()) {

                val errorTxt = "There should be no whitespaces!"
                errorText.text = errorTxt

            } else if(inputPassword.text.toString() != pass) {

                val errorTxt = "Incorrect password!"
                errorText.text = errorTxt

            }

        }

    }

    override fun onStop() {
        super.onStop()

        if(exitFlag) { finish() }

    }

}

class UserActivity: AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.black)

        val addCard: Button = findViewById(R.id.addCard)
        val cardLinearLayout: LinearLayout = findViewById(R.id.cardHolder)
        val constraintLayout: ConstraintLayout = findViewById(R.id.constraintFather)

        val cardList = MasterDb(applicationContext).retrieveAllCards()
        for(i in 0 until cardList.size step 1) {

            mainUiCard(applicationContext , cardLinearLayout , constraintLayout , cardList[i])

        }

        addCard.setOnClickListener {

            startActivity(Intent("AddCards"))

        }

    }

    @SuppressLint("InflateParams")
    private fun mainUiCard(context: Context, cardLinearLayout: LinearLayout , constraintLayout: ConstraintLayout , cardList: MutableList<String?>) {

        val inflater = LayoutInflater.from(context)
        val getLayout = inflater.inflate(R.layout.main_card_inflate , null) as ViewGroup
        val getPromptLayout = inflater.inflate(R.layout.remove_card , null) as ViewGroup

        val card: CardView = getLayout.findViewById(R.id.cardSurface)
        val cardText: TextView = getLayout.findViewById(R.id.cardName)
        val cardItem: CardView = getLayout.findViewById(R.id.cardItem)

        cardText.text = cardList[1].toString()
        cardItem.setCardBackgroundColor(cardList[10]!!.toColorInt())

        card.setOnClickListener {

            globalCardId = cardList[0].toString()
            startActivity(Intent("CardActivity"))

        }

        card.setOnLongClickListener {

            if(cardMainBoolean) {

                cardMainBoolean = false

                // DELETE CARD FORM DB
                removeCard(cardList[0], cardList[1], cardLinearLayout, getPromptLayout, getLayout, constraintLayout)

            }

            true
        }

        cardLinearLayout.addView(getLayout)
    }

    private fun removeCard(cardId: String? , cardName: String? , cardLinearLayout: LinearLayout, promptLayout: ViewGroup, cardLayout: ViewGroup, constraintLayout: ConstraintLayout) {

        val constraintFather: ConstraintLayout = promptLayout.findViewById(R.id.constraintFather)
        val constraintChild: CardView = promptLayout.findViewById(R.id.constraintChild)
        val buttonLayoutText: TextView = promptLayout.findViewById(R.id.textView5)
        val delete: Button = promptLayout.findViewById(R.id.accept)
        val goBack: Button = promptLayout.findViewById(R.id.deny)

        val preMadeText: Array<String> = arrayOf("Yes", "No","  $cardName")
        delete.text = preMadeText[0]
        goBack.text = preMadeText[1]
        buttonLayoutText.text = preMadeText[2]


        delete.setOnClickListener {

            // DELETE CARD FORM DB
            MasterDb(applicationContext).removeCard(cardId!!)


            constraintLayout.removeView(promptLayout)
            cardLinearLayout.removeView(cardLayout)

            cardMainBoolean = true
        }

        goBack.setOnClickListener {

            constraintLayout.removeView(promptLayout)

            cardMainBoolean = true
        }

        constraintFather.setOnClickListener {

            constraintLayout.removeView(promptLayout)

            cardMainBoolean = true

        }; constraintChild.setOnClickListener { } // TODO: "DOSE NOTHING , ITS HERE SO THE FATHER DOESN'T KILL HIM"

        constraintLayout.addView(promptLayout)
    }

    override fun onRestart() {
        super.onRestart()

        startActivity(Intent("UserActivity"))
        finish()

    }


}

class AddCards: AppCompatActivity() {

    private var isExit: Boolean = false

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_cards)
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.black)

        val addCard: Button = findViewById(R.id.addCard)
        val visaButton: Button = findViewById(R.id.visaButton)
        val masterCardButton: Button = findViewById(R.id.masterCardButton)
        val otherButton: Button = findViewById(R.id.otherButton)

        val cardName: EditText = findViewById(R.id.cardName)
        val cardHolder: EditText = findViewById(R.id.cardHolder)
        val bankName: EditText = findViewById(R.id.bankName)
        val cardNumber: EditText = findViewById(R.id.cardNumber)
        val cardExpM: EditText = findViewById(R.id.cardExp_M)
        val cardExpY: EditText = findViewById(R.id.cardExp_Y)
        val cardCcv: EditText = findViewById(R.id.cardCcv)
        val cardIban: EditText = findViewById(R.id.cardIban)
        val errorText: TextView = findViewById(R.id.errorText)

        var networkType: Int = 0

        addCard.setOnClickListener {

            if(cardName.length() >= 1 && cardName.text.isNotBlank()) {

                if(cardHolder.length() >= 0) {

                    if(bankName.length() >= 0) {

                        if (cardNumber.length() == 16) {

                            if(cardExpM.length() == 2) {

                                if (cardExpY.length() == 2) {

                                    if(cardCcv.length() == 3 || cardCcv.length() == 4) {

                                        if(networkType != 0) {

                                            try {

                                                MasterDb(applicationContext).
                                                insertCard(
                                                    cardName.text.toString(),
                                                    bankName.text.toString(),
                                                    cardNumber.text.toString(),
                                                    cardExpM.text.toString(),
                                                    cardExpY.text.toString(),
                                                    cardCcv.text.toString(),
                                                    cardHolder.text.toString(),
                                                    networkType.toString(),
                                                    cardIban.text.toString()
                                                )

                                                isExit = true
                                                onStop()

                                            } catch (e: Exception) {

                                                Log.e("ADD_CARDS", "ERROR: " + e.message)

                                            }

                                        } else { errorText.text = "Payment network should be selected!" }

                                    } else { errorText.text = "CCV should be between three and four characters" }

                                } else { errorText.text = "Year should be two characters {24 , 25 ... 99}" }

                            } else { errorText.text = "Month should be two characters{01 , 02 ... 12}" }

                        } else { errorText.text = "Card Number should be 16 characters" }

                    }

                }

            } else { errorText.text = "Card Name should be at least one character" }

        }

        visaButton.setOnClickListener {

            networkType = 1

            visaButton.backgroundTintList = getColorStateList(R.color.blue)
            visaButton.setTextColor(getColor(R.color.white))

            masterCardButton.backgroundTintList = getColorStateList(R.color.white)
            masterCardButton.setTextColor(getColor(R.color.black))

            otherButton.backgroundTintList = getColorStateList(R.color.white)
            otherButton.setTextColor(getColor(R.color.black))

        }

        masterCardButton.setOnClickListener {

            networkType = 2;

            masterCardButton.backgroundTintList = getColorStateList(R.color.red)
            masterCardButton.setTextColor(getColor(R.color.white))

            visaButton.backgroundTintList = getColorStateList(R.color.white)
            visaButton.setTextColor(getColor(R.color.black))

            otherButton.backgroundTintList = getColorStateList(R.color.white)
            otherButton.setTextColor(getColor(R.color.black))

        }

        otherButton.setOnClickListener {

            networkType = 3

            otherButton.backgroundTintList = getColorStateList(R.color.green)
            otherButton.setTextColor(getColor(R.color.white))

            visaButton.backgroundTintList = getColorStateList(R.color.white)
            visaButton.setTextColor(getColor(R.color.black))

            masterCardButton.backgroundTintList = getColorStateList(R.color.white)
            masterCardButton.setTextColor(getColor(R.color.black))

        }

    }

    override fun onStop() {
        super.onStop()

        if(isExit) {

            finish()

        }

    }

}


class CardActivity: AppCompatActivity() {

    private var isExit: Boolean = false
    private var nfcAdapter: NfcAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.card_activity)
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.black)

        val goBack: Button = findViewById(R.id.goBack)
        val cardInfo = MasterDb(applicationContext).retrieveCard(globalCardId)

        val cardHolderText: String = "CARD HOLDER: " + cardInfo[7]
        val cardHolder: TextView = findViewById(R.id.cardHolder); cardHolder.text = cardHolderText

        val bankNameText: String? = cardInfo[2]
        val bankName: TextView  = findViewById(R.id.bankName); bankName.text = bankNameText

        val cardNumberText: String = "NUMBER: " + cardInfo[3]
        val cardNumber: TextView = findViewById(R.id.cardNumber); cardNumber.text = cardNumberText

        val cardExpText: String = "EXP: " + cardInfo[4] + "/" + cardInfo[5]
        val cardExp: TextView = findViewById(R.id.cardExp); cardExp.text = cardExpText

        val cardCcvText: String = "CCV: " + cardInfo[6]
        val cardCcv: TextView = findViewById(R.id.cardCcv); cardCcv.text = cardCcvText

        val cardIbanText: String = "IBAN: " + cardInfo[9]
        val cardIban: TextView = findViewById(R.id.cardIban); cardIban.text = cardIbanText

        val cardNetworkOp: List<String> = listOf("NO INFO" , "VISA" , "MASTER CARD" , "OTHER")
        val cardNetwork: TextView = findViewById(R.id.cardType)
        when(cardInfo[8]?.toInt()) {

            0 -> {

                cardNetwork.text = cardNetworkOp[0]
                cardNetwork.setTextColor(getColor(R.color.white))

            }

            1 -> {

                cardNetwork.text = cardNetworkOp[1]
                cardNetwork.setTextColor(getColor(R.color.blue))

            }

            2 -> {

                cardNetwork.text = cardNetworkOp[2]
                cardNetwork.setTextColor(getColor(R.color.red))

            }

            3 -> {

                cardNetwork.text = cardNetworkOp[3]
                cardNetwork.setTextColor(getColor(R.color.green))

            }

        }

        val card: CardView = findViewById(R.id.creditCard)
        card.setCardBackgroundColor(cardInfo[10]!!.toColorInt())

        /*
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        if (nfcAdapter == null) {

            //TODO: NFC NOT SUPPORTED , DOSE NOTHING
            masterNfcNumber = null
            masterNfcExpServiceCode = null
            masterNfcAid = null
            masterNfcNetwork = null
            masterNfcAidLenght = null
            masterNfcAtcNumber = null


            Toast.makeText(this, "This device dose not support NFC!" , Toast.LENGTH_SHORT).show()

        } else if(cardInfo[3] != null  && cardInfo[5] != null && cardInfo[4] != null ) {

            //ATC NUMBER
            if (cardInfo[11]!!.toInt() < 10) {
                masterNfcAtcNumber = ("000 + ${cardInfo[11]!!}").toByteArray()
            }else if(cardInfo[11]!!.toInt() < 100) {
                masterNfcAtcNumber = ("00 + ${cardInfo[11]!!}").toByteArray()
            } else if(cardInfo[11]!!.toInt() < 1000) {
                masterNfcAtcNumber = ("0 + ${cardInfo[11]!!}").toByteArray()
            } else if(cardInfo[11]!!.toInt() <= 9999) {
                masterNfcAtcNumber = (cardInfo[11]!!).toByteArray()
            }

            //PAN NUMBER
            masterNfcNumber = stringToHexByteArray(cardInfo[3]!!)

            //AS THERE IS NO MORE YEARS WITH 02...09 ,SO WE % 10 THAT
            masterNfcExpServiceCode = stringToHexByteArray((((cardInfo[5]!!.toInt() % 10) * 100 + cardInfo[4]!!.toInt()) * 1000 + 221).toString())

            //AID
            masterNfcAid = when(cardInfo[8]!!.toInt()) { 1 -> { stringToHexByteArray("A0000000031010") } 2 -> { stringToHexByteArray("A0000000041010")} else -> { stringToHexByteArray("A0000000000000") } }

            //AID LENGHT
            masterNfcAidLenght = masterNfcAid!!.size.toByte()

            //NETWORK TYPE
            masterNfcNetwork = when(cardInfo[8]!!.toInt()) { 1 -> { "Visa Debit".toByteArray() } 2 -> { "Debit Mastercard".toByteArray() } else -> { "Debit Unknown".toByteArray() } }

            //SEPARATOR FOR DECADE 20 and 30 >> [2020-2039]
            if(cardInfo[5]!!.toInt() / 10 == 2 ) {
                masterNfcSeparator = stringToHexByteArray("D2")
            }
            else if(cardInfo[5]!!.toInt() / 10 == 3 ) {
                masterNfcSeparator = stringToHexByteArray("D3")
            }


            Log.d("MASTER NFC VAR INITIALIZED", "READY FOR POS PAYMENT: ${masterNfcAid!!.toHexString()}")


        }
        */
        goBack.setOnClickListener { isExit = true; onStop() }
    }

    override fun onStop() {
        super.onStop()

        /*
        masterNfcNumber = null
        masterNfcExpServiceCode = null
        masterNfcAid = null
        masterNfcNetwork = null
        masterNfcAidLenght = null
        masterNfcAtcNumber = null
        */

        finish()
    }

    //TRANSFORMS INT STRING INTO HEXADECIMAL FORMAT
    private fun stringToHexByteArray(input: String): ByteArray {
        val len = input.length

        return ByteArray(len / 2) { i ->

            val hexPair = input.substring(i * 2, i * 2 + 2)

            hexPair.toInt(16).toByte()

        }

    }

}
