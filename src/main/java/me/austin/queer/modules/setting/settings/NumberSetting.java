package me.austin.queer.modules.setting.settings;

import me.austin.queer.modules.Modulus;
import me.austin.queer.modules.setting.Setting;

public class NumberSetting<T extends Number> extends Setting<T> {
    protected Number min, max;

    public NumberSetting(String name, String description, T defaultValue, T min, T max, Modulus parent) {
        super(name, description, defaultValue, parent);
        this.min = min;
        this.max = max;
    }

    public Number getMin() {
        return this.min;
    }

    public Number getMax() {
        return this.max;
    }

    @Override
    public void set(Number value) {
        this.value = (T) value;
    }
}