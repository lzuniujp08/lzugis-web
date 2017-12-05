package com.lzugis.dao.jdbc.dialect;


import com.lzugis.dao.jdbc.IDbDao;

public class OracleDbDao implements IDbDao {

	public void forPaginate(StringBuilder sql, int pageNumber, int pageSize,
                            String select, String sqlExceptSelect) {
		int start = (pageNumber - 1) * pageSize + 1;
		int end = pageNumber * pageSize;
		sql.append("select * from ( select row_.*, rownum rownum_ from (  ");
		sql.append(select).append(" ").append(sqlExceptSelect);
		sql.append(" ) row_ where rownum <= ").append(end).append(") table_alias");
		sql.append(" where table_alias.rownum_ >= ").append(start);
	}

}
