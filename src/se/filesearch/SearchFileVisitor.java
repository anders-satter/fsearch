package se.filesearch;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Performs task each time a new file is found Will only consider
 * non-directories when trying to match the file name. This is the default
 * behavior at least. It might be interesting to look at file names, including
 * the path, for sometimes the path gives an indication as to the usage and
 * contents of a file. So this may change in the future.
 * 
 * This file was found somewhere on the internet, but has been adjusted by
 * me. 
 * 
 */
class SearchFileVisitor implements FileVisitor<Path> {

	private int fileCount;
	private int matchCount;
	private Pattern iFileNamePattern;

	/**
	 * This variable is not used as far as I can see No one
	 */
	private Pattern iFileContentPattern;

	/**
	 * 
	 * @param fileNamePattern
	 * @param fileContentPattern
	 */
	public SearchFileVisitor(Pattern fileNamePattern, Pattern fileContentPattern) {
		iFileNamePattern = fileNamePattern;
		iFileContentPattern = fileContentPattern;
		fileCount = 0;
		matchCount = 0;
	}

	@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
			throws IOException {
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult postVisitDirectory(Path dir, IOException exc)
			throws IOException {
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
			throws IOException {
		String tNameToMatch = file.getName(file.getNameCount() - 1).toString();
		if (!attrs.isDirectory()) {
			fileCount++;
			if (iFileNamePattern.matcher(tNameToMatch).matches()) {				
				if (iFileContentPattern != null) {
					matchFileContentPatternInFile(file);
				} else {
					System.out.println(file.toString());	
				}
			}
		} 
		return FileVisitResult.CONTINUE;
	}

	/**
	 * Goes through the lines in the file and search for the pattern.
	 * 
	 * The try-with-resources statement is a try statement that declares one or
	 * more resources. A resource is an object that must be closed after the
	 * program is finished with it. The try-with-resources statement ensures
	 * that each resource is closed at the end of the statement. Any object that
	 * implements java.lang.AutoCloseable, which includes all objects which
	 * implement java.io.Closeable, can be used as a resource.
	 * @param aPath
	 * @throws IOException
	 */
	private void matchFileContentPatternInFile(Path aPath) throws IOException {
		try (Stream<String> filteredLines = Files.lines(aPath).filter(
				s -> iFileContentPattern.matcher(s).matches())) {
			try {					
				List<String> matches = filteredLines.collect(Collectors.toList());
				if (matches.size()>0){
					System.out.println("->"+aPath.toString()+':');
					for (String string : matches) {
						matchCount++;
						System.out.println("    "+string);
					}
				}
			} catch (Exception e) {
				/*just ignore*/
			}
		}
	}


	@Override
	public FileVisitResult visitFileFailed(Path file, IOException exc)
			throws IOException {
		return FileVisitResult.CONTINUE;
	}

	public void printSummary() {
		System.out.println("--------------------------------------------");
		System.out.printf("Files: %d    Matches:%d%n"  , fileCount, matchCount);
	}

}