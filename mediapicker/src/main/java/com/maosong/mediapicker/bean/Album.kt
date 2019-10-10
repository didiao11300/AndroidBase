package com.maosong.mediapicker.bean

/**
 *create by colin 2018/9/14
 *
 * media资料实体
 */
data class Album(var name: String = "") {

    val datas: MutableList<MediaItem> = mutableListOf()

    override fun toString(): String {
        return "$name (${datas.size})"
    }
}