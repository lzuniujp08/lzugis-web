package com.lzugis.services.utils;

import com.amazonaws.util.json.JSONArray;
import com.amazonaws.util.json.JSONObject;
import com.vividsolutions.jts.geom.Geometry;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.geojson.feature.FeatureJSON;
import org.opengis.feature.simple.SimpleFeature;

import java.io.File;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class ShpFormatUtil {

    public StringBuffer shp2Json(String shpPath){
        StringBuffer sb = new StringBuffer();
        FeatureJSON fjson = new FeatureJSON();
        try{
            sb.append("{\"type\": \"FeatureCollection\",\"features\": ");

            File file = new File(shpPath);
            ShapefileDataStore shpDataStore = null;

            shpDataStore = new ShapefileDataStore(file.toURL());
            //设置编码
            Charset charset = Charset.forName("GBK");
            shpDataStore.setCharset(charset);
            String typeName = shpDataStore.getTypeNames()[0];
            SimpleFeatureSource featureSource = null;
            featureSource =  shpDataStore.getFeatureSource (typeName);
            SimpleFeatureCollection result = featureSource.getFeatures();
            SimpleFeatureIterator itertor = result.features();
            JSONArray array = new JSONArray();
            while (itertor.hasNext())
            {
                SimpleFeature feature = itertor.next();
                Geometry geo = (Geometry) feature.getAttribute("the_geom");

                StringWriter writer = new StringWriter();
                fjson.writeFeature(feature, writer);
                JSONObject json = new JSONObject(writer.toString());
                array.put(json);
            }
            itertor.close();
            sb.append(array.toString());
            sb.append("}");
        }
        catch(Exception e){
            e.printStackTrace();

        }
        return sb;
    }
}
