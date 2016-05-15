package fr.lirmm.graphik.graal.keyval;

import java.util.ArrayList;
import java.util.List;

import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.Term.Type;

public class Tree {
	private Node nodeRoot;
	
	public Tree(){
		this.nodeRoot = new Node();
	}

	public Node getNodeRoot() {
		return nodeRoot;
	}

	public void setNodeRoot(Node nodeRoot) {
		this.nodeRoot = nodeRoot;
	}
	
	public void branchNode(Node node1, Node node2){
		if(node1 != null && node2 != null){
			if(node1.getNodeChildren() == null){
				node1.setNodeChildren(new ArrayList<Node>());
			}
			node1.addChild(node2);
			node2.setNodeFather(node1);
		}
	}
	
	public void buildPath(Node node, List<String> path){
		if(node != null){
			Node tmp = node;
			for(String key : path){
				Node child = new Node();
				branchNode(tmp, child);
				if(child.getListTags() == null){
					child.setListTags(new ArrayList<String>());
				}
				child.addTag(key);
				tmp = child;
			}
		}
	}
	
	public List<Node> getNodeWithTag(Node node, String tag){
		List<Node> result = new ArrayList<>();
		if(node != null){
			List<String> listTags = node.getListTags(); 
			if(listTags != null){
				if(listTags.contains(tag)){
					result.add(node);
				}
				List<Node> nodeChildren = node.getNodeChildren();
				if(nodeChildren != null){
					for(Node child : nodeChildren){
						result.addAll(getNodeWithTag(child, tag));
					}
				}
			}
		}
		return result;
	}
	
	public List<Node> getNodeWithPath(Node node, List<String> path){
		List<Node> result = new ArrayList<>();
		if(node != null && path != null){
			if(containPathStartNode(node, path)){
				result.add(node);
			}
			List<Node> nodeChildren = node.getNodeChildren();
			if(nodeChildren != null){
				for(Node child : nodeChildren){
					result.addAll(getNodeWithPath(child, path));
				}
			}
		}
		return result;
	}
	
	public boolean containPathStartNode(Node node, List<String> path){
		if(path != null && node != null){
			List<String> listTags = node.getListTags();
			int size = path.size();
			if(size == 0){
				return false;
			}
			else if(size == 1){
				if(listTags != null && listTags.contains(path.get(0))){
					return true;
				}
				else{
					return false;
				}
			}
			else{
				if(listTags != null && listTags.contains(path.get(0))){
					List<String> newPath = path.subList(1, path.size());
					List<Node> children = node.getNodeChildren();
					if(children != null && children.size() > 0){
						boolean result = false;
						for(Node child : children){
							result |= containPathStartNode(child, newPath);
						}
						return result;
					}
					else{
						return false;
					}
				}
				else{
					return false;
				}
			}
		}
		else{
			return false;
		}
	}
		
	public List<Node> getEndOfPath(Node nodeStart, List<String> path){
		List<Node> result = new ArrayList<>();
		if(nodeStart != null && path != null){
			int sizePath = path.size(); 
			if(sizePath == 0){
				return result;
			}
			if(sizePath == 1){
				List<String> listTags = nodeStart.getListTags();
				if(listTags != null){
					if(listTags.contains(path.get(0))){
						result.add(nodeStart);
						return result;
					}
					else{
						return result;
					}
				}
				else{
					return result;
				}
			}
			else{
				List<String> listTags = nodeStart.getListTags();
				if(listTags != null){
					if(listTags.contains(path.get(0))){
						List<Node> children = nodeStart.getNodeChildren();
						if(children != null){
							List<String> newPath = path.subList(1, path.size());
							for(Node child : children){
								result.addAll(this.getEndOfPath(child, newPath));
							}
							return result;
						}
						else{
							return result;
						}
					}
					else{
						return result;
					}
				}
				else{
					return result;
				}
			}
		}
		else{
			return result;
		}
	}
	
	public List<Node> getNodeOverlapEndBranchPath(Node node, List<String> path){
		List<Node> result = new ArrayList<>();
		if(node != null && path != null){
			if(checkOverlapEndBranchPath(node, path)){
				result.add(node);
			}
			List<Node> nodeChildren = node.getNodeChildren();
			if(nodeChildren != null){
				for(Node child : nodeChildren){
					result.addAll(getNodeOverlapEndBranchPath(child, path));
				}
			}
		}
		return result;
	}
	
	public boolean checkOverlapEndBranchPath(Node node, List<String> path){
		if(node != null && path != null){
			int sizePath = path.size();
			List<Node> children = node.getNodeChildren();
			if(sizePath == 0 || node.getListTags() == null){
				return false;
			}
			else if(sizePath == 1){
				if(children == null && node.getListTags().contains(path.get(0))){
					return true;
				}
				else{
					return false;
				}
			}
			else{
				if(children == null){
					if(node.getListTags().contains(path.get(0))){
						return true;
					}
					else{
						return false;
					}
				}
				else{
					if(node.getListTags().contains(path.get(0))){
						List<String> newPath = path.subList(1, path.size());
						boolean result = false;
						for(Node child : children){
							result |= checkOverlapEndBranchPath(child, newPath);
						}
						return result;
					}
					else{
						return false;
					}
				}
			}
		}
		else{
			return false;
		}
	}
	
	public List<PathQuery> getPathQueries(Node node, Term term){
		List<PathQuery> result = new ArrayList<>();
		if(node != null && term != null){
			List<List<String>> paths = getPaths(node);
			for(List<String> p : paths){
				PathPredicate pthPred = new PathPredicate();
				for(String key : p){
					pthPred.addPredicate(new Predicate(key, 1));
				}
				result.add(new PathQuery(pthPred, term));
			}
		}
		return result;
	}
	
	public void applyNoRL1(Node node, NoRule r){
		if(r != null && node != null){
			List<String> head = r.getHeadPathAtom().getPathPredicate().predicatesToStrings();
			List<String> body = r.getBodyPathAtom().getPathPredicate().predicatesToStrings();
			if(head.size() == 1 && body.size() == 1){
				List<String> listTags = node.getListTags();
				if(listTags != null){
					for(String tag : listTags){
						if(tag.equals(head.get(0))){
							String key = body.get(0);
							if(!listTags.contains(key)){
								node.addTag(key);
								break;
							}
						}
					}
				}
				List<Node> nodeChildren = node.getNodeChildren();
				if(nodeChildren != null){
					for(Node child : nodeChildren){
						applyNoRL1(child, r);
					}
				}
			}
		}
	}
	
	public void copyAndBranchTargetOnNode(Node node, Node target){
		if(target != null && node != null){
			Node copyTarget = new Node();
			copyTarget.setNodeFather(node);
			copyTarget.setListTags(new ArrayList<String>());
			for(String tag : target.getListTags()){
				copyTarget.addTag(tag);
			}
			node.setNodeChildren(new ArrayList<Node>());
			node.addChild(copyTarget);
			List<Node> children = target.getNodeChildren();
			if(children != null){
				for(Node child : target.getNodeChildren()){
					this.copyAndBranchTargetOnNode(copyTarget, child);
				}
			}
		}
	}
	
	public Node createCopy(Node node){
		Node copy = new Node();
		copyAndBranchTargetOnNode(copy, node);
		return copy.getChild(0);
	}
	
	public List<Node> getNodePathSubsume(Node father, List<String> path){
		List<Node> result = new ArrayList<>();
		if(father != null && path != null){
			List<Node> childrenFather = father.getNodeChildren();
			if(childrenFather != null){
				for(Node childFather : childrenFather){
					if(containPathStartNode(childFather, path)){
						result.add(childFather);
					}
				}
			}
		}
		return result;
	}
	
	public boolean isSubsumed(Node father, List<String> path){
		if(father != null && path != null){
			List<Node> childrenFather = father.getNodeChildren();
			if(childrenFather != null){
				for(Node childFather : childrenFather){
					List<List<String>> paths = getPaths(childFather);
					for(List<String> p : paths){
						if(p.size() <= path.size()){
							int cpt = 0;
							for(int i = 0; i < p.size(); i++){
								if(p.get(i).equals(path.get(i))){
									cpt++;
								}
							}
							if(cpt == p.size()){
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}
	
	public List<List<String>> getPaths(Node node){
		List<List<String>> result = new ArrayList<>();
		if(node != null){
			List<String> listTags = node.getListTags();
			List<Node> children = node.getNodeChildren();
			if(children == null || children.size() == 0){
				if(listTags != null && listTags.size() > 0){
					for(String tag : listTags){
						List<String> path = new ArrayList<>();
						path.add(tag);
						result.add(path);
					}
				}
			}
			else{
				if(listTags != null && listTags.size() > 0){
					for(String tag : listTags){
						for(Node child : children){
							result.addAll(constructPaths(tag, getPaths(child)));
						}
					}
				}
				else{
					for(Node child : children){
						result.addAll(getPaths(child));
					}
				}
			}
		}
		return result;
	}
	
	public List<List<String>> constructPaths(String key, List<List<String>> paths){
		if(key != null && paths != null){
			for(List<String> p : paths){
				p.add(0, key);
			}
		}
		return paths;
	}
	
	public void print(Node node){
		if(node != null){
			String str = "";
			Node father = node.getNodeFather();
			str += "Id = " + node.getId() + " ";
			if(father != null){
				str += "Father = " + father.getId() + " ";
			}
			List<String> tags = node.getListTags();
			if(tags != null){
				str += "Tags = " + tags + " ";
			}
			System.out.println(str);
			List<Node> children = node.getNodeChildren();
			if(children != null){
				for(Node child : children){
					print(child);
				}
			}
		}
	}
}