package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.os.Build
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.concurrent.LinkedBlockingQueue

class AudioTrackPlayer(
    private val context: Context,
    private val scope: CoroutineScope
) : TextToSpeech.OnInitListener {

    companion object {
        const val SAMPLE_RATE_LIVE = 24000 // 24kHz for Gemini Live
        const val SAMPLE_RATE_STANDARD = 16000
        private const val TAG = "AudioTrackPlayer"
    }

    private var audioTrack: AudioTrack? = null
    private var textToSpeech: TextToSpeech? = null
    private var isTtsInitialized = false

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _isMuted = MutableStateFlow(false)
    val isMuted: StateFlow<Boolean> = _isMuted.asStateFlow()

    private val _speechRate = MutableStateFlow(1.0f)
    val speechRate: StateFlow<Float> = _speechRate.asStateFlow()

    private val _outputAmplitude = MutableStateFlow(0f)
    val outputAmplitude: StateFlow<Float> = _outputAmplitude.asStateFlow()

    private val pcmQueue = LinkedBlockingQueue<ByteArray>()
    private var isPcmPlaying = false

    init {
        try {
            textToSpeech = TextToSpeech(context.applicationContext, this)
        } catch (e: Exception) {
            Log.e(TAG, "TTS creation error: ${e.message}")
        }
        initAudioTrack(SAMPLE_RATE_LIVE)
    }

    private fun initAudioTrack(sampleRate: Int) {
        try {
            val minBufferSize = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )
            val bufferSize = maxOf(minBufferSize, sampleRate * 2)

            val attributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build()

            val format = AudioFormat.Builder()
                .setSampleRate(sampleRate)
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                .build()

            audioTrack = AudioTrack(
                attributes,
                format,
                bufferSize,
                AudioTrack.MODE_STREAM,
                AudioManager.AUDIO_SESSION_ID_GENERATE
            )
            audioTrack?.play()
        } catch (e: Exception) {
            Log.e(TAG, "AudioTrack init failed: ${e.message}")
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val hindiLocale = Locale.Builder().setLanguage("hi").setRegion("IN").build()
            val result = textToSpeech?.setLanguage(hindiLocale)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Fallback to general Hindi or default
                textToSpeech?.setLanguage(Locale.Builder().setLanguage("hi").build())
            }
            textToSpeech?.setSpeechRate(_speechRate.value)
            textToSpeech?.setPitch(1.0f)
            textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _isPlaying.value = true
                }

                override fun onDone(utteranceId: String?) {
                    _isPlaying.value = false
                    _outputAmplitude.value = 0f
                }

                override fun onError(utteranceId: String?) {
                    _isPlaying.value = false
                    _outputAmplitude.value = 0f
                }
            })
            isTtsInitialized = true
        }
    }

    fun playPcmChunk(pcmData: ByteArray) {
        if (_isMuted.value) return
        pcmQueue.offer(pcmData)
        if (!isPcmPlaying) {
            startPcmPlayLoop()
        }
    }

    private fun startPcmPlayLoop() {
        isPcmPlaying = true
        _isPlaying.value = true
        scope.launch(Dispatchers.IO) {
            while (isPcmPlaying) {
                val chunk = pcmQueue.poll()
                if (chunk != null) {
                    audioTrack?.write(chunk, 0, chunk.size)
                    // estimate amplitude for visualizer
                    _outputAmplitude.value = 0.6f
                } else {
                    if (pcmQueue.isEmpty()) {
                        isPcmPlaying = false
                        _isPlaying.value = false
                        _outputAmplitude.value = 0f
                        break
                    }
                }
            }
        }
    }

    fun speakHindiText(text: String, utteranceId: String = System.currentTimeMillis().toString()) {
        if (_isMuted.value) return
        if (isTtsInitialized && textToSpeech != null) {
            _isPlaying.value = true
            val params = android.os.Bundle()
            params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId)
            textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, params, utteranceId)
        }
    }

    fun stopPlayback() {
        textToSpeech?.stop()
        pcmQueue.clear()
        isPcmPlaying = false
        _isPlaying.value = false
        _outputAmplitude.value = 0f
    }

    fun toggleMute() {
        _isMuted.value = !_isMuted.value
        if (_isMuted.value) {
            stopPlayback()
        }
    }

    fun setSpeed(speed: Float) {
        _speechRate.value = speed
        textToSpeech?.setSpeechRate(speed)
    }

    fun release() {
        stopPlayback()
        try {
            audioTrack?.stop()
            audioTrack?.release()
        } catch (e: Exception) {
            Log.e(TAG, "AudioTrack release error: ${e.message}")
        }
        audioTrack = null
        textToSpeech?.shutdown()
        textToSpeech = null
    }
}
