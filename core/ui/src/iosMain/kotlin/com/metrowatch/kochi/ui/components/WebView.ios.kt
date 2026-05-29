package com.metrowatch.kochi.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import platform.WebKit.WKWebView
import platform.Foundation.NSURLRequest
import platform.Foundation.NSURL

@Composable
actual fun WebView(url: String, modifier: Modifier) {
    UIKitView(
        factory = {
            WKWebView()
        },
        update = { webView ->
            val nsUrl = url.let { NSURL.URLWithString(it) }
            if (nsUrl != null) {
                webView.loadRequest(NSURLRequest.requestWithURL(nsUrl))
            }
        },
        modifier = modifier
    )
}
