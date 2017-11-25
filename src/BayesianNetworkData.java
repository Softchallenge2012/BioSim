import processing.core.PApplet;

import java.text.DecimalFormat;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.data.Table;
import processing.data.TableRow;
import processing.core.*;



public class BayesianNetworkData extends PApplet {

	
	Table table ;
	int workingTableIndex ;
	PGraphics imageView = null;
	PGraphics staticView = null;
	String filepath;
	int x_width = 0;
	int y_height = 0;
	int attrCount = 0;
	int x_draw = 0;
	int y_draw = 0;
	int side = 5;
	static int[][] colors ;

	
	public void settings(){
	  size(800,600,JAVA2D);
	  filepath = BayesianNetworkFrame.dataFileName;
	  table= loadTable(filepath,"header");
	}
	
	public void setup(){
		
		x_width = width -10;
		y_height = height -10;
		attrCount = table.getColumnCount();
		x_draw = x_width/attrCount;
		y_draw = y_height/attrCount;
		side = 5;	 

		colors = new int [][]{
		    {0, 0, 0},
		    {128, 128, 128},
		    {255, 255, 255}
		  };
		
	}
	
	
	public void draw() {

		if(staticView == null){
			 staticView = createGraphics(width,height,JAVA2D);
		     staticView.beginDraw();
		     staticView.background(colors[1][0], colors[1][1], colors[1][2]);
		     staticView.stroke(colors[2][0], colors[2][1], colors[2][2]);
		     staticView.fill(colors[2][0], colors[2][1], colors[2][2]);

		     for(int i = 0; i < attrCount; i++){
		    	 
		    	 staticView.image(drawGraph(x_draw, y_draw, 0, i),side+x_draw*i, side+0);
		    	 staticView.image(drawLabel(x_draw, y_draw, i), side + x_draw*i, side+0);
		     }
		     
		     for(int i = 0; i < attrCount; i++){
				 staticView.line(side+x_draw*i,side+0,side+(i+1)*x_draw,side+ 0);
				 staticView.line(side+x_draw*i,side+y_draw,side+(i+1)*x_draw,side+y_draw);
				 staticView.line(side+i*x_draw,side+0,side+i*x_draw, side+y_draw);
					 
			 }
		     staticView.line(0+side, 0+side, 0+side, height-side);
		     staticView.line(width-side, 0+side, width-side, height-side);
		     staticView.line(0+side, height-side, width-side, height-side);
		     
		     
		     staticView.endDraw();
			    
		      
		      
		    if(imageView == null){
		      imageView = createGraphics(width,height,JAVA2D);
		      imageView.beginDraw();
		      imageView.image(staticView, 0,0);
		      imageView.fill(colors[2][0], colors[2][1], colors[2][2]);		      
		      imageView.textSize(14);
		      imageView.textAlign(CENTER,TOP);
		      //imageView.text(table.getColumnTitle(x_table_index)+" vs "+table.getColumnTitle(y_table_index), init_x + x_draw/2, init_y + y_draw + 10);
		      imageView.endDraw();
		    }
		    image(imageView, 0, 0);
	     
		}
			

	}
	

	public PGraphics drawLabel(int x, int y, int idx){
	  PGraphics graph = createGraphics(x,y,JAVA2D);
	  graph.beginDraw();
	  graph.textAlign(CENTER,CENTER);
	  graph.fill(colors[0][0], colors[0][1], colors[0][2]);
	  graph.textSize(10);
	  graph.text(table.getColumnTitle(idx), graph.width/2, side);
	
	  graph.endDraw();
	  return graph;
	}
	
	public PGraphics drawGraph(int x, int y, int x_table_index, int y_table_index){
		
		for(int i = 0; i < BayesianNetworkFrame.matrix.size();){
			Node aRow = (Node)BayesianNetworkFrame.matrix.get(i);
			if(aRow.getDelete()){				
				table.removeRow(i);
				BayesianNetworkFrame.matrix.remove(i);
			}
			else{
				 i++;
			}
			
		}
		
		
		
		  float min_x = table.getRow(0).getInt(x_table_index);
		  float max_x = table.getRow(0).getInt(x_table_index); 
		  float min_y = table.getRow(0).getInt(y_table_index);
		  float max_y = table.getRow(0).getInt(y_table_index);
		  float sum = 0;
		  float mean = 0;
		  float std = 0;
		  
		  for(TableRow i : table.rows()){
			    min_x = (i.getFloat(x_table_index) < min_x)?i.getFloat(x_table_index):min_x;
			    max_x = (i.getFloat(x_table_index) > max_x)?i.getFloat(x_table_index):max_x;
			    min_y = (i.getFloat(y_table_index) < min_y)?i.getFloat(y_table_index):min_y;
			    max_y = (i.getFloat(y_table_index) > max_y)?i.getFloat(y_table_index):max_y;
		  }
		  
		  min_x = (float) (min_x * 0.99);
		  max_x = (float) (max_x * 1.01);
		  min_y = (float) (min_y * 0.99);
		  max_y = (float) (max_y * 1.01);
		  
		  
		  
		
		  PGraphics graph = createGraphics(x, y, JAVA2D);
		  graph.beginDraw();
		  graph.background(colors[1][0], colors[1][1], colors[1][2]);
		  graph.stroke(colors[2][0], colors[2][1], colors[2][2]);
		  graph.fill(colors[0][0], colors[0][1], colors[0][2]);
		  graph.ellipseMode(RADIUS);
		  

		  graph.image(drawLabel(graph.width,graph.height,y_table_index),0, 0);
		  
		
		  float xpos, ypos;
		  for(int i=0; i<table.getRowCount(); i++){
			    xpos = (float) (table.getRow(i).getFloat(x_table_index) - min_x)/(max_x - min_x) * graph.width;
			    ypos = (float) (table.getRow(i).getFloat(y_table_index) - min_y)/(max_y - min_y) * graph.height;
			    Node aRow = (Node)BayesianNetworkFrame.matrix.get(i);
			    
			    
	    		aRow.setX(xpos);
	    		aRow.setY(graph.height-ypos);
			    	
			    
			    BayesianNetworkFrame.matrix.set(i, aRow);
			    
			    graph.ellipse(xpos, graph.height-ypos, 5, 5);
		  }
		  
		  for(int i=0; i<table.getRowCount(); i++){			    
			    ypos = (float) (table.getRow(i).getFloat(y_table_index) - min_y)/(max_y - min_y) * graph.height;
			    sum += ypos ;
		  }
		  mean = sum / table.getRowCount();
		  for(int i=0; i<table.getRowCount(); i++){			    
			    ypos = (float) (table.getRow(i).getFloat(y_table_index) - min_y)/(max_y - min_y) * graph.height;
			    std += (ypos-mean)*(ypos-mean) ;			   
		  }
		  std = (float)Math.sqrt(std/table.getRowCount() );
		  
		  graph.line(0, mean,graph.width, mean);
		  graph.line(0, mean+std,graph.width, mean+std);
		  graph.line(0, mean+std*2,graph.width, mean+std*2);
		  graph.line(0, mean+std*3,graph.width, mean+std*3);
		  graph.line(0, mean-std,graph.width, mean-std);
		  graph.line(0, mean-std*2,graph.width, mean-std*2);
		  graph.line(0, mean-std*3,graph.width, mean-std*3);
		  graph.textAlign(CENTER,CENTER);
		  graph.fill(colors[0][0], colors[0][1], colors[0][2]);
		  graph.textSize(10);
		  graph.text("mean", graph.width/2, mean);

		  
		  
		
		  graph.endDraw();
		  return graph;
		}
	
	public void mouseClicked(){
		for(int i = 0; i < attrCount; i++){
			
			if(mouseY > side && mouseY < (side+y_draw)){				
				if(mouseX > (side + i*x_draw) && mouseX < (side + (i+1)*x_draw)){
					image( drawGraph(x_width-side, y_height-y_draw-side, 0, i),side+1, (side + y_draw+1));
					workingTableIndex = i;
					
				}
			}
		}
		

		if(mouseY > side + y_draw && mouseY < y_height + side){
			if(mouseX > side && mouseX < x_width + side){
				for(int i = 0; i < BayesianNetworkFrame.matrix.size(); ){
					Node aRow = (Node)BayesianNetworkFrame.matrix.get(i);
					double x = aRow.getX()+side;
					double y = aRow.getY()+side + y_draw;
					if(mouseX >( x-10) && mouseX <(x+10) && mouseY >( y-10) && mouseY < (y+10)){
						System.out.println("mouse select"+mouseX+", "+ aRow.getX() +", "+ mouseY +", "+ aRow.getY());
						aRow.setX(0);
						aRow.setY(0);
						aRow.setDelete(true);
						BayesianNetworkFrame.matrix.set(i,aRow);
						image( drawGraph(x_width-side, y_height-y_draw-side, 0, workingTableIndex),side+1, (side + y_draw+1));
						break;
					}
					else{i++;}
					
				}
				
			}
			
		}
	
	}
	

}