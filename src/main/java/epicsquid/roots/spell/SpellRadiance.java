package epicsquid.roots.spell;

import epicsquid.mysticallib.network.PacketHandler;
import epicsquid.roots.Roots;
import epicsquid.roots.init.ModDamage;
import epicsquid.roots.init.ModItems;
import epicsquid.roots.modifiers.instance.ModifierInstanceList;
import epicsquid.roots.network.fx.MessageRadianceBeamFX;
import epicsquid.roots.recipe.ingredient.GoldOrSilverIngotIngredient;
import epicsquid.roots.util.types.Property;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.oredict.OreIngredient;

import java.util.ArrayList;
import java.util.List;

public class SpellRadiance extends SpellBase {
  public static Property.PropertyCooldown PROP_COOLDOWN = new Property.PropertyCooldown(10);
  public static Property.PropertyCastType PROP_CAST_TYPE = new Property.PropertyCastType(EnumCastType.CONTINUOUS);
  public static Property.PropertyCost PROP_COST_1 = new Property.PropertyCost(0, new SpellCost("cloud_berry", 0.5));
  public static Property.PropertyCost PROP_COST_2 = new Property.PropertyCost(1, new SpellCost("pereskia", 0.25));
  public static Property<Float> PROP_DISTANCE = new Property<>("distance", 32f).setDescription("maximum reach of radiance beam");
  public static Property.PropertyDamage PROP_DAMAGE = new Property.PropertyDamage(5f).setDescription("damage dealt each time by radiance beam");
  public static Property<Float> PROP_UNDEAD_DAMAGE = new Property<>("undead_damage", 3f).setDescription("damage dealt each time by radiance beam on undead mobs");

  public static ResourceLocation spellName = new ResourceLocation(Roots.MODID, "spell_radiance");
  public static SpellRadiance instance = new SpellRadiance(spellName);

  private float distance;
  private float damage;
  private float undeadDamage;

  public SpellRadiance(ResourceLocation name) {
    super(name, TextFormatting.WHITE, 255f / 255f, 255f / 255f, 64f / 255f, 255f / 255f, 255f / 255f, 192f / 255f);
    properties.addProperties(PROP_COOLDOWN, PROP_CAST_TYPE, PROP_COST_1, PROP_COST_2, PROP_DISTANCE, PROP_DAMAGE, PROP_UNDEAD_DAMAGE);
  }

  @Override
  public void init () {
    addIngredients(
        new GoldOrSilverIngotIngredient(),
        new OreIngredient("torch"),
        new OreIngredient("dyeYellow"),
        new ItemStack(ModItems.cloud_berry),
        new ItemStack(ModItems.pereskia)
    );
  }

  @Override
  public boolean cast(EntityPlayer player, ModifierInstanceList modifiers, int ticks) {
    if (!player.world.isRemote && player.ticksExisted % 2 == 0) {
      RayTraceResult result = player.world.rayTraceBlocks(player.getPositionVector().add(0, player.getEyeHeight(), 0),
          player.getPositionVector().add(0, player.getEyeHeight(), 0).add(player.getLookVec().scale(distance)));
      Vec3d direction = player.getLookVec();
      ArrayList<Vec3d> positions = new ArrayList<Vec3d>();
      float offX = 0.5f * (float) Math.sin(Math.toRadians(-90.0f - player.rotationYaw));
      float offZ = 0.5f * (float) Math.cos(Math.toRadians(-90.0f - player.rotationYaw));
      positions.add(new Vec3d(player.posX + offX, player.posY + player.getEyeHeight(), player.posZ + offZ));
      PacketHandler.sendToAllTracking(new MessageRadianceBeamFX(player.getUniqueID(), player.posX, player.posY + 1.0f, player.posZ), player);
      if (result != null) {
        positions.add(result.hitVec);
        if (result.typeOfHit == RayTraceResult.Type.BLOCK) {
          Vec3i hitSide = result.sideHit.getDirectionVec();
          float xCoeff = 1f;
          if (hitSide.getX() != 0) {
            xCoeff = -1f;
          }
          float yCoeff = 1f;
          if (hitSide.getY() != 0) {
            yCoeff = -1f;
          }
          float zCoeff = 1f;
          if (hitSide.getZ() != 0) {
            zCoeff = -1f;
          }
          direction = new Vec3d(direction.x * xCoeff, direction.y * yCoeff, direction.z * zCoeff);
          distance -= result.hitVec.subtract(player.getPositionVector()).length();
          if (distance > 0) {
            RayTraceResult result2 = player.world.rayTraceBlocks(result.hitVec, result.hitVec.add(direction.scale(distance)));
            if (result2 != null) {
              positions.add(result2.hitVec);
              if (result2.typeOfHit == RayTraceResult.Type.BLOCK) {
                hitSide = result2.sideHit.getDirectionVec();
                xCoeff = 1f;
                if (hitSide.getX() != 0) {
                  xCoeff = -1f;
                }
                yCoeff = 1f;
                if (hitSide.getY() != 0) {
                  yCoeff = -1f;
                }
                zCoeff = 1f;
                if (hitSide.getZ() != 0) {
                  zCoeff = -1f;
                }
                direction = new Vec3d(direction.x * xCoeff, direction.y * yCoeff, direction.z * zCoeff);
                distance -= result2.hitVec.subtract(player.getPositionVector()).length();
                if (distance > 0) {
                  RayTraceResult result3 = player.world.rayTraceBlocks(result2.hitVec, result2.hitVec.add(direction.scale(distance)));
                  if (result3 != null) {
                    positions.add(result3.hitVec);
                  } else {
                    positions.add(result2.hitVec.add(direction.scale(distance)));
                  }
                }
              }
            } else {
              positions.add(result.hitVec.add(direction.scale(distance)));
            }
          }
        }
      } else {
        positions.add(player.getPositionVector().add(0, player.getEyeHeight(), 0).add(player.getLookVec().scale(distance)));
      }
      int count = 0;
      if (positions.size() > 1) {
        for (int i = 0; i < positions.size() - 1; i++) {
          double bx = Math.abs(positions.get(i + 1).x - positions.get(i).x) * 0.1f;
          double by = Math.abs(positions.get(i + 1).y - positions.get(i).y) * 0.1f;
          double bz = Math.abs(positions.get(i + 1).z - positions.get(i).z) * 0.1f;
          for (float j = 0; j < 1; j += 0.1f) {
            double x = positions.get(i).x * (1.0f - j) + positions.get(i + 1).x * j;
            double y = positions.get(i).y * (1.0f - j) + positions.get(i + 1).y * j;
            double z = positions.get(i).z * (1.0f - j) + positions.get(i + 1).z * j;
            List<EntityLivingBase> entities = player.world
                .getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(x - bx, y - by, z - bz, x + bx, y + by, z + bz));
            for (EntityLivingBase e : entities) {
              if (!(e instanceof EntityPlayer && !FMLCommonHandler.instance().getMinecraftServerInstance().isPVPEnabled())
                  && e.getUniqueID().compareTo(player.getUniqueID()) != 0) {
                if (e.hurtTime <= 0 && !e.isDead) {
                  e.attackEntityFrom(ModDamage.radiantDamageFrom(player), damage);
                  if (e.isEntityUndead()) {
                    e.attackEntityFrom(ModDamage.radiantDamageFrom(player), undeadDamage);
                  }
                  e.setRevengeTarget(player);
                  e.setLastAttackedEntity(player);
                  count++;
                }
              }
            }
          }
        }
      }
      return count > 0;
    }
    return false;
  }

  @Override
  public void doFinalise() {
    this.castType = properties.get(PROP_CAST_TYPE);
    this.cooldown = properties.get(PROP_COOLDOWN);
    this.distance = properties.get(PROP_DISTANCE);
    this.damage = properties.get(PROP_DAMAGE);
    this.undeadDamage = properties.get(PROP_UNDEAD_DAMAGE);
  }
}
