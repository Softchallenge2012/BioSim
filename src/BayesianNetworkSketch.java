import processing.core.PApplet;
import java.text.DecimalFormat;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.data.Table;
import processing.data.TableRow;




public class BayesianNetworkSketch extends PApplet{

		
	int side, x_draw, y_draw, tickMarks;
	PGraphics imageView = null;
	PGraphics staticView = null;
	Table table;
	String filePath;
	static int[][] colors = new int [][]{	  
	    {0, 0, 0},
	    {128, 128, 128},
	    {255, 255, 255}
	  };
	
	float[] xValues;
	float[] yValues;
	
	int editFrom;
	int editTo;
	
	
	public void settings(){
	  size(800,600,JAVA2D);
	}
	
	public void setup(){
		
	  filePath = BayesianNetworkFrame.WorkDirectory+"/"+BayesianNetworkFrame.BaysianNetworkFile;
	  table = loadTable(filePath,"header");
	  

		 int arcCount = table.getRowCount();
		 BayesianNetworkFrame.adjacentMatrix = new int[BayesianNetworkFrame.attrCount][BayesianNetworkFrame.attrCount];
		 for(int i = 0; i < BayesianNetworkFrame.attrCount; i++){
			 for(int j = 0; j < BayesianNetworkFrame.attrCount; j++){
				 BayesianNetworkFrame.adjacentMatrix[i][j] = 0;
			 }
		 }
		 
		  for(TableRow i : table.rows()){
			   String attrFrom = i.getString(1);
			   String attrTo = i.getString(2);
			   int fromIndex = 0;
			   int toIndex = 0;
			   for(int j = 0; j < BayesianNetworkFrame.attrCount; j++){
				   
				   if(attrFrom.equalsIgnoreCase(BayesianNetworkFrame.attrNames[j])){
					   fromIndex = j;
				   }
			   }

			   for(int j = 0; j < BayesianNetworkFrame.attrCount; j++){
				   
				   if(attrTo.equalsIgnoreCase(BayesianNetworkFrame.attrNames[j])){
					   toIndex = j;
				   }
			   }
			   
			   BayesianNetworkFrame.adjacentMatrix[fromIndex][toIndex] = 1;

		  }
	  
	  side = 5;
	  x_draw = (width - 10);
	  y_draw = (height - 10);
	  tickMarks = 2;
	  
	  
	 
	}
	
	public void draw() {
		if(staticView == null){
			 staticView = createGraphics(width,height,JAVA2D);
		     staticView.beginDraw();
		     staticView.background(colors[1][0], colors[1][1], colors[1][2]);
		     staticView.stroke(colors[2][0], colors[2][1], colors[2][2]);
		     staticView.fill(colors[2][0], colors[2][1], colors[2][2]);


		     staticView.image(drawGraph(x_draw, y_draw),side, side);
		     staticView.image(drawLabel(x_draw,y_draw/8,"Bayesian Network Model"),side, side);
		     
		     staticView.line(0+side, 0+side, 0+side, height-side);
		     staticView.line(width-side, 0+side, width-side, height-side);
		     staticView.line(0+side, 0+side, width-side, 0+side);
		     staticView.line(0+side, height-side, width-side, height-side);		     
		     
		     staticView.endDraw();
			    
		      
		      
		    if(imageView == null){
		      imageView = createGraphics(width,height,JAVA2D);
		      imageView.beginDraw();
		      imageView.image(staticView, 0,0);
		      imageView.fill(colors[2][0], colors[2][1], colors[2][2]);		      
		      imageView.endDraw();
		    }
		    image(imageView, 0, 0);
	     
		}
	 
	}
	

	
	public void mousePressed(){
		editFrom = -1;
		for(int i = 0; i < BayesianNetworkFrame.attrCount; i++){
			
			if(mouseX >(xValues[i]-25) && mouseX < (xValues[i] + 25) && mouseY>(yValues[i]-25) && mouseY <(yValues[i]+25)){
				editFrom = i;
			}
		}
		
	}
	
	public void mouseReleased(){
		editTo = -1;
		for(int i = 0; i < BayesianNetworkFrame.attrCount; i++){
			
			if(mouseX >(xValues[i]-25) && mouseX < (xValues[i] + 25) && mouseY>(yValues[i]-25) && mouseY <(yValues[i]+25)){
				editTo = i;
			}
		}
		
		if(editFrom !=-1 && editTo !=-1 && editFrom != editTo){
			
			if(BayesianNetworkFrame.adjacentMatrix[editFrom][editTo]==0){
				BayesianNetworkFrame.adjacentMatrix[editFrom][editTo] = 1;
			}
			else{
				BayesianNetworkFrame.adjacentMatrix[editFrom][editTo] = 0;
			}
			
			image(drawGraph(x_draw, y_draw),side, side);
			
		}
	}
	
	
	
	public PGraphics drawLabel(int x, int y, String title){
	  PGraphics graph = createGraphics(x,y,JAVA2D);
	  graph.beginDraw();
	  
	  graph.textAlign(CENTER,CENTER);
	  graph.fill(colors[0][0], colors[0][1], colors[0][2]);
	  graph.textSize(14);
	  graph.text(title, graph.width/2, graph.height/2);
	
	  graph.endDraw();
	  return graph;
	}
	
	
	public PGraphics drawGraph(int x, int y){
	 
	  PGraphics graph = createGraphics(x, y, JAVA2D);
	  graph.beginDraw();
	  graph.background(colors[1][0], colors[1][1], colors[1][2]);
	  graph.stroke(colors[2][0], colors[2][1], colors[2][2]);
	  graph.fill(colors[1][0], colors[1][1], colors[1][2]);
	  graph.ellipseMode(RADIUS);
	
	  float xpos, ypos;
	  xValues = new float[BayesianNetworkFrame.attrCount];
	  yValues = new float[BayesianNetworkFrame.attrCount];
	  for(int i=0; i<BayesianNetworkFrame.attrCount; i++){
	    xpos = (float) (x_draw/4 *Math.cos(360/BayesianNetworkFrame.attrCount * i));
	    ypos = (float) (x_draw/4 *Math.sin(360/BayesianNetworkFrame.attrCount * i));
	    xValues[i]=graph.width/2 + xpos;
	    yValues[i]=graph.height/2+ypos;
	    graph.ellipse(xValues[i], yValues[i], 20, 20);	
	    
	    //graph.text(BayesianNetworkFrame.attrNames[i], graph.width/2 + xpos, graph.height/2+ypos);
	    graph.image(drawLabel(50,50, BayesianNetworkFrame.attrNames[i]), graph.width/2 + xpos, graph.height/2+ypos);
	  }
	  
	  for(int i = 0; i < BayesianNetworkFrame.attrCount; i++){

		  for(int j = 0; j < BayesianNetworkFrame.attrCount; j++){
			
			  if(BayesianNetworkFrame.adjacentMatrix[i][j]!=0){
				  float x1 = xValues[i];
				  float y1 = yValues[i];
				  float x2 = xValues[j];
				  float y2 = yValues[j];
				  graph.line(x1,y1,x2,y2);
				  graph.pushMatrix();
				  graph.translate(x2, y2);
				  float a = atan2(x1-x2, y2-y1);
				  graph.rotate(a);
				  graph.line(0, 0, -5, -5);
				  graph.line(0, 0, 5, -5);
				  graph.popMatrix();
			  }
		  }
	  }
	  
	  
	
	  graph.endDraw();
	  return graph;
	}
	
}

