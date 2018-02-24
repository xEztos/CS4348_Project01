import java.io.*;
import java.util.Scanner;
import java.lang.Runtime;

public class CPU{
	static int PC;	// program counter
	static int SP;	// stack register (keeps track of a stack call)... Start from 2000 and goes down to 0 but 2000 does not exist?
	static int IR;	// instruction register (holds the instruction currently being decoded)
	static int AC;	// Accumulator (stores intermediate logic and arithmetic results)
	static int X;
	static int Y;

	static boolean userMode;	// true for userMode [default] and false for kernal mode

	static Process memoryProcess;		// memory process
	static InputStream memoryOutput;	// output from the memory
	// static OutputStream memoryInput;	// input into the memory
	static PrintWriter memoryConsole;



	public static void main(String[] args) throws IOException{
		userMode = true;
		PC = 0;
		SP = 2000;
		AC = X = Y = 0;

		System.out.print("Type in the program name: ");
		Scanner sc = new Scanner(System.in);
		File program = null;
		String fileIn = null;
		// try{
			fileIn = sc.nextLine();
			// program = new File(fileIn);
		// } catch (FileNotFoundException e){
		// 	System.out.println("The file " + fileIn + " does not exist. Program is terminating");
		// 	System.exit(0);
		// }

		memoryProcess = Runtime.getRuntime().exec("java Memory");
		OutputStream memoryInput = memoryProcess.getOutputStream();	// CPU -> Memory
		memoryOutput = memoryProcess.getInputStream();	// Memory -> CPU
		memoryConsole = new PrintWriter(memoryInput);
		memoryConsole.println(fileIn);
		memoryConsole.flush();


		while(true){
			// System.out.println("PC: " + PC);
			IR = memoryRead(PC);
			// System.out.println("IR: " + IR);
			// System.out.println("X: " + X);
			// System.out.println("Y: " + Y);
			// System.out.println("AC: " + AC);
			processInstruction();
		}

	}

	/**
	 *	Interfaces with the memory module and requests a read from the memory
	 *	@param memAddress the address of the memory to read from
	 *	@return the integer data that the memory at memAddress contains
	 */
	public static int memoryRead(int memAddress){
		checkMemoryOutofBounds(memAddress);
		memoryConsole.println(memAddress);
		memoryConsole.flush();

		Scanner sc = new Scanner(memoryOutput);
		if(sc.hasNextLine()){
			return Integer.parseInt(sc.nextLine());
		}
		return -1;
	}

	/**
	 *	Interfaces with the memory module and requests a write to the memory
	 *	@param memAddress the memory address to write the data to
	 *	@param data the data to write onto the memory
	 */
	public static void memoryWrite(int memAddress, int data){
		checkMemoryOutofBounds(memAddress);
		memoryConsole.println(memAddress + " " + data);
		memoryConsole.flush();
	}

	public static void processInstruction(){
		switch(IR){
			case 1: AC = memoryRead(++PC);
					// System.out.println("Case: " + IR);
					PC++;
					break;	// Load the value into the AC
			case 2: AC = memoryRead(memoryRead(++PC));
					PC++;
					break;	// Load the value at the address into the AC
			case 3: AC = memoryRead(memoryRead(++PC));
					PC++;
					break;	// Load the value from the address found in the given address into the AC
			case 4: AC = memoryRead(memoryRead(++PC) + X);
					PC++;
					break;	// Load the value at (address + X) into the AC
			case 5: AC = memoryRead(memoryRead(++PC) + Y);
					PC++;
					break;	// Load the value at (Address + Y) into the AC
			case 6: AC = memoryRead(SP+X); 
					PC++;
					break;	// Load from (SP+X) into the AC (if SP is 990, and X is 1, load from 991).
			case 7: memoryWrite(memoryRead(++PC), AC);
					PC++;
					break;	// Store the value in the AC into the address
			case 8: AC = (int)(Math.random() * 100 + 1); 
					PC++;
					break;	// Gets a random int from 1 to 100 into the AC
			case 9: int port = memoryRead(++PC);
					if(port == 1) System.out.print(AC);
					if(port == 2) System.out.print((char)AC);
					PC++;
					break;	// If port=1, write AC as an int to the screen, if port =2, write AC as char onto screen
			case 10: AC += X;
					PC++; 
					break;	// Adds the value in X to the AC
			case 11: AC += Y; 
					PC++;
					break;	// Adds the value in Y to the AC
			case 12: AC -= X; 
					PC++;
					break;	// Subs the value in X from the AC
			case 13: AC -= Y; 
					PC++;
					break;	// Subs the value in Y from the AC
			case 14: X = AC; 
					PC++;
					break;	// Copy the value in the AC to X
			case 15: AC = X; 
					PC++;
					break;	// Copy the value in X to the AC
			case 16: Y = AC; 
					PC++;
					break;	// Copy the value in the AC to Y
			case 17: AC = Y; 
					PC++;
					break;	// Copy the value in Y to the AC
			case 18: SP = AC; 
					PC++;
					break;	// Copy the value in AC to the SP
			case 19: AC = SP; 
					PC++;
					break;	// Copy the value in SP to the AC
			case 20: PC = memoryRead(PC+1);
					break;	// Jump to the address
			case 21: if(AC == 0)
						PC = memoryRead(PC + 1);
					else PC+=2;
					break;	// Jump to the address only if the value in the AC is zero
			case 22: if(AC != 0)
						PC = memoryRead(PC + 1);
					else PC+=2;
					break;	// Jump to the address only if the value in the AC is NOT zero
			case 23: SP++;
					memoryWrite(SP, PC);
					PC = memoryRead(PC + 1);
					break;	// Push return address onto the stack, jump to the address
			case 24: PC = memoryRead(SP--);
					break;	// Pop return address from the stack, jump to the address
			case 25: X++; 
					PC++;
					break;	// Increment the value in X
			case 26: X--; 
					PC++;
					break;	// Decrement the value in X
			case 27: SP++;
					memoryWrite(SP, AC);
					PC++;
					break;	// Push AC onto the stack
			case 28: AC = memoryRead(SP--);
					PC++;
					break;	// Pop from stack into the AC
			case 29: userMode = false;
					PC++;
					break;	// Perform system call
			case 30: userMode = true;
					PC++;
					break;	// Return from system call
			case 50: System.exit(0);	// End execution
			default: System.out.println("Case: default");
					break;
		}
	}

	private static void checkMemoryOutofBounds(int address){
		if(address >= 1000 && userMode){
			System.out.println("Address '" + address + "' Out Of Bounds. System Stack Out of Limits. Process Exiting");
			System.exit(0);
		}
	}
}