package com.beehivestudio.mylittleforrest

data class PushDTO(var to: String? = null,
                   var notification: Notification? = Notification()) {
    data class Notification(var body: String? = null,
                            var title: String? = null)
}