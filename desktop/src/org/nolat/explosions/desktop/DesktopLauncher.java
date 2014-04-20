package org.nolat.explosions.desktop;

import org.nolat.explosions.Config;
import org.nolat.explosions.Explosions;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        
        config.useGL30 = true;
        config.vSyncEnabled = true;
        config.title = Config.NAME + " - v" + Config.VERSION;
        config.width = (int) Config.WIDTH;
        config.height = (int) Config.HEIGHT;
        config.resizable = false; //temporary hack until viewports are properly implemented
        config.fullscreen = false;
        config.addIcon("icons/icon_16.png", FileType.Internal);
        config.addIcon("icons/icon_32.png", FileType.Internal);
        
        new LwjglApplication(new Explosions(), config);
    }
}
