package preprocessor;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class PreProcessor {
	
	public PreProcessor() {
	}

	public void run(String[] args) {
		for(int i = 1; i < args.length; i++) {
			String input = read_file(args[i]);
		    write_new_file(args[i].substring(0, args[i].lastIndexOf('.'))+"_cflow.java", input);
		}
	}
	
	public String read_file(String fileName) {
		String returnValue = "";
		FileReader file = null;
		String line = "";
		
		try {
			file = new FileReader(fileName);
			BufferedReader reader = new BufferedReader(file);
			while ((line = reader.readLine()) != null) {
				returnValue = lexical_converter(returnValue, line, reader);
			}
			reader.close();
		} catch (FileNotFoundException e) {
			throw new RuntimeException("File not found");
		} catch (IOException e) {
			throw new RuntimeException("IO Error occured");
		} finally {
			if (file != null) {
				try {
					file.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return returnValue;
	}

	public void write_new_file(String fileName, String s) {
		FileWriter output = null;
		try {
			output = new FileWriter(fileName);
			BufferedWriter writer = new BufferedWriter(output);
			writer.write(s);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private String ignore_white_spaces(String s) {
		String returnValue = "";
		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) != ' ' && s.charAt(i) != '\t')
				returnValue += s.charAt(i);
		}
		return returnValue;
	}
	
	private int check_curly_brackets(String line) {
		int returnValue = 0;
		for(int i = 0; i < line.length();i++) {
			if(line.charAt(i) == '{')
				returnValue++;
			if(line.charAt(i) == '}')
				returnValue--;
		}
		return returnValue;
	}

	private String lexical_converter(String returnValue, String line, BufferedReader reader) {
		System.out.println(line);
		if (ignore_white_spaces(line).length() > 9 && ignore_white_spaces(line).substring(0, 9).equalsIgnoreCase("//@BLOCK:")) {
			returnValue += "Cflow.transition(\"" + ignore_white_spaces(line).substring(9) + "\");\n";
		}
		else if (ignore_white_spaces(line).equalsIgnoreCase("publicstaticvoidmain(String[]args){")) {
			int block = 1;
			returnValue += line + "\n";
			while(block > 0) {
				try {
					line = reader.readLine();
				} catch (IOException e) {
					e.printStackTrace();
				}
				block += check_curly_brackets(line);
				if(block == 0) {
					returnValue += "\tCflow.show_result();\n" + line + "\n";
				}
				else if (ignore_white_spaces(line).length() > 9 && ignore_white_spaces(line).substring(0, 9).equalsIgnoreCase("//@BLOCK:")) {
					returnValue += "Cflow.transition(\"" + ignore_white_spaces(line).substring(9) + "\");\n";
				}
				else {
					returnValue += line + "\n";
				}
			}
		}
		else
			returnValue += line + "\n";
		
		return returnValue;
	}
}