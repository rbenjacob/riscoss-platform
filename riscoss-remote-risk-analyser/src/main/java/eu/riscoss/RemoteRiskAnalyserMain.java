/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package eu.riscoss;

import java.net.URL;
import java.io.File;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import java.lang.reflect.Method;
import java.lang.reflect.Field;

public class RemoteRiskAnalyserMain
{
    static void loadJSmile() throws Exception
    {
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

    public static void main(String[] args) throws Exception
    {
        loadJSmile();
        Class rra = Class.forName("eu.riscoss.RemoteRiskAnalyser");
        Method meth = rra.getMethod("main");
        meth.invoke(null);
    }
}
