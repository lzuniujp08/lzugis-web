package com.lzugis.dao;

import com.lzugis.dao.jdbc.util.AnnotationUtil;
import com.lzugis.helper.CommonConfig;
import com.lzugis.services.model.County;
import com.lzugis.services.model.GeocodePoint;
import org.json.simple.JSONObject;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Repository;
import org.sqlite.SQLiteDataSource;

import java.util.List;
import java.util.UUID;

/**
 * Created by admin on 2017/11/14.
 */
@Repository
public class TestDao extends CommonDao {

    public TestDao(){
        jdbcTemplate = new JdbcTemplate();
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(CommonConfig.getVal("database.driverclassname"));
        dataSource.setUrl(CommonConfig.getVal("database.url"));
        dataSource.setUsername(CommonConfig.getVal("database.username"));
        dataSource.setPassword(CommonConfig.getVal("database.password"));
        jdbcTemplate.setDataSource(dataSource);

        SQLiteDataSource source = new SQLiteDataSource();
        String dbPath = CommonConfig.getVal("geocode.dbpath");
        source.setUrl("jdbc:sqlite:"+dbPath);
        sqliteJdbcTemplate = new JdbcTemplate(source);
    }

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
        //插入数据
//        for(int i=0;i<10;i++){
//            String name = "LZUGIS"+i;
//            GeocodePoint point = new GeocodePoint();
//            point.setId(UUID.randomUUID().toString());
//            point.setName(name);
//            int flat = test.save(point);
//            if(flat>0)System.out.println(name+" has saved success!");
//        }

        //获取对象以及更新对象
//        String name="LZUGIS18";
//        GeocodePoint point = (GeocodePoint)test.get(GeocodePoint.class, name, "name");
//        point.setName("LZUGIS10");
//        test.save(point);
//        if(null==point){
//            System.out.println(name);
//        }else{
//            System.out.println(point.getName());
//        }
        //删除对象
//        test.delete(test.table(GeocodePoint.class), "name", point.getName());

        //分页查询
        String select = "select gid, name, x, y";
        String exceptSelect =  " from "+test.table(County.class)+" t where 1=1 ";
        Page<County> page = test.paginate(County.class, 1, 10, select,exceptSelect,null);
        System.out.println(page.getPageNumber());
        System.out.println(page.getTotalPage());
        System.out.println(page.getTotalRow());
        System.out.println(page.getPageSize());
        System.out.println(page.getList().toString());


//        test.jdbcTempTest();
    }/**/
}
