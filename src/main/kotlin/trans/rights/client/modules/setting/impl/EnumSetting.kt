package trans.rights.client.modules.setting.impl

import trans.rights.client.modules.setting.Setting

class EnumSetting(name: String, default: Enum<*>, private val values: Array<Enum<*>>) : Setting<Enum<*>>(name, default) {
    fun cycle() {
        if (this.value == this.values[this.values.size - 1]) {
            this.value = this.values[0]
            return
        }
        this.value = this.values[this.values.indexOf(this.value) + 1]
    }
}