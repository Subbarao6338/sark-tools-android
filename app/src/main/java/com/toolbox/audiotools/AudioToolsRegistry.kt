package com.toolbox.audiotools

import androidx.compose.runtime.Composable
import com.toolbox.measurement.soundmeter.SoundMeterScreen
import com.toolbox.everyday.whitenoise.WhiteNoiseScreen
import com.toolbox.everyday.metronome.MetronomeScreen
import com.toolbox.measurement.pitchtuner.PitchTunerScreen
import com.toolbox.measurement.spectrum.SpectrumScreen
import com.toolbox.everyday.tonegenerator.ToneGeneratorScreen
import com.toolbox.everyday.voicerecorder.VoiceRecorderScreen
import com.toolbox.media.audio.AudioEditorScreen

object AudioToolsRegistry {
    @Composable fun SoundMeter() = SoundMeterScreen()
    @Composable fun WhiteNoise() = WhiteNoiseScreen()
    @Composable fun Metronome() = MetronomeScreen()
    @Composable fun PitchTuner() = PitchTunerScreen()
    @Composable fun SpectrumAnalyzer() = SpectrumScreen()
    @Composable fun ToneGenerator() = ToneGeneratorScreen()
    @Composable fun VoiceRecorder() = VoiceRecorderScreen()
    @Composable fun AudioEditor() = AudioEditorScreen()
}
