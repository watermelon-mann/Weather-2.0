package com.watermelonman.domain.utils

import com.watermelonman.data.remoteservice.WeatherAPI
import com.watermelonman.entities.enums.IconSize


fun String.convertIconCodenameToIconUrl(iconSize: IconSize = IconSize.X_1): String {
    return "${WeatherAPI.ICON_URL}$this${iconSize.size}.png"
}