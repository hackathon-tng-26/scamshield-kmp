package my.scamshield.core.platform

import android.content.Intent
import android.net.Uri
import my.scamshield.core.data.local.AndroidContextHolder

class AndroidCaller : Caller {
    override fun dial(number: String) {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number")).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        AndroidContextHolder.requireContext().startActivity(intent)
    }
}
