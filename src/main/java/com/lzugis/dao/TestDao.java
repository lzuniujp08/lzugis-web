package com.lzugis.dao;

import com.lzugis.dao.jdbc.util.AnnotationUtil;
import com.lzugis.services.model.GeocodePoint;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Created by admin on 2017/11/14.
 */
@Repository
public class TestDao extends CommonDao {
    public List getDbData() throws Exception{
        List list = jdbcTemplate.queryForList("select name, lon, lat from capital");
        return list;
    }

    public void jdbcTempTest() {
        // sql语句
        String dsql ="delete from capital_buffer where id=?";
        jdbcTemplate.update(dsql, new Object[]{44});
        String sql = "select name, lon, lat, id from capital_buffer";
        List num = jdbcTemplate.queryForList(sql);
        System.out.println(num.toString());
    }

    public static void main(String[] args){
        TestDao test = new TestDao();

//        for(int i=0;i<10;i++){
//            String name = "LZUGIS"+i;
//            GeocodePoint point = new GeocodePoint();
//            point.setId(UUID.randomUUID().toString());
//            point.setName(name);
//            int flat = test.save(point);
//            if(flat>0)System.out.println(name+" has saved success!");
//        }

        String name="LZUGIS1";
        GeocodePoint point = (GeocodePoint)test.get(GeocodePoint.class, name, "name");
        if(null==point){
            System.out.println(name);
        }else{

            System.out.println(point.getName());
        }

//        test.jdbcTempTest();
    }
}
