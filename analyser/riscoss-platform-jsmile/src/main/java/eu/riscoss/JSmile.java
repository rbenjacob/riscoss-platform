package eu.riscoss;

import java.net.URL;
import java.io.File;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import java.lang.reflect.Method;
import java.lang.reflect.Field;

public class JSmile
{
    static void load0() throws Exception
    {
        try {
            System.loadLibrary("jsmile");
            return;
        } catch (UnsatisfiedLinkError e) { }

        File dir = new File(FileUtils.getTempDirectory(),
            "jnativelibs-" + RandomStringUtils.randomAlphabetic(30));
        if (!dir.mkdir()) {
            throw new Exception("failed to make dir");
        }
        File jsmile = new File(dir, "libjsmile.so");
        URL jsmURL = Thread.currentThread().getContextClassLoader().getResource("libjsmile.so");
        FileUtils.copyURLToFile(jsmURL, jsmile);
        System.setProperty("java.library.path",
            System.getProperty("java.library.path") + ":" + dir.getAbsolutePath());

        // flush the library paths
        Field sysPathsField = ClassLoader.class.getDeclaredField("sys_paths");
        sysPathsField.setAccessible(true);
        sysPathsField.set(null, null);

        // Check that it works...
        System.loadLibrary("jsmile");

        dir.deleteOnExit();
        jsmile.deleteOnExit();
    }

    public static void load()
    {
        try {
            load0();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
