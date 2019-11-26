package SqlParse;

public class UpdateSqlParser extends BaseSqlParser{

	public UpdateSqlParser(String originalSql) {
		super(originalSql);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void initializeSegments() {
		// TODO Auto-generated method stub
		segments.add(new SqlSegment("(update)(.+)(set)","[,]"));
		segments.add(new SqlSegment("(set)(.+?)( where | ENDOFSQL)","[,]"));
		segments.add(new SqlSegment("(where)(.+)(ENDOFSQL)","[ ]+"));
	}
	
}
