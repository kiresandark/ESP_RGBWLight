package com.example.espmechapp.Color;

import android.graphics.Color;

public class ColorInt {
    public void getColorCicle() {
        new ColorPickerPopup.Builder(this)
                .initialColor(Color.RED) // Set initial color
                .enableBrightness(true) // Enable brightness slider or not
                .enableAlpha(true) // Enable alpha slider or not
                .okTitle("Choose")
                .cancelTitle("Cancel")
                .showIndicator(true)
                .showValue(true)
                .build()
                .show(v, new ColorPickerPopup.ColorPickerObserver() {
                    @Override
                    public void onColorPicked(int color) {
                        v.setBackgroundColor(color);
                    }

                    @Override
                    public void onColor(int color, boolean fromUser) {

                    }
                });
    }
}
