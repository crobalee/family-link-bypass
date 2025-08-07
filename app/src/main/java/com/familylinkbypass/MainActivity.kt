package com.familylinkbypass

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.familylinkbypass.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    
    companion object {
        private const val OVERLAY_PERMISSION_REQUEST_CODE = 1001
        private const val ACCESSIBILITY_PERMISSION_REQUEST_CODE = 1002
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupUI()
        checkPermissions()
        updateStatus()
    }
    
    private fun setupUI() {
        binding.startBypassButton.setOnClickListener {
            if (checkAllPermissions()) {
                startBypassProcess()
            } else {
                requestPermissions()
            }
        }
        
        binding.setupAccountButton.setOnClickListener {
            if (checkAllPermissions()) {
                setupGoogleAccount()
            } else {
                requestPermissions()
            }
        }
    }
    
    private fun checkPermissions(): Boolean {
        val overlayPermission = Settings.canDrawOverlays(this)
        val accessibilityPermission = isAccessibilityServiceEnabled()
        
        logMessage("권한 상태 확인:")
        logMessage("- 오버레이 권한: ${if (overlayPermission) "허용" else "거부"}")
        logMessage("- 접근성 서비스: ${if (accessibilityPermission) "활성화" else "비활성화"}")
        
        return overlayPermission && accessibilityPermission
    }
    
    private fun checkAllPermissions(): Boolean {
        return Settings.canDrawOverlays(this) && isAccessibilityServiceEnabled()
    }
    
    private fun isAccessibilityServiceEnabled(): Boolean {
        val accessibilityEnabled = Settings.Secure.getInt(
            contentResolver,
            Settings.Secure.ACCESSIBILITY_ENABLED, 0
        )
        
        if (accessibilityEnabled == 1) {
            val service = "${packageName}/.BypassService"
            val settingValue = Settings.Secure.getString(
                contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            )
            return settingValue?.contains(service) == true
        }
        return false
    }
    
    private fun requestPermissions() {
        logMessage("필요한 권한을 요청합니다...")
        
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            startActivityForResult(intent, OVERLAY_PERMISSION_REQUEST_CODE)
        }
        
        if (!isAccessibilityServiceEnabled()) {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            startActivityForResult(intent, ACCESSIBILITY_PERMISSION_REQUEST_CODE)
        }
    }
    
    private fun startBypassProcess() {
        logMessage("패밀리 링크 우회 프로세스를 시작합니다...")
        
        try {
            // 접근성 서비스 시작
            startAccessibilityService()
            
            logMessage("패밀리 링크 우회 프로세스가 시작되었습니다!")
            updateStatus()
            Toast.makeText(this, "패밀리 링크 우회 프로세스가 시작되었습니다", Toast.LENGTH_LONG).show()
            
        } catch (e: Exception) {
            logMessage("우회 실패: ${e.message}")
            Toast.makeText(this, "우회에 실패했습니다: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
    
    private fun startAccessibilityService() {
        logMessage("접근성 서비스 시작...")
        
        val intent = Intent(this, BypassService::class.java)
        startService(intent)
        
        logMessage("접근성 서비스가 시작되었습니다")
    }
    
    private fun setupGoogleAccount() {
        logMessage("구글 계정 설정을 시작합니다...")
        
        try {
            // 구글 계정 설정 액티비티 시작
            val intent = Intent(Settings.ACTION_ADD_ACCOUNT)
            intent.putExtra("account_types", arrayOf("com.google"))
            startActivity(intent)
            
            // 계정 설정 도우미 액티비티 시작
            startAccountSetupActivity()
            
            logMessage("구글 계정 설정 액티비티가 시작되었습니다")
            Toast.makeText(this, "구글 계정 설정 화면으로 이동합니다", Toast.LENGTH_LONG).show()
            
        } catch (e: Exception) {
            logMessage("계정 설정 실패: ${e.message}")
            Toast.makeText(this, "계정 설정에 실패했습니다: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
    
    private fun startAccountSetupActivity() {
        val intent = Intent(this, AccountSetupActivity::class.java)
        startActivity(intent)
    }
    
    private fun updateStatus() {
        val bypassEnabled = !isFamilyLinkActive()
        val accountExists = checkGoogleAccountExists()
        
        binding.bypassStatusText.text = "패밀리 링크: ${if (bypassEnabled) "우회됨" else "제한됨"}"
        binding.bypassStatusText.setTextColor(
            ContextCompat.getColor(this, if (bypassEnabled) R.color.green else R.color.red)
        )
        
        binding.accountStatusText.text = "구글 계정: ${if (accountExists) "설정됨" else "설정되지 않음"}"
        binding.accountStatusText.setTextColor(
            ContextCompat.getColor(this, if (accountExists) R.color.green else R.color.red)
        )
    }
    
    private fun isFamilyLinkActive(): Boolean {
        return try {
            val familyLinkEnabled = Settings.Secure.getString(
                contentResolver,
                "family_link_enabled"
            )
            familyLinkEnabled == "1"
        } catch (e: Exception) {
            false
        }
    }
    
    private fun checkGoogleAccountExists(): Boolean {
        return try {
            val accountManager = android.accounts.AccountManager.get(this)
            val accounts = accountManager.getAccountsByType("com.google")
            accounts.isNotEmpty()
        } catch (e: Exception) {
            false
        }
    }
    
    private fun logMessage(message: String) {
        val timestamp = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())
        val logMessage = "[$timestamp] $message"
        
        Log.d("FamilyLinkBypass", logMessage)
        
        runOnUiThread {
            binding.logText.append("$logMessage\n")
            // 스크롤을 맨 아래로
            val scrollAmount = binding.logText.layout.getLineTop(binding.logText.lineCount) - binding.logText.height
            if (scrollAmount > 0) {
                binding.logText.scrollTo(0, scrollAmount)
            }
        }
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        
        when (requestCode) {
            OVERLAY_PERMISSION_REQUEST_CODE -> {
                if (Settings.canDrawOverlays(this)) {
                    logMessage("오버레이 권한이 허용되었습니다")
                } else {
                    logMessage("오버레이 권한이 거부되었습니다")
                }
            }
            ACCESSIBILITY_PERMISSION_REQUEST_CODE -> {
                if (isAccessibilityServiceEnabled()) {
                    logMessage("접근성 서비스가 활성화되었습니다")
                } else {
                    logMessage("접근성 서비스가 비활성화되어 있습니다")
                }
            }
        }
        
        updateStatus()
    }
    
    override fun onResume() {
        super.onResume()
        updateStatus()
    }
}
