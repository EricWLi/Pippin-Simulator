package pippin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Eric W. Li
 */
public class Assembler {
	public static Set<String> noArgument = new TreeSet<String>();   
	static {
		noArgument.add("HALT");
		noArgument.add("NOP");
		noArgument.add("NOT");
	}
	
	/**
	 * Method to assemble a file to its executable representation. 
	 * If the input has errors one of the errors will be reported 
	 * the StringBulder. The error may not be the first error in 
	 * the code and will depend on the order in which instructions 
	 * are checked. The line number of the error that is reported 
	 * is returned as the value of the method. 
	 * A return value of 0 indicates that the code had no errors 
	 * and an output file was produced and saved. If the input or 
	 * output cannot be opened, the return value is -1.
	 * The unchecked exception IllegalArgumentException is thrown 
	 * if the error parameter is null, since it would not be 
	 * possible to provide error information about the source code.
	 * @param input the source assembly language file
	 * @param output the executable version of the program if 
	 * the source program is correctly formatted
	 * @param error the StringBuilder to store the description 
	 * of the error that is reported. It will be empty (length 
	 * zero) if no error is found.
	 * @return 0 if the source code is correct and the executable 
	 * is saved, -1 if the input or output files cannot be opened, 
	 * otherwise the line number of the reported error.
	 */
	public static int assemble(File input, File output, StringBuilder error) {
		if (error == null)
			throw new IllegalArgumentException("Coding error: the error buffer is null");
		int retVal = 0;
		ArrayList<String> inputText = new ArrayList<>();
		
		try (Scanner inp = new Scanner(input)) {
			int lineNum = 0;
			boolean blankLineFound = false;
			int firstBlankLineNum = 0;
			
			while(inp.hasNextLine() && retVal == 0) {
				String line = inp.nextLine();
				if (line.trim().length() == 0) { // if line is blank
					firstBlankLineNum = lineNum;
					blankLineFound = true;
				} else if (blankLineFound) {
					error.append("Illegal blank line in the source file");
					retVal = firstBlankLineNum;
				} else if (line.charAt(0) == ' ' || line.charAt(0) == '\t') {
					error.append("Line starts with illegal white space");
					retVal = lineNum;
				} else {
					inputText.add(line.trim());
				}
				lineNum++;
			}
			if (retVal != 0)
				return retVal;
		} catch (FileNotFoundException e) {
			error.append("Unable to open the assembled file");
			retVal = -1;
		}
		
		
		ArrayList<String> outputCode = new ArrayList<>();
		
		for(int i = 0; i < inputText.size() && retVal == 0; i++) {
			String[] parts = inputText.get(i).split("\\s+");
			
			if (!InstructionMap.opcode.containsKey(parts[0].toUpperCase()))
				error.append("Error on line " + (i+1) + ": illegal mnemonic");
			else if (!parts[0].equals(parts[0].toUpperCase()))
				error.append("Error on line " + (i+1) + ": mnemonic must be upper case");
			else if (noArgument.contains(parts[0])) {
				if (parts.length > 1)
					error.append("Error on line " + (i+1) + ": this mnemonic cannot take arguments");
				else {
					int opPart = 8 * InstructionMap.opcode.get(parts[0]);
					opPart += Instruction.numOnes(opPart) % 2;
					outputCode.add(Integer.toString(opPart, 16) + " 0");
				}
			} else if (parts.length > 2)
				error.append("Error on line " + (i+1) + ": this mnemonic has too many arguments");
			else if (parts.length == 1)
				error.append("Error on line " + (i+1) + ": this mnemonic a missing arguments");
			else {
				try {
					int flags = 0;
					
					if (parts[1].charAt(0) == '#') {
						flags = 2;
						parts[1] = parts[1].substring(1);
					}
					
					if (parts[1].charAt(0) == '@') {
						flags = 4;
						parts[1] = parts[1].substring(1);
					}
					
					if (parts[1].charAt(0) == '&') {
						flags = 6;
						parts[1] = parts[1].substring(1);
					}
					
					int arg = Integer.parseInt(parts[1],16);
					
					int opPart = 8 * InstructionMap.opcode.get(parts[0]) + flags;
					opPart += Instruction.numOnes(opPart)%2;
					outputCode.add(Integer.toString(opPart, 16) + " " + Integer.toString(arg, 16));
				} catch (NumberFormatException e) {
					error.append("Error on line " + (i+1) + ": argument is not a hex number");
				}
			}
		}
		if (retVal == 0) {
			try (PrintWriter outp = new PrintWriter(output)){
				for (String str : outputCode)
					outp.println(str);
				outp.close();
			} catch (FileNotFoundException e) {
				error.append("Error: Unable to write the assembled program to the output file");
			    retVal = -1;
			}
		}
		return retVal;
	}
	
	public static void main(String[] args) {
		StringBuilder error = new StringBuilder();
		System.out.println("Enter the name of the file without extension: ");
		try (Scanner keyboard = new Scanner(System.in)) {
			String filename = keyboard.nextLine();
			int i = assemble(new File(filename + ".pasm"), 
					new File(filename + ".pexe"), error);
			System.out.println(i + " " + error);
		}
	}
}
