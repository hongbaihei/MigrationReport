package com.cib.util;

import java.io.File;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Zip;
import org.apache.tools.ant.types.FileSet;

public class ZipCompressor {
	private File zipFile;  
	  
    public ZipCompressor(String destPathName) {  
        zipFile = new File(destPathName);  
    }  
      
    public void compress(String srcPathName,String includes) {  
        File srcdir = new File(srcPathName);  
        if (!srcdir.exists())  
            throw new RuntimeException(srcPathName + "不存在！");  
        if(zipFile.exists())
        	zipFile.delete();
        	
        Project prj = new Project();  
        Zip zip = new Zip();  
        zip.setProject(prj);  
        zip.setDestFile(zipFile);
        zip.setEncoding("GBK");
        FileSet fileSet = new FileSet();  
        fileSet.setProject(prj);  
        fileSet.setDir(srcdir);  
        fileSet.setIncludes(includes); //包括哪些文件或文件夹  "*.jpg *.JPG"
        //fileSet.setExcludes(...); //排除哪些文件或文件夹  
        zip.addFileset(fileSet);
        zip.execute();
        
//        if(isDeleteSrc)
        	//
    }
    
//    public static void main(String[] args) {  
//        ZipCompressor zc = new  ZipCompressor("E:\\szhzip.zip");
//        zc.compress("E:\\");  
//    }  
}
