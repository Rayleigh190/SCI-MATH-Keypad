package com.jnu_alarm.scimath_keypad;
import com.jnu_alarm.scimath_keypad.BuildConfig;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class MainActivity extends AppCompatActivity {
    private WebView webView;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.webview);
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);

        // WebView 설정
        webView.getSettings().setJavaScriptEnabled(true); // JavaScript 사용 가능하게 설정
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                // 페이지 로드가 완료되면 새로고침 애니메이션 중지
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        // 헤더 추가하여 URL 로드
        String url = "http://192.168.0.10:8000/keypad/";
        webView.loadUrl(url, getCustomHeaders());

        // 스와이프 새로고침 설정
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // 현재 페이지 새로고침
                webView.reload();
            }
        });
    }

    // X-APP-ID 헤더를 추가하는 메서드
    private Map<String, String> getCustomHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("X-APP-ID", BuildConfig.X_APP_ID);  // Django 서버에서 기대하는 토큰
        return headers;
    }
}