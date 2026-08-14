package com.vastosine.obsidian.mixin;

import com.vastosine.obsidian.item.OCItems;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class ObsidianFireProtectionMixin {
    @Inject(method = "getDamageAfterMagicAbsorb", at = @At("RETURN"), cancellable = true)
    protected void getDamageAfterMagicAbsorb(DamageSource damageSource, float damage, CallbackInfoReturnable<Float> cir) {
        float value = cir.getReturnValue();
        if (value <= 0.0F || !damageSource.is(DamageTypeTags.IS_FIRE)) {
            return ;
        }
        int pieces = OCItems.countObsidianArmor((LivingEntity) (Object) this);
        if (pieces > 0) {
            cir.setReturnValue((float) (value * Math.pow(0.8F, pieces)));
        }
    }
}
