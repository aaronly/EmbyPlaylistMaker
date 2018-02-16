package us.echols.embyplaylistmaker.data.prefs

import android.content.SharedPreferences
import com.f2prateek.rx.preferences2.RxSharedPreferences
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class SharedPreferencesHelper(private val sharedPreferences: SharedPreferences) :
        PreferencesHelper {

    private val serverAddressKey = "server_address"
    private val defaultServerAddress = "127.0.0.1"

    private val serverPortKey = "server_port"
    private val defaultServerPort = "8096"

    private val rxPreferences = RxSharedPreferences.create(sharedPreferences)

    override var serverAddress: String
        get() = sharedPreferences.getString(serverAddressKey, defaultServerAddress)
        set(address) {
            val editor = sharedPreferences.edit()
            editor.putString(serverAddressKey, address)
            editor.apply()
        }

    override var serverPort: String
        get() = sharedPreferences.getString(serverPortKey, defaultServerPort)
        set(port) {
            val editor = sharedPreferences.edit()
            editor.putString(serverPortKey, port)
            editor.apply()
        }

    override fun getServerAddressObservable(): Observable<String> {
        return rxPreferences.getString(serverAddressKey).asObservable()
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
    }

    override fun getServerPort(): Observable<String> {
        return rxPreferences.getString(serverPortKey).asObservable()
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
    }

}
