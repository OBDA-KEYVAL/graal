package fr.lirmm.graphik.graal.keyval;

public abstract class Parser {
	//Parse une requete
	public abstract Object parseQuery(Object obj);
	//Parse une regle
	public abstract Object parseRule(Object obj);
}
