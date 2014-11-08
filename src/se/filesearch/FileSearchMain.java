package se.filesearch;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;


/**
 * Custom search program to be used primarily with 
 * Windows since the custom search function cannot
 * under any circumstances be used
 * @author anders
 *
 */
public class FileSearchMain {
	
	/**
	 * 
	 * @param args
	 * @throws IOException
	 */
    public static void main(String[] args) throws IOException {
    	
    	String fileNamePatternStr = args[1];
    	String contentPatterStr = null;
    	if (args.length > 2){
    		contentPatterStr = args[2];	
    	}
    	
    	Pattern fileNamePattern = Pattern.compile(fileNamePatternStr);
    	Pattern contentPattern = null;
    	if (contentPatterStr != null){
    		contentPattern = Pattern.compile(contentPatterStr);        		
    	}
    	
    	Path path;
    	if (args.length > 0){
    		path = Paths.get(args[0]);
    	} else {
    		path = Paths.get(".");	
    	}    	      
        visitRecursively(path, fileNamePattern, contentPattern);
    }

    /**
     * This visitor walks the file tree recursively.
     * @param path
     * @throws IOException
     */
    private static void visitRecursively(Path path, 
    		Pattern fileNamePattern, 
    		Pattern fileContentPattern) throws IOException {
        SearchFileVisitor visitor = new SearchFileVisitor(fileNamePattern, fileContentPattern);
        Files.walkFileTree(path, visitor);
        visitor.printSummary();
    }
}