package com.example.earthquake.Model

class EarthQuake {
    var place: String? = null
    var magnitude: Double? = null
    var time: Long = 0
    var detailLink: String? = null
    var type: String? = null
    var lat = 0.0
    var lon = 0.0

    constructor(place: String?, magnitude: Double?, time: Long, detailLink: String?, type: String?, lat: Double, lon: Double) {
        this.place = place
        this.magnitude = magnitude
        this.time = time
        this.detailLink = detailLink
        this.type = type
        this.lat = lat
        this.lon = lon
    }

    constructor() {}
}