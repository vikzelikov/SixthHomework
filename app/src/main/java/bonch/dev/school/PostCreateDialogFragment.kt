package bonch.dev.school

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import bonch.dev.school.networking.RetrofitFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class PostCreateDialogFragment(context: Context) : DialogFragment() {
    private val mainContext = context
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val inflater = activity?.layoutInflater?.inflate(R.layout.fragment_dialog, null)
        val titleTextView = view?.findViewById<TextView>(R.id.title)
        val bodyTextView = view?.findViewById<TextView>(R.id.body)

        builder.setPositiveButton("Send") { dialog, which ->

            val title = titleTextView?.text.toString()
            val body = bodyTextView?.text.toString()
            val service = RetrofitFactory.makeRetrofitService()
            CoroutineScope(Dispatchers.IO).launch {
                val response = service.getPost(title, body)
                try {
                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            Toast.makeText(mainContext, "Success : ${response.code()}", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(mainContext, "Fail : ${response.code()}", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (err: HttpException) {
                    Log.e("Retrofit", "${err.printStackTrace()}")
                }
            }
        }
        builder.setView(inflater)
        return builder.create()
    }
}