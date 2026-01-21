import android.util.Log
import android.widget.EditText
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException
import java.io.PrintWriter
import java.net.InetAddress
import java.net.ServerSocket

class ServerThread(private val port: Int, private val data: EditText) : Thread() {
    private var isRunning = false

    private var serverSocket: ServerSocket? = null

    data class MoneyIndex(
        val value: String,
        val timestamp: Long = System.currentTimeMillis()
    )
    val moneyIndex = HashMap<String, MoneyIndex>()

    fun startServer() {
        isRunning = true
        start()
        Log.v("Server Thread", "startServer() method was invoked")
    }

    fun stopServer() {
        isRunning = false
        try {
            serverSocket!!.close()
        } catch (ioException: IOException) {
            Log.e("Server Thread", "An exception has occurred: " + ioException.message)
        }
        Log.v("Server Thread", "stopServer() method was invoked")
    }

    override fun run() {
        Log.d("Server Threads", port.toString())
        serverSocket = ServerSocket(port, 50, InetAddress.getByName("0.0.0.0"))
        try {
            while (isRunning) {
                val socket = serverSocket!!.accept()
                Log.d("Server Threads", "accept()-ed: " + socket.getInetAddress())


                Thread {
                    val printWriter = PrintWriter(socket.getOutputStream(), true)
                    //printWriter.println("Hello")
                    Log.d("Server Thread", "Trying get")
                    var currency = data.text.toString()
                    if (!moneyIndex.containsKey(currency)
                        || System.currentTimeMillis() - moneyIndex[currency]!!.timestamp >= 10000
                    ) {
                        val client = OkHttpClient()
                        val request = Request.Builder()
                            .url("https://data-api.coindesk.com/index/cc/v1/latest/tick?market=cadli&instruments=BTC-$currency")
                            .build()

                        try {

                            client.newCall(request).execute().use { response ->
                                if (response.isSuccessful && response.body != null) {
                                    val content = response.body!!.string()

                                    Log.d("Server Thread", content)

                                    val objectJson = JSONObject(content).getJSONObject("Data")
                                        .getJSONObject("BTC-$currency").getString("VALUE")
                                    Log.d("Server Thread", "Value is " + objectJson.toString())
                                    printWriter.println(objectJson.toString())
                                    val new_entry = MoneyIndex(objectJson.toString())
                                    moneyIndex[currency] = new_entry
                                }
                            }
                            socket.close()
                        } catch (e: IOException) {
                            Log.e("TAAG", "Cererea de rețea a eșuat: ${e.message}")
                        }
                    } else {
                        Log.d("Server Thread", "Already passed request")
                        Log.d("ServerThread", "Value is " + moneyIndex[currency]!!.value)
                        printWriter.println(moneyIndex[currency]!!.value)
                        socket.close()
                    }
                }.start()
            }
        } catch (ioException: IOException) {
            Log.e("Server Thread", "An exception has occurred: " + ioException.message)
        }
    }
}