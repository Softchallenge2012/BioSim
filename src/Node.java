
/**
 *
 * @author shen
 */
public class Node {
    private double x = 0;
    private double y = 0;
    private java.util.ArrayList parents = new java.util.ArrayList();
    private java.util.ArrayList children = new java.util.ArrayList();    
    private double[] values = null;
    private String[] attributes = null;
    private int numAttributes;
    private boolean deleted = false;
    
    public void setX(double x){this.x = x;}
    public double getX(){return this.x;}
    
    public void setY(double y){this.y = y;}
    public double getY(){return this.y;}
    
    public boolean getDelete(){return this.deleted;}
    public void setDelete(boolean del){this.deleted = del;}
    
    public void addParents(Node n){this.parents.add(n);}
    public Node getParents(int i){return (Node)this.parents.get(i);}
    
    public void addChildren(Node n){this.children.add(n);}
    public Node getChildren(int i){return (Node)this.children.get(i);}
    
    public void addValues(double[] val){ values = val; }
    public double[] getValues(){return values;}
    
    public void addAttributes(String[] attr){attributes = attr;}
    public String[] getAttributes(){return attributes;}
    
    public int getNumAttributes(){
    	
    	if(attributes != null){
    		
    		numAttributes = attributes.length;
    		return numAttributes;
    	}
    	else
    		return 0;
    }
          
    
}
