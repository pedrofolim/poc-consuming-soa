package hello;

import java.util.ArrayList;
import java.util.List;

public class FlowContext {

	private List<String> attributes = new ArrayList<>();
	private List<Step> steps = new ArrayList<>();
	
	public List<String> getAttributes() {
		return attributes;
	}
	public void setAttributes(List<String> attributes) {
		this.attributes = attributes;
	}
	public List<Step> getSteps() {
		return steps;
	}
	public void setSteps(List<Step> steps) {
		this.steps = steps;
	}
	
	
}

class Step {
	
		Step(String name, String content){
			this.name = name;
			this.content = content;
		}
	
		private String name;
		private String content;
		private List<String> internalAttributes = new ArrayList<>();;
		
		
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getContent() {
			return content;
		}
		public void setContent(String content) {
			this.content = content;
		}
		public List<String> getInternalAttributes() {
			return internalAttributes;
		}
		public void setInternalAttributes(List<String> internalAttributes) {
			this.internalAttributes = internalAttributes;
		}
}
