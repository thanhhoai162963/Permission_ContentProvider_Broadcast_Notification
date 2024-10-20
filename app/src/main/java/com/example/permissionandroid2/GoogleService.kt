package com.example.permissionandroid2

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


object GoogleService {

    fun initOneTap(context:Context){
        val credentialManager1 = CredentialManager.create(context)
        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(context.getString(R.string.web_app_one_tap))
            .setAutoSelectEnabled(true)
        .build()

        val request: GetCredentialRequest = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val result = credentialManager1.getCredential(
                    request = request,
                    context = context,
                )
                handleSignIn(result){
                }
            } catch (e: GetCredentialException) {
                Log.e("error:", "${e.message}")
            }
        }

    }
    private suspend fun handleSignIn(
        result: GetCredentialResponse?,
        callbackToken: suspend (String) -> Unit
    ) {
        when (val credential = result?.credential) {

            is CustomCredential -> {

                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data).idToken
                        callbackToken(googleIdTokenCredential)

                    } catch (e: GoogleIdTokenParsingException) {
                        Log.e("error:", "Received an invalid google id token response", e)
                    }
                } else {
                    Log.e("error:", "Unexpected type of credential")
                }
            }

            else -> {
                Log.e("TAG", "Unexpected type of credential")
            }
        }
    }
}