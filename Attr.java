package base; 
import java.util.*;
import java.util.HashSet;
public class Attr {
    private String attrName;
    private String attrType;
    private boolean primaryKey;
    private boolean foreignKey;
    private HashMap<Object, Integer> tuple_set;
    private ArrayList<Object> tuple;
    private String ref_table;
    private String ref_attr;

    public Attr(){
        tuple = new ArrayList <>();
        primaryKey = false;
        tuple_set = new HashMap<>();
        foreignKey = false;
    }

    public String getName() {
            return attrName;
        }

    public void setName(String name) {
            this.attrName = name;
        }

    public String getType() {
            return attrType;
        }

    public void setType(String type) {
            if(type.equals("int" ) || type.equals("Int") || type.equals("Integer"))
                type = "java.lang.Integer";
            if(type.equals("char" ) || type.equals("Char") || type.equals("Character"))
                type = "java.lang.Character";
            if(type.equals("string" ) || type.equals("String"))
                type = "java.lang.String";
            if(type.equals("double" ) || type.equals("Doule") || type.equals("float") || type.equals("Float"))
                type = "java.lang.Double";
            this.attrType = type;
        }

    public boolean isPrimaryKey() {
            return primaryKey;
        }

    public void setPrimaryKey(boolean primaryKey) {
            this.primaryKey = primaryKey;
        }
        
    public void setForeignKey(boolean foreignKey, String ref_table, String ref_attr){
            this.foreignKey = foreignKey;
            this.ref_table = ref_table;
            this.ref_attr = ref_attr;
        }
    public String[] getFkInfo(){
        String[] res = new String[2];
        res[0] = this.ref_table;
        res[1] = this.ref_attr;
        return res;
    }
    public boolean isForeignKey(){
            return foreignKey;
        }
    
    public String addTuple(Object t){
            if(this.attrType.equals("")){
                System.out.println("Please set type first");
                return "false";
            }
            if(t.getClass().getName().equals("java.lang.Integer") && this.attrType.equals("java.lang.Double"))
                t = Double.valueOf(t.toString());
            if(!t.getClass().getName().equals(this.attrType)){
                System.out.println("Type Error");
                return "false";
            }
            if(isPrimaryKey() && this.tuple_set.containsKey(t)){
                System.out.println("primary key can not be duplicate.");
                return "false";
            }
            if(isForeignKey()){
                Table ref = new Table();
                ref.getTable(this.ref_table);
                Attr ref_attr = ref.getAttr(this.ref_attr);
                if(!ref_attr.tuple_set.containsKey(t))
                {
                    System.out.println("Foreign key error.");
                    return "false";
                }
            }
            this.tuple.add(t);
            if(!tuple_set.containsKey(t))
                tuple_set.put(t,1);
            else
            {
                tuple_set.put(t, tuple_set.get(t)+1);
            }
            return "success";
        }

    public Object getTuple(int i){
            return tuple.get(i);
        }
    public void removeTuple(int i){
    	//System.out.println(i);
        Object tool = getTuple(i);
        if(tuple_set.get(tool) == 1)
            tuple_set.remove(tool);
        else
            tuple_set.put(tool, tuple_set.get(tool)-1);
        this.tuple.remove(i);
    }
    
    public void setTuple(ArrayList<Object> list)
    {
        this.tuple = list;
    }
    public ArrayList getAllTuple(){
        return this.tuple;
    }

    public int getTupleSize(){
        return tuple.size();
    }
    
    public void setTuple_value(Object before, Object after){
        if(!after.getClass().getName().equals(this.attrType))
            System.out.println("Type error.");
        else if(isPrimaryKey() && this.tuple_set.containsKey(after))
            System.out.println("PK error.");
        else{
            for(int i = 0; i < tuple.size(); i++)
            {
                if(tuple.get(i) == null)
                    tuple.set(i,"null");
                if(tuple.get(i).equals(before))
                {
                    tuple.set(i, after);
                    break;
                }
            }
        }
    }
    public void setTuple_index(int i, Object after){
        if(!after.getClass().getName().equals(this.attrType))
            System.out.println("Type Error!");
        else if(isPrimaryKey() && this.tuple_set.containsKey(after))
            System.out.println("PK error");
        else if(isForeignKey()){
                Table ref = new Table();
                ref.getTable(this.ref_table);
                Attr ref_attr = ref.getAttr(this.ref_attr);
                if(!ref_attr.tuple_set.containsKey(after))
                    System.out.println("Foreign key error.");
            }
        else{
            tuple.set(i, after);
        }
    }

    public float selectivity() {
        HashSet noDupSet = new HashSet();
        noDupSet.add(this.tuple);
        int count = noDupSet.size();
        return 1/count;
       }
        
}