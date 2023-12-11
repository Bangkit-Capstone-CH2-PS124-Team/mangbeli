package com.capstone.mangbeli.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.capstone.mangbeli.R
import com.capstone.mangbeli.data.remote.response.ErrorResponse
import com.capstone.mangbeli.databinding.ActivityLoginBinding
import com.capstone.mangbeli.ui.ViewModelFactory
import com.capstone.mangbeli.ui.home.HomeActivity
import com.capstone.mangbeli.ui.signup.SignUpActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException

class LoginActivity : AppCompatActivity() {

    private val contract = FirebaseAuthUIActivityResultContract()
    private val signInLauncher = registerForActivityResult(contract) {
        onSignInResult(it)
    }
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
            googleSignInButton.setOnClickListener {
                mulaiLogin()
            }
            btnToRegister.setOnClickListener {
                startActivity(Intent(this@LoginActivity, SignUpActivity::class.java))
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
                    ViewModelFactory.refreshInstance()
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

    private fun mulaiLogin() {
        val providers = arrayListOf(AuthUI.IdpConfig.GoogleBuilder().build())
        val intent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()
        signInLauncher.launch(intent)
    }



    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == RESULT_OK) {
            val nama = FirebaseAuth.getInstance().currentUser?.displayName
            val account = GoogleSignIn.getLastSignedInAccount(this)
            val idToken = account?.idToken.orEmpty() //
            lifecycleScope.launch {
                runOnUiThread {
                    showSuccessDialog(nama.toString() + idToken)
                    showLoading(false)
                }
            }
            Log.i("LOGIN", " $idToken berhasil login")
        } else {
            Log.i("LOGIN", "Login gagal: ${response?.error?.errorCode}")
        }
    }


    private fun showSuccessDialog(email: String) {
        AlertDialog.Builder(this).apply {
            setTitle(getString(R.string.success))
            setMessage(resources.getString(R.string.success) + email)
            setPositiveButton(resources.getString(R.string.next_btn)) { _, _ ->
                Intent(this@LoginActivity, HomeActivity::class.java).also {
                    startActivity(it)
                    finish()
                }
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

    private fun showLoading(isLoading: Boolean) {
        binding.loadingProgressBar.isVisible = isLoading
    }


}