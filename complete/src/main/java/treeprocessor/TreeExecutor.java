package treeprocessor;

import java.util.List;

public class TreeExecutor {
	
	private String sourceContent;
	private ExecutorProvider executorProvider;
	private ExecutorMappingProvider executorMappingProvider;
	private List<Node> nodes;
	
	public TreeExecutor executorProvider(ExecutorProvider executorProvider){
		this.executorProvider = executorProvider;
		return this;
	}
	
	public TreeExecutor executorMappingProvider(ExecutorMappingProvider executorMappingProvider){
		this.executorMappingProvider = executorMappingProvider;
		return this;
	}
	
	public TreeExecutor sourceContent(String sourceContent){
		this.sourceContent = sourceContent;
		return this;
	}
	
	public TreeExecutor treePath(String treeNodes){
		nodes = TreeUtils.makeTreeNodes(treeNodes);
		return this;
	}
	
	public void execute(){
		this.nodes.stream().forEach(node->{
			node.mapping(executorMappingProvider, sourceContent);
			node.execute(executorProvider);
		});
	}
}
