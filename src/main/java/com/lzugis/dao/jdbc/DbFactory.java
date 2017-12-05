package com.lzugis.dao.jdbc;


import com.lzugis.dao.jdbc.dialect.*;

public class DbFactory {
	public static IDbDao getDbDao(DbType dbType){
		IDbDao dbDao = null;
		switch(dbType){
			case ORACLE:
				dbDao = new OracleDbDao();
				break;
			case SQLSERVER:
				dbDao = new SqlServerDbDao();
				break;
			case POSTGRESQL:
				dbDao = new PostgreSqlDbDao();
				break;
			case MYSQL:
				dbDao = new MySqlDbDao();
				break;
			case SQLITE:
				dbDao = new Sqlite3DbDao();
				break;
			default:
				dbDao = new DbDao();
		}
		return dbDao;
	}
	
	public static DbType getDbType(String dbProductName){
		if ("Oracle".equalsIgnoreCase(dbProductName)) {
			return DbType.ORACLE;
		} else if ("SQL Server".equalsIgnoreCase(dbProductName)||"MS-SQL".equalsIgnoreCase(dbProductName)) {
			return DbType.SQLSERVER;
		}else if("PostgreSQL".equalsIgnoreCase(dbProductName)){
			return DbType.POSTGRESQL;
		}else if("MySql".equalsIgnoreCase(dbProductName)){
			return DbType.MYSQL;
		}else if("Sqlite".equalsIgnoreCase(dbProductName)){
			return DbType.SQLITE;
		}else{
			return DbType.ANSISQL;
		}
		//return null;
	}
}
