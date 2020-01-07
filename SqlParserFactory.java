package SQLParser;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SqlParserFactory {
	public static List<List<String>> generateParser(String sql) {
		BaseSqlParser tmp = null;
		
		if(contains(sql, "(insert)(.+)(select)(.+)")) {
			System.out.println("insert select");
			tmp = new InsertSelectSqlParser(sql);
		} else if(contains(sql, "(select)(.+)(from)(.+)")) {
			System.out.println("select");
			tmp = new SelectSqlParser(sql);
		} else if(contains(sql, "(delete from)(.+)")) {
			System.out.println("delete");
			tmp = new DeleteSqlParser(sql);
		} else if(contains(sql, "(update)(.+)(set)(.+)")) {
			System.out.println("update");
			tmp = new UpdateSqlParser(sql);
		} else if(contains(sql, "(insert into)(.+)(values)(.+)")) {
			System.out.println("insert");
			tmp = new InsertSqlParser(sql);
		} else if(contains(sql, "(create table)(.+)")) {
			System.out.println("create table");
			tmp = new CreateSqlParser(sql);
		} else if(contains(sql, "(drop table)(.+)")) {
			System.out.println("drop table");
			tmp = new DropSqlParser(sql);
		} else if(contains(sql, "(create index)(.+)")) {
			System.out.println("create index");
			tmp = new CreateSqlParser(sql);
		} else if(contains(sql, "(drop index)(.+)")) {
			System.out.println("drop index");
			tmp = new DropSqlParser(sql);
		} else if(contains(sql, "quit(.+)")) {
			System.out.println("Program exit...");
			return null;
		} else {
			System.out.println("Input SQL errors, please re-enter...");
			return null;
		}
		return tmp.splitSqlToSegment();
	}
	
	private static boolean contains(String sql, String regExp) {
		Pattern pattern = Pattern.compile(regExp, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(sql);
		return matcher.find();
	}
}
