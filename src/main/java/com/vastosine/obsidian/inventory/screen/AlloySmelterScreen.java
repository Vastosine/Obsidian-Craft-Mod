package com.vastosine.obsidian.inventory.screen;

import com.vastosine.obsidian.inventory.menu.AlloySmelterMenu;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import org.jspecify.annotations.NonNull;

@Environment(EnvType.CLIENT)
public class AlloySmelterScreen extends AbstractAlloySmelterScreen<AlloySmelterMenu> {


//    public AlloySmelterScreen(AbstractAlloySmelterMenu menu, Inventory inventory, Component title) {
//        super(menu, inventory, title);
//    }

    private static final Identifier HOPPER_LOCATION = Identifier.withDefaultNamespace("textures/gui/container/hopper.png");

    public AlloySmelterScreen(final AlloySmelterMenu menu, final Inventory inventory, final Component title) {
        super(menu, inventory, title, 176, 133);
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    public void extractBackground(final @NonNull GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);
        int xo = (this.width - this.imageWidth) / 2;
        int yo = (this.height - this.imageHeight) / 2;
        graphics.blit(RenderPipelines.GUI_TEXTURED, HOPPER_LOCATION, xo, yo, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);
    }
}
