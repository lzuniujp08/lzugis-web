package com.lzugis.dao;

import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by admin on 2017/11/14.
 */
@Repository
public class TestDao extends CommonDao{
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
        test.jdbcTempTest();
    }
}
