package SqlParse;

public class CreateSqlParser extends BaseSqlParser{

	public CreateSqlParser(String originalSql) {
		super(originalSql);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void initializeSegments() {
		// TODO Auto-generated method stub
		segments.add(new SqlSegment("(create table)(.+)([(])","[,]"));
		segments.add(new SqlSegment("(create index)(.+)(on)","[,]"));
		segments.add(new SqlSegment("(on)(.+)([(])", "[,]"));
		segments.add(new SqlSegment("([(])(.+)([)] ENDOFSQL)","[,]"));
	}

}
