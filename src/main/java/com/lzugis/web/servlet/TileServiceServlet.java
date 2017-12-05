package com.lzugis.web.servlet;

import com.lzugis.helper.CommonConfig;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by admin on 2017/9/10.
 */
@WebServlet(description = "wms services", urlPatterns =  {"/tile"})
public class TileServiceServlet extends HttpServlet {

    private String url = "https://s4.geohey.com/s/mapping/";
    private String tilepath = CommonConfig.getVal("tile.path");

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doGet(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String x = request.getParameter("x"),
                y = request.getParameter("y"),
                z = request.getParameter("z"),
                layer = request.getParameter("layer");

        StringBuffer tileUrl = new StringBuffer();
        tileUrl.append(url);
        tileUrl.append(layer+"/all?");
        tileUrl.append("z="+z+"&x="+x+"&y="+y);
        tileUrl.append("&retina=&ak=MGUxMmI2ZTk4YTVhNDEzYmJhZDJkNDM3ZWI5ZDAwOGE");

        String tilefile = tilepath+layer+"/"+z+"/"+x+"/"+y+".png";
        File tile = new File(tilefile);
        byte[] tileByte = null;
        /**
         * 如果文件存在，则直接读取文件
         * 如果文件不存在，在先保存
         */
        if(tile.exists()){
            tileByte = getFileBytes(tilefile);
        }
        else{
            tileByte = getUrlBytes(tileUrl.toString());
            saveTileFile(tileByte, tilefile);
        }
        OutputStream os = response.getOutputStream();
        InputStream is = new ByteArrayInputStream(tileByte);
        try {
            int count = 0;
            byte[] buffer = new byte[1024 * 1024];
            while ((count = is.read(buffer)) != -1) {
                os.write(buffer, 0, count);
            }
            os.flush();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            os.close();
            is.close();
        }
    }

    private void saveTileFile(byte[] tileByte, String tilefile){
        try {
            BufferedImage bi = null;
            bi = ImageIO.read(new ByteArrayInputStream(tileByte));
            //判断文件夹是否存在，否则创建
            File filetile = new File(tilefile);
            if (!filetile.getParentFile().exists()) {
                filetile.getParentFile().mkdirs();
            }
            ImageIO.write(bi, "png", filetile);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private byte[] getFileBytes(String tilefile){
        byte[] buffer = null;
        try {
            File file = new File(tilefile);
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }

    private byte[] getUrlBytes(String tilurl){
        try {
            //new一个URL对象
            URL url = new URL(tilurl);
            //打开链接
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //设置请求方式为"GET"
            conn.setRequestMethod("GET");
            //超时响应时间为5秒
            conn.setConnectTimeout(5 * 1000);
            //通过输入流获取图片数据
            InputStream inStream = conn.getInputStream();
            //得到图片的二进制数据，以二进制封装得到数据，具有通用性
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            //创建一个Buffer字符串
            byte[] buffer = new byte[1024];
            //每次读取的字符串长度，如果为-1，代表全部读取完毕
            int len = 0;
            //使用一个输入流从buffer里把数据读取出来
            while ((len = inStream.read(buffer)) != -1) {
                //用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度
                outStream.write(buffer, 0, len);
            }
            //关闭输入流
            inStream.close();
            //把outStream里的数据写入内存
            return outStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
