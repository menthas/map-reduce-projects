import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jan
 */
public class Measurements {

	static final Map map = new HashMap();

	public static void main(String[] args) throws Exception {
		String inputFile = args[0];
		String logFile = args[1];

		readOnly(inputFile, logFile);

		readWrite(inputFile, logFile);

		for (int i = 1; i < 20; i++)
			fibR(i, logFile);
	}

	private static void readOnly(String inputFile, String logFile) throws FileNotFoundException, IOException {
		Timer t = new Timer();
		FileReader fileReader;
		fileReader = new FileReader(inputFile);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		String line;
		while ((line = bufferedReader.readLine()) != null);
		fileReader.close();
		t.stop();
		String message = "J-readonly";
		t.log(logFile, message);
	}

	private static void readWrite(String inputFile, String logFile) throws FileNotFoundException, IOException {
		Timer t = new Timer();
		FileReader fileReader;
		fileReader = new FileReader(inputFile);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		String line;
		BufferedWriter writer = new BufferedWriter(new FileWriter("/tmp/" + System.currentTimeMillis()));
		while ((line = bufferedReader.readLine()) != null)
			writer.write(line);
		writer.close();
		fileReader.close();
		t.stop();
		String message = "J-readWrite";
		t.log(logFile, message);
	}

	/**
	 * @author Google "fibonacci code java"
	 * @param N
	 *            how many values to compute
	 * @return the value
	 */
	public static int fib(int N) {
		return (N == 1 || N == 2) ? 1 : fib(N - 1) + fib(N - 2);
	}

	private static void fibR(int N, String logFile) throws FileNotFoundException, IOException {
		Timer t = new Timer();
		int n = 0;
		for (int i = 0; i < 100000; i++) {
			n += fib(N);
		}
		t.stop();
		String message = "J-fibR-" + N;
		t.log(logFile, message);
	}
}