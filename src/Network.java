
public class Network {
	private String[] attributes;
	private int[][] adjacentMatrix;
	private java.util.ArrayList stack = new java.util.ArrayList();
	
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
	
	
	public void simulationODE(){
    			
		java.util.ArrayList reactions = new java.util.ArrayList();
		java.util.ArrayList species = new java.util.ArrayList();
		try{
			
			java.io.BufferedReader input = new java.io.BufferedReader(new java.io.FileReader("data/arcsUpdate.csv"));
			String line = input.readLine();
			line = input.readLine();
			
			while(line!= null){
				
				String[] nodes = line.split(",");
				reactions.add("(P+RNAP+"+nodes[1]+"->"+nodes[2]+")");
				if(!species.contains(nodes[1]))
					species.add(nodes[1]);
				else if(!species.contains(nodes[2]))
					species.add(nodes[2]);
				
				line = input.readLine();
			}
			input.close();
			
			
		}catch(java.io.IOException ioe){			
			System.out.println(ioe.getMessage());
		}
		
		
    	String[][] tblVariables = new String[species.size()+reactions.size()][2];    	
  
    	
		int k = 0;
		for(int j = 0; j < species.size(); j++){
			tblVariables[j][0] = species.get(j).toString();    	
			tblVariables[j][1] = "10";
			k++;
		}
		
		tblVariables[1][1]="20";
		tblVariables[2][1]="50";
		tblVariables[3][1]="70";
		tblVariables[4][1]="80";
		tblVariables[5][1]="100";
		tblVariables[6][1]="120";
		tblVariables[7][1]="140";
		tblVariables[8][1]="180";
		

		for(int j = 0; j < reactions.size(); j++){
			tblVariables[k][0] = reactions.get(j).toString();    	
			tblVariables[k][1] = "10";
			k++;
		}
		
    		

        String[] fromList;
        String[] toList;
        java.util.ArrayList configs;
        java.util.ArrayList configRates;
        java.util.ArrayList initConcentrations;
        double[] f;
        double[] rateChanges;
    	
    	double RNAP = 30.0;
    	double Kd = 0.0075;
    	double Kr = 0.25;
    	double K0 = 0.033;
    	double k0 = 0.05;
    	double nc = 2.0;
    	double np = 10.0;    	
    	
    	
    	configs = new java.util.ArrayList();
    	configRates = new java.util.ArrayList();
    	for(int i = 0; i < tblVariables.length; i++){
    		if(tblVariables[i][0]!= null && tblVariables[i][0].contains("(")){
    			configs.add(tblVariables[i][0]) ;
    			configRates.add( tblVariables[i][1]);
    		}
    	}
    	
    	f = new double[configs.size()];
    	
    	double fromSpecies = 0.0;
    	//java.util.ArrayList initConcentrations = new java.util.ArrayList();
    	initConcentrations = new java.util.ArrayList();
    	//String[] fromList = new String[configs.size()];
    	fromList = new String[configs.size()];
    	//String[] toList = new String[configs.size()];
    	toList = new String[configs.size()];
    	
    	for(int i = 0; i < configs.size(); i++){
    		
    		String p = configs.get(i).toString();
    		String s1 = p.split("->")[0];
    		String s2 = p.split("->")[1];
    		String[] s1Array = s1.split("\\+");
    		s1 = s1Array[s1Array.length-1];
    		s2 = s2.substring(0, s2.length()-1);
    		fromList[i] = s1;
    		toList[i] = s2;
    		
    		for(int j =0; j < tblVariables.length; j++){
    			if(tblVariables[j][0]!= null && tblVariables[j][0].equals(s1)){
    				fromSpecies = Double.parseDouble(tblVariables[j][1]);
    				initConcentrations.add(fromSpecies);
    			}    			
    		}    		
    		
    		
    	}
    	
    	java.util.ArrayList updateConcentrations = new java.util.ArrayList();
    	for(int i = 0; i < initConcentrations.size(); i++){
    		updateConcentrations.add(initConcentrations.get(i));
    	}
    	
    	double total = 0.0;
    	double aTotal = 0.0;
    	double threshold = 50;
    	int gS0 = 0;
    	
    	
    	for(int i = 0; i < updateConcentrations.size(); i++){
    		total += Double.parseDouble(updateConcentrations.get(i).toString());
    	}
    	

    	int[][] adjMatrix = new int[toList.length][fromList.length];
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
    	
    	
    	
    	for(int repeat = 0; repeat < 15; repeat++){
    	
    		
    		aTotal = 0; 
        	for(int i = 0; i < updateConcentrations.size(); i++){
        		aTotal += Double.parseDouble(updateConcentrations.get(i).toString());
        	}
        	
        	
        	System.out.println("Total Concentration = "+aTotal);
        	
        	// split: delete some reactions
        	
        	
        	if(Math.abs(total - aTotal) > threshold){
        		double average = 0;
        		for(int i = 0; i < updateConcentrations.size(); i++){
        			average += (Double.parseDouble(initConcentrations.get(i).toString()) - Double.parseDouble(updateConcentrations.get(i).toString()))/Double.parseDouble(initConcentrations.get(i).toString());
        			
        		}
        		average = average / toList.length;
        		
        		for(int i = 0; i < toList.length; i++){
        			
        			if(average > (Double.parseDouble(initConcentrations.get(i).toString()) - Double.parseDouble(updateConcentrations.get(i).toString()))/Double.parseDouble(initConcentrations.get(i).toString())){
        				// since the change is slow, cut connection
        				for(int j = 0; j < toList.length; j++){
        					if(fromList[i].equals(toList[j])){
        						adjMatrix[i][j] = 0;
        					}
        					
        				}
        			}
        		}
        		
        	}
        	
        	
        	
        	
        		
        		
    	for(int i = 0; i < configRates.size(); i++){
    		fromSpecies = Double.parseDouble(updateConcentrations.get(i).toString());
    		f[i] = k0 * K0 * RNAP * Double.parseDouble( configRates.get(i).toString()) /(1+ K0 * RNAP  + Kr * java.lang.Math.pow(fromSpecies,nc));    		
    	}
    	
    	rateChanges = new double[configs.size()];
    	
    	
    	for(int j = 0; j < toList.length; j++){
      		// get species concentration
    		rateChanges[j] = 0.0;
      		double sRate = 0.0;
      		
      		for(int n = 0; n < toList.length; n++){
      			if(fromList[j].equals(toList[n])){
      				
      				if(adjMatrix[j][n] == 1){
      					for(int m = 0; m < tblVariables.length; m++){
      		      			if(tblVariables[m][0]!= null && tblVariables[m][0].equals(toList[j])){
      		      				sRate = Double.parseDouble( tblVariables[m][1] );
      		      			}
      		      		}
      		      		rateChanges[j] = np * f[j] - Kd * sRate;    		
      				}
      			}
      		}
      		
      		
    	}
    	
    	for(int i = 0 ; i < toList.length; i++){    		
    		
    		System.out.println("Rate Change: d["+toList[i]+"]/dt = "+rateChanges[i]+", from "+fromList[i]+", rate="+updateConcentrations.get(i));  
    		
    		
    	}
    	
    	for(int i = 0; i < fromList.length; i++){
    		
    		System.out.println("f["+fromList[i]+"]="+f[i]);
    	}
    	
    	// pick rare event
    	int rareSpeciesIndex = 0;
    	double min = 9999;
    	for(int i = 0; i < rateChanges.length; i++){
    		if(Math.abs(rateChanges[i])>0 && min>Math.abs(rateChanges[i])){
    			min = Math.abs(rateChanges[i]);
    			rareSpeciesIndex = i;
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
    	
    	
    	for(int i = 0; i < rateChanges.length; i++){
    		rateChanges[i] = rateChanges[i] * relativeFunction[i];
    	}
    	
    	
    	
    	for(int i = 0; i < updateConcentrations.size(); i++){
    		
    		for(int j = 0; j < toList.length; j++){
    			if(toList[j].equals(fromList[i])){
    				double rate = Double.parseDouble(updateConcentrations.get(i).toString());
    				rate = rate + rateChanges[j];
    				updateConcentrations.set(i, rate);
    			}
    		}
    	}
    	
    	
    	
    	}
    	
    	
    	
    	
    }
	
	public static void main(String[] args){
		
		Network app = new Network();
		app.simulationODE();
	}
	
	

}
