package com.vastosine.obsidian.mixin;

import com.vastosine.obsidian.item.ModItems;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

// Hardcoded burn time reduction: each worn obsidian armor piece cuts fire burn
// time by 20% (multiplicative, 0.8^pieces). Only new ignitions (a value greater
// than the current tick count) are scaled, so the per-tick decrement, which also
// goes through setRemainingFireTicks, passes through untouched.
@Mixin(Entity.class)
public abstract class EntityMixin {
	@ModifyVariable(method = "setRemainingFireTicks", at = @At("HEAD"), ordinal = 0)
	private int obsidianReduceBurnTime(int remainingTicks) {
		Entity self = (Entity) (Object) this;
		if (remainingTicks <= 0 || remainingTicks <= self.getRemainingFireTicks()) {
			return remainingTicks;
		}

		if (self instanceof LivingEntity living) {
			int pieces = ModItems.countObsidianArmorPieces(living);
			if (pieces > 0) {
				return Math.max(1, (int) Math.floor(remainingTicks * (1 - 0.2 * pieces)));
			}
		}
		return remainingTicks;
	}
}
