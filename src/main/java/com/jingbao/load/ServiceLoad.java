package com.jingbao.load;

import com.jingbao.serverAnnotation.RequestMapping;
import com.jingbao.serverAnnotation.Service;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

/**
 * @author jijngbao
 * @date 19-7-20
 */
public class ServiceLoad {

    public static HashMap<String,ServiceEntity> urlMapping=new HashMap();
    /**
     * 获得包下面的所有的class
     *
     * @param
     *
     * @return List包含所有class的实例
     */

    public static List<Class<?>> getClasssFromPackage(String packageName) {
        List<Class<?>> clazzs = new ArrayList<>();
        // 是否循环搜索子包
        boolean recursive = true;
        // 包名对应的路径名称
        String packageDirName = packageName.replace('.', '/');
        Enumeration<URL> dirs;

        try {
            dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
            while (dirs.hasMoreElements()) {

                URL url = dirs.nextElement();
                String protocol = url.getProtocol();

                if ("file".equals(protocol)) {
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    findClassInPackageByFile(packageName, filePath, recursive, clazzs);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return clazzs;
    }

    /**
     * 在package对应的路径下找到所有的class
     */
    public static void findClassInPackageByFile(String packageName, String filePath, final boolean recursive,
                                                List<Class<?>> clazzs) {
        File dir = new File(filePath);
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        // 在给定的目录下找到所有的文件，并且进行条件过滤
        File[] dirFiles = dir.listFiles(new FileFilter() {

            public boolean accept(File file) {
                boolean acceptDir = recursive && file.isDirectory();// 接受dir目录
                boolean acceptClass = file.getName().endsWith("class");// 接受class文件
                return acceptDir || acceptClass;
            }
        });

        for (File file : dirFiles) {
            if (file.isDirectory()) {
                findClassInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath(), recursive, clazzs);
            } else {
                String className = "";
                if(!file.getName().contains("$")){
                    className=file.getName().substring(0, file.getName()
                            .length() - 6);
                }else {
                    continue;
                }
                try {
                    Class c=Thread.currentThread().getContextClassLoader()
                            .loadClass(packageName + "." + className);
                    checkMapping(c);
                    clazzs.add(c);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static void checkMapping(Class clazz){

        if(clazz.isAnnotationPresent(Service.class)){
            for (Method m :clazz.getMethods()) {
                if (m.isAnnotationPresent(RequestMapping.class)){
                    RequestMapping mapping=  m.getAnnotation(RequestMapping.class);
                    ServiceEntity serviceEntity=new ServiceEntity(m,clazz);
                    urlMapping.put(mapping.value(),serviceEntity);
                }
            }
        }

    }


    public static List findPackages(){
        List packageNames=new ArrayList();
        String projectPath = System.getProperty("user.dir");
        String srcPath=projectPath+"/src/main/java";
        File srcFile=new File(srcPath);
        if (srcFile.exists()) {
            File[] files = srcFile.listFiles();
            if (null != files) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        packageNames.add(file.getName());
                    }
                }
            }
        } else {
            System.out.println("文件不存在!");
        }
        return packageNames;

    }

    public static void getClasssFromPackages(){
        for (Object packageName:findPackages()
             ) {
           getClasssFromPackage(packageName.toString());
        }
    }



    public static void main(String[] args) {
        List<Class<?>> classList = ServiceLoad.getClasssFromPackage("com.jingbao.load");

        for (Class<?> aClass : classList) {

            System.out.println(aClass);
        }

        String path = System.getProperty("user.dir");
        System.out.println(path);
    }
}
