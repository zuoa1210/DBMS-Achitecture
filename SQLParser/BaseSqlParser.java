package SqlParse;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseSqlParser {
	protected String originalSql;
	protected List<SqlSegment> segments;
	
	public BaseSqlParser(String originalSql) {
		this.originalSql = originalSql;
		segments = new ArrayList<SqlSegment>();
		initializeSegments();
	}
	
	protected abstract void initializeSegments();
	
	protected List<List<String>> splitSqlToSegment() {
		List<List<String>> list = new ArrayList<List<String>>();
		for(SqlSegment sqlSegment : segments) {
			sqlSegment.parse(originalSql);
			// Eliminate empty conditions in the list.
			if(sqlSegment.getBody().equals("")) continue;
			list.add(sqlSegment.getBodyPieces());
		}
		return list;
	}
	
	public String getParsedSql() {
		StringBuffer sb = new StringBuffer();
		for(SqlSegment sqlSegment : segments) {
			sb.append(sqlSegment.getParsedSqlSegment() + "n");
		}
		String retval = sb.toString().replaceAll("n+", "n");
		return retval;
	}
}
