package com.capstone.mangbeli.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.capstone.mangbeli.R
import com.capstone.mangbeli.databinding.ActivityLoginBinding
import com.capstone.mangbeli.ui.ViewModelFactory
import com.capstone.mangbeli.ui.home.HomeActivity
import com.capstone.mangbeli.ui.home.TokenViewModelFactory
import com.capstone.mangbeli.ui.role.AddRoleActivity
import com.capstone.mangbeli.ui.signup.SignUpActivity
import com.capstone.mangbeli.utils.Result
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch


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
//            googleSignInButton.setOnClickListener {
//                mulaiLogin()
//            }
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

            viewModel.login(email, password).observe(this@LoginActivity) { result ->
                ViewModelFactory.refreshInstance()
                TokenViewModelFactory.refreshInstance()
                when (result) {
                    is Result.Loading -> {
                        binding.loadingProgressBar.visibility = View.VISIBLE
                    }

                    is Result.Success -> {
                        binding.loadingProgressBar.visibility = View.GONE
                        val userData = result.data

                        if (userData.role != null) {
                            val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            val intent = Intent(this@LoginActivity, AddRoleActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }

                    is Result.Error -> {
                        binding.loadingProgressBar.visibility = View.GONE
                        runOnUiThread {
                            showfailedDialog(result.error)
                            showLoading(false)
                        }
                        Log.d("LoginActivity", "onCreate: ${result.error}")
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
            val token = FirebaseAuth.getInstance().currentUser?.getIdToken(true)
            lifecycleScope.launch {
                runOnUiThread {
                    showSuccessDialog(nama.toString() + token.toString())
                    showLoading(false)
                }
            }
            Log.i("LOGIN", "$nama berhasil login")
        } else {
            Log.i("LOGIN", "Login gagal: ${response?.error?.errorCode}")
        }
    }

    private fun showSuccessDialog(email: String) {
        AlertDialog.Builder(this).apply {
            setTitle(getString(R.string.success))
            setMessage(resources.getString(R.string.success) + email)

            setPositiveButton(resources.getString(R.string.next_btn)) { _, _ ->
                startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                binding.btnLoginActivity.isEnabled = true
                finish()
            }
            create()
            show()
        }
    }

    private fun showfailedDialog(errorMessage: String) {
        AlertDialog.Builder(this).apply {
            when (errorMessage) {
                "Email is not registered" -> {
                    setTitle(getString(R.string.error))
                    setMessage(getString(R.string.email_not_registered))
                }
                "Wrong Password" -> {
                    setTitle(getString(R.string.error))
                    setMessage(getString(R.string.wrong_password))
                }
                "Password must be at least 8 characters" -> {
                    setTitle(getString(R.string.error))
                    setMessage(getString(R.string.minimal_8_characters))
                }
                else -> {
                    setTitle(getString(R.string.error))
                    setMessage(errorMessage)
                }
            }
            binding.btnLoginActivity.isEnabled = true
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