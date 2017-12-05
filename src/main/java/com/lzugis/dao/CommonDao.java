package com.lzugis.dao;

import com.lzugis.helper.CommonConfig;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.sqlite.SQLiteDataSource;

/**
 * Created by admin on 2017/11/14.
 */
public class CommonDao {
    // 创建JDBC模板
    protected JdbcTemplate jdbcTemplate = new JdbcTemplate();
    protected JdbcTemplate sqliteJdbcTemplate;
    public CommonDao(){
        super();
        /**
         * 初始化Postgres
         */
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(CommonConfig.getVal("database.driverclassname"));
        dataSource.setUrl(CommonConfig.getVal("database.url"));
        dataSource.setUsername(CommonConfig.getVal("database.username"));
        dataSource.setPassword(CommonConfig.getVal("database.password"));
        jdbcTemplate.setDataSource(dataSource);
        /**
         * 初始化sqlite
         */
        SQLiteDataSource source = new SQLiteDataSource();
        String dbPath = CommonConfig.getVal("geocode.dbpath");
        source.setUrl("jdbc:sqlite:"+dbPath);
        sqliteJdbcTemplate = new JdbcTemplate(source);
    }

    public int deleteByFilter(String table, String field, Object value){
        StringBuffer sql = new StringBuffer();
        sql.append("delete from ").append(table).append(" where ")
                .append(field).append("=?");
        Object[] para = new Object[]{value};
        return jdbcTemplate.update(sql.toString(), para);
    }
}
