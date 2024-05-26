package io.github.lapis256.flux_integrations.mixin.exponentialPower;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.mosadie.exponentialpower.entities.BaseClasses.GeneratorBE;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sonar.fluxnetworks.api.FluxCapabilities;
import sonar.fluxnetworks.common.device.TileFluxPlug;


@Mixin(value = GeneratorBE.class, remap = false)
public class MixinGeneratorBE {
    @Shadow public double energy;

    @Inject(method = "handleSendingEnergy", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/BlockEntity;getCapability(Lnet/minecraftforge/common/capabilities/Capability;Lnet/minecraft/core/Direction;)Lnet/minecraftforge/common/util/LazyOptional;", shift = At.Shift.BEFORE), cancellable = true)
    private void onSendingEnergy(CallbackInfo ci, @Local BlockEntity blockEntity) {
        if(!(blockEntity instanceof TileFluxPlug plug)) {
            return;
        }
        plug.getCapability(FluxCapabilities.FN_ENERGY_STORAGE).ifPresent(energyStorage -> {
            if(!energyStorage.canReceive()) {
                return;
            }
            this.energy -= energyStorage.receiveEnergyL((long) Math.min(this.energy, Long.MAX_VALUE), false);
        });
        ci.cancel();
    }
}
