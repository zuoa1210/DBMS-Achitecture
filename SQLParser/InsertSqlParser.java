package SqlParse;

public class InsertSqlParser extends BaseSqlParser{

	public InsertSqlParser(String originalSql) {
		super(originalSql);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void initializeSegments() {
		// TODO Auto-generated method stub
		segments.add(new SqlSegment("(insert into)(.+?)(values)","[,]"));
		//segments.add(new SqlSegment("([(])(.+?)([)] values [(])","[,]"));
		segments.add(new SqlSegment("(values [(])(.+)([)] ENDOFSQL)","[,]"));
	}

	public String getParsedSql() {
		String retval = super.getParsedSql();
		retval=retval+")";
		return retval;
	}
}
