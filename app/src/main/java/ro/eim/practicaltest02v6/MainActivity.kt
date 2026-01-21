package ro.eim.practicaltest02v6

import ClientAsyncTask
import ServerThread
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val startServerButton = findViewById<View>(R.id.connect_server).setOnClickListener {
            val portNumber = findViewById<EditText>(R.id.server_port)
            val request = findViewById<EditText>(R.id.data)
            val intPort = portNumber.text.toString().toInt()

            var serverThread = ServerThread(intPort, request)
            serverThread.startServer()
        }

        val getWeatherButton = findViewById<View>(R.id.send_request).setOnClickListener {
            val clientButton = findViewById<TextView>(R.id.result)
            val serverAddressEditText = findViewById<EditText>(R.id.server_address)
            val serverPort = findViewById<EditText>(R.id.server_port_client)
            val clientAsyncTask = ClientAsyncTask(clientButton)
            clientAsyncTask.execute(
                serverAddressEditText.text.toString(),
                serverPort!!.text.toString()
            )
        }
    }
}