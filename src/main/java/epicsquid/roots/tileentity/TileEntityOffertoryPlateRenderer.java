package epicsquid.roots.tileentity;

import com.mojang.blaze3d.platform.GlStateManager;
import epicsquid.roots.Roots;
import epicsquid.roots.block.OffertoryPlateBlock;
import epicsquid.roots.init.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;

import java.util.Random;

public class TileEntityOffertoryPlateRenderer extends TileEntityRenderer<TileEntityOffertoryPlate> {

  @Override
  public void render(TileEntityOffertoryPlate tei, double x, double y, double z, float partialTicks, int destroyStage) {
    if (!tei.inventory.getStackInSlot(0).isEmpty()) {
      int count = getCount(tei.inventory.getStackInSlot(0));
      ItemRenderer r = Minecraft.getInstance().getItemRenderer();
      BlockState state = tei.getWorld().getBlockState(tei.getPos());
      if (state.getBlock() != ModBlocks.offertory_plate) {
        Roots.logger.error("Fatal error rendering offertory plate, block state was " + state.toString() + " when offertory plate was expected.");
        return;
      }
      Direction f = state.get(OffertoryPlateBlock.FACING);
      for (int i = 0; i < count; i++) {
        GlStateManager.pushMatrix();
        GlStateManager.translated(x + 0.5, y + 0.8125 + 0.0625 * (double) i + 0.0625 * (tei.inventory.getStackInSlot(0).getItem() instanceof BlockItem ? 1.0 : 0), z + 0.5);
        GlStateManager.rotated(180 - f.getHorizontalAngle(), 0, 1, 0);
        GlStateManager.rotatef(67.5f, 1.0f, 0, 0);
        Random random = new Random();
        random.setSeed(tei.inventory.getStackInSlot(0).hashCode() + 256 * i);
        GlStateManager.translatef(0.125f * (random.nextFloat() - 0.5f), -0.1875f + 0.125f * (random.nextFloat() - 0.5f), 0);
        r.renderItem(tei.inventory.getStackInSlot(0), TransformType.GROUND);
        GlStateManager.popMatrix();
      }
    }
  }

  public int getCount(ItemStack s) {
    if (s.getCount() == 64) {
      return 5;
    }
    if (s.getCount() > 33) {
      return 4;
    }
    if (s.getCount() > 16) {
      return 3;
    }
    if (s.getCount() >= 2) {
      return 2;
    }
    if (s.getCount() == 1) {
      return 1;
    }
    return 0;
  }

}