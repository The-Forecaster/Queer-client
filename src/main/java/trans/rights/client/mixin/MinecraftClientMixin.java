package trans.rights.client.mixin;

import static trans.rights.client.Globals.EVENTBUS;
import static trans.rights.client.Globals.mc;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.util.profiler.Profiler;
import trans.rights.client.events.TickEvent.PostTick;
import trans.rights.client.events.TickEvent.PreTick;

@Mixin(MinecraftClient.class)
public final class MinecraftClientMixin extends MinecraftClient {
    @Shadow
    private Profiler profiler;

    private MinecraftClientMixin(RunArgs args) {
        super(args);
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private final void beforeTick(CallbackInfo info) {
        var event = PreTick.get(mc.player != null && mc.world != null);

        EVENTBUS.post(event);
        if (event.isCancelled())
            info.cancel();
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;pop()V", ordinal = 0, shift = At.Shift.AFTER), cancellable = true)
    private final void onTick(CallbackInfo info) {
        var event = PostTick.get(mc.player != null && mc.world != null);

        EVENTBUS.post(event);
        if (event.isCancelled())
            info.cancel();
    }
}