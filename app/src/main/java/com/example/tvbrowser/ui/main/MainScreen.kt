package com.example.tvbrowser.ui.main

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.KeyEvent
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation3.runtime.NavKey

@SuppressLint("SetJavaScriptEnabled")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onItemClick: (NavKey) -> Unit,
    modifier: Modifier = Modifier,
) {
    var url by remember { mutableStateOf("https://www.google.com") }
    var inputUrl by remember { mutableStateOf("https://www.google.com") }
    var isOverlayVisible by remember { mutableStateOf(true) }
    var webViewRef by remember { mutableStateOf<WebView?>(null) }
    
    val focusRequester = remember { FocusRequester() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .onKeyEvent { event ->
                if (event.type == KeyEventType.KeyUp) {
                    when (event.nativeKeyEvent.keyCode) {
                        KeyEvent.KEYCODE_MENU, KeyEvent.KEYCODE_DPAD_CENTER -> {
                            if (!isOverlayVisible) {
                                isOverlayVisible = true
                                true
                            } else {
                                false
                            }
                        }
                        KeyEvent.KEYCODE_BACK -> {
                            if (isOverlayVisible) {
                                isOverlayVisible = false
                                true
                            } else if (webViewRef?.canGoBack() == true) {
                                webViewRef?.goBack()
                                true
                            } else {
                                false
                            }
                        }
                        else -> false
                    }
                } else {
                    false
                }
            }
            .focusRequester(focusRequester)
            .focusable()
    ) {
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    
                    // Hardware Acceleration & Performance
                    setLayerType(android.view.View.LAYER_TYPE_HARDWARE, null)
                    
                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        
                        // Disable zoom since TV remotes don't use pinch-to-zoom
                        setSupportZoom(false)
                        builtInZoomControls = false
                        displayZoomControls = false
                        
                        // Security & Resource Optimization
                        allowFileAccess = false
                        allowContentAccess = false
                        setGeolocationEnabled(false)
                        
                        // Caching Strategy for faster load but low memory
                        cacheMode = android.webkit.WebSettings.LOAD_DEFAULT
                    }
                    
                    webViewClient = object : WebViewClient() {
                        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                            super.onPageStarted(view, url, favicon)
                            if (url != null) inputUrl = url
                        }

                        override fun shouldInterceptRequest(
                            view: WebView?,
                            request: WebResourceRequest?
                        ): WebResourceResponse? {
                            val adDomains = listOf("doubleclick.net", "adservice.google.com", "ads.yahoo.com", "taboola.com")
                            val reqUrl = request?.url?.toString() ?: ""
                            if (adDomains.any { reqUrl.contains(it) }) {
                                return WebResourceResponse("text/plain", "UTF-8", null)
                            }
                            return super.shouldInterceptRequest(view, request)
                        }
                    }
                    loadUrl(url)
                }
            },
            update = { webView ->
                webViewRef = webView
                if (webView.url != url && webView.originalUrl != url) {
                    webView.loadUrl(url)
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        if (isOverlayVisible) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .background(Color.Black.copy(alpha = 0.85f))
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(onClick = { webViewRef?.goBack() }) {
                        Text("Back")
                    }
                    Button(onClick = { webViewRef?.goForward() }) {
                        Text("Forward")
                    }
                    Button(onClick = { webViewRef?.reload() }) {
                        Text("Refresh")
                    }
                    OutlinedTextField(
                        value = inputUrl,
                        onValueChange = { inputUrl = it },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
                        keyboardActions = KeyboardActions(
                            onGo = {
                                var finalUrl = inputUrl
                                if (!finalUrl.startsWith("http://") && !finalUrl.startsWith("https://")) {
                                    finalUrl = "https://$finalUrl"
                                }
                                url = finalUrl
                                isOverlayVisible = false
                            }
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        )
                    )
                    Button(onClick = { isOverlayVisible = false }) {
                        Text("Hide")
                    }
                }
            }
        }
        
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
    }
}
