package com.lzugis.dao;

import com.amazonaws.util.json.JSONArray;
import com.amazonaws.util.json.JSONObject;
import com.lzugis.helper.CommonConfig;
import com.lzugis.helper.CommonMethod;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RailInfo {
    private CommonMethod cm = new CommonMethod();
    private String filePath = "D:\\project\\2017年\\railway\\stops\\";
    private JdbcTemplate jdbcTemplate;

    public RailInfo(){
        jdbcTemplate = new JdbcTemplate();
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(CommonConfig.getVal("database.driverclassname"));
        dataSource.setUrl(CommonConfig.getVal("database.url"));
        dataSource.setUsername(CommonConfig.getVal("database.username"));
        dataSource.setPassword(CommonConfig.getVal("database.password"));
        jdbcTemplate.setDataSource(dataSource);
    }

    public List getRailList(){
        //长珲城际
        String url = "https://kyfw.12306.cn/otn/leftTicket/query?leftTicketDTO.train_date=2017-12-30&leftTicketDTO.from_station=CCT&leftTicketDTO.to_station=HUL&purpose_codes=ADULT";

        //哈大高铁
//        String url = "https://kyfw.12306.cn/otn/leftTicket/query?leftTicketDTO.train_date=2017-12-30&leftTicketDTO.from_station=HBB&leftTicketDTO.to_station=DFT&purpose_codes=ADULT";
        List list = new ArrayList();

        String railPath = filePath+"rails.csv";
        StringBuffer sb = new StringBuffer();
        sb.append("\r\ntrain_code,train_name,start_time,arrive_time,stopover_time");
        try {
            JSONObject json = cm.getUrlJSON(url);
            JSONObject data = (JSONObject)json.get("data");
            JSONArray result = (JSONArray)data.get("result");
            for(int i=0;i<result.length();i++){
                String raininfo = result.get(i).toString();
                String[] raininfos = raininfo.split("\\|");
                if(raininfos.length>10) {
                    Map map = new HashMap();
                    map.put("train_code", raininfos[2]);
                    map.put("train_name", raininfos[3]);
                    map.put("start_time", raininfos[8]);
                    map.put("arrive_time", raininfos[9]);
                    map.put("stopover_time", raininfos[10]);
                    list.add(map);
                    sb.append("\r\n"+raininfos[2]+","+raininfos[3]+","+raininfos[8]+","+raininfos[9]+","+raininfos[10]);
                }
            }
            cm.append2File(railPath, sb.toString(), true);
        }catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }

    public String getRainInfo(Map rail){
        StringBuffer sb = new StringBuffer();
        Map map = rail;
        String code = map.get("train_code").toString();

        String url = "https://kyfw.12306.cn/otn/czxx/queryByTrainNo?train_no="+code
                +"&from_station_telecode=VAB&to_station_telecode=DFT&depart_date=2017-12-30";
        try{
            JSONObject info = cm.getUrlJSON(url);
            JSONObject data = (JSONObject)info.get("data");
            JSONArray _stops = (JSONArray)data.get("data");
            map.put("stops", _stops);
            String _fields = "station_train_code,station_no,station_name,start_time,arrive_time,stopover_time";
            String[] fields = _fields.split(",");
            for(int i=0;i<_stops.length();i++){
                JSONObject _stop = (JSONObject)_stops.get(i);
                sb.append("\r\n"+code+",");
                for(int j=1;j<fields.length;j++){
                    String field = fields[j];
                    String _info = _stop.getString(field);
                    sb.append(_info+"," );
                }
                String station_name = _stop.getString("station_name");
                Map stop = getStopInfo(station_name);
                double lon = stop!=null?(double)stop.get("lon"):99.99,
                        lat = stop!=null?(double)stop.get("lat"):99.99;
                sb.append(lon+","+lat);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return sb.toString();
    }

    public Map getStopInfo(String stopname){
        String sql = "select * from rail_station where name like ?";
        List list = jdbcTemplate.queryForList(sql, new Object[]{stopname});
        return list.size()>0?(Map)list.get(0):new HashMap();
    }

    public static void main(String[] args){
        RailInfo rail = new RailInfo();
        List list = rail.getRailList();
        String stopsPath = rail.filePath+"stops.csv";
        for(int i=0;i<list.size();i++){
            Map map = (Map)list.get(i);
            if(i>0)rail.cm.append2File(stopsPath, "", true);
            String rainStops = rail.getRainInfo(map);
            rail.cm.append2File(stopsPath, rainStops, false);
        }
        System.out.println("success");
    }
}
