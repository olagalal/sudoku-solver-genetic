package sudoku;

import java.util.ArrayList;
import java.util.Random;

public class SudokuGeneticAlgorithm{
    private ArrayList<SudokuBoard> population;  
    private ArrayList<Integer> populationScore;
    private ArrayList<SudokuBoard> matingSelections;  
    private Random rand;
    private int bestGenetics;
    private SudokuBoard original;
    private SudokuBoard firstAttempt;
    private SudokuBoard best;
    private int bestScore;
    
    public SudokuGeneticAlgorithm(int basePopulation){
        population = new ArrayList<>(basePopulation);
        populationScore = new ArrayList<>(basePopulation);
        matingSelections = new ArrayList<>(basePopulation);
        rand = new Random();
        generateDefaultPopulation(basePopulation);
        bestGenetics = 9*9*3;
        bestScore = 0;
    }
    
    public SudokuGeneticAlgorithm(SudokuBoard sb, int basePopulation){
        original = sb;
        population = new ArrayList<>(basePopulation+1);
        populationScore = new ArrayList<>(basePopulation+1);
        matingSelections = new ArrayList<>(basePopulation+1);
        rand = new Random();
        generatePopulation(sb, basePopulation);   
    }
    
    public void run(int generations){
        int count = 0;
    
        scoreFirstGeneration();
        
        while(count < generations && bestScore < bestGenetics){
            //System.out.println("Generation: " + (count+1));
            selection();
            
            //Grab next two couples to mate
            for(int i = 0; i < matingSelections.size(); i+=2){
                crossOver(matingSelections.get(i), matingSelections.get(i+1));
            }
            ++count;
        }
    }
    
    public void generatePopulation(SudokuBoard sb, int basePopulation){
        SudokuBoard firstGen;
        population.add(sb);
        
        for(int i = 0; i < basePopulation; i++){
            firstGen = new SudokuBoard(sb);
            firstGen.fillBlanks(); //Randomly fill
            firstAttempt = firstGen;
            population.add(firstGen);
        }
    }
    
    public void generateDefaultPopulation(int basePopulation){
        SudokuBoard firstGen;
        
        for(int i = 0; i < basePopulation; i++){
            firstGen = new SudokuBoard(true);
            firstGen.fillBlanks(); //Randomly fill
            firstAttempt = firstGen;
            population.add(firstGen);
        }
    }
    
    //Scoring based on row and squares
    public int fitness(SudokuBoard currBoard){
        return scoreRows(currBoard.getBoard()) + scoreCols(currBoard.getBoard()) + scoreSquares(currBoard.getBoard());
    }
    
    //Bubble sort based on score(H -> L)
    private void sortPopulation(){
        int popSize = population.size();
        SudokuBoard temp;
        int scoreTemp;
        
        for(int i = 0; i < popSize; i++){
            for(int j = i+1; j < popSize; j++){
                if(populationScore.get(j) > populationScore.get(i)){
                    //swap
                    temp = population.get(i);
                    scoreTemp = populationScore.get(i);
                    
                    population.set(i, population.get(j));
                    populationScore.set(i, populationScore.get(j));
                    
                    population.set(j, temp);
                    populationScore.set(j, new Integer(scoreTemp));
                }
            }
        }
    }
    
    //Will only select the top 100 boards to mate
    public void selection(){
        matingSelections.clear(); //reset
        sortPopulation();
        
        int limit = (population.size() > 100) ? 100 : population.size();
        
        for(int i = 0; i < limit; i++){
            matingSelections.add(population.get(i));
        }
        
        //Odd coupling check
        if(matingSelections.size() % 2 != 0){
            //Select a random top candidate to mate twice
            matingSelections.add(matingSelections.get(rand.nextInt(matingSelections.size()-1)));
        }
        
        //Shuffle population
        //shufflePopulation();
        
        //remove those past 100 to maintain the top 100 boards
        while(population.size() > 100){
            population.remove(population.size()-1);
            populationScore.remove(population.size()-1);
        }
    }
    
    public void crossOver(SudokuBoard parent1, SudokuBoard parent2){
        int score1, score2;
        SudokuBoard offSpring1 = new SudokuBoard(parent1); 
        SudokuBoard offSpring2 = new SudokuBoard(parent2);
        
        //Crossover genetics of parents
        for(int xPoint = rand.nextInt(9); xPoint < 9; xPoint++){
            for(int yPoint = rand.nextInt(9); yPoint < 9; yPoint++){
                offSpring1.changeValueAt(xPoint, yPoint, parent2.getValueAt(xPoint, yPoint));
                offSpring2.changeValueAt(xPoint, yPoint, parent1.getValueAt(xPoint, yPoint));
            }
        }
        
        mutation(offSpring1);
        mutation(offSpring2);
       
        //Check to see if offsprings are better than the curr best 
        score1 = fitness(offSpring1);
        score2 = fitness(offSpring2);
       
        if(score1 > bestScore){
            bestScore = score1;
            best = offSpring1;
        }
        
        if(score2 > bestScore){
            bestScore = score2;
            best = offSpring2;
        }
       
        //Add offsprings and score to pool
        population.add(offSpring1);
        populationScore.add(score1);
        population.add(offSpring2);
        populationScore.add(score2);
        
        //totalFitness += (score1 + score2);
    }
    
    public void mutation(SudokuBoard sb){
        int col = rand.nextInt(9);
        int row1 = rand.nextInt(9);
        int row2 = rand.nextInt(9);
        
        while(!sb.swap(col, row1, row2)){
            row1 = rand.nextInt(9);
            row2 = rand.nextInt(9);
        }
    }
    
    private void scoreFirstGeneration(){
        int currScore;
        for(int i = 0; i < population.size(); i++){
           currScore = fitness(population.get(i));
           
           if(currScore > bestScore){
                bestScore = currScore;
                best = population.get(i);
           }
           
           populationScore.add(new Integer(currScore));
        }
    }
    
    public SudokuBoard getOriginal(){
        return original;
    }
    
    public SudokuBoard getFirst(){
        return firstAttempt;
    }
    
    public int getFirstScore(){
        return (scoreRows(firstAttempt.getBoard()) + scoreCols(firstAttempt.getBoard()) +scoreSquares(firstAttempt.getBoard()));
    }
    
    public SudokuBoard getBest(){
        return best;
    }
    
    public int getBestScore(){
        return bestScore;
    }

    private int scoreRows(int [][]board){
        int []duplicates;
        int score = 0;
        
        for(int row = 0; row < board.length; row++){
		
		    //index zero will be use to state a duplicate was found
		    duplicates = new int[10]; //reset
		
		    for(int col = 0; col < board[row].length; col++){
		        duplicates[board[row][col]]++;
		    }
		
		    //plus one for each non-duplicated number
		    for(int x = 1; x < duplicates.length; x++){
		        if(duplicates[x] == 1){
		            ++score;
		        }
	        }
	    }
	    return score;
    }
    
    private int scoreCols(int [][]board){
        int []duplicates;
        int score = 0;
        
        for(int col = 0; col < board.length; col++){
            
            duplicates = new int[10]; //reset
            
            for(int row = 0; row < board[col].length; row++){
                duplicates[board[row][col]]++;
            }
            
            for(int x = 1; x < duplicates.length; x++){
                if(duplicates[x] == 1){
                    ++score;
                }
            }
        }
        return score;
    }

    private int scoreSquares(int [][]board){
        int []duplicates;
        int score = 0;
        int row = 0;
	    int col = 0;
	    int lim1 = row + 3;
	    int lim2 = col + 3;
	
	    while(row < board.length){
	    	duplicates = new int[10];
		
		    while(row < lim1){
			    col = lim2 - 3;
			    while(col < lim2){
				    duplicates[board[row][col]]++;
				    ++col;
			    }
			    ++row;
		    }
		
		    //plus one for each non-duplicated number
		    for(int x = 1; x < duplicates.length; x++){
		        if(duplicates[x] == 1){
		            ++score;
		        }
	        }
		
		    if(lim2 < board.length){
			    row = lim1 - 3;
			    col = lim2;
			    lim2 += 3;
		    }
		    else{
			    col = 0;
			    lim2 = 3;
			    row = lim1;
			    lim1 = row + 3;
		    }
	    }
	    return score;
    }
}
