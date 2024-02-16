package com.codingwithsaurav.sociallogin

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.codingwithsaurav.sociallogin.databinding.ActivityMainBinding
import com.codingwithsaurav.sociallogin.sign_in.GoogleAuthUiClient
import com.codingwithsaurav.sociallogin.sign_in.SignInViewModel
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null
    private var callbackManager: CallbackManager? = null
    private lateinit var auth: FirebaseAuth

    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }
    var viewModel: SignInViewModel?= null
    val launcher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            lifecycleScope.launch {
                val signInResult = googleAuthUiClient.signInWithIntent(
                    intent = result.data ?: return@launch
                )
                viewModel?.onSignInResult(signInResult)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        callbackManager = CallbackManager.Factory.create()
        viewModel = ViewModelProvider(this)[SignInViewModel::class.java]
        auth = Firebase.auth
        binding?.faceLoginTextView?.setOnClickListener {
            auth.signOut()
            initializeFaceBookLogin()
        }
        binding?.googleLoginTextView?.setOnClickListener {
            lifecycleScope.launch {
                googleAuthUiClient.signOut()
                val signInIntentSender = googleAuthUiClient.signIn()
                launcher.launch(IntentSenderRequest.Builder(signInIntentSender ?: return@launch).build())
            }
        }
        viewModel?.state?.observe(this, Observer { signInState ->
            if(signInState.isSignInSuccessful){
                binding?.userNameTextView?.text = googleAuthUiClient.getSignedInUser()?.username
                binding?.profileImageView?.let { image->
                    Glide.with(this)
                        .load(googleAuthUiClient.getSignedInUser()?.profilePictureUrl)
                        .placeholder(R.drawable.placeholder_image) // Optional placeholder image
                        .error(R.drawable.error_image) // Optional error image
                        .into(image)
                }
            }else{
                Toast.makeText(this, signInState.signInError, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun initializeFaceBookLogin() {
        LoginManager.getInstance().logInWithReadPermissions(this, listOf("public_profile"));
        LoginManager.getInstance().registerCallback(callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    handleFacebookAccessToken(loginResult.accessToken)
                }
                override fun onCancel() {
                }
                override fun onError(exception: FacebookException) {
                }
            })
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d("xzlkcnlzxkn", "handleFacebookAccessToken:$token")
        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("xzlkcnlzxkn", "signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("xzlkcnlzxkn", "signInWithCredential:failure", task.exception)
                    Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }

    private fun updateUI(firebaseUser: FirebaseUser?) {
        firebaseUser?.let {
            binding?.userNameTextView?.text = it.displayName
            binding?.profileImageView?.let { image->
                Glide.with(this)
                    .load(it.photoUrl)
                     .placeholder(R.drawable.placeholder_image) // Optional placeholder image
                    .error(R.drawable.error_image) // Optional error image
                    .into(image)
            }
          Toast.makeText(this, "User Name = ${it.displayName} Profile Url ${it.photoUrl} Email ${it.email}", Toast.LENGTH_SHORT).show()
            Log.d("xzlkcnlzxkn", "User Name = ${it.displayName} Profile Url ${it.photoUrl} Email ${it.email}")
       }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Pass the activity result back to the Facebook SDK
        callbackManager?.onActivityResult(requestCode, resultCode, data)
    }

}