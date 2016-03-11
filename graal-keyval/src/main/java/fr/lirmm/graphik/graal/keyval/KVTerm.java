package fr.lirmm.graphik.graal.keyval;

import fr.lirmm.graphik.graal.api.core.AbstractTerm;

public class KVTerm extends AbstractTerm {
	
	private String mLabel;
	private Type mType;

	public KVTerm(String label, Type type){
		this.mLabel = label;
		this.mType= type;
	}
	
	public Object getIdentifier() {
		// TODO Auto-generated method stub
		return this.mLabel;
	}

	public Type getType() {
		// TODO Auto-generated method stub
		return this.mType;
	}

	public boolean isConstant() {
		// TODO Auto-generated method stub
		if(this.mType == Type.CONSTANT){
			return true;
		}
		return false;
	}

	public String toString(){
		return this.mLabel;
	}
	
}
