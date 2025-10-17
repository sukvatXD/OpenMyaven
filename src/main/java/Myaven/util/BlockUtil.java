package Myaven.util;

import Myaven.Myaven;
import Myaven.management.RotationManager;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.BlockBasePressurePlate;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockBrewingStand;
import net.minecraft.block.BlockButton;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockDropper;
import net.minecraft.block.BlockEnchantmentTable;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockFlowerPot;
import net.minecraft.block.BlockFurnace;
import net.minecraft.block.BlockGlass;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.BlockJukebox;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockLever;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.BlockWorkbench;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.util.Vec3i;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockUtil {
   private static long[] a;
   private static Integer[] b;

   public static BlockData getPlaceableBlock(double y) {
      return findValidPlacement(y);
   }

   public static BlockData findValidPlacement(double theY) {
      int startY = MathHelper.floor_double(Myaven.mc.thePlayer.posY);
      BlockPos targetPos = new BlockPos(
         (double)MathHelper.floor_double(Myaven.mc.thePlayer.posX), theY, (double)MathHelper.floor_double(Myaven.mc.thePlayer.posZ)
      );
      if (!isContainerBlock(targetPos)) {
         return null;
      } else {
         ArrayList<BlockPos> positions = new ArrayList<>();

         for (int x = -4; x <= 4; x++) {
            for (int y = -4; y <= 0; y++) {
               for (int z = -4; z <= 4; z++) {
                  BlockPos pos = targetPos.add(x, y, z);
                  if (!isContainerBlock(pos)
                     && !isContainer(pos)
                     && !(
                        Myaven.mc.thePlayer.getDistance((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5)
                           > (double)Myaven.mc.playerController.getBlockReachDistance()
                     )) {
                     for (EnumFacing facing : EnumFacing.VALUES) {
                        if (facing != EnumFacing.DOWN) {
                           BlockPos blockPos = pos.offset(facing);
                           if (isContainerBlock(blockPos)) {
                              positions.add(pos);
                           }
                        }
                     }
                  }
               }
            }
         }

         if (positions.isEmpty()) {
            return null;
         } else {
            positions.sort(
               Comparator.comparingDouble(
                  o -> o.distanceSqToCenter((double)targetPos.getX() + 0.5, (double)targetPos.getY() + 0.5, (double)targetPos.getZ() + 0.5)
               )
            );
            BlockPos blockPos = positions.get(0);
            EnumFacing facingx = getFacingBetween(blockPos, targetPos);
            return facingx == null ? null : new BlockData(blockPos, facingx);
         }
      }
   }

   public static EnumFacing getFacingBetween(BlockPos blockPos1, BlockPos blockPos3) {
      double offset = 0.0;
      EnumFacing enumFacing = null;

      for (EnumFacing facing : EnumFacing.VALUES) {
         if (facing != EnumFacing.DOWN) {
            BlockPos pos = blockPos1.offset(facing);
            if (pos.getY() <= blockPos3.getY()) {
               double distance = pos.distanceSqToCenter((double)blockPos3.getX() + 0.5, (double)blockPos3.getY() + 0.5, (double)blockPos3.getZ() + 0.5);
               if (enumFacing == null || distance < offset || distance == offset && facing == EnumFacing.UP) {
                  offset = distance;
                  enumFacing = facing;
               }
            }
         }
      }

      return enumFacing;
   }

   public static boolean isContainer(BlockPos blockPos) {
      return isContainer(Myaven.mc.theWorld.getBlockState(blockPos).getBlock());
   }

   public static boolean isContainer(Block block) {
      if (block instanceof BlockContainer) {
         return true;
      } else if (block instanceof BlockWorkbench) {
         return true;
      } else if (block instanceof BlockAnvil) {
         return true;
      } else if (block instanceof BlockBed) {
         return true;
      } else if (block instanceof BlockDoor && block.getMaterial() != Material.iron) {
         return true;
      } else if (block instanceof BlockTrapDoor) {
         return true;
      } else if (block instanceof BlockFenceGate) {
         return true;
      } else if (block instanceof BlockFence) {
         return true;
      } else if (block instanceof BlockButton) {
         return true;
      } else {
         return block instanceof BlockLever ? true : block instanceof BlockJukebox;
      }
   }

   public static boolean isContainerBlock(BlockPos blockPos) {
      return isReplaceable(Myaven.mc.theWorld.getBlockState(blockPos).getBlock());
   }

   public static boolean isReplaceable(Block block) {
      if (!block.getMaterial().isReplaceable()) {
         return false;
      } else {
         return !(block instanceof BlockSnow) ? true : !(block.getBlockBoundsMaxY() > 0.125);
      }
   }

   public static boolean isReplaceable(BlockPos blockPos, BlockPos blockPos2) {
      return blockPos == blockPos2 || blockPos.getX() == blockPos2.getX() && blockPos.getY() == blockPos2.getY() && blockPos.getZ() == blockPos2.getZ();
   }

   public static EnumFacing getClosestFacing(BlockPos target) {
      Vec3 center = new Vec3((double)target.getX() + 0.5, (double)target.getY() + 0.5, (double)target.getZ() + 0.5);
      double minDist = Double.MAX_VALUE;
      EnumFacing closest = EnumFacing.NORTH;

      for (EnumFacing facing : EnumFacing.values()) {
         Vec3 faceCenter = center.addVector(
            (double)facing.getFrontOffsetX() * 0.5, (double)facing.getFrontOffsetY() * 0.5, (double)facing.getFrontOffsetZ() * 0.5
         );
         double dist = ClientUtil.getPlayerEyesPosition().distanceTo(faceCenter);
         if (dist < minDist) {
            minDist = dist;
            closest = facing;
         }
      }

      return closest;
   }

   public static boolean isEntityColliding(BlockPos pos) {
      new AxisAlignedBB(
         (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), (double)(pos.getX() + 1), (double)(pos.getY() + 1), (double)(pos.getZ() + 1)
      );

      for (Entity entity : Myaven.mc.theWorld.loadedEntityList) {
         if (intersectsEntity(pos, entity)) {
            return true;
         }
      }

      return intersectsEntity(pos, Myaven.mc.thePlayer);
   }

   public static boolean intersectsEntity(BlockPos pos, Entity entity) {
      IBlockState state = Myaven.mc.theWorld.getBlockState(pos);
      Block block = state.getBlock();
      AxisAlignedBB blockBB = block.getCollisionBoundingBox(Myaven.mc.theWorld, pos, state);
      if (blockBB == null) {
         return false;
      } else {
         AxisAlignedBB entityBB = entity.getEntityBoundingBox();
         return blockBB.intersectsWith(entityBB);
      }
   }

   public static Vec3 toVec3(BlockPos blockPos) {
      return new Vec3((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ());
   }

   public static BlockPos toBlockPos(Vec3 vec3) {
      return new BlockPos(vec3.xCoord, vec3.yCoord, vec3.zCoord);
   }

   public static List<Vec3> getBlockSurfacePoints(BlockPos pos, EnumFacing facing) {
      List<Vec3> points = new ArrayList<>();
      double dx = 0.1;

      for (int i = 0; i < 8; i++) {
         double dy = 0.1;

         for (int y = 0; y < 8; y++) {
            Vec3 point;
            switch (facing) {
               case UP:
               case DOWN:
                  point = new Vec3((double)pos.getX() + dx, (double)(pos.getY() + (facing == EnumFacing.UP ? 1 : 0)), (double)pos.getZ() + dy);
                  break;
               case NORTH:
               case SOUTH:
                  point = new Vec3((double)pos.getX() + dx, (double)pos.getY() + dy, (double)(pos.getZ() + (facing == EnumFacing.SOUTH ? 1 : 0)));
                  break;
               case EAST:
               case WEST:
                  point = new Vec3((double)(pos.getX() + (facing == EnumFacing.EAST ? 1 : 0)), (double)pos.getY() + dy, (double)pos.getZ() + dx);
                  break;
               default:
                  continue;
            }

            points.add(point);
            dy += 0.1;
         }

         dx += 0.1;
      }

      return points;
   }

   public static Vec3 getBestViewPoint(Vec3 eyePos, float desiredYaw, List<Vec3> candidates) {
      Vec3 best = candidates.get(0);
      float BestYaw = RotationUtil.getRotationToVec(best)[0];

      for (Vec3 target : candidates) {
         if (RotationUtil.getRotationToVec(target)[0] == desiredYaw) {
            return target;
         }

         if (RotationUtil.getAngleDifference(desiredYaw, RotationUtil.getRotationToVec(target)[0]) <= RotationUtil.getAngleDifference(desiredYaw, BestYaw)) {
            best = target;
            BestYaw = RotationUtil.getRotationToVec(target)[0];
         }
      }

      return best;
   }

   public static float getBlockBreakingSpeed(Block block, ItemStack itemStack, boolean ignoreSlow, boolean ignoreGround) {
      float getBlockHardness = block.getBlockHardness(Myaven.mc.theWorld, null);
      if (getBlockHardness < 0.0F) {
         return 0.0F;
      } else {
         return !block.getMaterial().isToolNotRequired() && (itemStack == null || !itemStack.canHarvestBlock(block))
            ? getToolEffectiveness(itemStack, block, ignoreSlow, ignoreGround) / getBlockHardness / 100.0F
            : getToolEffectiveness(itemStack, block, ignoreSlow, ignoreGround) / getBlockHardness / 30.0F;
      }
   }

   public static float getToolEffectiveness(ItemStack itemStack, Block block, boolean ignoreSlow, boolean ignoreGround) {
      float n = itemStack == null ? 1.0F : itemStack.getItem().getStrVsBlock(itemStack, block);
      if (n > 1.0F) {
         int getEnchantmentLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, itemStack);
         if (getEnchantmentLevel > 0 && itemStack != null) {
            n += (float)(getEnchantmentLevel * getEnchantmentLevel + 1);
         }
      }

      if (Myaven.mc.thePlayer.isPotionActive(Potion.digSpeed)) {
         n *= 1.0F + (float)(Myaven.mc.thePlayer.getActivePotionEffect(Potion.digSpeed).getAmplifier() + 1) * 0.2F;
      }

      if (!ignoreSlow) {
         if (Myaven.mc.thePlayer.isPotionActive(Potion.digSlowdown)) {
            float n2;
            switch (Myaven.mc.thePlayer.getActivePotionEffect(Potion.digSlowdown).getAmplifier()) {
               case 0:
                  n2 = 0.3F;
                  break;
               case 1:
                  n2 = 0.09F;
                  break;
               case 2:
                  n2 = 0.0027F;
                  break;
               default:
                  n2 = 8.1E-4F;
            }

            n *= n2;
         }

         if (Myaven.mc.thePlayer.isInsideOfMaterial(Material.water) && !EnchantmentHelper.getAquaAffinityModifier(Myaven.mc.thePlayer)) {
            n /= 5.0F;
         }

         if (!Myaven.mc.thePlayer.onGround && !ignoreGround) {
            n /= 5.0F;
         }
      }

      return n;
   }

   public static Vec3 getHitVector(BlockData cache) {
      BlockPos pos = cache.pos;
      EnumFacing face = cache.facing;
      double x = (double)pos.getX() + 0.5;
      double y = (double)pos.getY() + 0.5;
      double z = (double)pos.getZ() + 0.5;
      double var9;
      double var10;
      double var11;
      return new Vec3(
         var9 = x + (double)face.getFrontOffsetX() / 2.0, var10 = y + (double)face.getFrontOffsetY() / 2.0, var11 = z + (double)face.getFrontOffsetZ() / 2.0
      );
   }

   public static float[] getBestRotations(BlockPos blockPos, EnumFacing face) {
      Vec3i faceVec = face.getDirectionVec();
      float maxX;
      float minX;
      if (faceVec.getX() == 0) {
         minX = 0.1F;
         maxX = 0.9F;
      } else if (faceVec.getX() == 1) {
         maxX = 1.0F;
         minX = 1.0F;
      } else if (faceVec.getX() == -1) {
         maxX = 0.0F;
         minX = 0.0F;
      } else {
         minX = 0.1F;
         maxX = 0.9F;
      }

      float maxY;
      float minY;
      if (faceVec.getY() == 0) {
         minY = 0.1F;
         maxY = 0.9F;
      } else if (faceVec.getY() == 1) {
         maxY = 1.0F;
         minY = 1.0F;
      } else if (faceVec.getY() == -1) {
         maxY = 0.0F;
         minY = 0.0F;
      } else {
         minY = 0.1F;
         maxY = 0.9F;
      }

      float maxZ;
      float minZ;
      if (faceVec.getZ() == 0) {
         minZ = 0.1F;
         maxZ = 0.9F;
      } else if (faceVec.getZ() == 1) {
         maxZ = 1.0F;
         minZ = 1.0F;
      } else if (faceVec.getZ() == -1) {
         maxZ = 0.0F;
         minZ = 0.0F;
      } else {
         minZ = 0.1F;
         maxZ = 0.9F;
      }

      float[] bestRot = RotationUtil.getRotationFromBlock(blockPos);
      double bestDist = RotationUtil.getAngleDifference(bestRot);

      for (float x = minX; x <= maxX; x += 0.1F) {
         for (float y = minY; y <= maxY; y += 0.1F) {
            for (float z = minZ; z <= maxZ; z += 0.1F) {
               Vec3 candidateLocal = new Vec3((double)x, (double)y, (double)z);
               Vec3 candidateWorld = candidateLocal.add(new Vec3((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ()));
               double diff = RotationUtil.getAngleDifference(candidateWorld);
               if (diff < bestDist) {
                  bestDist = diff;
                  bestRot = RotationUtil.getRotationToVec(candidateWorld);
               }
            }
         }
      }

      return bestRot;
   }

   public static float[] getOptimalRotations(BlockData cache) {
      BlockPos blockPos = cache.pos;
      EnumFacing face = cache.facing;
      MovingObjectPosition initialRay = rayTraceWithRotation(RotationManager.getRotationAngles(), 4.5, 1.0F);
      if (isSamePosition(blockPos, initialRay.getBlockPos()) && initialRay.sideHit.equals(face)) {
         return RotationManager.getRotationAngles();
      } else {
         Vec3 center = getBlockFaceCenter(blockPos, face);
         List<Vec3> testPoints = new ArrayList<>();
         testPoints.add(center);
         testPoints.add(center.addVector(0.2, 0.2, 0.2));
         testPoints.add(center.addVector(-0.2, -0.2, -0.2));
         testPoints.add(center.addVector(0.3, -0.3, 0.1));
         testPoints.add(center.addVector(-0.3, 0.3, -0.1));
         float[] bestRot = null;
         float bestDiff = Float.MAX_VALUE;

         for (Vec3 point : testPoints) {
            float[] rot = RotationUtil.getRotationToVec(point);
            if (!Float.isNaN(rot[0]) && !Float.isNaN(rot[1])) {
               MovingObjectPosition ray = rayTraceWithRotation(rot, 4.5, 1.0F);
               if (isSamePosition(blockPos, ray.getBlockPos()) && ray.sideHit.equals(face)) {
                  float diff = RotationUtil.getAngleBetweenRotations(RotationManager.getRotationAngles(), rot);
                  if (diff < bestDiff) {
                     bestDiff = diff;
                     bestRot = rot;
                  }
               }
            }
         }

         return bestRot != null ? bestRot : RotationUtil.getRotationToVec(center);
      }
   }

   public static Vec3 getBlockFaceCenter(BlockPos pos, EnumFacing face) {
      double x = (double)pos.getX() + 0.5;
      double y = (double)pos.getY() + 0.5;
      double z = (double)pos.getZ() + 0.5;
      x += (double)face.getFrontOffsetX() / 2.0;
      z += (double)face.getFrontOffsetZ() / 2.0;
      y += (double)face.getFrontOffsetY() / 2.0;
      return new Vec3(x, y, z);
   }

   public static boolean isSamePosition(BlockPos blockPos, BlockPos blockPos2) {
      return blockPos == null ? false : blockPos2.getX() == blockPos.getX() && blockPos2.getY() == blockPos.getY() && blockPos2.getZ() == blockPos.getZ();
   }

   public static MovingObjectPosition rayTraceFromPlayer(double reach, float partialTicks) {
      Vec3 from = Myaven.mc.thePlayer.getPositionEyes(partialTicks);
      Vec3 direction = Myaven.mc.thePlayer.getLookVec();
      return rayTrace(from, direction, reach);
   }

   public static MovingObjectPosition rayTraceWithRotation(float[] rot, double reach, float partialTicks) {
      Vec3 from = Myaven.mc.thePlayer.getPositionEyes(partialTicks);
      Vec3 direction = RotationUtil.getLookVectorFromAngles(rot[1], rot[0]);
      return rayTrace(from, direction, reach);
   }

   public static MovingObjectPosition rayTrace(Vec3 from, Vec3 direction, double reach) {
      Vec3 to = from.addVector(direction.xCoord * reach, direction.yCoord * reach, direction.zCoord * reach);
      MovingObjectPosition result = Myaven.mc.theWorld.rayTraceBlocks(from, to, false, false, false);
      return result == null ? new MovingObjectPosition(MovingObjectType.MISS, to, EnumFacing.UP, new BlockPos(to)) : result;
   }

   public static boolean isWithinRange(BlockPos center, BlockPos target, double range) {
      double dx = (double)(center.getX() - target.getX());
      double dy = (double)(center.getY() - target.getY());
      double dz = (double)(center.getZ() - target.getZ());
      return dx * dx + dy * dy + dz * dz <= range * range;
   }

   public static boolean canReachBlock(double reach) {
      Vec3 from = Myaven.mc.thePlayer.getPositionEyes(ClientUtil.getRenderPartialTicks());
      Vec3 direction = Myaven.mc.thePlayer.getLookVec();
      Vec3 to = from.addVector(direction.xCoord * reach, direction.yCoord * reach, direction.zCoord * reach);
      MovingObjectPosition result = Myaven.mc.theWorld.rayTraceBlocks(from, to, false, true, false);
      return result != null;
   }

   public static Block getBlock(BlockPos blockPos) {
      return u(blockPos).getBlock();
   }

   public static Block getBlock(double x, double y, double z) {
      return getBlock(new BlockPos(x, y, z));
   }

   public static IBlockState u(BlockPos blockPos) {
      return Myaven.mc.theWorld.getBlockState(blockPos);
   }

   public static boolean checkPlayerHeadspaceClear() {
      byte offset = 0;

      while ((double)offset < Myaven.mc.thePlayer.posY + (double)Myaven.mc.thePlayer.getEyeHeight()) {
         BlockPos blockPos = new BlockPos(Myaven.mc.thePlayer.posX, (double)offset, Myaven.mc.thePlayer.posZ);

         if (Myaven.mc.theWorld.getBlockState(blockPos).getBlock() != Blocks.air) {
            return true;
         }

         offset += 2;
      }

      return false;
   }

   public static boolean isReplaceableBlock(BlockPos blockPos) {
      return getBlock(blockPos).isReplaceable(Myaven.mc.theWorld, blockPos);
   }

   @Nullable
   public static AxisAlignedBB getCollisionBox(BlockPos blockPos) {
      IBlockState blockState = u(blockPos);
      Block block = blockState.getBlock();
      if (block instanceof BlockAir) {
         return null;
      } else {
         return block instanceof BlockGlass
            ? new AxisAlignedBB(
               (double)blockPos.getX(),
               (double)blockPos.getY(),
               (double)blockPos.getZ(),
               (double)(blockPos.getX() + 1),
               (double)(blockPos.getY() + 1),
               (double)(blockPos.getZ() + 1)
            )
            : block.getCollisionBoundingBox(Myaven.mc.theWorld, blockPos, blockState);
      }
   }

   public static boolean isInteractableBlock(Block block) {
      return block instanceof BlockFenceGate
         || block instanceof BlockLadder
         || block instanceof BlockFlowerPot
         || block instanceof BlockBasePressurePlate
         || isLiquidBlock(block)
         || block instanceof BlockFence
         || block instanceof BlockAnvil
         || block instanceof BlockEnchantmentTable
         || block instanceof BlockChest;
   }

   public static boolean isLiquidBlock(Block block) {
      return block.getMaterial() == Material.lava || block.getMaterial() == Material.water;
   }

   public static boolean isFurnace(Block block) {
      return block instanceof BlockFurnace
         || block instanceof BlockFenceGate
         || block instanceof BlockChest
         || block instanceof BlockEnderChest
         || block instanceof BlockEnchantmentTable
         || block instanceof BlockBrewingStand
         || block instanceof BlockBed
         || block instanceof BlockDropper
         || block instanceof BlockDispenser
         || block instanceof BlockHopper
         || block instanceof BlockAnvil
         || block == Blocks.crafting_table;
   }

   @NotNull
   public static List<BlockPos> getBlocksInArea(@NotNull BlockPos from, @NotNull BlockPos to) {
      List<BlockPos> blocks = new ArrayList<>();
      BlockPos min = new BlockPos(Math.min(from.getX(), to.getX()), Math.min(from.getY(), to.getY()), Math.min(from.getZ(), to.getZ()));
      int a = from.getZ();
      int b = to.getZ();
      int a1 = from.getY();
      int b1 = to.getY();
      int a2 = from.getX();
      int b2 = to.getX();
      BlockPos max = new BlockPos(a2 >= b2 ? a2 : b2, a1 >= b1 ? a1 : b1, a >= b ? a : b);

      for (int x = min.getX(); x <= max.getX(); x++) {
         for (int y = min.getY(); y <= max.getY(); y++) {
            for (int z = min.getZ(); z <= max.getZ(); z++) {
               blocks.add(new BlockPos(x, y, z));
            }
         }
      }

      return blocks;
   }

   public static class BlockData {
      public BlockPos pos;
      public EnumFacing facing;

      public BlockData(BlockPos blockPos, EnumFacing facing) {
         this.pos = blockPos;
         this.facing = facing;
      }
   }
}
