package betterblockentities.client.render.immediate;

/* local */
import betterblockentities.client.render.immediate.blockentity.extentions.BlockEntityExt;
import betterblockentities.client.render.immediate.blockentity.misc.RenderingMode;

/* minecraft */
import com.mojang.blaze3d.vertex.MeshData;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.feature.phase.SimpleFeatureRenderPhase;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.world.level.block.entity.BlockEntity;

/* mojang */
import com.mojang.blaze3d.vertex.PoseStack;

public final class OverlayRenderer {
    private OverlayRenderer() { }

    public static <S> boolean manageCrumblingOverlay(BlockEntity blockEntity, SubmitNodeCollector submitNodeCollector, PoseStack poseStack, Model<? super S> model, S state, int light, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay, DrawStage stage) {
        if (crumblingOverlay == null)
            return false;

        BlockEntityExt blockEntityExt = (BlockEntityExt)blockEntity;
        if (blockEntityExt.renderingMode() == RenderingMode.TERRAIN && blockEntityExt.terrainMeshReady()) {
            submitCrumblingOverlay(submitNodeCollector, poseStack, model, state, light, crumblingOverlay, stage);
            return true;
        }
        return false;
    }

    public static <S> void submitCrumblingOverlay(SubmitNodeCollector submitNodeCollector, PoseStack poseStack, Model<? super S> model, S state, int light, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay, DrawStage stage) {
        SubmitNodeCollection submitNodeCollection = getSubmitNodeCollection(submitNodeCollector);
        if (submitNodeCollection == null) return;

        SimpleFeatureRenderPhase phase = stage == DrawStage.BEFORE_TERRAIN_TRANSLUCENT ?
                submitNodeCollection.breakingOverlay : submitNodeCollection.afterTerrain;

        phase.submit(new ModelFeatureRenderer.Submit<>(
                ModelBakery.DESTROY_TYPES.get(crumblingOverlay.progress()),
                poseStack.last().copy(),
                model,
                state,
                light,
                OverlayTexture.NO_OVERLAY,
                -1,
                null,
                crumblingOverlay.cameraPose()
        ));
    }

    private static SubmitNodeCollection getSubmitNodeCollection(SubmitNodeCollector submitNodeCollector) {
        if (submitNodeCollector instanceof SubmitNodeCollection submitNodeCollection) {
            return submitNodeCollection;
        }

        OrderedSubmitNodeCollector orderedSubmitNodeCollector = submitNodeCollector.order(0);
        return orderedSubmitNodeCollector instanceof SubmitNodeCollection submitNodeCollection ? submitNodeCollection : null;
    }

    public enum DrawStage {
        BEFORE_TERRAIN_TRANSLUCENT,
        AFTER_TERRAIN_TRANSLUCENT
    }
}
