package me.emafire003.dev.ohmymeteors.compat.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.emafire003.dev.ohmymeteors.OhMyMeteors;
import me.fzzyhmstrs.fzzy_config.api.ConfigApi;


public class ModMenuIntegration implements ModMenuApi {
    /*@Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ConfigScreen::new;
    }*/

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            ConfigApi.INSTANCE.openScreen(OhMyMeteors.MOD_ID);
            return null;
        };
    }

}