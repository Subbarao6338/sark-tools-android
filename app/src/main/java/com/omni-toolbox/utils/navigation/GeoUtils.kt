package omni.toolbox.utils.navigation

import kotlin.math.*

object GeoUtils {
    private const val EARTH_RADIUS_METERS = 6371000.0

    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val phi1 = Math.toRadians(lat1)
        val phi2 = Math.toRadians(lat2)
        val deltaPhi = Math.toRadians(lat2 - lat1)
        val deltaLambda = Math.toRadians(lon2 - lon1)

        val a = sin(deltaPhi / 2).pow(2) + cos(phi1) * cos(phi2) * sin(deltaLambda / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return (EARTH_RADIUS_METERS * c).toFloat()
    }

    fun calculateBearing(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val phi1 = Math.toRadians(lat1)
        val phi2 = Math.toRadians(lat2)
        val deltaLambda = Math.toRadians(lon2 - lon1)

        val y = sin(deltaLambda) * cos(phi2)
        val x = cos(phi1) * sin(phi2) - sin(phi1) * cos(phi2) * cos(deltaLambda)
        val bearingDegrees = Math.toDegrees(atan2(y, x))

        return ((bearingDegrees + 360) % 360).toFloat()
    }

    fun destination(startLat: Double, startLon: Double, distance: Double, bearing: Double): Pair<Double, Double> {
        val lat1 = Math.toRadians(startLat)
        val lon1 = Math.toRadians(startLon)
        val brng = Math.toRadians(bearing)

        val lat2 = asin(sin(lat1) * cos(distance / EARTH_RADIUS_METERS) +
                cos(lat1) * sin(distance / EARTH_RADIUS_METERS) * cos(brng))
        val lon2 = lon1 + atan2(sin(brng) * sin(distance / EARTH_RADIUS_METERS) * cos(lat1),
                cos(distance / EARTH_RADIUS_METERS) - sin(lat1) * sin(lat2))

        val normalizedLon = (Math.toDegrees(lon2) + 540) % 360 - 180
        return Pair(Math.toDegrees(lat2), normalizedLon)
    }
}
