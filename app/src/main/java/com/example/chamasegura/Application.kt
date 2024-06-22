import android.app.Application

class MyApp : Application() {
    companion object {
        lateinit var userId: String
        lateinit var firstName: String
        lateinit var role: String
    }
}
