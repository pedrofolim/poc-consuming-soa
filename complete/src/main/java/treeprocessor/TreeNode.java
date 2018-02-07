package treeprocessor;

import java.util.List;

public class TreeNode implements Node{
	
	private String sourcePath;
	private String yourPath;
	private TypeNode typeNode;
	private List<Node> childrens;
	private String nodeContent;
	
	@Override
	public void mapping(ExecutorMappingProvider mappingProvider, String sourceContent) {
		nodeContent = mappingProvider.mapping(sourceContent, sourcePath, typeNode);
	}
	
	@Override
	public void execute(ExecutorProvider executorProvider){
		
	}

	public String getSourcePath() {
		return sourcePath;
	}

	public void setSourcePath(String sourcePath) {
		this.sourcePath = sourcePath;
	}

	public String getYourPath() {
		return yourPath;
	}

	public void setYourPath(String yourPath) {
		this.yourPath = yourPath;
	}

	public TypeNode getTypeNode() {
		return typeNode;
	}

	public void setTypeNode(TypeNode typeNode) {
		this.typeNode = typeNode;
	}

	public List<Node> getChildrens() {
		return childrens;
	}

	public void setChildrens(List<Node> childrens) {
		this.childrens = childrens;
	}

	public String getNodeContent() {
		return nodeContent;
	}

	public void setNodeContent(String nodeContent) {
		this.nodeContent = nodeContent;
	}
}
