package com.example.audio

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.log10
import kotlin.math.sqrt

class AudioRecorderStreamer(
    private val context: Context,
    private val scope: CoroutineScope
) {
    companion object {
        const val SAMPLE_RATE = 16000 // 16kHz
        const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
        const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
        private const val TAG = "AudioRecorderStreamer"
    }

    private var audioRecord: AudioRecord? = null
    private var recordingJob: Job? = null

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private val _currentAmplitude = MutableStateFlow(0f)
    val currentAmplitude: StateFlow<Float> = _currentAmplitude.asStateFlow()

    private val _waveformData = MutableStateFlow(List(16) { 0.1f })
    val waveformData: StateFlow<List<Float>> = _waveformData.asStateFlow()

    var onAudioChunkRecorded: ((ByteArray) -> Unit)? = null

    @SuppressLint("MissingPermission")
    fun startRecording(onChunk: (ByteArray) -> Unit) {
        if (_isRecording.value) return
        onAudioChunkRecorded = onChunk

        val minBufferSize = AudioRecord.getMinBufferSize(
            SAMPLE_RATE,
            CHANNEL_CONFIG,
            AUDIO_FORMAT
        )
        val bufferSize = maxOf(minBufferSize, SAMPLE_RATE * 2) // 1 second buffer

        try {
            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE,
                CHANNEL_CONFIG,
                AUDIO_FORMAT,
                bufferSize
            )

            if (audioRecord?.state != AudioRecord.STATE_INITIALIZED) {
                Log.e(TAG, "AudioRecord initialization failed")
                return
            }

            audioRecord?.startRecording()
            _isRecording.value = true

            recordingJob = scope.launch(Dispatchers.IO) {
                val chunkBuffer = ByteArray(1024 * 2) // ~64ms chunks at 16kHz 16-bit
                val waveHistory = ArrayList<Float>(16)
                for (i in 0 until 16) waveHistory.add(0.05f)

                while (isActive && _isRecording.value) {
                    val bytesRead = audioRecord?.read(chunkBuffer, 0, chunkBuffer.size) ?: -1
                    if (bytesRead > 0) {
                        val validChunk = chunkBuffer.copyOf(bytesRead)
                        onAudioChunkRecorded?.invoke(validChunk)

                        // Calculate RMS amplitude
                        var sum = 0.0
                        var i = 0
                        while (i < bytesRead - 1) {
                            val sample = (chunkBuffer[i + 1].toInt() shl 8) or (chunkBuffer[i].toInt() and 0xFF)
                            sum += sample * sample
                            i += 2
                        }
                        val sampleCount = bytesRead / 2
                        val rms = if (sampleCount > 0) sqrt(sum / sampleCount) else 0.0
                        val normalized = (rms / 32768.0).toFloat().coerceIn(0.02f, 1f)

                        _currentAmplitude.value = normalized

                        // Update waveform history
                        waveHistory.removeAt(0)
                        waveHistory.add(normalized)
                        _waveformData.value = waveHistory.toList()
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error starting audio recorder: ${e.message}", e)
            _isRecording.value = false
        }
    }

    fun stopRecording() {
        _isRecording.value = false
        recordingJob?.cancel()
        recordingJob = null
        try {
            audioRecord?.stop()
            audioRecord?.release()
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping audio recorder: ${e.message}")
        }
        audioRecord = null
        _currentAmplitude.value = 0f
    }
}
