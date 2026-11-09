package com.toolbox.healthfitness

import androidx.compose.runtime.Composable

object HealthFitnessRegistry {
    @Composable fun WaterIntakeCalc() = WaterIntakeCalcScreen()
    @Composable fun BodyFatCalc() = BodyFatCalcScreen()
    @Composable fun TargetHeartRate() = TargetHeartRateScreen()
    @Composable fun MacroCalc() = MacroCalcScreen()
    @Composable fun PaceCalc() = PaceCalcScreen()
}
