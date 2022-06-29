package com.example.myapplication

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.ui.AppBarConfiguration
import com.example.myapplication.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val urlArr by lazy {
        listOf(
            "https://www.aparat.com/v/n029j",
            "https://www.aparat.com/v/SG39a",
            "https://www.aparat.com/v/TgImf"
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val webSettings: WebSettings = binding.webview.getSettings()
        webSettings.javaScriptEnabled = true
        settingWebView(binding.webview)
        setMobile(binding.webview)
        var index = 0
        binding.webview.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                lifecycleScope.launch {
                    delay(5000)
                    runCatching {
                        view.loadUrl(
                            """javascript:(function f() {
        var btns = document.getElementsByClassName('single-details__header')[0].getElementsByTagName('h1')[0].getElementsByTagName('span')[0];
        console.log('Title: \n '+ btns.textContent);
        btns;
      })()""")
                    }.onSuccess {
                        Log.i("Title", "onPageFinished: $index" )
                        delay(1000)
                        if (index < urlArr.size)
                            binding.webview.loadUrl(urlArr[index++])
                    }
                }
            }
        }
        binding.webview.loadUrl(urlArr[index])
        index++

    }

    companion object {
        private const val TAG = "MainActivity"
    }


    fun settingWebView(webView: WebView) {
        webView.settings.builtInZoomControls = true
        webView.settings.displayZoomControls = false
        webView.settings.useWideViewPort = true
        webView.settings.loadWithOverviewMode = true
        webView.scrollBarStyle = WebView.SCROLLBARS_OUTSIDE_OVERLAY
        webView.isScrollbarFadingEnabled = false
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun setMobile(webView: WebView) {
        webView.settings.allowContentAccess = true
        webView.settings.setAppCacheEnabled(true)
        webView.settings.loadWithOverviewMode = true
        webView.settings.setSupportZoom(true)
        webView.settings.javaScriptEnabled = true
        webView.settings.saveFormData = true
        webView.settings.domStorageEnabled = true
        webView.getSettings()
            .setUserAgentString("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36");

    }

    fun setDesktopMode(webView: WebView, enabled: Boolean) {
        var newUserAgent: String? = webView.settings.userAgentString
        if (enabled) {
            try {
                val ua: String = webView.settings.userAgentString
                val androidOSString: String = webView.settings.userAgentString.substring(
                    ua.indexOf("("),
                    ua.indexOf(")") + 1
                )
                newUserAgent =
                    webView.settings.userAgentString.replace(
                        androidOSString,
                        "(X11; Linux x86_64)"
                    )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            newUserAgent = null
        }
        webView.settings.apply {
            userAgentString = newUserAgent
            useWideViewPort = enabled
            loadWithOverviewMode = enabled
        }
        webView.reload()
    }
}
