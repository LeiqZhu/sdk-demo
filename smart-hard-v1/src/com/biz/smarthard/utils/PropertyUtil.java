package com.biz.smarthard.utils;

import java.io.*;
import java.util.Properties;

public class PropertyUtil {
    private static Properties properties;

    public static Properties getInstance(){
        if (properties == null){
            properties = new Properties();
        }
        // 使用ClassLoader加载properties配置文件生成对应的输入流
        //InputStream in = PropertyUtil.class.getClassLoader().getResourceAsStream("pay.properties");
        // 使用properties对象加载输入流

        try {
            // 使用InPutStream流读取properties文件
            String proPath = System.getProperty("user.dir") + "/../pay.properties";
            BufferedReader bufferedReader = new BufferedReader(new FileReader(proPath));
            properties.load(bufferedReader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    public static void main(String[] args) {
        File file = new File("netty.properties");
        System.out.println(file.exists());
        System.out.println(getInstance());
    }
}
