package net.hotdoguy90.additionalsaturation;

import io.github.foundationgames.mealapi.api.v0.MealItemRegistry;
import io.github.foundationgames.mealapi.api.v0.PlayerFullnessUtil;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.hotdoguy90.additionalsaturation.util.ModTags;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdditionalSaturation implements ModInitializer {
	public static final String MOD_ID = "additional_saturation";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ServerTickEvents.END_WORLD_TICK.register((world) -> {
			for (PlayerEntity player : world.getPlayers()) {
				final ItemStack HELD_ITEM = player.getMainHandStack();
				if (!HELD_ITEM.isEmpty() && HELD_ITEM.isIn(ModTags.Items.DETECTED_EDIBLE_FOOD_ITEMS)) {
					int max_fullness = getFullness(HELD_ITEM.getItem().getFoodComponent().getHunger());
					int fullness = max_fullness - getFullness(20 - player.getHungerManager().getFoodLevel());
					if (fullness < 0) {
						fullness = 0;
					}

					final int true_fullness = fullness;

					MealItemRegistry.instance().register(HELD_ITEM.getItem(), (playerEntity, itemStack) -> true_fullness);
				}
			}
		});
	}

	public static int getFullness(int hunger) {
		return (hunger / 2) * 12;
	}
}