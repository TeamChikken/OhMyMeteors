package me.emafire003.dev.ohmymeteors.compat.modmenu;

import me.emafire003.dev.ohmymeteors.OhMyMeteors;
import me.emafire003.dev.ohmymeteors.config.Config;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

public class ConfigScreen extends Screen {
    private final Screen parent;

    public ConfigScreen (Screen parent) {
        super(Text.empty());
        this.parent = parent;
    }

    @Override
    protected void init() {
        Util.getOperatingSystem().open(Config.FILEPATH.toUri());
        client.setScreen(parent);
    }
}
