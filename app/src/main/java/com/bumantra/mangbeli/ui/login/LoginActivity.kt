package com.bumantra.mangbeli.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.bumantra.mangbeli.R
import com.bumantra.mangbeli.data.remote.response.ErrorResponse
import com.bumantra.mangbeli.databinding.ActivityLoginBinding
import com.bumantra.mangbeli.ui.ViewModelFactory
import com.bumantra.mangbeli.ui.home.HomeActivity
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        binding.apply {
            btnLoginActivity.setOnClickListener {
                val email = binding.edtEmail.text.toString()
                val password = binding.edtPassword.text.toString()
                loginSet(email, password)
            }
        }
    }

    private fun loginSet(email: String, password: String) {
        binding.apply {
            if (email.isEmpty()) {
                edtEmail.error = getString(R.string.email_is_required)
                edtEmail.requestFocus()
                return
            }
            if (password.isEmpty()) {
                edtPassword.error = getString(R.string.password_is_required)
                edtPassword.requestFocus()
                return
            }
            showLoading(true)
            btnLoginActivity.isEnabled = false

            lifecycleScope.launch {
                try {
                    viewModel.login(email, password)
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
                startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
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
                binding.btnLoginActivity.isEnabled = true
            }
            create()
            show()
        }
    }
    private fun showLoading(isLoading: Boolean){
        binding.loadingProgressBar.isVisible = isLoading
    }


}