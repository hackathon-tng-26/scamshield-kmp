package my.scamshield.core.platform

import platform.Foundation.NSURL
import platform.UIKit.UIApplication

class IosCaller : Caller {
    override fun dial(number: String) {
        val url = NSURL(string = "tel:$number")
        val app = UIApplication.sharedApplication
        if (app.canOpenURL(url)) {
            app.openURL(url)
        }
    }
}
