package com.vastosine.obsidian.mixin;

import com.vastosine.obsidian.ObsidianCraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ExampleMixin {
	@Inject(at = @At("HEAD"), method = "mineBlock")
	public void mineBlock(ItemStack itemStack, Level level, BlockState state, BlockPos pos, LivingEntity owner, CallbackInfoReturnable<Boolean> cir) {
		// This code is injected into the start of Item.mineBlock()V
		ObsidianCraft.LOGGER.info("Hello World");
	}
}