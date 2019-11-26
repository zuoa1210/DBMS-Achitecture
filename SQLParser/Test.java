package SqlParse;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Test {
	public static void main(String arg[]) {
		System.out.println("Please input SQL:");
		boolean temp = true;
		while(temp)
		{
			Scanner input = new Scanner(System.in);
			String sql = input.nextLine();

			while(sql.lastIndexOf(";")!=sql.length()-1){
				sql = sql + " " + input.nextLine();
			}
			
			sql = sql.trim();
			sql = sql.toLowerCase();
			sql = sql.replaceAll("\\s+", " ");
			
			sql = sql.substring(0, sql.lastIndexOf(";"));
			sql = "" + sql + " ENDOFSQL";
			
			List<List<String>> parameter_list=new ArrayList<List<String>>(); 

			if(sql.equals("quit ENDOFSQL"))
			{
				temp = false;
			}
			else
			{	
				parameter_list = SqlParserFactory.generateParser(sql);
				
				if(parameter_list == null) continue;
				
				System.out.println("Finish executing SQL Parser...");
				
				for(int i = 0;i < parameter_list.size();i++)
				{
					System.out.println(parameter_list.get(i));
				}
			}
		}
	}
}
