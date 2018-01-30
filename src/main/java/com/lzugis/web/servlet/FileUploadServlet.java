package com.lzugis.web.servlet;

import com.lzugis.helper.CommonMethod;
import com.lzugis.services.utils.ShpFormatUtil;
import com.lzugis.services.utils.ZipUtil;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.json.simple.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/9/10.
 */
@WebServlet(description = "shp file upload", urlPatterns =  {"/shp-upload"})
public class FileUploadServlet extends HttpServlet {

    private String rootPath = "";
    private ZipUtil zipUtil;
    private ShpFormatUtil shpUtil;
    private CommonMethod cm;

    public FileUploadServlet() {
        super();
        rootPath = "D:\\shppath\\";
        zipUtil = new ZipUtil();
        shpUtil = new ShpFormatUtil();
        cm = new CommonMethod();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doGet(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");
        //1、创建一个DiskFileItemFactory工厂
        DiskFileItemFactory factory = new DiskFileItemFactory();
        //2、创建一个文件上传解析器
        ServletFileUpload upload = new ServletFileUpload(factory);
        //限制单个文件大小为1M
        upload.setFileSizeMax(1024*1024);
        //限制整个表单大小为1G
        upload.setSizeMax(1024*1024*1024);
        //解决上传文件名的中文乱码
        upload.setHeaderEncoding("UTF-8");
        factory.setSizeThreshold(1024 * 500);//设置内存的临界值为500K
        File linshi = new File(rootPath);//当超过500K的时候，存到一个临时文件夹中
        factory.setRepository(linshi);
        upload.setSizeMax(1024 * 1024 * 10);//设置上传的文件总的大小不能超过5M
        Map result = new HashMap<>();
        try {
            // 1. 得到 FileItem 的集合 items
            List<FileItem> /* FileItem */items = upload.parseRequest(request);
            // 2. 遍历 items:
            for (FileItem item : items) {
                // 若是一个一般的表单域, 打印信息
                if (item.isFormField()) {
                    String name = item.getFieldName();
                    String value = item.getString("utf-8");
                    System.out.println(name + ": " + value);
                }
                // 若是文件域则把文件保存到 e:\\files 目录下.
                else {
                    String fileName = item.getName();
                    long sizeInBytes = item.getSize();
//                    System.out.println(fileName);
//                    System.out.println(sizeInBytes);
                    InputStream in = item.getInputStream();
                    byte[] buffer = new byte[1024];
                    int len = 0;
                    //文件最终上传的位置
                    fileName = rootPath + fileName;
                    OutputStream out = new FileOutputStream(fileName);
                    while ((len = in.read(buffer)) != -1) {
                        out.write(buffer, 0, len);
                    }
                    out.close();
                    in.close();
                    String shpPath = zipUtil.unZipFiles(fileName, rootPath);
                    System.out.println("shp path"+shpPath);
                    StringBuffer json = shpUtil.shp2Json(shpPath);
                    result.put("status", "200");
                    result.put("geojson", json.toString());
//                    cm.append2File(rootPath + "json.json", json.toString());
                    JSONObject.writeJSONString(result, response.getWriter());
                }
            }
        } catch (FileUploadException e) {
            e.printStackTrace();
        }
    }
}
