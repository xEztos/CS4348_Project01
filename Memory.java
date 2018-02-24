import java.util.Scanner;
import java.io.*;

public class Memory{
	//  consist of 2000 integer entries, 0-999 for the user program, 1000-1999 for system code
	private static int memory[] = new int[2000];	// max used 1700 (?)

	public static void main(String[] args) throws FileNotFoundException{
		Scanner input = new Scanner(System.in);
		File file = new File(input.nextLine());

		parseFile(file);							// initialize the array
		// System.out.println(MemtoString());			// only used for debugging


		while(true){								// infinite loop for waiting to recieve input
			if(input.hasNextLine()){				// processes one line at a time
				String temp = input.nextLine();
				String[] tempArr = temp.split(" ");

				// If the line submitted only has one number, it is a read request and if two numbers, it is a write request
				if(tempArr.length == 1){
					// if(tempArr[0] != -1)
						System.out.println(read(Integer.parseInt(tempArr[0])));
					// else System.exit(0);

				}
				if(tempArr.length == 2){
					write(Integer.parseInt(tempArr[0]), Integer.parseInt(tempArr[1]));
				}
			}
		}
	}

	/**
	 *	Returns the memory value at a specified memory address
	 *	@reutrn the memory value at a specified memory address
	 */
	public static int read(int memAddress){
		return memory[memAddress];
	}

	/**
	 *	Writes a specified memory value to a specified memory address
	 *	@param memAddress the specified memory address to write the data to
	 *	@param data the integer data to write into the specified memory address
	 */
	public static void write(int memAddress, int data){
		memory[memAddress] = data;
	}

	/**
	 *	Parses an instruction file and writes the data parsed into the global memory array given the file name
	 *	@param file the file to be parsed for intruction and data values
	 *	@exception FileNotFoundException
	 */
	static void parseFile(File file) throws FileNotFoundException{
		Scanner parser = new Scanner(file);
		int mc = 0; // memory counter and incrementor

		while(parser.hasNextLine()){
			if(parser.hasNextInt())
				memory[mc++] = parser.nextInt();
			else if (parser.hasNextLine()){
				try{									// GET RID OF THIS TRY DEBUG DEBUG DEBUG DEBUG
					String temp = parser.next();
					if(temp.charAt(0) == '.')
						mc = Integer.parseInt(temp.substring(1));
					else parser.nextLine();	// skip to the next line
				} catch (Exception e) {}				// GET RID OF THIS CATCH DEBUGDEBUGDEBUG DEBUG
			}
		}
	}

	/**
	 * Returns the memory array in string form
	 * @return the memory array in string form
	 */
	public static String MemtoString(){
		String ret = "";
		for(int mc = 0; mc < memory.length; mc++){
			ret = ret + String.format("%04d | %-4d%n", mc, memory[mc]);
		}

		return ret;
	}
}