package firebase.companionPersona.enti24

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val btnRegister = findViewById<Button>(R.id.btnRegisterConfirm)

        btnRegister.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}
