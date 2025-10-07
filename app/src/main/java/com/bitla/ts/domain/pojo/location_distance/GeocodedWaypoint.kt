package com.bitla.ts.domain.pojo.location_distance

data class GeocodedWaypoint(
    val geocoder_status: String,
    val place_id: String,
    val types: List<String>
)
