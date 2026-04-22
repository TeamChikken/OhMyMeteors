package me.emafire003.dev.ohmymeteors.blocks;

import me.emafire003.dev.ohmymeteors.OhMyMeteors;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class OMMProperties {

    public static final BooleanProperty SHOW_AREA = BooleanProperty.create("show_area");
    public static final BooleanProperty FIRING = BooleanProperty.create("firing");
    public static final BooleanProperty IN_COOLDOWN = BooleanProperty.create("in_cooldown");
    public static final EnumProperty<RockTemperature> ROCK_TEMPERATURE = EnumProperty.create("rock_temperature", RockTemperature.class);
    public static final BooleanProperty PRESERVED = BooleanProperty.create("preserved");

    public static void registerBlockProperties(){
        OhMyMeteors.LOGGER.debug("Registering OhMyMeteors block properties...");

    }
}
