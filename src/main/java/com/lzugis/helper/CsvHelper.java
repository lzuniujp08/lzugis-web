package com.lzugis.helper;

import java.util.List;
import java.util.Map;

public class CsvHelper {
    private CommonMethod cm;

    public CsvHelper(){
        super();
        cm = new CommonMethod();
    }

    public void write2CsvFile(String csvPath, String[] header, List data){
        StringBuffer csvContent = new StringBuffer();
        //写入头信息
        for(int i= 0;i<header.length;i++){
            String field = header[i];
            csvContent.append(field+", ");
        }
        csvContent.append("\r\n");
        //写入文件信息
        for(int i=0;i<data.size();i++){
            Map<String, Object> row = (Map)data.get(i);
            for (Map.Entry entry : row.entrySet()) {
                csvContent.append(entry.getValue().toString()+", ");
            }
            csvContent.append("\r\n");
        }
        //输出CSV
        cm.append2File(csvPath, csvContent.toString());
    }
}
