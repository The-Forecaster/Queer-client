package trans.rights.impl.command

import com.mojang.brigadier.Command.SINGLE_SUCCESS
import com.mojang.brigadier.arguments.StringArgumentType.getString
import com.mojang.brigadier.arguments.StringArgumentType.word
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder.argument
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.command.CommandSource
import trans.rights.Queer
import trans.rights.api.Wrapper
import trans.rights.api.command.Command
import trans.rights.util.clientSend

object CReloadCommand : Command("creload", "Reload parts of the client or mc", "/creload <mc or client>"), Wrapper {
    private fun reloadClient() {
        clientSend("Reloading the client...")

        Queer.unload()
        Queer.load()
    }

    private fun reloadMc() {
        clientSend("Reloading minecraft...")
        minecraft.reloadResourcesConcurrently()
    }

    private fun reload(): Int {
        reloadMc()
        reloadClient()

        return SINGLE_SUCCESS
    }

    @Throws(CommandSyntaxException::class)
    private fun reload(context: CommandContext<FabricClientCommandSource>): Int {
        try {
            when (getString(context, "type")) {
                null, "mc", "minecraft" -> reloadMc()
                "client" -> reloadClient()
                "all", "full" -> reload()
                else -> throw builtin.dispatcherUnknownArgument().create()
            }
        } catch (e: IllegalArgumentException) {
            reloadClient()
        }

        return SINGLE_SUCCESS
    }

    override fun build(builder: LiteralArgumentBuilder<FabricClientCommandSource>): LiteralArgumentBuilder<FabricClientCommandSource> =
        builder.executes(::reload).then(argument("type", word())).executes(::reload)
}
