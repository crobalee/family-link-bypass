package com.familylinkbypass

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.familylinkbypass.databinding.ActivityAccountSetupBinding

class AccountSetupActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityAccountSetupBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountSetupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupUI()
        startAccountSetup()
    }
    
    private fun setupUI() {
        binding.emailText.text = "구글 계정 설정 도우미"
        binding.statusText.text = "계정 설정을 시작합니다..."
        
        binding.retryButton.setOnClickListener {
            startAccountSetup()
        }
        
        binding.manualButton.setOnClickListener {
            openManualAccountSetup()
        }
    }
    
    private fun startAccountSetup() {
        Log.d("AccountSetup", "계정 설정 시작")
        binding.statusText.text = "구글 계정 설정을 시작합니다..."
        
        try {
            // 구글 계정 추가 인텐트 시작
            val intent = Intent(Settings.ACTION_ADD_ACCOUNT)
            intent.putExtra("account_types", arrayOf("com.google"))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            
            binding.statusText.text = "구글 계정 설정 화면으로 이동했습니다.\n접근성 서비스가 자동으로 입력을 도와줍니다."
            
        } catch (e: Exception) {
            Log.e("AccountSetup", "계정 설정 실패: ${e.message}")
            binding.statusText.text = "계정 설정에 실패했습니다: ${e.message}"
            Toast.makeText(this, "계정 설정에 실패했습니다", Toast.LENGTH_LONG).show()
        }
    }
    
    private fun openManualAccountSetup() {
        try {
            // 설정 > 계정으로 이동
            val intent = Intent(Settings.ACTION_SYNC_SETTINGS)
            startActivity(intent)
            
            binding.statusText.text = "설정 > 계정으로 이동했습니다.\n수동으로 계정을 추가해주세요."
            
        } catch (e: Exception) {
            Log.e("AccountSetup", "수동 설정 실패: ${e.message}")
            binding.statusText.text = "수동 설정에 실패했습니다: ${e.message}"
        }
    }
    
    override fun onResume() {
        super.onResume()
        
        // 계정이 추가되었는지 확인
        checkAccountStatus()
    }
    
    private fun checkAccountStatus() {
        try {
            val accountManager = android.accounts.AccountManager.get(this)
            val accounts = accountManager.getAccountsByType("com.google")
            
            if (accounts.isNotEmpty()) {
                binding.statusText.text = "구글 계정이 성공적으로 추가되었습니다!\n계정: ${accounts[0].name}"
                Toast.makeText(this, "계정 설정이 완료되었습니다!", Toast.LENGTH_LONG).show()
                
                // 메인 액티비티로 돌아가기
                finish()
            } else {
                binding.statusText.text = "아직 계정이 추가되지 않았습니다.\n계속 진행하거나 수동으로 설정해주세요."
            }
            
        } catch (e: Exception) {
            Log.e("AccountSetup", "계정 상태 확인 실패: ${e.message}")
            binding.statusText.text = "계정 상태 확인에 실패했습니다: ${e.message}"
        }
    }
}
