package SqlParse;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SqlSegment {
	private static final String ctrlF = "|";
	private static final String fourSpace = "    ";
	
	private String start;
	private String body;
	private String end;
	
	private String bodySplitPattern;
	private String segmentRegExp;
	private List<String> bodyPieces;
	
	public SqlSegment(String segmentRegExp, String bodySplitPattern) {
		start = "";
		body = "";
		end = "";
		this.segmentRegExp = segmentRegExp;
		this.bodySplitPattern = bodySplitPattern;
		this.bodyPieces = new ArrayList<String>();
	}
	
	public void parse(String sql) {
		Pattern pattern = Pattern.compile(segmentRegExp, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(sql);
		while(matcher.find()) {
			start = matcher.group(1);
			body = matcher.group(2);
			end = matcher.group(3);
			parseBody();
		}
	}
	
	private void parseBody() {
		List<String> ls = new ArrayList<String>();
		Pattern p = Pattern.compile(bodySplitPattern, Pattern.CASE_INSENSITIVE);
		body = body.trim();
		Matcher m = p.matcher(body);
		StringBuffer sb = new StringBuffer();
		boolean result = m.find();
		
		while(result) {
			m.appendReplacement(sb, ctrlF);
			result = m.find();
		}
		m.appendTail(sb);
		
		if(start.equals("(")) {
			start = "columns";
		}
		
		if(start.contains("(")){
			start = start.substring(0, start.length()-1);
		}
		
		start = start.trim();
		
		ls.add(start);
		
		String s = sb.toString();
		if(s.indexOf("primary key(") != -1) {
			s = s.substring(0, s.indexOf("primary key(") - 1);
		} 
		if(s.indexOf("foreign key(") != -1) {
			s = s.substring(0, s.indexOf("foreign key(") - 1);
		} 
		if(s.indexOf("(") != -1) {
			s = s.replace("(", "|");
		}
		
		String[] arr = s.split("[|]");
		int arrLength = arr.length;
		for(int i = 0; i < arrLength; i++) {
			ls.add(arr[i]);
		}
		bodyPieces = ls;
	}
	
	public String getParsedSqlSegment() {
		StringBuffer sb = new StringBuffer();
		sb.append(start+this.ctrlF);
		for(String piece : bodyPieces) {
			sb.append(piece+this.ctrlF);
		}
		return sb.toString();
	}
	
	public String getStart() {
		return start;
	}
	
	public void setStart(String start) {
		this.start = start;
	}
	
	public String getBody() {
		return body;
	}
	
	public void setBody(String body) {
		this.body = body;
	}
	
	public String getEnd() {
		return end;
	}
	
	public void setEnd(String end) {
		this.end = end;
	}
	
	public String getBodySplitPattern() {
		return bodySplitPattern;
	}
	
	public void setBodySplitPattern(String bodySplitPattern) {
		this.bodySplitPattern = bodySplitPattern;
	}
	
	public String getSegmentRegExp() {
		return segmentRegExp;
	}
	
	public void setSegmentRegExp(String segmentRegExp) {
		this.segmentRegExp = segmentRegExp;
	}
	
	public List<String> getBodyPieces() {
		return bodyPieces;
	}
	
	public void setBodyPieces(List<String> bodyPieces) {
		this.bodyPieces = bodyPieces;
	}
}
