package base; 
import java.util.HashSet;
import java.util.*;
import java.io.*;
import java.lang.*;

public class Table{
    private String tableName;
    private HashSet<String> attrSet = new HashSet<>();  //hashset used to check exist
    private ArrayList<Attr> tableAttr = new ArrayList<>();
    File tabledir;
    File dictdir;
    private static String dbName = "db";    //test 实现时要另外创建dbname下数据文件目录

    public Table()
    {}
    public Table(String tableName)
    {
        this.tableName = tableName;
        this.tabledir = new File("data/" + dbName + "/" + tableName + ".csv");
        this.dictdir = new File("dict/" + dbName + "/" + tableName + ".dict");
        if(!tabledir.getParentFile().exists()) {
            //如果目标文件所在的目录不存在，则创建父目录
            System.out.println("目标文件所在目录不存在，准备创建它！");
            if(!tabledir.getParentFile().mkdirs()) {
                System.out.println("创建目标文件所在目录失败！");
            }
        }

    }

    public void setName(String name){
        this.tableName = name;
    }
    
    public String getName(){
        return tableName;
    }

    public void setTableDir(String tableName)
    {
        this.tabledir = new File("data/" + dbName + "/" + tableName + ".csv");
    }

    public void setDictDir(String tableName)
    {
        this.dictdir = new File("dict/" + dbName + "/" + tableName + ".dict");
    }
    public int getAttrNum(){
        return this.attrSet.size();
    }
    public Attr getAttr(int i)
    {
        return this.tableAttr.get(i);
    }
    //通过名字找attri
    public Attr getAttr(String name){
        int index = 0;
        Attr a = new Attr();
        try
        {
            while(index < this.tableAttr.size())
            {
                if(this.tableAttr.get(index).getName().equals(name))
                {
                    //System.out.println("get");
                    a = this.tableAttr.get(index);
                    break;
                }
                index++;
            }
        }
        catch(Exception e) { 
            System.err.println(e); 
        } 
        return a;
    }
   
    public void addAttr(Attr a){
        this.tableAttr.add(a);
        this.attrSet.add(a.getName());
    }
    //保存attri信息
    public void savedict(Attr a, PrintWriter pw)
    {
        String name = a.getName();
        String type = a.getType();
        String pk = "";
        String fk = "";
        String[] fkInfo = a.getFkInfo();
        //如果是主键字段后面加*
        if (a.isPrimaryKey()) 
            pk = "*";
        else //非主键^
            pk = "^";
        if (a.isForeignKey())
            fk = "*";
        else //非主键^
            fk = "^";
        pw.println(name + " " + type + " " + pk + " " + fk + " "+ fkInfo[0] + " " + fkInfo[1]);
    }
    //是否存在.csv文件
    public boolean isExistTable(){
        return tabledir.exists();
    }
    //在有table的前提下 这个table有没有这个attri
    public boolean isExistAttr(String attrName){
        return this.attrSet.contains(attrName);
    }
    //是否应该弄在构造函数里
    //新建一个.csv文件
    public void createTable(){
        if(isExistTable())
        {
            System.out.println("Error. Table already exist.");
        }
        else{
            try
            {
                this.tabledir.createNewFile();
                System.out.println("File path: "+tabledir.getAbsolutePath());
            } 
            catch (Exception e) { 
                System.err.println(e); 
            } 

        }
        
    }
    //获得表的行数
    public int gettablesize(){
        int length = 0;
        for(int i = 0; i < this.attrSet.size(); i++)
        {
            // System.out.print(this.tableAttr.get(i).getName()+'\t');
            if(length < this.tableAttr.get(i).getTupleSize())
                length = this.tableAttr.get(i).getTupleSize();
        }
        return length;
    }
    //保存.csv
    public void saveTable(){
        if(!tabledir.getParentFile().exists()) {
            //如果目标文件所在的目录不存在，则创建父目录
            System.out.println("目标文件所在目录不存在，准备创建它！");
            if(!tabledir.getParentFile().mkdirs()) {
                System.out.println("创建目标文件所在目录失败！");
            }
        }
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(tabledir));
            int length = 0;
            for(int i = 0; i < this.attrSet.size(); i++)
            {
                bw.write(this.tableAttr.get(i).getName()+',');
                if(length < this.tableAttr.get(i).getTupleSize())
                    length = this.tableAttr.get(i).getTupleSize();
            }
                
            int j = 0;

            while(j < length)
            {   
                bw.newLine();
                for(int i = 0; i < this.attrSet.size(); i++)
                    bw.write(String.valueOf(this.tableAttr.get(i).getTuple(j)) + ',');
                j++;
            }
            bw.flush();
            bw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //save dict
        if(!dictdir.getParentFile().exists()) {
            //如果目标文件所在的目录不存在，则创建父目录
            System.out.println("目标文件所在目录不存在，准备创建它！");
            if(!dictdir.getParentFile().mkdirs()) {
                System.out.println("创建目标文件所在目录失败！");
            }
        }
        try (
                FileWriter fw = new FileWriter(this.dictdir);
                PrintWriter pw = new PrintWriter(fw)
        ) {
        for(int i = 0; i < attrSet.size(); i++)
            this.savedict(this.tableAttr.get(i),pw);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //System.out.println("Saved");

    }
    
    //读.csv
    public void getTable(String tableName){
    	this.setName(tableName);
        this.setTableDir(tableName);
        this.setDictDir(tableName);
        try { 
            BufferedReader tablereader = new BufferedReader(new FileReader(this.tabledir));//换成你的文件名
            BufferedReader dictreader = new BufferedReader(new FileReader(this.dictdir));
            //读dict
            String line = null; 
            while((line=dictreader.readLine())!=null){ 
                String item[] = line.split(" ",-1);//CSV格式文件为逗号分隔符文件，这里根据逗号切分    
                //t.attrSet.add(item[0]);
                Attr a = new Attr();
                a.setName(item[0]);
                a.setType(item[1]);
                if(item[2].equals("^"))
                    a.setPrimaryKey(false);
                else
                    a.setPrimaryKey(true);
                if(item[3].equals("^"))
                    a.setForeignKey(false, item[4], item[5]);
                else
                    a.setForeignKey(true, item[4], item[5]);
                this.addAttr(a);
            }
            dictreader.close();
            //读表
            String line2 = null;
            tablereader.readLine();//第一行信息，为标题信息
            while((line2=tablereader.readLine())!=null){ 
                //System.out.println(line2);
                String item2[] = line2.split(",", -1);
                for(int i = 0; i < this.attrSet.size(); i++)
                {
                    //System.out.println(this.tableAttr.get(i).getType());
                    if(this.tableAttr.get(i).getType().equals("java.lang.Integer"))
                    {
                        //System.out.println("got");
                        int item_int = Integer.parseInt(item2[i]);
                        this.tableAttr.get(i).addTuple(item_int);
                    }
                    else if(this.tableAttr.get(i).getType().equals("java.lang.Character"))
                    {
                        char item_c = item2[i].charAt(0);
                        this.tableAttr.get(i).addTuple(item_c);
                    }
                    else if(this.tableAttr.get(i).getType().equals("java.lang.Double"))
                    {
                        Double item_double = Double.parseDouble(item2[i]);
                        this.tableAttr.get(i).addTuple(item_double);
                    }
                    else
                        this.tableAttr.get(i).addTuple(item2[i]);
                    //System.out.println(item2[i].getClass().getName());
                }
                    
            } 
            tablereader.close();
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
    }
    //打印表
    public void displayTable()
    {
        int length = this.gettablesize();
        for(int i = 0; i < this.attrSet.size(); i++)
            System.out.print(this.tableAttr.get(i).getName()+'\t');
        System.out.print("\n");
        for(int j = 0; j < length; j++)  
        {
                for(int i = 0; i < this.attrSet.size(); i++)
                    System.out.print(String.valueOf(this.tableAttr.get(i).getTuple(j)) + '\t');
                System.out.print("\n");
        }
    }
    //打印attri信息
    public void displayDict()
    {
        System.out.print("Attribute"+"\t"+"Type"+"\t"+"isPKey "+"\t"+"FKey"+"\t"+"Ref_table"+"\t"+"Ref_attr");
        System.out.print("\n");
        for(int i = 0; i < this.tableAttr.size(); i++)
        {
            System.out.print(this.tableAttr.get(i).getName()+ "\t");
            System.out.print(this.tableAttr.get(i).getType()+ "\t");
            if(this.tableAttr.get(i).isPrimaryKey() == true)
                System.out.print("true" + "\t");
            else
                System.out.print("false" + "\t");
            if(this.tableAttr.get(i).isForeignKey() == true)
                System.out.print("true" + "\t");
            else
                System.out.print("false" + "\t");
            System.out.print(this.tableAttr.get(i).getFkInfo()[0]+"\t");
            System.out.print(this.tableAttr.get(i).getFkInfo()[1]+"\t");
            System.out.print("\n");
        }
    }
    //删表
    public String dropTable(String tbName) {
        File tbfile = new File("data/" + dbName + "/" + tbName + ".csv");
        if(!tbfile.exists())
            return "Error, table file not exist.";
        else
            tbfile.delete();
        File dictfile = new File("dict/" + dbName + "/" + tbName + ".dict");
        if(!dictfile.exists())
            return "Error, dict file not exist.";
        else
            dictfile.delete();
        return "Drop success!";
    }
    public Object[][] TabletoArray(){
        int length = this.gettablesize();
        System.out.println(length);
        int columns = this.attrSet.size();
        Object[][] res = new Object[length][columns];
        //j是行数，i是列数
         for(int j = 0; j < length; j++)
         {
             for(int i = 0; i < columns; i++)
                 res[j][i] = this.tableAttr.get(i).getTuple(j);
         }
        return res;
    }
    public int getAttrIndex(String name){
        int res = -1;
        for(int i = 0 ; i < this.attrSet.size(); i++)
        {
        	//System.out.println("for");
            if(this.tableAttr.get(i).getName().equals(name))
            {
            	//System.out.println("if");
                res = i;
                break;
            }
        }
        return res;
    }
    public void changeAttrOrder(int index){
        Attr a = new Attr();
        a = this.tableAttr.get(index);
        this.tableAttr.set(index,this.tableAttr.get(0));
        this.tableAttr.set(0,a);
    }
    public int numOfAttr(){
        return this.attrSet.size();
    }
    public void setattrSet(ArrayList<String> attr)
    {
        for(int i = 0; i < attr.size(); i++)
        {
            if(!this.attrSet.contains(attr.get(i)))
                attrSet.add(attr.get(i));
        }
    }

    public void ListToTable(ArrayList<Object[]> list, ArrayList<String> attr)
    {
        this.setattrSet(attr);
        for(int i = 0; i < attr.size(); i++)
        {
            Attr a = new Attr();
            a.setName(attr.get(i));
            a.setType(list.get(0)[i].getClass().getName());
            for(int j = 0; j < list.size(); j++)
            {
                a.addTuple(list.get(j)[i]);
            }
            this.addAttr(a);
        }
        //return res;
    }
}