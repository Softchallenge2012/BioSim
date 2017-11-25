import processing.core.PApplet;

import java.text.DecimalFormat;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.data.Table;
import processing.data.TableRow;
import processing.core.*;



public class BayesianNetworkSimulation extends PApplet {

	
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
	

	  double RNAP = 0.0;
	  double Kd = 0.0;
	  double Kr = 0.0;
	  double K0 = 0.0;
	  double k0 = 0.0;
	  double nc = 0.0;
	  double np = 0.0;
	  
	String[][] tblVariables = BayesianNetworkFrame.tblVariables;
	String[] fromList = BayesianNetworkFrame.fromList;
	String[] toList = BayesianNetworkFrame.toList;
	java.util.ArrayList initConcentrations = BayesianNetworkFrame.initConcentrations;
	java.util.ArrayList configs = BayesianNetworkFrame.configs;
	java.util.ArrayList configRates = BayesianNetworkFrame.configRates;
	
	double total = 0.0;
	double aTotal = 0.0;
	double threshold = 20;
	int gS0 = 0;
	int rareSpeciesIndex = -1;
	int[][] adjMatrix;	
	
	float[] updateConcentrations;
	double[] f = BayesianNetworkFrame.f;
	double[] rateChangesODE ;	
	double[] rateChangesSSA ;
	double[] rateChangesIS;	
	
	private java.util.ArrayList stack = new java.util.ArrayList();
	

	
	
  

	
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
		     

		     
		     staticView.image(drawCurves(width-side*10, height-y_draw-side-side*10), 0+side*5, y_draw+side+side*5);
		     
		     staticView.text("0", 0+side*5, y_draw+side+side*5-2);
		     

		     staticView.line(0+side*5, y_draw+side +side*5, width-side*5, y_draw+side+side*5);
		     float x1 = 0+side*5;
		     float y1 = y_draw+side+side*5;
		     float x2 = width-side*5;
		     float y2 = y_draw+side+side*5;
		     staticView.pushMatrix();
		     staticView.translate(x2, y2);
			 float a = atan2(x1-x2, y2-y1);
			 staticView.rotate(a);
			 staticView.line(0, 0, -5, -5);
			 staticView.line(0, 0, 5, -5);
			 staticView.popMatrix();
			 staticView.text("Time", x2-15, y2+15);
			  
		     staticView.line(0+side*5, y_draw+side +side*5, 0+side*5, height-side*5);
		     x1 = 0+side*5;
		     y1 = y_draw+side+side*5;
		     x2 = 0+side*5;
		     y2 = height-side*5;
		     staticView.pushMatrix();
		     staticView.translate(x2, y2);
			 a = atan2(x1-x2, y2-y1);
			 staticView.rotate(a);
			 staticView.line(0, 0, -5, -5);
			 staticView.line(0, 0, 5, -5);
			 staticView.popMatrix();
			 staticView.text("Concentration",x2+15,y2-15);
		     
		     
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
	
	public PGraphics drawCurves(int x, int y){
				  
		  for(int i = 0; i < tblVariables.length; i++){
		  	if(tblVariables[i][0]!= null){
		  	if(tblVariables[i][0].equals("RNAP"))
		  		RNAP = Double.parseDouble(tblVariables[i][1]);
		  	else if(tblVariables[i][0].equals("kd"))
		  			Kd = Double.parseDouble(tblVariables[i][1]);
		  	else if(tblVariables[i][0].equals("Kr"))
		  		Kr = Double.parseDouble(tblVariables[i][1]);
		  	else if(tblVariables[i][0].equals("K0"))
		  		K0 = Double.parseDouble(tblVariables[i][1]);
		  	else if(tblVariables[i][0].equals("k0"))
		  		k0 = Double.parseDouble(tblVariables[i][1]);
		  	else if(tblVariables[i][0].equals("nc"))
		  		nc = Double.parseDouble(tblVariables[i][1]);
		  	else if(tblVariables[i][0].equals("np"))
		  		np = Double.parseDouble(tblVariables[i][1]);
		  	}
		  }
		  
		  rateChangesODE = new double[BayesianNetworkFrame.rateChanges.length];
		  rateChangesIS = new double[BayesianNetworkFrame.rateChanges.length];
		  for(int i =0; i < BayesianNetworkFrame.rateChanges.length; i++){
			  
			  rateChangesODE[i]= BayesianNetworkFrame.rateChanges[i];
			  rateChangesIS[i]= BayesianNetworkFrame.rateChanges[i];
		  }

		  // repeat simulation for 10 times
		  
		  updateConcentrations = new float[initConcentrations.size()];
		  for(int i = 0; i < initConcentrations.size(); i++){
			  updateConcentrations[i] = Float.parseFloat(initConcentrations.get(i).toString());
		  }
		  
		  PGraphics graph = createGraphics(x, y, JAVA2D);
		  graph.beginDraw();
		  graph.background(colors[1][0], colors[1][1], colors[1][2]);
		  graph.stroke(colors[2][0], colors[2][1], colors[2][2]);
		  graph.fill(colors[0][0], colors[0][1], colors[0][2]);	
		  
		  float[] floatX = new float[updateConcentrations.length];
		  float[] floatY = new float[ updateConcentrations.length];
		  float intervalX= 50;
		  
		  for(int i = 0; i < updateConcentrations.length; i++){
			  
			  floatY[i] = updateConcentrations[i];	
			  floatX[i] = 0.0f;
		  }
		  

		  
		  for(int i = 0; i < 15; i++){
		  	// draw diagram with current concentration
			graph.textAlign(LEFT,CENTER);
			graph.textSize(10);
			graph.ellipseMode(RADIUS);
			for(int j = 0; j < updateConcentrations.length; j++){
				if((floatY[j]*5 +graph.height/10)>0 && (floatY[j]*5+graph.height/10)<height){
					graph.line(floatX[j], (floatY[j]*5 +graph.height/10), (intervalX*i), (updateConcentrations[j]*5  + graph.height/10));
					graph.ellipse((intervalX*i), (updateConcentrations[j]*5  + graph.height/10),5,5);
					graph.text(fromList[j]+":"+(updateConcentrations[j] ), intervalX*i+side*2, (updateConcentrations[j]*5 +graph.height/10+side*2));
					floatY[j] = updateConcentrations[j];
					floatX[j] = intervalX * i;
				}
			}

			
		    
			
			simulateODE();
		  }
		  
		  
		  
		  updateConcentrations = new float[initConcentrations.size()];
		  for(int i = 0; i < initConcentrations.size(); i++){
			  updateConcentrations[i] = Float.parseFloat(initConcentrations.get(i).toString());
		  }
	    	
	    	
	    	for(int i = 0; i < updateConcentrations.length; i++){
	    		total += updateConcentrations[i];
	    	}

	    	adjMatrix = new int[toList.length][fromList.length];
	    	for(int i = 0; i < toList.length; i++)
	    		for(int j = 0; j < fromList.length; j++)
	    			adjMatrix[i][j] = 0;
	    	
	    	for(int i = 0; i < toList.length; i++){
	    		for(int j = 0; j < toList.length; j++){
	    			if(fromList[i].equals(toList[j])){
	    				adjMatrix[i][j] = 1;
	    			}
	    		}
	    	}
	    	

		 for(int i = 0; i < updateConcentrations.length; i++){
				  
				  floatY[i] = updateConcentrations[i];	
				  floatX[i] = 0.0f;
		 }
	    	
	    	
		  for(int i = 0; i < 15; i++){
			  	// draw diagram with current concentration
				graph.textAlign(LEFT,CENTER);
				graph.textSize(10);
				
				for(int j = 0; j < updateConcentrations.length; j++){
					if((floatY[j]*5+graph.height/10)>0 && (floatY[j]*5+graph.height/10)<height){
						graph.line(floatX[j], (floatY[j]*5+graph.height/10), (intervalX*i), (updateConcentrations[j]*5 + graph.height/10));
						graph.rect((intervalX*i), (updateConcentrations[j]*5 + graph.height/10),5,5);
						graph.text(fromList[j]+":"+(updateConcentrations[j]), intervalX*i+side*2, (updateConcentrations[j]*5+graph.height/10+side*2));
						floatY[j] = updateConcentrations[j];
						floatX[j] = intervalX * i;
					}
				}

				simulateIS();

				
			  }
		  
		  
		  
		  graph.text("ODE"+":", graph.width-side*30, graph.height-side*10);
		  graph.ellipse(graph.width-side*20, graph.height-side*10, 5, 5);
		  graph.text("ODEIS"+":", graph.width-side*30, graph.height-side*7);
		  graph.rect(graph.width-side*20, graph.height-side*6, 5, 5);
		  graph.text("Rare Event:"+fromList[rareSpeciesIndex]+"->"+toList[rareSpeciesIndex], graph.width-side*30, graph.height-side*4);
		
		  graph.endDraw();
		  return graph;
		}
	
		private void simulateODE(){


		  	// update rates and concentration
		  	for(int j = 0; j < configRates.size(); j++){
		  		f[j] = k0 * K0 * RNAP * Double.parseDouble(configRates.get(j).toString()) /(1+ K0 * RNAP  + Kr * java.lang.Math.pow(updateConcentrations[j],nc));
		  	}

		  	for(int j = 0; j < toList.length; j++){
		      		// get species concentration
		      		double sRate = 0.0;
		      		for(int m = 0; m < tblVariables.length; m++){
		      			if(tblVariables[m][0]!= null && tblVariables[m][0].equals(toList[j])){
		      				sRate = Double.parseDouble( tblVariables[m][1] );
		      			}
		      		}
		      		rateChangesODE[j] = np * f[j] - Kd * sRate;    		
		      }

		      for(int m = 0; m < updateConcentrations.length; m++){
		      	for(int n = 0; n < toList.length; n++){
		      		if(toList[n].equals(fromList[m])){		      			
		      			updateConcentrations[m] += rateChangesODE[n];
		      		}

		      	}

		      }
		}
		
		private void simulateIS(){

    		aTotal = 0; 
        	for(int i = 0; i < updateConcentrations.length; i++){
        		aTotal += updateConcentrations[i];
        	}
        	
        	
        	System.out.println("Total Concentration = "+aTotal);
        	
        	// split: delete some reactions
        	
        	
        	if(Math.abs(total - aTotal) > threshold){
        		double average = 0;
        		for(int i = 0; i < updateConcentrations.length; i++){
        			average += (Double.parseDouble(initConcentrations.get(i).toString()) - updateConcentrations[i])/Double.parseDouble(initConcentrations.get(i).toString());
        			
        		}
        		average = average / toList.length;
        		
        		for(int i = 0; i < toList.length; i++){
        			
        			if(average > (Double.parseDouble(initConcentrations.get(i).toString()) - updateConcentrations[i])/Double.parseDouble(initConcentrations.get(i).toString())){
        				// since the change is slow, cut connection
        				for(int j = 0; j < toList.length; j++){
        					if(fromList[i].equals(toList[j])){
        						adjMatrix[i][j] = 0;
        						configRates.set(i, 0);
        					}
        					
        				}
        			}
        		}
        		
        	}
        		
        		
	    	for(int i = 0; i < configRates.size(); i++){
	    		double fromSpecies = updateConcentrations[i];
	    		f[i] = k0 * K0 * RNAP * Double.parseDouble( configRates.get(i).toString()) /(1+ K0 * RNAP  + Kr * java.lang.Math.pow(fromSpecies,nc));    		
	    	}
	    	
	    	
	    	for(int j = 0; j < toList.length; j++){
	      		// get species concentration
	    		rateChangesIS[j] = 0.0;
	      		double sRate = 0.0;
	      		
	      		for(int n = 0; n < toList.length; n++){
	      			if(fromList[j].equals(toList[n])){
	      				
	      				if(adjMatrix[j][n] == 1){
	      					for(int m = 0; m < tblVariables.length; m++){
	      		      			if(tblVariables[m][0]!= null && tblVariables[m][0].equals(toList[j])){
	      		      				sRate = Double.parseDouble( tblVariables[m][1] );
	      		      			}
	      		      		}
	      		      		rateChangesIS[j] = np * f[j] - Kd * sRate;    		
	      				}
	      			}
	      		}
	      		
	      		
	    	}
	    	
	    	for(int i = 0 ; i < toList.length; i++){    		
	    		
	    		System.out.println("Rate Change: d["+toList[i]+"]/dt = "+rateChangesIS[i]+", from "+fromList[i]+", rate="+updateConcentrations[i]);  
	    		
	    		
	    	}
	    	
	    	for(int i = 0; i < fromList.length; i++){
	    		
	    		System.out.println("f["+fromList[i]+"]="+f[i]);
	    	}
	    	
	    	// pick rare event
	    	double min = 9999;
	    	if(rareSpeciesIndex ==-1){
		    	for(int i = 0; i < rateChangesIS.length; i++){
		    		if(Math.abs(rateChangesIS[i])>0 && min>Math.abs(rateChangesIS[i])){
		    			min = Math.abs(rateChangesIS[i]);
		    			rareSpeciesIndex = i;
		    		}
		    	}	    	
	    	}
	    	
	    	int[] visited = new int[toList.length];
	    	int[] g = new int[toList.length];
	    	int[] relativeFunction = new int[toList.length];
	    	
	    	g[rareSpeciesIndex] = 0;
	    	push(rareSpeciesIndex);
	    	while(stack.size()>0){
	    		
	    		int s = pop();
	    		for(int i = 0; i < adjMatrix[s].length; i++){
	    			int sPrime;
	    			if(adjMatrix[s][i] == 1){
	    				sPrime = i;
	    				if(visited[sPrime]!=1){
	    					visited[sPrime] = 1;
	    					g[sPrime] = g[s] + 1;
	    					push(sPrime);
	    				}
	    			}
	    		}
	    		
	    	}
	    	
	    	if(gS0 ==0){
		    	for(int i = 0; i < g.length; i++){
		    		if(gS0 < g[i]){
		    			gS0 = g[i];
		    		}
		    	}
		    	
	
		    	gS0 += 1;
	    	
	    	}
	    	
	    	for(int i = 0; i < visited.length; i++){
	    		if(visited[i]!=1){
	    			g[i] = gS0;
	    		}
	    		
	    	}
	    	
	    	for(int i = 0; i < relativeFunction.length; i++){    		
	    		relativeFunction[i] = gS0 - g[i];    		
	    	}
	    	
	    	
	    	for(int i = 0; i < rateChangesIS.length; i++){
	    		rateChangesIS[i] = rateChangesIS[i] * relativeFunction[i];
	    	}
	    	
	    	
	    	
	    	for(int i = 0; i < updateConcentrations.length; i++){
	    		
	    		for(int j = 0; j < toList.length; j++){
	    			if(toList[j].equals(fromList[i])){
	    				//double rate = rateChangesIS[j];
	    				updateConcentrations[i] += (float)rateChangesIS[j];
	    			}
	    		}
	    	}
	    	
			
		}
		
		private void push(int i){
			stack.add(i);
		}
		
		private int pop(){
			
			int s = Integer.parseInt( stack.get(0).toString());
			int i = 0;
			for(i = 1; i < stack.size(); i++){
				
				stack.set(i-1, stack.get(i) );
			}
			stack.remove(i-1);
			return s;
		}
	
	

}