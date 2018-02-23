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
	static OutputStream memoryInput;	// input into the memory

	static PrintWriter pw;
	static InputStream is;



	public static void main(String[] args){
		userMode = true;
		PC = 0;
		SP = 2000;
		AC = X = Y = 0;

	}

	/**
	 *	Interfaces with the memory module and requests a read from the memory
	 *	@param memAddress the address of the memory to read from
	 *	@return the integer data that the memory at memAddress contains
	 */
	public static int memoryRead(int memAddress){
		checkMemoryOutofBounds(memAddress);
		pw.println(memAddress);
		pw.flush();

		Scanner sc = new Scanner(is);
		if(sc.hasNextInt()){
			return sc.nextInt();
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
		pw.println(memAddress + " " + data);
		pw.flush();
	}

	public void processInstruction(){
		switch(IR){
			case 1: AC = memoryRead(++PC);
					break;	// Load the value into the AC
			case 2: AC = memoryRead(memoryRead(++PC));
					break;	// Load the value at the address into the AC
			case 3: AC = memoryRead(memoryRead(++PC));
					break;	// Load the value from the address found in the given address into the AC
			case 4: AC = memoryRead(++PC) + X;
					break;	// Load the value at (address + X) into the AC
			case 5: AC = memoryRead(++PC) + Y;
					break;	// Load the value at (Address + Y) into the AC
			case 6: AC = memoryRead(SP+X); 
					break;	// Load from (SP+X) into the AC (if SP is 990, and X is 1, load from 991).
			case 7: memoryWrite(memoryRead(++PC), AC);
					break;	// Store the value in the AC into the address
			case 8: AC = (int)(Math.random() * 100 + 1); 
					break;	// Gets a random int from 1 to 100 into the AC
			case 9: int port = memoryRead(++PC);
					if(port == 1) System.out.print(AC);
					if(port == 2) System.out.print((char)AC);
					break;	// If port=1, write AC as an int to the screen, if port =2, write AC as char onto screen
			case 10: AC += X; 
					break;	// Adds the value in X to the AC
			case 11: AC += Y; 
					break;	// Adds the value in Y to the AC
			case 12: AC -= X; 
					break;	// Subs the value in X from the AC
			case 13: AC -= Y; 
					break;	// Subs the value in Y from the AC
			case 14: X = AC; 
					break;	// Copy the value in the AC to X
			case 15: AC = X; 
					break;	// Copy the value in X to the AC
			case 16: Y = AC; 
					break;	// Copy the value in the AC to Y
			case 17: AC = Y; 
					break;	// Copy the value in Y to the AC
			case 18: SP = AC; 
					break;	// Copy the value in AC to the SP
			case 19: AC = SP; 
					break;	// Copy the value in SP to the AC
			case 20: PC = memoryRead(PC+1);
					break;	// Jump to the address
			case 21: if(AC == 0)
						PC = memoryRead(PC + 1);
					else PC++;
					break;	// Jump to the address only if the value in the AC is zero
			case 22: if(AC != 0)
						PC = memoryRead(PC + 1);
					else PC++;
					break;	// Jump to the address only if the value in the AC is NOT zero
			case 23: SP++;
					memoryWrite(SP, PC);
					PC = memoryRead(PC + 1);
					break;	// Push return address onto the stack, jump to the address
			case 24: PC = memoryRead(SP--);
					break;	// Pop return address from the stack, jump to the address
			case 25: X++; 
					break;	// Increment the value in X
			case 26: X--; 
					break;	// Decrement the value in X
			case 27: SP++;
					memoryWrite(SP, AC);
					break;	// Push AC onto the stack
			case 28: AC = memoryRead(SP--);
					break;	// Pop from stack into the AC
			case 29: break;	// Perform system call
			case 30: break;	// Return from system call
			case 50: System.exit(0);	// End execution
			default: break;
		}
	}

	private static void checkMemoryOutofBounds(int address){
		if(address >= 1000){
			System.out.println("Address '" + address + "' Out Of Bounds. System Stack Out of Limits. Process Exiting");
			System.exit(0);
		}
	}
}