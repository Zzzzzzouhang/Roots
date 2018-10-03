package epicsquid.roots.ritual;

import epicsquid.mysticalworld.init.ModItems;
import epicsquid.roots.entity.EntityRitualWarden;
import epicsquid.roots.entity.EntityRitualWindwall;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RitualWindwall extends RitualBase {
  public RitualWindwall(String name, int duration, boolean doUpdateValidity) {
    super(name, duration, doUpdateValidity);
    addIngredient(new ItemStack(ModItems.pereskia, 1));
    addIngredient(new ItemStack(ModItems.pereskia_bulb, 1));
    addIngredient(new ItemStack(ModItems.bark_spruce, 1));
    addIngredient(new ItemStack(ModItems.bark_birch, 1));
    addIngredient(new ItemStack(Items.FEATHER, 1));
  }

  @Override
  public boolean isValidForPos(World world, BlockPos pos) {
    BlockPos topPos = world.getTopSolidOrLiquidBlock(pos);
    return topPos.getY() == pos.getY() || topPos.getY() == pos.getY() + 1;
  }

  @Override
  public void doEffect(World world, BlockPos pos) {
    this.spawnEntity(world, pos, EntityRitualWindwall.class);
  }
}