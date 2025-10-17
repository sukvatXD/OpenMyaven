package Myaven.mixins.accessor;

import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@SideOnly(Side.CLIENT)
@Mixin({EntityRenderer.class})
public interface AccessorEntityRenderer {
   @Invoker("orientCamera")
   void callOrientCamera(float float1);

   @Invoker("setupCameraTransform")
   void callSetupCameraTransform(float float1, int integer);

   @Invoker("loadShader")
   void callLoadShader(ResourceLocation resourceLocation);

   @Accessor("shaderResourceLocations")
   ResourceLocation[] getShaderResourceLocations();

   @Accessor("useShader")
   boolean getUseShader();

   @Accessor("useShader")
   void setUseShader(boolean boolean1);

   @Accessor("shaderIndex")
   int getShaderIndex();

   @Accessor("shaderIndex")
   void setShaderIndex(int integer);

   @Accessor("thirdPersonDistance")
   void setThirdPersonDistance(float float1);
}
