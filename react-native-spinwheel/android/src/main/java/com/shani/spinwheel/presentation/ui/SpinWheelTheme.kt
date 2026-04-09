package com.shani.spinwheel.presentation.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.shani.spinwheel.R

/**
 * This file contains the Compose theme definitions used to style the Spin Wheel UI.
 */

internal val PoppinsBold = FontFamily(Font(R.font.poppins_bold))

internal val BrandColor = Color(0xFF7B4FBF)

internal val BrandTextStyle = TextStyle(
    fontFamily = PoppinsBold,
    color = BrandColor,
    fontSize = 14.sp
)
