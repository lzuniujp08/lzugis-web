package com.lzugis.web.servlet;

import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.gce.geotiff.GeoTiffReader;
import org.geotools.geometry.DirectPosition2D;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.opengis.geometry.DirectPosition;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/10/27.
 */
@WebServlet(description = "close browser", urlPatterns =  {"/dem/query"})
public class DemQueryServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doGet(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String points_data = request.getParameter("points");
        String demPath = "/Users/lzugis/Documents/ncdata/bj_dem.tif";
        File file = new File(demPath);
        GeoTiffReader tifReader = new GeoTiffReader(file);
        GridCoverage2D coverage = tifReader.read(null);

        CoordinateReferenceSystem crs = coverage.getCoordinateReferenceSystem2D();
        String[] points = points_data.split(";");

        List list = new ArrayList();

        for(int i=0;i<points.length;i++) {
            String strLonlat = points[i];
            String[] strLonlats = strLonlat.split(",");

            double lon = Double.parseDouble(strLonlats[0]),
                    lat = Double.parseDouble(strLonlats[1]);

            DirectPosition position = new DirectPosition2D(crs, lon, lat);
            int[] results = (int[]) coverage.evaluate(position);
            results = coverage.evaluate(position, results);
            Map map = new HashMap();
            map.put("lon", lon);
            map.put("lat", lon);
            map.put("dem", results[0]);
            list.add(JSONObject.toJSONString(map));
            response.getWriter().println(JSONArray.toJSONString(list));
        }

    }

}
