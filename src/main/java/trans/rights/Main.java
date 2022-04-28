package trans.rights;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.util.Locale;

public final class Main {
    private static final ImageIcon icon = new ImageIcon("src/main/resources/assets/transrights/transpride.png");

    private Main() {}

    /**
     * This opens a new Panel when you run the jar so idiots know what to do
     *
     * @param args run arguments
     * @throws ClassNotFoundException          if the client is using an unsupported OS / has weird files
     * @throws InstantiationException          when the OS is not compatible with this jvm
     * @throws IllegalAccessException          when the client has the wrong security for this file to be run
     * @throws UnsupportedLookAndFeelException if the current OS look style isn't compatible with this jvm
     * @throws IOException                     if there was an error creating files or folders
     * @author Austin, Toxic
     */
    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException, IOException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        if (JOptionPane.showConfirmDialog(null, "Don't run this file, put it in your mods folder!\nWould you like to open up your mods folder?", "Trans-rights", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, icon) == 0) {
            var modsFile = switch (getOS()) {
                case WINDOWS -> new File(System.getenv("AppData") + "/.minecraft/mods");
                case OSX -> new File(System.getProperty("user.home") + "/Library/Application Support/minecraft/mods");
                case NIX -> new File(System.getProperty("user.home") + "/.minecraft");
                default -> throw new IllegalStateException("Unknown OS: " + getOS());
            };

            if (!modsFile.exists()) Files.createDirectories(modsFile.toPath());

            getOS().open(modsFile.toURI());
        }
    }

    private static OS getOS() {
        var osName = System.getProperty("os.name").toLowerCase(Locale.ROOT);

        if (osName.contains("nux") || osName.contains("nix")) return OS.NIX;
        else if (osName.contains("darwin") || osName.contains("mac")) return OS.OSX;
        else if (osName.contains("win")) return OS.WINDOWS;

        return OS.UNKNOWN;
    }

    private enum OS {
        WINDOWS {
            @Override
            protected String[] getCommand(@NotNull URL url) {
                return new String[]{"rundll32", "url.dll,FileProtocolHandler", url.toString()};
            }
        }, OSX {
            @Override
            protected String[] getCommand(@NotNull URL url) {
                return new String[]{"open", url.toString()};
            }
        }, NIX, UNKNOWN;

        public void open(@NotNull URI uri) throws IOException {
            Runtime.getRuntime().exec(this.getCommand(uri.toURL()));
        }

        protected String[] getCommand(@NotNull URL url) {
            var string = url.toString();

            if ("file".equals(url.getProtocol())) string = string.replace("file:", "file://");

            return new String[]{"Xdg-open", string};
        }
    }
}
