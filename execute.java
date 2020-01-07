package execute;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import SQLParser.SqlParserFactory;
import base.Attr;
import base.Table;
import base.sql;
import query_tree.index;
import query_tree.query_tree;




public class execute {
	ArrayList<Table> all_tables = new ArrayList<Table>();
	sql utils;
	ArrayList<index> index_list = new ArrayList<index>();
	
	//load the corresponding tables, need to modify, just for temporary using
	public execute() {
		File folder = new File("data/db");
		File[] listOfFiles = folder.listFiles();
		ArrayList<String> name = new ArrayList<String>();
		for (int i = 0; i < listOfFiles.length; i++) {
		  if (listOfFiles[i].isFile()) {
			  String[] temp = listOfFiles[i].getName().split("\\.");
			  if(temp.length > 1 && temp[1].equals("csv"))
				  name.add(temp[0]);
		  }
		}
		for(String n : name) {
			Table temp = new Table();
			temp.getTable(n);
			
			this.all_tables.add(temp);
		}
		this.utils = new sql(this.all_tables, this.index_list);
		run();
	}
	
	public void run() {
		
		boolean temp = true;
		while(temp)
		{  
			System.out.println("Please input SQL:");
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
				for(int i = 0;i < parameter_list.size();i++)
				{
					System.out.println(parameter_list.get(i));
				}
				
				long startTime=System.currentTimeMillis();
				
				if(parameter_list.get(0).get(0).equals("select")) {
					// check and get the tables that used in the query
					ArrayList<Table> involve_tables = new ArrayList<Table>();
					boolean flag = true;
					for(List<String> ls : parameter_list) {
						if(ls.get(0).equals("from")) {
							for(int i = 1; i < ls.size(); i++) {
								for(Table t : this.all_tables) {
									//if relation in the input belong to the database
									if(t.getName().toLowerCase().equals(ls.get(i))){
										involve_tables.add(t);
										break;
									}
								}
							
							}
							if(involve_tables.size() != ls.size() - 1)
								flag = false;
							break;
								
						}
						
					}
					if(flag == false) {
						System.out.println("Wrong input! No such table in this database");
						continue;
					}
						
					// pass the tables need to be used and the parsed query to query tree
					query_tree qt = new query_tree(parameter_list, involve_tables, utils, this.index_list);
					qt.operation_handler();
				}
				
				//drop table
				if(parameter_list.get(0).get(0).equals("drop table")) {
					this.utils.drop(parameter_list.get(0).get(1));
//					for(Table t : this.all_tables) {
//					System.out.println(t.getName());
//				}
					
				}
//				//delete tuples
				else if(parameter_list.get(0).get(0).equals("delete from")) {
					//System.out.println("enter delete");
					for(Table t : this.all_tables) {
						if(t.getName().toLowerCase().equals(parameter_list.get(0).get(1))) {
							//System.out.println("find table");
							this.utils.delete(t, parameter_list.get(1).get(1)
								, parameter_list.get(1).get(2)
								, (Object)parameter_list.get(1).get(3));
							t.displayTable();
						}
						
					}
					
				}
				else if(parameter_list.get(0).get(0).equals("update")) {
					String[] tempp = parameter_list.get(1).get(1).split("=");
					this.utils.update(parameter_list.get(0).get(1), parameter_list.get(2).get(1), parameter_list.get(2).get(2), parameter_list.get(2).get(3), tempp[0], tempp[1]);
					for(int i = 0; i < this.all_tables.size(); i++) {
			        	Table temppp = this.all_tables.get(i);
			        	if(temppp.getName().equals(parameter_list.get(0).get(1))) {
			        		temppp.displayTable();
			        	}
			        }
				}
				else if(parameter_list.get(0).get(0).equals("create table")) {
					boolean find = false;
					for(Table t : this.all_tables) {
						if(t.getName().equals(parameter_list.get(0).get(1))) {
							//System.out.println(t.getName());
							System.out.println("the table already exist!");
							find = true;
							break;
						}
					}
					if(find == false) {
						ArrayList<String> attr = new ArrayList<String>();
						ArrayList<String> type = new ArrayList<String>();
						boolean pk = false;
						boolean fk = false;
						for(List<String> aa : parameter_list) {
							if(aa.get(0).equals("primary key"))
								pk = true;
							if(aa.get(0).equals("foreign key"))
								fk = true;
						}
						for(int i = 1; i < parameter_list.get(1).size(); i++) {
							String[] tempp = parameter_list.get(1).get(i).split(" ");
							attr.add(tempp[0]);
							type.add(tempp[1]);
						}
						if(fk && pk) {
							String[] ff = parameter_list.get(4).get(1).split("\\(|\\)");
							this.utils.create(parameter_list.get(0).get(1), attr, type, parameter_list.get(2).get(1), parameter_list.get(3).get(1), ff[0], ff[1]);
						}
							
						else if(fk&&!pk) {
							String[] ff = parameter_list.get(4).get(1).split("\\(|\\)");
							this.utils.create(parameter_list.get(0).get(1), attr, type, parameter_list.get(2).get(1), ff[0], ff[1]);
						
						}
							else if(!fk&&pk)
							this.utils.create(parameter_list.get(0).get(1), attr, type, parameter_list.get(2).get(1));
						else
							this.utils.create(parameter_list.get(0).get(1), attr, type);
					}
					
					
//					for(Table t : this.all_tables) {
//						System.out.println(t.getName());
//					}
				}
				else if(parameter_list.get(0).get(0).equals("insert into")) {
					ArrayList<String> values = new ArrayList<String>();
					for(int i = 1; i < parameter_list.get(1).size(); i++) {
						values.add(parameter_list.get(1).get(i));
					}
					
					this.utils.insert(parameter_list.get(0).get(1),values);
					for(Table t : this.all_tables) {
						if(t.getName().equals(parameter_list.get(0).get(1))) {
							//t.displayTable();
							break;
						}
					}
				}
				else if(parameter_list.get(0).get(0).equals("create index")) {
					//get the attri for the index
					boolean find = false;
					for(index i : this.index_list) {
						if(i.index_name.equals(parameter_list.get(0).get(1)) && i.table_name.equals(parameter_list.get(1).get(1))) {
							find = true;
							break;
						}
					}
					if(find == true) {
						System.out.println("The index exists!");
					}
					else {
						for(Table t : this.all_tables) {
							if(t.getName().equals(parameter_list.get(1).get(1))) {
								index tempi = new index(t.getAttr(parameter_list.get(2).get(1)), parameter_list.get(0).get(1), parameter_list.get(1).get(1));
								this.index_list.add(tempi);
								System.out.println("index created");
								break;
							}
						}
					}
				}
				else if(parameter_list.get(0).get(0).equals("drop index")) {
					String[] tempp = parameter_list.get(0).get(1).split(" on ");
					for(int i = 0; i < this.index_list.size(); i++) {
						index tempi = this.index_list.get(i);
						if(tempi.index_name.equals(tempp[0]) && tempi.table_name.equals(tempp[1])) {
							this.index_list.remove(i);
							System.out.println("index deleted!");
						}
					}
				}
				
				
				
				long endTime=System.currentTimeMillis();
				 float excTime=(float)(endTime-startTime)/1000;
				 System.out.println("execution time："+excTime+"s");
				
				System.out.println("Finish executing SQL Parser...");
			}
		}
	}
	

}
