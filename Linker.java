/**
 * 
 * @author Yash Jalan <yj627@nyu.edu>
 * 
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.HashMap;
import java.util.Map;

public class Linker {
	
	public static void main(String[] args) throws FileNotFoundException {
		
		Scanner file = new Scanner(new File(args[0]));
		file.useDelimiter("\\s*(\\n|\\s)\\s*");
		//1st pass
		int nModules = Integer.parseInt(file.next());
		int baseAddress = 0;
		
        Map<String, Integer> symbols = new HashMap<>();
        Map<String, Integer> symbolsNotUsed = new HashMap<>();
        Map<String, Integer> symbolsNotUsed1 = new HashMap<>();
		System.out.println("Symbol Table");
		for (int i = 0; i < nModules; i++) {
			int nDefs = Integer.parseInt(file.next());
			for (int j = 0; j < nDefs; j++) {
				String s = file.next();
				int t = Integer.parseInt(file.next());
				if (symbols.containsKey(s)) {
					System.out.println("Error: " + s + " is multiply defined; first value used.");
				} else {
					symbols.put(s, t + baseAddress);
					symbolsNotUsed.put(s, i);

				}
			}
			int nUse = Integer.parseInt(file.next());
			for (int k = 0; k < nUse; k++) {
				file.next();
			}
			int nText = Integer.parseInt(file.next());
			baseAddress += nText;
			
			for (int l = 0; l < nText; l++) {
				file.next();
			}
		}
		for (Map.Entry<String, Integer> entry : symbols.entrySet()) {
		    System.out.println(entry.getKey()+" = "+entry.getValue());
		}
		file.close();
		
		//2nd pass
		System.out.println("Memory Map");
		int n = 0;
		Scanner file1 = new Scanner(new File(args[0]));
		file1.useDelimiter("\\s*(\\n|\\s)\\s*");
		file1.next();
		baseAddress = 0;
		for (int i  = 0; i < nModules; i++) {
			int nDefs = Integer.parseInt(file1.next());
			for (int j = 0; j < (2*nDefs); j++) {
				file1.next();
			}
			int nUse = Integer.parseInt(file1.next());
			String[] use = new String[nUse];
			boolean[] useCheck = new boolean[nUse];
			for (int k = 0; k < nUse; k++) {
				use[k] = file1.next();
				symbolsNotUsed.remove(use[k]);
			}
			int nText = Integer.parseInt(file1.next());
			for (int l = 0; l < nText; l++) {
				int current = Integer.parseInt(file1.next());
				int lastDigit = current%10;
				int x = current/10;
				if (lastDigit == 1) {
					System.out.println(n + "  :  " + x);
					n++;
				} else if (lastDigit == 2) {
					if ((x%1000) > 600) {
						System.out.println(n + "  :  " + ("" + x/1000) + "000 Error: Absolute address exceeds machine size;"
								+ " zero used" );
					} else {
						System.out.println(n + "  :  " + x);
					}
					n++;
				} else if (lastDigit == 3) {
					if (((x+baseAddress)%1000) > 600) {
						System.out.println(n + "  :  " + ("" + x/1000) + "000 Error: Absolute address exceeds machine size;"
								+ " zero used" );
					} else if ((x%1000) > nText) {
						System.out.println(n + "  :  " +  ("" + x/1000) + "000 Error: Relative address exceeds module size;"
								+ " zero used" );
					} else{
						System.out.println(n + "  :  " + (x + baseAddress));
					}
					n++;
				} else if (lastDigit == 4) {
					int new_x = Integer.parseInt(Integer.toString(x).substring(1));
					if (new_x >= use.length) {
						System.out.println(n + " : " + x + " Error: external address exceeds length of use list;"
								+ "treated as immediate.");
					} else if (!symbols.containsKey(use[new_x])) {
						System.out.println(n + "  :  " + ("" + x/1000) + "000 Error: " + use[new_x] + " is not defined;"
								+ " zero used" );
					} else {
						useCheck[new_x] = true;
						System.out.println(n + "  :  " + ("" + x/1000) + String.format("%03d", symbols.get(use[new_x])));
					}
					n++;
				} else {
					System.out.println("This shouldn't appear!");
				}
				
			}
			for (int j = 0; j < useCheck.length; j++) {
				if (!useCheck[j]) {
					symbolsNotUsed1.put(use[j], i);
				}
			}
			baseAddress += nText;				
		}
		
		for (Map.Entry<String, Integer> entry : symbolsNotUsed.entrySet()) {
		    System.out.println("Warning: "+ entry.getKey()+" was defined in module "+entry.getValue() + " but never used.");
		}
		for (Map.Entry<String, Integer> entry1 : symbolsNotUsed1.entrySet()) {
		    System.out.println("Warning: "+ entry1.getKey()+" is on the use list in module "+entry1.getValue() + " but isn't used.");
		}
		
		file1.close();
	}
	
}
