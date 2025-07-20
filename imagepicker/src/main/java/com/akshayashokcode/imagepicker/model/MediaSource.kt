package com.akshayashokcode.imagepicker.model

sealed class MediaSource {
    data object Gallery : MediaSource()
    data object Camera : MediaSource()
    data object Both : MediaSource()
}