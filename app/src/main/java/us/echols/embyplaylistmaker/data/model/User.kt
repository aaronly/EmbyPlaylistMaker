package us.echols.embyplaylistmaker.data.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.UnsupportedEncodingException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*

@Entity(tableName = "users")
class User(
        @PrimaryKey
        @SerializedName("Id")
        val id: String,
        @SerializedName("Name")
        val name: String,
        @SerializedName("HasPassword")
        @ColumnInfo(name = "has_password")
        var hasPassword: Boolean,
        @SerializedName("PrimaryImageTag")
        @ColumnInfo(name = "image_id")
        val imageId: String?
) {

    @SerializedName("AccessToken")
    var token: String? = null

    @ColumnInfo(name = "password")
    var pw: String? = null
    @Ignore
    lateinit var password: String
    @Ignore
    lateinit var passwordMd5: String

    @ColumnInfo(name = "last_active")
    var lastActive: Boolean = false

    @Ignore
    constructor(id: String, name: String)
            : this(id, name, false, null)

    init {
        setPasswords("")
    }

    fun setPasswords(pw: String) {
        hasPassword = pw != ""
        this.pw = pw
        password = encrypt(pw, "SHA-1")
        passwordMd5 = encrypt(pw, "MD5")
    }

    private fun encrypt(password: String, type: String): String {
        try {
            val crypt = MessageDigest.getInstance(type)
            val msgBytes = password.toByteArray(charset("UTF-8"))
            val digest = crypt.digest(msgBytes)

            return encodeHex(digest)
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }

        return ""
    }

    private fun encodeHex(hash: ByteArray): String {
        Formatter().use { formatter ->
            for (b in hash) {
                formatter.format("%02x", b)
            }
            return formatter.toString()
        }
    }

}