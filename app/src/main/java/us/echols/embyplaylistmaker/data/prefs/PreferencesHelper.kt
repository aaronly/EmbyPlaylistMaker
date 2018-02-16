package us.echols.embyplaylistmaker.data.prefs

import io.reactivex.Observable

interface PreferencesHelper {

    var serverAddress: String

    var serverPort: String

    fun getServerAddressObservable(): Observable<String>

    fun getServerPort(): Observable<String>
}
