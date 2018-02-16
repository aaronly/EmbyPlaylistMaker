package us.echols.embyplaylistmaker.util

import android.util.Log

interface MyLogger {

    val loggerTag: String
        get() = getTag(javaClass)

}

fun MyLogger.verbose(message: Any?) {
    if (isJUnitTest()) {
        System.out.println("VERBOSE:$loggerTag: $message")
    } else {
        log(this, message, { tag, msg -> Log.v(tag, msg) })
    }
}

inline fun MyLogger.verbose(message: () -> Any?) {
    val msg = message()?.toString() ?: "null"
    if (isJUnitTest()) {
        System.out.println("VERBOSE:$loggerTag: $msg")
    } else {
        Log.v(loggerTag, msg)
    }
}

fun MyLogger.debug(message: Any?) {
    if (isJUnitTest()) {
        System.out.println("DEBUG:$loggerTag: $message")
    } else {
        log(this, message, { tag, msg -> Log.d(tag, msg) })
    }
}

inline fun MyLogger.debug(message: () -> Any?) {
    val msg = message()?.toString() ?: "null"
    if (isJUnitTest()) {
        System.out.println("DEBUG:$loggerTag: $msg")
    } else {
        Log.d(loggerTag, msg)
    }
}

fun MyLogger.info(message: Any?) {
    if (isJUnitTest()) {
        System.out.println("INFO:$loggerTag: $message")
    } else {
        log(this, message, { tag, msg -> Log.i(tag, msg) })
    }
}

inline fun MyLogger.info(message: () -> Any?) {
    val msg = message()?.toString() ?: "null"
    if (isJUnitTest()) {
        System.out.println("INFO:$loggerTag: $msg")
    } else {
        Log.i(loggerTag, msg)
    }
}

fun MyLogger.warn(message: Any?) {
    if (isJUnitTest()) {
        System.out.println("WARN:$loggerTag: $message")
    } else {
        log(this, message, { tag, msg -> Log.w(tag, msg) })
    }
}

inline fun MyLogger.warn(message: () -> Any?) {
    val msg = message()?.toString() ?: "null"
    if (isJUnitTest()) {
        System.out.println("WARN:$loggerTag: $msg")
    } else {
        Log.w(loggerTag, msg)
    }
}

fun MyLogger.error(message: Any?) {
    if (isJUnitTest()) {
        System.out.println("ERROR:$loggerTag: $message")
    } else {
        log(this, message, { tag, msg -> Log.e(tag, msg) })
    }
}

inline fun MyLogger.error(message: () -> Any?) {
    val msg = message()?.toString() ?: "null"
    if (isJUnitTest()) {
        System.out.println("ERROR:$loggerTag: $msg")
    } else {
        Log.e(loggerTag, msg)
    }
}

fun MyLogger.wtf(message: Any?) {
    if (isJUnitTest()) {
        System.out.println("WTF:$loggerTag: $message")
    } else {
        log(this, message, { tag, msg -> Log.wtf(tag, msg) })
    }
}

inline fun MyLogger.wtf(message: () -> Any?) {
    val msg = message()?.toString() ?: "null"
    if (isJUnitTest()) {
        System.out.println("WTF:$loggerTag: $msg")
    } else {
        Log.wtf(loggerTag, msg)
    }
}

private inline fun log(logger: MyLogger, message: Any?, f: (String, String) -> Unit) {
    val tag = logger.loggerTag
    f(tag, message?.toString() ?: "null")
}

private fun getTag(clazz: Class<*>): String {
    val tag = clazz.simpleName
    return if (tag.length <= 23) {
        tag
    } else {
        tag.substring(0, 23)
    }
}

fun isJUnitTest(): Boolean {
    val stackTrace = Thread.currentThread().stackTrace

    stackTrace.forEach { element ->
        if (element.className.startsWith("org.junit.")) {
            return true
        }
    }
    return false

}
