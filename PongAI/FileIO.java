import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class FileIO {

	
	public static void save_population(Population pop) throws IOException {
		FileWriter file = new FileWriter("weights.txt");
		BufferedWriter buffer = new BufferedWriter(file);
		for(int i = 0; i < pop.SIZE; i++) {
			buffer.write("AI #" + i + " Generation " + pop.population[i].generation);
			for(int j = 0; j < pop.population[i].weights1.length; j++) {
				for(int k = 0; k < pop.population[i].weights1[0].length; k++) {
					buffer.write(Float.toString(pop.population[i].weights1[j][k]) + " ");
				}
				buffer.newLine();
			}
			buffer.newLine();
		}
	}
}
