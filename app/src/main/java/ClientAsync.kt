import android.os.AsyncTask
import android.util.Log
import android.widget.TextView
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.Socket

class ClientAsyncTask(private val serverMessageTextView: TextView) :
    AsyncTask<String?, String?, Void?>() {
    override fun doInBackground(vararg params: String?): Void? {
        var socket: Socket? = null

        try {
            var address = params[0]!!
            var port = params[1]!!.toInt()
            socket = Socket(address, port)

            Log.d("Client", "Connect to server")
            var data = BufferedReader(InputStreamReader(socket.getInputStream()))
            var currentLine: String?
            while ((data.readLine().also { currentLine = it }) != null) {
                publishProgress(currentLine)
            }
        } catch (ioException: IOException) {
            Log.e("Client Thread", "An exception has occurred: " + ioException.message)
        } finally {
            try {
                if (socket != null) {
                    socket.close()
                }
                Log.v("Client Thread", "Connection closed")
            } catch (ioException: IOException) {
                Log.e("Client Thread", "An exception has occurred: " + ioException.message)
            }
        }
        return null
    }


    override fun onPreExecute() {
        serverMessageTextView.setText("")
    }

    override fun onProgressUpdate(vararg progress: String?) {
        serverMessageTextView.append(progress[0] + "\n")
    }

    override fun onPostExecute(result: Void?) {}

}
