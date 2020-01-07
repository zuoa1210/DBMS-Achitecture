package SQLParser;

public class DropSqlParser extends BaseSqlParser{

	public DropSqlParser(String originalSql) {
		super(originalSql);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void initializeSegments() {
		// TODO Auto-generated method stub
		segments.add(new SqlSegment("(drop table)(.+)( ENDOFSQL)","[,]"));
		segments.add(new SqlSegment("(drop index)(.+)( ENDOFSQL)","[,]"));
	}
}
