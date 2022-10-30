package trans.rights.client.impl.hack

import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.network.packet.c2s.play.UpdatePlayerAbilitiesC2SPacket
import net.minecraft.network.packet.s2c.play.PlayerAbilitiesS2CPacket
import net.minecraft.util.math.Vec3d
import trans.rights.TransRights
import trans.rights.client.api.hack.Hack
import trans.rights.client.events.PacketEvent
import trans.rights.client.events.TickEvent
import trans.rights.client.impl.setting.NumberSetting
import trans.rights.client.impl.setting.Settings
import trans.rights.event.EventHandler
import trans.rights.event.listener

object FlightHack : Hack("Flight", "Fly using hacks") {
    private val speed = NumberSetting("Speed", "How fast you want to fly.", 15.0)
    // private val cancelSpeed = settings.add(BooleanSetting("Cancel-speed", "Do you want to cancel current speed before doing adding speed?", true))

    override val settings = Settings(speed)

    @EventHandler
    val updateListener = listener<TickEvent.Post> {
        if (!nullCheck()) player!!.setFlySpeed(trueSpeed(), true)
    }

    @EventHandler
    val packetReceiveListener = listener<PacketEvent.PostReceive> { event ->
        if (event.packet is PlayerAbilitiesS2CPacket) (event.packet as PlayerAbilitiesS2CPacket).let {
            it.allowFlying = true
            it.flying = true
            it.flySpeed = trueSpeed()
        }
    }

    @EventHandler
    val packetSendListener = listener<PacketEvent.PreSend> { event ->
        if (event.packet is UpdatePlayerAbilitiesC2SPacket) (event.packet as UpdatePlayerAbilitiesC2SPacket).flying = true
    }

    private fun trueSpeed() = (speed.value / 10).toFloat()

    override fun onEnable() {
        if (nullCheck()) disable()

        TransRights.LOGGER.info("$name enabled")
    }

    override fun onDisable() {
        if (!nullCheck()) {
            player!!.run {
                if (!this.isCreative) this.abilities.allowFlying = false
                this.abilities.flySpeed = 0.05f
                this.abilities.flying = false
            }
        }
    }
}

private fun ClientPlayerEntity.setFlySpeed(speed: Float, cancelSpeed: Boolean) {
    if (!this.isSpectator || MinecraftClient.getInstance().world != null) {
        if (cancelSpeed) this.velocity = Vec3d.ZERO

        this.abilities.allowFlying = true
        this.abilities.flying = true
        this.abilities.flySpeed = speed
    }
}