package SQLParser;

public class DeleteSqlParser extends BaseSqlParser{

	public DeleteSqlParser(String originalSql) {
		super(originalSql);
	}

	@Override
	protected void initializeSegments() {
		segments.add(new SqlSegment("(delete from)(.+)( where)", "[,]"));
		segments.add(new SqlSegment("(where)(.+)( ENDOFSQL)","[ ]+"));
	}
	
}
