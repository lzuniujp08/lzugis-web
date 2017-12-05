package com.lzugis.dao.jdbc.dialect;


import com.lzugis.dao.jdbc.IDbDao;

public class PostgreSqlDbDao implements IDbDao {

	public void forPaginate(StringBuilder sql, int pageNumber, int pageSize,
                            String select, String sqlExceptSelect) {
		int offset = pageSize * (pageNumber - 1);
		sql.append(select).append(" ");
		sql.append(sqlExceptSelect);
		sql.append(" limit ").append(pageSize).append(" offset ").append(offset);
	}

}
