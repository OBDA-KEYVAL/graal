package fr.lirmm.graphik.graal.keyval;

import java.util.List;

public class Node{
	private Node nodeFather;
	private List<Node> nodeChildren;
	private List<String> listTags;
	private static int nbNode = 0;
	private int id;
	
	public Node(){
		this.nodeFather = null;
		this.nodeChildren = null;
		this.listTags = null;
		id = nbNode;
		nbNode++;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Node getNodeFather() {
		return nodeFather;
	}

	public void setNodeFather(Node nodeFather) {
		this.nodeFather = nodeFather;
	}

	public List<Node> getNodeChildren() {
		return nodeChildren;
	}

	public void setNodeChildren(List<Node> nodeChildren) {
		this.nodeChildren = nodeChildren;
	}
	
	public int getNbChild(){
		return this.nodeChildren.size();
	}
	
	public void addChild(Node child){
		this.nodeChildren.add(child);
	}	
	
	public void rmChild(int position){
		this.nodeChildren.remove(position);
	}
	
	public Node getChild(int position){
		return this.nodeChildren.get(position);
	}

	public List<String> getListTags() {
		return listTags;
	}

	public void setListTags(List<String> listTags) {
		this.listTags = listTags;
	}
	
	public int getNbTag(){
		return this.listTags.size();
	}
	
	public void addTag(String tag){
		this.listTags.add(tag);
	}
	
	public String getTag(int position){
		return this.listTags.get(position);
	}
}