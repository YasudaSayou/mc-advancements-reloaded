package codes.atomys.advr;

import codes.atomys.advr.config.Configuration;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementNode;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.CommonColors;

/**
 * The progress instance of a progress item, used to display and translate a single progress condition.
 */
public class ReloadedCriterionProgress {
  private final AdvancementNode advancementNode;
  private final AdvancementProgress progress;
  private final ResourceLocation criterion;

  private boolean obtained;

  /**
   * Represents the progress of a specific criterion within an advancement.
   *
   * @param advancementNode The advancement node to which the criterion belongs.
   * @param progress        The overall progress of the advancement.
   * @param criterionName   The name of the criterion being tracked.
   */
  public ReloadedCriterionProgress(final AdvancementNode advancementNode, final AdvancementProgress progress,
      final String criterionName) {
    this.advancementNode = advancementNode;
    this.progress = progress;
    this.criterion = ResourceLocation.parse(criterionName);

    this.obtained = progress.getCriterion(criterionName).isDone();
  }

  public AdvancementNode getAdvancementNode() {
    return this.advancementNode;
  }

  public Advancement getAdvancement() {
    return this.advancementNode.holder().value();
  }

  public ResourceLocation getResourceLocation() {
    return this.advancementNode.holder().id();
  }

  public AdvancementProgress getProgress() {
    return this.progress;
  }

  public Component getTitle() {
    return Component.nullToEmpty(this.criterion.toString());
  }

  public int getColor() {
    return this.isObtained() ? CommonColors.GREEN : CommonColors.RED;
  }

  public boolean isObtained() {
    return this.obtained;
  }

  public Component getHumanCriterionName() {
    return Configuration.criteriasTranslationMode ? retrieveTranslationOnGame() : Component.literal(this.criterion.getPath());
  }

  private Component retrieveTranslationOnGame() {
    final String criterionNamespace = this.getResourceLocation().getNamespace();
    final String criteria = this.criterion.getPath();
    // Try to translate the name by finding the item in the namespace and the
    // default namespace (if not the same as the namespace).
    final List<String> namespaces = Lists.newArrayList(criterionNamespace, "minecraft");
    final String[] keyTypes = { "item", "block", "entity", "biome", "color", "effect", "enchantment", "painting",
        "jukebox_song", "instrument" };

    for (final String namespace : namespaces) {
      for (final String keyType : keyTypes) {
        // Special case for paintings since they have a different translation key
        final String translationKey = keyType + "." + namespace + "." + criteria
            + ("painting".equals(keyType) ? ".title" : "");
        final Component translation = Component.translatable(translationKey);
        if (!translation.getString().equals(translationKey)) {
          return translation/*.copy().withStyle(style -> style.withItalic(true))*/;
        }
      }
    }
    return Component.literal(criteria);
  }
}