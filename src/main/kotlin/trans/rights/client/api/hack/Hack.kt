package trans.rights.client.api.hack

import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import trans.rights.TransRights.Companion.LOGGER
import trans.rights.client.api.Modular
import trans.rights.client.api.setting.Settings
import trans.rights.client.impl.setting.BooleanSetting
import trans.rights.client.impl.setting.EnumSetting
import trans.rights.client.impl.setting.NumberSetting
import trans.rights.client.util.file.FileHelper
import trans.rights.event.bus.impl.BasicEventManager
import java.io.File

abstract class Hack(
    name: String,
    description: String,
) : Modular(name, description) {
    var enabled: Boolean = false
    val settings: Settings = Settings()
    val file: File

    init {
        this.file = File(HackManager.directory.absolutePath + "$name.json")

        if (!this.file.exists()) this.file.createNewFile()
    }

    protected fun enable() {
        if (!this.enabled) {
            BasicEventManager.register(this)

            this.onEnable()
            this.enabled = true
        }
    }

    protected fun disable() {
        if (this.enabled) {
            BasicEventManager.unregister(this)

            this.onDisable()
            this.enabled = false
        }
    }

    fun toggle() {
        if (this.enabled) disable() else enable()
    }

    open fun onEnable() {}

    open fun onDisable() {}

    // TODO: Make this better / more streamlined
    fun load(file: File) {
        try {
            if (FileHelper.read(this.file.toPath()) == "") {
                this.save(file)
                return
            }

            val json = FileHelper.fromJson(file.toPath())

            this.enabled = json.get("enabled").asBoolean

            for (setting in settings.values) {
                when (setting) {
                    is BooleanSetting -> setting.value = json.get(setting.name).asBoolean
                    is NumberSetting -> setting.value = json.get(setting.name).asDouble
                }
            }
        } catch (e: Exception) {
            FileHelper.clearJson(file.toPath())

            LOGGER.error("$name failed to load")

            e.printStackTrace()
        }
    }

    fun save(file: File) {
        try {
            val json = JsonObject()

            json.add("enabled", JsonPrimitive(enabled))

            for (setting in settings.values) {
                when (setting) {
                    is BooleanSetting -> json.add(setting.name, JsonPrimitive(setting.value))
                    is NumberSetting -> json.add(setting.name, JsonPrimitive(setting.value))
                    is EnumSetting -> json.add(setting.name, JsonPrimitive(setting.value.toString()))
                }
            }

            FileHelper.writeToJson(json, file.toPath())
        } catch (e: Exception) {
            LOGGER.error("$name failed to save")

            e.printStackTrace()
        }
    }
}