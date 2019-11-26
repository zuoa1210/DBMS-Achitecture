package SqlParse;

public class DeleteSqlParser extends BaseSqlParser{

	public DeleteSqlParser(String originalSql) {
		super(originalSql);
	}

	@Override
	protected void initializeSegments() {
		segments.add(new SqlSegment("(delete from)(.+)( where | ENDOFSQL)", "[,]"));
		segments.add(new SqlSegment("(where)(.+)( ENDOFSQL)","[ ]+"));
	}
	
}
