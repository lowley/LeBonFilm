package lorry.lebonfilm._tools

import android.util.Log
import org.koin.core.annotation.Singleton

class Logger {

    private val TAG = "movie"

    fun debug(args: String) {
        Log.d(TAG, args)
    }

    fun info(args: String) {
        Log.i(TAG, args)
    }

    fun error(args: String) {
        Log.e(TAG, args)
    }
}