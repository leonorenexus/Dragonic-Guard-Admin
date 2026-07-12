package com.dragonic.guardparent.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dragonic.guardparent.model.*
import com.dragonic.guardparent.repository.ParentRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ParentViewModel(app: Application) : AndroidViewModel(app) {

    val repo = ParentRepository()
    private val ctx = app.applicationContext

    // ── Selected device ──────────────────────────────────────────────────────

    private val _selectedDeviceId = MutableStateFlow<String?>(null)
    val selectedDeviceId: StateFlow<String?> = _selectedDeviceId

    private val _deviceIds = MutableStateFlow<List<String>>(emptyList())
    val deviceIds: StateFlow<List<String>> = _deviceIds

    // ── Live data from Firestore ─────────────────────────────────────────────

    private val _childDevice = MutableStateFlow<ChildDevice?>(null)
    val childDevice: StateFlow<ChildDevice?> = _childDevice

    private val _rules = MutableStateFlow<List<AppRule>>(emptyList())
    val rules: StateFlow<List<AppRule>> = _rules

    private val _todayUsage = MutableStateFlow<List<AppUsageRecord>>(emptyList())
    val todayUsage: StateFlow<List<AppUsageRecord>> = _todayUsage

    // ── UI state ─────────────────────────────────────────────────────────────

    private val _commandSent = MutableStateFlow<String?>(null)
    val commandSent: StateFlow<String?> = _commandSent

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadDeviceIds()
    }

    fun loadDeviceIds() {
        _deviceIds.value = repo.getSavedDeviceIds(ctx)
        if (_deviceIds.value.isNotEmpty() && _selectedDeviceId.value == null) {
            selectDevice(_deviceIds.value.first())
        }
    }

    fun selectDevice(deviceId: String) {
        _selectedDeviceId.value = deviceId
        viewModelScope.launch {
            repo.watchChildDevice(deviceId).collect { _childDevice.value = it }
        }
        viewModelScope.launch {
            repo.watchRules(deviceId).collect { _rules.value = it }
        }
        viewModelScope.launch {
            repo.watchTodayUsage(deviceId).collect { _todayUsage.value = it }
        }
    }

    fun addDevice(deviceId: String) {
        repo.saveDeviceId(ctx, deviceId.trim())
        loadDeviceIds()
        selectDevice(deviceId.trim())
    }

    fun removeDevice(deviceId: String) {
        repo.removeDeviceId(ctx, deviceId)
        loadDeviceIds()
        if (_selectedDeviceId.value == deviceId) {
            _selectedDeviceId.value = _deviceIds.value.firstOrNull()
            _childDevice.value = null
        }
    }

    // ── Remote commands ──────────────────────────────────────────────────────

    fun lockDevice() = sendCommand(RemoteCommand(type = "LOCK_DEVICE"), "✅ Perintah kunci dikirim!")

    fun syncRules() = sendCommand(RemoteCommand(type = "SYNC_RULES"), "✅ Sinkron aturan dikirim!")

    fun ping() = sendCommand(RemoteCommand(type = "PING"), "✅ Ping dikirim!")

    private fun sendCommand(cmd: RemoteCommand, feedback: String) {
        val id = _selectedDeviceId.value ?: return
        viewModelScope.launch {
            try {
                repo.sendCommand(id, cmd)
                _commandSent.value = feedback
            } catch (e: Exception) {
                _commandSent.value = "❌ Gagal: ${e.message}"
            }
        }
    }

    fun setAppBlocked(rule: AppRule, blocked: Boolean) {
        val id = _selectedDeviceId.value ?: return
        viewModelScope.launch {
            try {
                repo.setAppBlocked(id, rule, blocked)
                _commandSent.value = if (blocked) "🚫 ${rule.appName} diblokir" else "✅ ${rule.appName} dibuka"
            } catch (e: Exception) {
                _commandSent.value = "❌ Gagal: ${e.message}"
            }
        }
    }

    fun setDailyLimit(rule: AppRule, minutes: Int) {
        val id = _selectedDeviceId.value ?: return
        viewModelScope.launch {
            try {
                repo.setDailyLimit(id, rule, minutes)
                _commandSent.value = "⏱️ Limit ${rule.appName}: ${minutes}m/hari"
            } catch (e: Exception) {
                _commandSent.value = "❌ Gagal: ${e.message}"
            }
        }
    }

    fun clearFeedback() { _commandSent.value = null }
}
