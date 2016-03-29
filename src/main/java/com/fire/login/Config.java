/**
 * 
 */
package com.fire.login;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author lhl
 *
 *         2016年2月19日 上午9:42:04
 */
public class Config
{
    private static Properties prop;

    /**
     * 解析配置文件
     * 
     * @param configFile 相对类路径文件名
     */
    public static void parse(String configFile) {
        try (InputStream in = Config.class.getClassLoader().getResourceAsStream(configFile)) {
            Properties prop = new Properties();
            prop.load(in);
            Config.prop = prop;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static int getInt(String key) {
        return Integer.parseInt(getString(key));
    }

    public static String getString(String key) {
        if (prop == null) {
            throw new IllegalStateException("Configure has not been initialized");
        }

        return prop.getProperty(key);
    }
}
