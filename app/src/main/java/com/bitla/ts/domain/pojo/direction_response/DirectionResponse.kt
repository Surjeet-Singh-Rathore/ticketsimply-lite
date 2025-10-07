package com.bitla.ts.domain.pojo.direction_response

import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName


class DirectionsResponse {
    @SerializedName("routes")
    private val routes: List<Route>? = null

    val path: List<LatLng>
        get() {
            val path: MutableList<LatLng> = ArrayList()
            if (!routes.isNullOrEmpty()) {
                for (route in routes) {
                    for (leg in route.legs!!) {
                        for (step in leg.steps!!) {
                            path.add(step.startLocation!!.toLatLng())
                            path.add(step.endLocation!!.toLatLng())
                        }
                    }
                }
            }
            return path
        }

    private inner class Route {
        @SerializedName("legs")
        val legs: List<Leg>? = null
    }

    private inner class Leg {
        @SerializedName("steps")
        val steps: List<Step>? = null
    }

    private inner class Step {
        @SerializedName("start_location")
        val startLocation: Location? = null

        @SerializedName("end_location")
        val endLocation: Location? = null
    }

    private inner class Location {
        @SerializedName("lat")
        private val lat = 0.0

        @SerializedName("lng")
        private val lng = 0.0

        fun toLatLng(): LatLng {
            return LatLng(lat, lng)
        }
    }
}
