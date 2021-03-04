package sudoku;

public class Main{
    public static void main(String []args){
        
        SudokuGeneticAlgorithm ga = new SudokuGeneticAlgorithm(10); 
           
        System.out.println("First Solution");
        System.out.println(ga.getFirst());
        System.out.println("Score");
        System.out.println(ga.getFirstScore());
        
        System.out.println("\n"+"Finding the best solution, wait...");
        
        ga.generateDefaultPopulation(300);
        ga.run(60000);
        
        System.out.println("Best Solution");
        System.out.println(ga.getBest());
        System.out.println("Score");
        System.out.println(ga.getBestScore());
    }
}
