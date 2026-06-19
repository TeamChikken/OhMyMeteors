package me.emafire003.dev.ohmymeteors.compat.modmenu;

import me.emafire003.dev.ohmymeteors.config.Config;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;

public class ConfigScreen extends Screen {
    private final Screen parent;

    public ConfigScreen (Screen parent) {
        super(Component.empty());
        this.parent = parent;
    }

    @Override
    protected void init() {
        Util.getPlatform().openUri(Config.FILEPATH.toUri());
        minecraft.gui.setScreen(parent);
    }
}
