package trans.rights.client.modules.hack

import java.io.File
import trans.rights.client.modules.Manager
import trans.rights.client.modules.hack.hacks.*
import trans.rights.client.util.file.maindir
import kotlin.requireNotNull

object HackManager : Manager<Hack>(mutableSetOf()) {
    val dir = File("$maindir/hacks")

    init {
        if (!dir.exists()) dir.mkdirs()

        this.add(FlightHack)
        this.add(AutoHit)
    }

    fun save() {
        this.values.stream().forEach { hack -> hack.save(hack.file) }
    }

    override fun load() {
        this.values.stream().forEach { hack ->
            hack.load(hack.file)
            hack.save(hack.file)
        }
    }

    override fun unload() {
        this.values.stream().forEach { hack ->
            hack.save(hack.file)
            hack.settings.clear()
        }
        values.clear()
    }

    fun forEachEnabledO(action: (Hack) -> Unit) {
        requireNotNull(action)

        this.values.stream().filter(Hack::isEnabled).forEach { hack ->
            action.invoke(hack)
        }
    }
}
