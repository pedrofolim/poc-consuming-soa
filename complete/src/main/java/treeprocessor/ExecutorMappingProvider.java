package treeprocessor;

public interface ExecutorMappingProvider {

	String mapping(String sourceContent, String sourcePath, TypeNode typeNode);
}
