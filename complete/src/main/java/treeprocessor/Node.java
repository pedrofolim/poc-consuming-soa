package treeprocessor;

public interface Node {

	void mapping(ExecutorMappingProvider mappingProvider, String sourceContent);
	void execute(ExecutorProvider executorProvider);
}
