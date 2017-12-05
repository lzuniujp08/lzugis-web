package com.lzugis.dao.jdbc.dialect;

import com.lzugis.dao.jdbc.IDbDao;

public class MySqlDbDao implements IDbDao {

	public void forPaginate(StringBuilder sql, int pageNumber, int pageSize,
                            String select, String sqlExceptSelect) {
		int offset = pageSize * (pageNumber - 1);
		sql.append(select).append(" ");
		sql.append(sqlExceptSelect);
		sql.append(" limit ").append(offset).append(", ").append(pageSize);	// limit can use one or two '?' to pass paras

	}

}
