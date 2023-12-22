package com.capstone.mangbeli.ui.signup

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.capstone.mangbeli.R
import com.capstone.mangbeli.data.remote.response.ErrorResponse
import com.capstone.mangbeli.databinding.ActivitySignUpBinding
import com.capstone.mangbeli.ui.ViewModelFactory
import com.capstone.mangbeli.ui.login.LoginActivity
import com.capstone.mangbeli.utils.isNetworkAvailable
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException


class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private val viewModel by viewModels<SignUpViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        binding.apply {
            binding.btnSignup.setOnClickListener {
                val name = binding.nameEditText.text.toString()
                val email = binding.emailEditText.text.toString()
                val password = binding.passwordEditText.text.toString()
                val confPassword = binding.confpasswordEditText.text.toString()
                if (!isNetworkAvailable(this@SignUpActivity)) {
                    Toast.makeText(
                        this@SignUpActivity,
                        "Internet connection is required",
                        Toast.LENGTH_LONG
                    )
                        .show()
                } else {
                    registerSet(name, email, password, confPassword)

                }
            }
        }
    }
    private fun registerSet(
        name: String,
        email: String,
        password: String,
        confPassword: String,
//        role: String
    ) {
        binding.apply {
            if (name.isEmpty()) {
                nameEditText.error = getString(R.string.name_is_required)
                nameEditText.requestFocus()
                return
            }

            if (email.isEmpty()) {
                emailEditText.error = getString(R.string.email_is_required)
                emailEditText.requestFocus()
                return
            }
            if (password.isEmpty()) {
                passwordEditText.error = getString(R.string.password_is_required)
                passwordEditText.requestFocus()
                return
            }
            if (password !=  confPassword) {
                confpasswordEditText.error = getString(R.string.confirpassword_is_required)
                confpasswordEditText.requestFocus()
                return
            }
            showLoading(true)
            btnSignup.isEnabled = false

            lifecycleScope.launch {
                try {
                    viewModel.register(name, email, password, confPassword)
                    runOnUiThread {
                        showSuccessDialog(email)
                        showLoading(false)
                    }
                } catch (e: HttpException) {
                    val jsonInString = e.response()?.errorBody()?.string()
                    val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
                    val errorMessage = errorBody.message
                    runOnUiThread {
                        errorMessage?.let { showfailedDialog(it) }
                        showLoading(false)
                    }
                }
            }
        }
    }
    private fun showSuccessDialog(email: String) {
        AlertDialog.Builder(this).apply {
            setTitle(getString(R.string.success))
            setMessage( resources.getString(R.string.success) + email)
            setPositiveButton(resources.getString(R.string.next_btn)) { _, _ ->
                startActivity(Intent(this@SignUpActivity, LoginActivity::class.java))
                finish()
            }
            create()
            show()
        }
    }
    private fun showfailedDialog(errorMessage: String) {
        AlertDialog.Builder(this).apply {
            setTitle(getString(R.string.error))
            setMessage(errorMessage)
            setPositiveButton(resources.getString(R.string.next_btn)) { _, _ ->
                binding.btnSignup.isEnabled = true
            }
            create()
            show()
        }
    }
    private fun showLoading(isLoading: Boolean){
        binding.loadingProgressBar.isVisible = isLoading
    }
}