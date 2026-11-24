package com.moralist.android.playlistimport

import androidx.compose.runtime.Composable
import com.moralist.shared.deezerimport.DeezerImportManager
import com.moralist.shared.generated.resources.Res
import com.moralist.shared.generated.resources.deezer_login_failed
import com.moralist.shared.generated.resources.import_connect_deezer_account

@Composable
@Suppress("FunctionName")
fun DeezerLoginScreen(
    isAuthenticating: Boolean,
    authenticationError: String?,
    onClearAuthenticationError: () -> Unit,
    onSessionToken: (String) -> Unit,
    onNavigateBack: () -> Unit,
) {
    ProviderLoginShell(
        titleRes = Res.string.import_connect_deezer_account,
        loginErrorRes = Res.string.deezer_login_failed,
        cookieUrl = "https://www.deezer.com",
        loginUrl = "https://www.deezer.com/",
        logTag = "DeezerImport",
        credentialLogMessage = "Deezer WebView session token",
        extractCredentials = { DeezerImportManager.extractArl(it) },
        credentialKey = { it },
        isAuthenticating = isAuthenticating,
        authenticationError = authenticationError,
        onClearAuthenticationError = onClearAuthenticationError,
        onCredentials = onSessionToken,
        onNavigateBack = onNavigateBack,
    )
}
