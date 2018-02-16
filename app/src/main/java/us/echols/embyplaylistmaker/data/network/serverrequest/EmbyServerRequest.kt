package us.echols.embyplaylistmaker.data.network.serverrequest

import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import us.echols.embyplaylistmaker.util.MyLogger
import us.echols.embyplaylistmaker.util.debug
import us.echols.embyplaylistmaker.util.error
import us.echols.embyplaylistmaker.util.info
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.UnknownHostException
import java.nio.charset.StandardCharsets

class EmbyServerRequest : MyLogger {

    var address: String? = null
    private val port = 7359
    private val request = "who is EmbyServer?"

    @Throws(IOException::class)
    fun getResponse(): Observable<EmbyServerResponse?> {
        return Observable.fromCallable { pingServer() }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    private fun pingServer(): EmbyServerResponse? {
        val inetAddress: InetAddress

        try {
            inetAddress = InetAddress.getByName(address)
        } catch (ex: UnknownHostException) {
            error("Error creating url from: $address")
            ex.printStackTrace()
            return null
        }

        DatagramSocket().use { c ->
            c.broadcast = true
            c.soTimeout = 1000

            val sendData = request.toByteArray(StandardCharsets.UTF_8)
            val sendPacket = DatagramPacket(sendData, sendData.size, inetAddress, port)
            c.send(sendPacket)
            debug("Request packet sent to: $inetAddress.hostAddress")

            val receiveBuffer = ByteArray(1000)
            val receivePacket = DatagramPacket(receiveBuffer, receiveBuffer.size)

            c.receive(receivePacket)
            debug("Broadcast response from server: $receivePacket.address.hostAddress")

            val message = String(receivePacket.data, StandardCharsets.UTF_8).trim { it <= ' ' }
            info("Response from server: $message")

            return Gson().fromJson(message, EmbyServerResponse::class.java)
        }
    }


}
