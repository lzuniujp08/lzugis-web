package com.lzugis.helper;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import org.geotools.data.FeatureWriter;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import java.io.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ShapeHelper {

    public ShapeHelper(){
        super();

    }

    public void write2ShapeFile(String shpPath, String[] header, List data){
        try{
            //创建shape文件对象
            File file = new File(shpPath);
            Map<String, Serializable> params = new HashMap<String, Serializable>();
            params.put( ShapefileDataStoreFactory.URLP.key, file.toURI().toURL() );
            ShapefileDataStore ds = (ShapefileDataStore) new ShapefileDataStoreFactory().createNewDataStore(params);
            //定义图形信息和属性信息
            SimpleFeatureTypeBuilder tb = new SimpleFeatureTypeBuilder();
            tb.setCRS(DefaultGeographicCRS.WGS84);
            tb.setName("shapefile");
            tb.add("the_geom", Point.class);
            for(int i= 0;i<header.length;i++){
                String field = header[i];
                tb.add(field.toUpperCase(), String.class);
            }
            ds.createSchema(tb.buildFeatureType());
            //设置编码
            Charset charset = Charset.forName("GBK");
            ds.setCharset(charset);
            //设置Writer
            FeatureWriter<SimpleFeatureType, SimpleFeature> writer = ds.getFeatureWriter(ds.getTypeNames()[0], Transaction.AUTO_COMMIT);
            //写入文件信息
            for(int i=0;i<data.size();i++){
                SimpleFeature feature = writer.next();
                Map<String, Object> row = (Map)data.get(i);
                Geometry geom = new GeometryFactory().createPoint(new Coordinate((Double)row.get("x"), (Double)row.get("y")));
                feature.setAttribute("the_geom", geom);
                for (Map.Entry entry : row.entrySet()) {
                    feature.setAttribute(entry.getKey().toString().toUpperCase()
                            , entry.getValue());
                }
            }
            writer.write();
            writer.close();
            ds.dispose();

            //添加到压缩文件
            zipShapeFile(shpPath);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void zipShapeFile(String shpPath){
        try{
            File shpFile = new File(shpPath);
            String shpRoot = shpFile.getParentFile().getPath(),
                    _shpName = shpFile.getName(),
                    shpName = _shpName.substring(0, _shpName.lastIndexOf("."));

            String zipPath = shpRoot+File.separator+shpName+".zip";
            File zipFile = new File(zipPath);
            InputStream input = null;
            ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFile));
            // zip的名称为
            zipOut.setComment(shpName);
            String[] shpFiles = new String[]{
                    shpRoot+File.separator+shpName+".dbf",
                    shpRoot+File.separator+shpName+".prj",
                    shpRoot+File.separator+shpName+".shp",
                    shpRoot+File.separator+shpName+".shx",
            };
            for(int i=0;i<shpFiles.length;i++){
                File _file = new File(shpFiles[i]);
                input = new FileInputStream(_file);
                zipOut.putNextEntry(new ZipEntry(_file.getName()));
                int temp = 0;
                while ((temp = input.read()) != -1) {
                    zipOut.write(temp);
                }
                input.close();
            }
            zipOut.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        long start = System.currentTimeMillis();
        String shpPath = "D:/lzugis/code/lzugis-web/src/main/resources/shp/globedata_temp.shp";
        ShapeHelper shphelper = new ShapeHelper();
        shphelper.zipShapeFile(shpPath);
        System.out.println("共耗时"+(System.currentTimeMillis() - start)+"ms");
    }
}
