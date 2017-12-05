package com.lzugis.web.controller;

import com.lzugis.services.GeocodeService;
import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/11/13.
 */
@Controller
public class GeocodeController {
    @Autowired
    private GeocodeService geoService;

    @RequestMapping(value="geocode")
    public void getLonlatGeocode(double lon, double lat, int zoom, String type, HttpServletResponse response){
        Map result = new HashMap();
        try {
            Map area = geoService.getLonlatGeocode(lon, lat, zoom);
            Map poi = geoService.getPoiByLonlat(lon, lat, zoom);
            response.setHeader("Content-type", "text/html;charset=UTF-8");
            if(!StringUtils.isBlank(type)&&"json".equals(type.toLowerCase())){
                result.put("area", area);
                result.put("poi", poi);
                JSONObject.writeJSONString(result, response.getWriter());
            }else{
                result.put("area", area.get("areaname"));
                result.put("poi", poi.get("poiname"));
                JSONObject.writeJSONString(result, response.getWriter());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @RequestMapping(value="geocode/getareas")
    public void getGeocodeArea(int limit, int offset, String pagetype, HttpServletResponse response){
        try {
            Map result = geoService.getGeocodeArea(limit, offset);
            response.setHeader("Content-type", "text/html;charset=UTF-8");
            if(pagetype.equals("client")) {
                JSONArray.writeJSONString((List) result.get("rows"), response.getWriter());
            }else{
                JSONObject.writeJSONString(result, response.getWriter());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @RequestMapping(value="geocode/getpois")
    public void getGeocodePois(int limit, int offset, String pagetype, HttpServletResponse response){
        try {
            Map result = geoService.getGeocodePoi(limit, offset);
            response.setHeader("Content-type", "text/html;charset=UTF-8");
            if(pagetype.equals("client")) {
                JSONArray.writeJSONString((List) result.get("rows"), response.getWriter());
            }else{
                JSONObject.writeJSONString(result, response.getWriter());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @RequestMapping(value="geocode/down")
    public void downGeocodePois(String type, HttpServletResponse response){
        try {
            String downFile = geoService.getDownFile(type);
            File file = new File(downFile);
            String filename = file.getName();// 获取日志文件名称
            InputStream fis = new BufferedInputStream(new FileInputStream(file));
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
            response.reset();
            // 先去掉文件名称中的空格,然后转换编码格式为utf-8,保证不出现乱码,这个文件名称用于浏览器的下载框中自动显示的文件名
            response.addHeader("Content-Disposition", "attachment;filename=" + new String(filename.replaceAll(" ", "").getBytes("utf-8"),"iso8859-1"));
            response.addHeader("Content-Length", "" + file.length());
            OutputStream os = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/octet-stream");
            os.write(buffer);// 输出文件
            os.flush();
            os.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
