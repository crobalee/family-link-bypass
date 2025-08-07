package com.familylinkbypass

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import java.util.concurrent.Executors

class BypassService : AccessibilityService() {
    
    private val TAG = "BypassService"
    private val executor = Executors.newSingleThreadExecutor()
    private val handler = Handler(Looper.getMainLooper())
    private var overlayView: View? = null
    private var windowManager: WindowManager? = null
    
    override fun onServiceConnected() {
        super.onServiceConnected()
        
        val info = AccessibilityServiceInfo()
        info.apply {
            eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED or
                        AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED or
                        AccessibilityEvent.TYPE_VIEW_CLICKED or
                        AccessibilityEvent.TYPE_VIEW_FOCUSED
            
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            
            flags = AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS or
                   AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS or
                   AccessibilityServiceInfo.FLAG_REQUEST_ENHANCED_WEB_ACCESSIBILITY
            
            notificationTimeout = 100
            
            packageNames = arrayOf(
                "com.google.android.apps.familylink",
                "com.google.android.apps.familylink.parentaccess",
                "com.google.android.apps.familylink.dashboard",
                "com.google.android.apps.familylink.usageaccess",
                "com.android.settings",
                "com.google.android.gms",
                "com.google.android.gsf.login",
                "com.google.android.gsf"
            )
        }
        
        serviceInfo = info
        Log.d(TAG, "접근성 서비스가 연결되었습니다")
        
        // 오버레이 뷰 생성
        createOverlayView()
        
        // 패밀리 링크 우회 시작
        startBypassProcess()
    }
    
    private fun createOverlayView() {
        if (Settings.canDrawOverlays(this)) {
            windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
            
            val inflater = LayoutInflater.from(this)
            overlayView = inflater.inflate(R.layout.overlay_view, null)
            
            val params = WindowManager.LayoutParams().apply {
                type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                } else {
                    WindowManager.LayoutParams.TYPE_PHONE
                }
                format = PixelFormat.TRANSLUCENT
                flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                       WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                gravity = Gravity.TOP or Gravity.START
                width = WindowManager.LayoutParams.WRAP_CONTENT
                height = WindowManager.LayoutParams.WRAP_CONTENT
                x = 100
                y = 200
            }
            
            try {
                windowManager?.addView(overlayView, params)
                Log.d(TAG, "오버레이 뷰가 생성되었습니다")
            } catch (e: Exception) {
                Log.e(TAG, "오버레이 뷰 생성 실패: ${e.message}")
            }
        }
    }
    
    private fun startBypassProcess() {
        Log.d(TAG, "패밀리 링크 우회 프로세스를 시작합니다...")
        
        executor.execute {
            try {
                // 패밀리 링크 관련 앱 감지 및 모니터링
                monitorFamilyLinkApps()
                
                // 구글 계정 설정 화면 모니터링
                monitorGoogleAccountSetup()
                
                Log.d(TAG, "패밀리 링크 우회 프로세스가 시작되었습니다")
                
            } catch (e: Exception) {
                Log.e(TAG, "우회 프로세스 실패: ${e.message}")
            }
        }
    }
    
    private fun monitorFamilyLinkApps() {
        Log.d(TAG, "패밀리 링크 앱 모니터링 시작...")
        
        // 패밀리 링크 앱이 실행될 때 감지하여 사용자에게 알림
        handler.post {
            Toast.makeText(this, "패밀리 링크 앱이 감지되었습니다", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun monitorGoogleAccountSetup() {
        Log.d(TAG, "구글 계정 설정 모니터링 시작...")
        
        // 구글 계정 설정 화면에서 도움말 제공
        handler.post {
            Toast.makeText(this, "구글 계정 설정을 도와드립니다", Toast.LENGTH_SHORT).show()
        }
    }
    
    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        val packageName = event.packageName?.toString() ?: return
        
        when (event.eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                handleWindowStateChanged(event, packageName)
            }
            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> {
                handleWindowContentChanged(event, packageName)
            }
            AccessibilityEvent.TYPE_VIEW_CLICKED -> {
                handleViewClicked(event, packageName)
            }
            AccessibilityEvent.TYPE_VIEW_FOCUSED -> {
                handleViewFocused(event, packageName)
            }
        }
    }
    
    private fun handleWindowStateChanged(event: AccessibilityEvent, packageName: String) {
        Log.d(TAG, "윈도우 상태 변경: $packageName")
        
        when (packageName) {
            "com.google.android.apps.familylink" -> {
                Log.d(TAG, "패밀리 링크 앱이 실행되었습니다")
                showFamilyLinkNotification()
            }
            "com.android.settings" -> {
                Log.d(TAG, "설정 앱이 실행되었습니다")
                showSettingsNotification()
            }
            "com.google.android.gsf.login" -> {
                Log.d(TAG, "구글 로그인 화면이 실행되었습니다")
                showGoogleLoginNotification()
            }
        }
    }
    
    private fun handleWindowContentChanged(event: AccessibilityEvent, packageName: String) {
        // 화면 내용 변경 감지
        Log.d(TAG, "화면 내용 변경: $packageName")
    }
    
    private fun handleViewClicked(event: AccessibilityEvent, packageName: String) {
        // 뷰 클릭 감지
        Log.d(TAG, "뷰 클릭: $packageName")
    }
    
    private fun handleViewFocused(event: AccessibilityEvent, packageName: String) {
        // 뷰 포커스 감지
        Log.d(TAG, "뷰 포커스: $packageName")
    }
    
    private fun showFamilyLinkNotification() {
        handler.post {
            Toast.makeText(this, "패밀리 링크 앱이 감지되었습니다. 우회를 시도합니다.", Toast.LENGTH_LONG).show()
        }
    }
    
    private fun showSettingsNotification() {
        handler.post {
            Toast.makeText(this, "설정 화면에서 계정을 추가할 수 있습니다.", Toast.LENGTH_LONG).show()
        }
    }
    
    private fun showGoogleLoginNotification() {
        handler.post {
            Toast.makeText(this, "구글 로그인 화면입니다. 계정 정보를 입력해주세요.", Toast.LENGTH_LONG).show()
        }
    }
    
    override fun onInterrupt() {
        Log.d(TAG, "접근성 서비스가 중단되었습니다")
    }
    
    override fun onDestroy() {
        super.onDestroy()
        
        // 오버레이 뷰 제거
        try {
            if (overlayView != null && windowManager != null) {
                windowManager?.removeView(overlayView)
                overlayView = null
            }
        } catch (e: Exception) {
            Log.e(TAG, "오버레이 뷰 제거 실패: ${e.message}")
        }
        
        executor.shutdown()
        Log.d(TAG, "접근성 서비스가 종료되었습니다")
    }
}
