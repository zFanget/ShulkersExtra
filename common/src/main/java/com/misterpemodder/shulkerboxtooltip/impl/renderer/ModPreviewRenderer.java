package com.misterpemodder.shulkerboxtooltip.impl.renderer;

import com.misterpemodder.shulkerboxtooltip.ShulkerBoxTooltip;
import com.misterpemodder.shulkerboxtooltip.api.PreviewType;
import com.misterpemodder.shulkerboxtooltip.api.color.ColorKey;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ModPreviewRenderer extends BasePreviewRenderer {
  private static final Identifier DEFAULT_TEXTURE_LIGHT = new Identifier("shulkerboxtooltip",
      "textures/gui/shulker_box_tooltip.png");
  public static final ModPreviewRenderer INSTANCE = new ModPreviewRenderer();

  ModPreviewRenderer() {
    super(18, 18, 8, 8);
  }

  @Override
  public int getWidth() {
    return 14 + Math.min(this.getMaxRowSize(), this.getInvSize()) * 18;
  }

  @Override
  public int getHeight() {
    return 14 + (int) Math.ceil(this.getInvSize() / (double) this.getMaxRowSize()) * 18;
  }

  /**
   * Sets the color of the preview window.
   */
  private void setColor() {
    ColorKey key;

    if (ShulkerBoxTooltip.config.colors.coloredPreview) {
      key = this.provider.getWindowColorKey(this.previewContext);
    } else {
      key = ColorKey.DEFAULT;
    }
    float[] color = key.rgbComponents();
    RenderSystem.setShaderColor(color[0], color[1], color[2], 1.0f);
  }

  /**
   * Sets the texture to be used.
   */
  private void setTexture() {
    Identifier texture = this.textureOverride;

    if (texture == null) {
      texture = DEFAULT_TEXTURE_LIGHT;
    }
    RenderSystem.setShaderTexture(0, texture);
  }

  private void drawBackground(int x, int y, int z, MatrixStack matrices) {
    int zOffset = z + 100;
    int invSize = this.getInvSize();
    int xOffset = 7;
    int yOffset = 7;
    int rowTexYPos = 7;
    int rowSize = Math.min(this.getMaxRowSize(), invSize);
    int rowWidth = rowSize * 18;

    this.setColor();
    this.setTexture();

    // top side
    for (int size = rowSize; size > 0; size -= 9) {
      int s = Math.min(size, 9);

      DrawableHelper.drawTexture(matrices, x + xOffset, y, zOffset, 7, 0, s * 18, 7, 256, 256);
      xOffset += s * 18;
    }

    while (invSize > 0) {
      xOffset = 7;
      // left side
      DrawableHelper.drawTexture(matrices, x, y + yOffset, zOffset, 0, rowTexYPos, 7, 18, 256, 256);
      for (int rSize = rowSize; rSize > 0; rSize -= 9) {
        int s = Math.min(rSize, 9);

        // center
        DrawableHelper.drawTexture(matrices, x + xOffset, y + yOffset, zOffset, 7, rowTexYPos, s * 18, 18, 256, 256);
        xOffset += s * 18;
      }
      // right side
      DrawableHelper.drawTexture(matrices, x + xOffset, y + yOffset, zOffset, 169, rowTexYPos, 7, 18, 256, 256);
      yOffset += 18;
      invSize -= rowSize;
      rowTexYPos = rowTexYPos >= 43 ? 7 : rowTexYPos + 18;
    }

    xOffset = 7;
    for (int size = rowSize; size > 0; size -= 9) {
      int s = Math.min(size, 9);

      // bottom side
      DrawableHelper.drawTexture(matrices, x + xOffset, y + yOffset, zOffset, 7, 61, s * 18, 7, 256, 256);
      xOffset += s * 18;
    }

    // top-left corner
    DrawableHelper.drawTexture(matrices, x, y, zOffset, 0, 0, 7, 7, 256, 256);
    // top-right corner
    DrawableHelper.drawTexture(matrices, x + rowWidth + 7, y, zOffset, 169, 0, 7, 7, 256, 256);
    // bottom-right corner
    DrawableHelper.drawTexture(matrices, x + rowWidth + 7, y + yOffset, zOffset, 169, 61, 7, 7, 256, 256);
    // bottom-left corner
    DrawableHelper.drawTexture(matrices, x, y + yOffset, zOffset, 0, 61, 7, 7, 256, 256);
  }

  @Override
  public void draw(int x, int y, int z, MatrixStack matrices, TextRenderer textRenderer, ItemRenderer itemRenderer,
      TextureManager textureManager) {
    if (this.items.isEmpty() || this.previewType == PreviewType.NO_PREVIEW)
      return;
    RenderSystem.enableDepthTest();
    this.drawBackground(x, y, z, matrices);
    this.drawItems(x, y, z, textRenderer, itemRenderer);
  }
}
