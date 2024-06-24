package com.payment.rho

//TODO: RHO CARD HOLDER 1.0

//ID-UL CARDULUI IN FOLOSIRE
var globalCardId: String? = null

//---->MAIN UI
var cardMainBoolean: Boolean = true

//---->MasterNFC
var masterNfcNumber: ByteArray? = null
var masterNfcExpServiceCode: ByteArray? = null
var masterNfcAtcNumber: ByteArray? = null
var masterNfcAid: ByteArray? = null
var masterNfcNetwork: ByteArray? = null
var masterNfcAidLenght: Byte? = null
var masterNfcSeparator: ByteArray? = null


// TODO: IN THE FUTURE MAYBE THIS WILL BE A PAYMENT APP , FOR NOW , I HAVE NO CLUES OF TO BUILD THE TRANSACTION SYSTEM FOR EMV

// FOR NOW RHO IS JUST A CREDIT CARD HOLDER, MORE OPTIONS FOR OTHER TYPE OF CARD I MAY ADD IN THE FUTURE
