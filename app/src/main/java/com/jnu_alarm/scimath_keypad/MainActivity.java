package com.jnu_alarm.scimath_keypad;

import com.jnu_alarm.scimath_keypad.BuildConfig;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.annotation.Nullable;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class MainActivity extends AppCompatActivity {
    private WebView webView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Handler handler = new Handler();
    private Runnable autoReloadTask;
    String url = BuildConfig.BASE_URL;
//    String url = "http://192.168.0.10:8000/keypad/";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.webview);
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);

        // WebView 설정
        webView.getSettings().setJavaScriptEnabled(true); // JavaScript 사용 가능하게 설정

        // JavaScript Interface 등록
        webView.addJavascriptInterface(new WebAppInterface(), "AndroidInterface");

        // WebViewClient 설정
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

        // URL 로드 및 헤더 설정
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

    @Override
    protected void onStart() {
        super.onStart();
        // 자동 새로고침 작업 시작
        startAutoReload();
    }

    // X-APP-ID 헤더를 추가하는 메서드
    private Map<String, String> getCustomHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("X-APP-ID", BuildConfig.X_APP_ID);  // Django 서버에서 기대하는 토큰
        return headers;
    }

    // JavaScript Interface 정의
    public class WebAppInterface {
        @JavascriptInterface
        public String getAppId() {
            // BuildConfig에서 X-APP-ID를 가져옴
            return BuildConfig.X_APP_ID;
        }

        @JavascriptInterface
        public void reloadWebView() {
            // 웹뷰 새로고침
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    webView.reload();
                }
            });
        }
    }

    private void startAutoReload() {
        autoReloadTask = new Runnable() {
            @Override
            public void run() {
                // 현재 시간 확인
                Calendar now = Calendar.getInstance();
                int hour = now.get(Calendar.HOUR_OF_DAY);
                // 0시 또는 6시에 WebView 새로고침
                if (hour == 0 || hour == 6 || hour == 9) {
                    webView.loadUrl(url, getCustomHeaders());
                }

                // 1시간 후 다시 실행
                handler.postDelayed(this, 60 * 60 * 1000); // 1시간 (3600초)
            }
        };

        // 첫 실행
        handler.post(autoReloadTask);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Handler에서 작업 제거
        if (handler != null && autoReloadTask != null) {
            handler.removeCallbacks(autoReloadTask);
        }
    }
}
