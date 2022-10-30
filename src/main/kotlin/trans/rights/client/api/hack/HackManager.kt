package trans.rights.client.api.hack

import trans.rights.BasicEventManager
import trans.rights.TransRights.Companion.mainDirectory
import trans.rights.client.api.command.CommandManager
import trans.rights.client.api.commons.Manager
import trans.rights.client.impl.command.HackCommand
import trans.rights.client.impl.hack.*
import java.nio.file.Files
import java.nio.file.Path

object HackManager : Manager<Hack, List<Hack>> {
    override val values = listOf(AntiFabric, AntiKick, AuraHack, FlightHack, WallHack).sortedWith(Comparator.comparing(Hack::name))

    internal val directory: Path = Path.of("$mainDirectory/hacks")

    override fun load() {
        if (!Files.exists(directory)) Files.createDirectory(directory)

        for (hack in values) hack.load()

        HackCommand.register(CommandManager.dispatcher)
    }

    override fun unload() {
        for (hack in values) {
            BasicEventManager.unregister(hack)
            hack.save()
        }
    }
}
