package com.vastosine.obsidian.mixin;

import com.vastosine.obsidian.item.ModItems;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Hardcoded Fire Protection II on obsidian armor: each worn piece reduces fire damage
// by 10% (multiplicative, exactly like a Fire Protection II enchantment on that piece).
// Burn time reduction lives in EntityMixin.
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
	@Inject(method = "getDamageAfterMagicAbsorb", at = @At("RETURN"), cancellable = true)
	private void obsidianFireProtection(DamageSource damageSource, float damage, CallbackInfoReturnable<Float> cir) {
		float value = cir.getReturnValue();
		if (value <= 0.0F || !damageSource.is(DamageTypeTags.IS_FIRE)) {
			return;
		}

		LivingEntity self = (LivingEntity) (Object) this;
		int pieces = ModItems.countObsidianArmorPieces(self);
		if (pieces > 0) {
			cir.setReturnValue(value * (float) Math.pow(0.8, pieces));
		}
	}
}
