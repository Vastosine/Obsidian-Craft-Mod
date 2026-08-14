package com.vastosine.obsidian.mixin;

import com.vastosine.obsidian.item.OCItems;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Entity.class)
public class EntityMixin {
    @ModifyVariable(method = "setRemainingFireTicks", at = @At("HEAD"), ordinal = 0)
    private int obsidianReduceBurnTime(int remainingTicks) {
        Entity self = (Entity) (Object) this;
        if (remainingTicks <= 0 || remainingTicks <= self.getRemainingFireTicks()) {
            return remainingTicks;
        }
//        ObsidianCraft.LOGGER.info("Original remaining fire ticks: " + remainingTicks);
        if (self instanceof LivingEntity livingEntity) {
            int pieces = OCItems.countObsidianArmor(livingEntity);
            if (pieces > 0) {
                remainingTicks = Math.max(0, remainingTicks = (int) Math.ceil(remainingTicks * (1 - pieces * 0.18F)));
            }
        }
//        ObsidianCraft.LOGGER.info("Remaining fire ticks after obsidian armor reduction: " + remainingTicks);
        return remainingTicks;
    }
}
