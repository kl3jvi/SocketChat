package com.kl3jvi.chat

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.kl3jvi.chat.fragments.ChatViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinComponent

class MainActivity : AppCompatActivity(), KoinComponent {
    private val viewModel: ChatViewModel by viewModel()
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navController = findNavController(R.id.nav_host_fragment_activity_main)

        showConnectionDialog()
    }

    private fun showConnectionDialog() {
        val ipInputLayout = TextInputLayout(this).apply {
            hint = "IP Address"
            boxBackgroundColor = Color.TRANSPARENT
            layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT).apply {
                setMargins(dpToPx(16), dpToPx(0), dpToPx(16), dpToPx(4))
            }
        }
        val ipEditText = TextInputEditText(ipInputLayout.context)
        ipInputLayout.addView(ipEditText)

        val portInputLayout = TextInputLayout(this).apply {
            hint = "Port"
            boxBackgroundColor = Color.TRANSPARENT
            layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT).apply {
                setMargins(dpToPx(16), dpToPx(0), dpToPx(16), dpToPx(0))
            }
        }
        val portEditText = TextInputEditText(portInputLayout.context)
        portInputLayout.addView(portEditText)

        val usernameInputLayout = TextInputLayout(this).apply {
            hint = "Username"
            boxBackgroundColor = Color.TRANSPARENT
            layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT).apply {
                setMargins(dpToPx(16), dpToPx(0), dpToPx(16), dpToPx(0))
            }
        }
        val usernameEditText = TextInputEditText(portInputLayout.context)
        usernameInputLayout.addView(usernameEditText)

        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle("Connect to Server")
            .setCancelable(false)
            .setView(
                LinearLayout(this).apply {
                    orientation = LinearLayout.VERTICAL
                    addView(ipInputLayout)
                    addView(portInputLayout)
                    addView(usernameInputLayout)
                }
            )
            .setPositiveButton("Connect") { _, _ ->
                val ipAddress = ipEditText.text.toString()
                val port = portEditText.text.toString().toIntOrNull() ?: 0
                val username = usernameEditText.text.toString()
                // Connect to the server using ChatViewModel
                viewModel.connect(ipAddress, port, username)
            }
            .show()

        // Disable the positive button initially
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false

        // Enable the positive button only when both IP and port fields are filled
        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val isIPFilled = ipEditText.text?.isNotEmpty() == true
                val isPortFilled = portEditText.text?.isNotEmpty() == true
                val isUsernameFilled = usernameEditText.text?.isNotEmpty() == true
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled =
                    isIPFilled && isPortFilled && isUsernameFilled
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        ipEditText.addTextChangedListener(textWatcher)
        portEditText.addTextChangedListener(textWatcher)
        usernameEditText.addTextChangedListener(textWatcher)
    }

    private fun dpToPx(dp: Int): Int {
        val density = resources.displayMetrics.density
        return (dp * density).toInt()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }
}
