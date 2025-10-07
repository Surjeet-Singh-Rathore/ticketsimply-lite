package com.bitla.ts.domain.pojo.location_distance

data class Eta(
    val geocoded_waypoints: List<GeocodedWaypoint>,
    val routes: List<Route>,
    val status: String
)
