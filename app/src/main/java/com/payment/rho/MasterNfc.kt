package com.payment.rho

import android.nfc.cardemulation.HostApduService
import android.os.Bundle
import android.util.Log
import java.util.Locale

/*
class NfcApdu : HostApduService() {

    override fun processCommandApdu(commandApdu: ByteArray, extras: Bundle?): ByteArray {
        Log.i("NfcApdu", "Received APDU: ${commandApdu.toHexString()}")

        val command = CommandApdu(commandApdu)
        return when (command.ins) {

            //SELECT COMMAND
            0xA4.toByte() -> SelectCommandHandler.processCommand(command)
            //GET COMMAND
            0xA8.toByte() -> GetProcessOpCommandHandler.processCommand(command)
            //READ RECORD COMMAND
            0xB2.toByte() -> ReadRecordCommandHandler.processCommand(command)
            //GENERATE AC CRY COMMAND
            0xAE.toByte() -> GenerateAcCommandHandler.processCommand(command)
            0xCA.toByte() -> byteArrayOf(0x62, 0x83.toByte()) // AS I SEEN IN THE LOGS FOR CA
            //HANDLE ERROR CASE
            else -> generateErrorResponse()

        }

    }

    private fun generateErrorResponse(): ByteArray {
        return byteArrayOf(0x6A, 0x82.toByte()) // Status word indicating an error
    }

    override fun onDeactivated(reason: Int) {
        Log.d("NfcApdu", "Service deactivated, reason: $reason")
        // Handle deactivation of the service
    }

}


class CommandApdu(apdu: ByteArray) {

    val cla: Byte = apdu.getOrElse(0) { 0 }
    val ins: Byte = apdu.getOrElse(1) { 0 }
    val p1: Byte =  apdu.getOrElse(2) { 0 }
    val p2: Byte =  apdu.getOrElse(3) { 0 }
    private val lc: Byte =  apdu.getOrElse(4) { 0 }
    val data: ByteArray = apdu.copyOfRange(5, 5 + lc)
    val le: Byte? = apdu.getOrNull(5 + lc.toInt())
    // ALL APDU FIELDS COMPLETED

}



object SelectCommandHandler {

    fun processCommand(command: CommandApdu): ByteArray {

        return byteArrayOf(0x90.toByte(), 0x00.toByte())
    }

}

object GetProcessOpCommandHandler {

    fun processCommand(command: CommandApdu): ByteArray {

        // Define the AIP bytes based on the above structure
        val aipByte1: Byte = 0b00011001.toByte() // Bits 8-5: 0001 (Offline PIN supported), Bits 4-1: 1001 (No CVM required)
        val aipByte2: Byte = 0x80.toByte() // RFU (Reserved for Future Use)

        // Construct the AIP byte array
        val aip: ByteArray = byteArrayOf(aipByte1, aipByte2)

        //ONLY THE NECESSARY ENTRY'S FOR POS
        // AFL entries for card information
        val aflEntry1: ByteArray = byteArrayOf(
            0x10, // SFI = 1
            0x01, // Start record number = 1
            0x01, // End record number = 1 (Only one record)
            0x01  // Number of records = 1
        )
        val aflEntry2: ByteArray = byteArrayOf(
            0x20, // SFI = 2
            0x00, // Start record number = 0 , DOSE NOT EXIST
            0x00, // End record number = 0 (Zero records)
            0x00  // Number of records = 0
        )


        val afl: ByteArray = aflEntry1 + aflEntry2


        return byteArrayOf(0x77.toByte()) + aip.size.toByte() + aip + byteArrayOf(0x94.toByte()) + afl.size.toByte() + afl + byteArrayOf(0x90.toByte(), 0x00.toByte())

    }

}

object ReadRecordCommandHandler {
    @OptIn(ExperimentalStdlibApi::class)
    fun processCommand(command: CommandApdu): ByteArray {

        return byteArrayOf(0x90.toByte(), 0x00.toByte())
    }

}

object GenerateAcCommandHandler {
    fun processCommand(command: CommandApdu): ByteArray {
        // NOT REQUIRED FOR THE APP
        // Example: return success response with cryptogram
        return byteArrayOf(0x90.toByte(), 0x00.toByte())
    }
}

// Utility function to convert ByteArray to Hexadecimal string
fun ByteArray.toHexString(): String {
    return joinToString("") { byte -> "%02x".format(byte) }
}

*/