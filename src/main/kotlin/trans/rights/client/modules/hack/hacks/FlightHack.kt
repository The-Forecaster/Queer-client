package trans.rights.client.modules.hack.hacks

import trans.rights.event.listener.impl.LambdaListener
import trans.rights.event.annotation.Listener
import trans.rights.client.events.TickEvent
import trans.rights.client.misc.Globals
import trans.rights.client.misc.Globals.mc
import trans.rights.client.modules.hack.Hack
import trans.rights.client.util.player.*

object FlightHack : Hack("Flight", "Fly using hacks"), Globals {
    private var vanilla = true
    private var speed = 10.0f
    private var withElytra = false
    private var cancelSpeed = false

    @Listener
    val updateListener = LambdaListener({ event ->
        if (!nullCheck() || mc.player!!.isFallFlying && withElytra || event.isInWorld)
            when (vanilla) {
                true -> this.doVanillaFlight()
                false -> this.doVelocity()
            }
    }, -50, this, TickEvent.PostTick::class.java)

    init {
        settings["Vanilla"] = vanilla
        settings["Flight-speed"] = speed
        settings["With-elytra"] = withElytra
        settings["Cancel-speed"] = cancelSpeed
    }

    private fun doVanillaFlight() {
        setFlySpeed(trueSpeed(), cancelSpeed)
    }

    private fun doVelocity() {
        setVelocity(trueSpeed(), cancelSpeed)
    }

    private fun trueSpeed(): Float {
        return speed / 10
    }

    override fun onDisable() {
        mc.player!!.abilities.allowFlying = false
        mc.player!!.abilities.flySpeed = 0.05f
    }
}
