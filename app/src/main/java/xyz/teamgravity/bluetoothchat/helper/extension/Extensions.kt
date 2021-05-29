package xyz.teamgravity.bluetoothchat.helper.extension

import android.view.View

fun log(tag: String, message: String) = println("$tag $message")

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

val <T> T.exhaustive: T
    get() = this