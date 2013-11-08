package org.nolat.explosions;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        if (arg.length > 0) {
            if (arg[0].equalsIgnoreCase("debug")) {
                Config.debug = true;
            }
        }
        config.useGL20 = true;
        config.vSyncEnabled = true;
        config.title = Config.NAME + " - v" + Config.VERSION;
        config.width = 1280;//Config.WIDTH;
        config.height = 720;//Config.HEIGHT;
        config.fullscreen = false;
        config.addIcon("icons/icon_16.png", FileType.Internal);
        config.addIcon("icons/icon_32.png", FileType.Internal);
        new LwjglApplication(new Explosions(), config);
    }
}
