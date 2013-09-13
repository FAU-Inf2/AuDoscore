package parser;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.*;

import parser.bast.nodes.BastProgram;
import parser.odin.JavaParser;
import parser.visitors.MixingJavaPrinter;

public class Mixer {
	public static void main(String args[]) {
		// replace by argument parsing
		if (args.length < 3) {
			System.err.println("Read the source to guess the usage! :-)");
			System.exit(-1);
		}
		String cleanName = args[0];
		ArrayList<String> keep = new ArrayList<>();
		for (int i = 2; i < args.length; i++) {
			keep.add(args[i]);
		}

		File cleanFile = new File(cleanName);
		BastProgram studentFileProg = JavaParser.getInstance().parse(cleanFile);

		// generate and print mixed file
		MixingJavaPrinter printer = new MixingJavaPrinter(args[1], keep);
		studentFileProg.accept(printer);
		printer.print(new File(cleanFile.getAbsolutePath() + ".pretty"));
	}
}
