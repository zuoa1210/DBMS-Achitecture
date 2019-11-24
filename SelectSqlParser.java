package SqlParse;

public class SelectSqlParser extends BaseSqlParser{

	public SelectSqlParser(String originalSql) {
		super(originalSql);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void initializeSegments() {
		// TODO Auto-generated method stub
		segments.add(new SqlSegment("(select)(.+)(from)","[,]"));
		segments.add(new SqlSegment("(from)(.+?)(where |group\\s+by|having|order\\s+by | ENDOFSQL)","(,|s+lefts+joins+|s+rights+joins+|s+inners+joins+)"));
		segments.add(new SqlSegment("(where)(.+?)(group\\s+by |having| order\\s+by | ENDOFSQL)","(and|or)"));
		segments.add(new SqlSegment("(group\\s+by)(.+?)(having|order\\s+by| ENDOFSQL)","[,]"));
		segments.add(new SqlSegment("(having)(.+?)(order\\s+by| ENDOFSQL)","(and|or)"));
		segments.add(new SqlSegment("(order\\s+by)(.+)( ENDOFSQL)","[,]"));
	}
}
