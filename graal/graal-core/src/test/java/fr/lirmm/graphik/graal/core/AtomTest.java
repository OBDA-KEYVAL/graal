package fr.lirmm.graphik.graal.core;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

public class AtomTest {

    @Test
    public void constructorTest() {
        Predicate predicate = new Predicate("pred", 3);
        Term[] terms = new Term[3];
        terms[0] = new Term("X", Term.Type.VARIABLE);
        terms[1] = new Term("a", Term.Type.CONSTANT);
        terms[2] = new Term("b", Term.Type.CONSTANT);

        Atom atom = new DefaultAtom(predicate, Arrays.asList(terms));

        Assert.assertTrue(atom.getPredicate().equals(predicate));
        Assert.assertTrue(atom.getTerm(0).equals(terms[0]));
        Assert.assertTrue(atom.getTerm(1).equals(terms[1]));
        Assert.assertTrue(atom.getTerm(2).equals(terms[2]));

        Assert.assertTrue("The list eiuae ",
                atom.getTerms().equals(Arrays.asList(terms)));
    }

    @Test
    public void setterTest() {
        Predicate predicate = new Predicate("pred", 3);
        Term[] terms = new Term[3];
        terms[0] = new Term("X", Term.Type.VARIABLE);
        terms[1] = new Term("a", Term.Type.CONSTANT);
        terms[2] = new Term("b", Term.Type.CONSTANT);
        Atom atom = new DefaultAtom(predicate, Arrays.asList(terms));

        Term newTerm = new Term("new", Term.Type.CONSTANT);
        Predicate newPredicate = new Predicate("newPred", 3);

        atom.setPredicate(newPredicate);
        Assert.assertTrue(atom.getPredicate().equals(newPredicate));

        atom.setTerm(2, newTerm);
        Assert.assertTrue(atom.getTerm(2).equals(newTerm));
    }

    @Test
    public void equalsTest() {
        Predicate predicate = new Predicate("pred", 3);
        Term[] terms = new Term[3];
        terms[0] = new Term("X", Term.Type.VARIABLE);
        terms[1] = new Term("a", Term.Type.CONSTANT);
        terms[2] = new Term("b", Term.Type.CONSTANT);
        Atom atom = new DefaultAtom(predicate, Arrays.asList(terms));

        Assert.assertTrue("Atom not equals itself", atom.equals(atom));
        Assert.assertTrue("Atom not equals it clone",
                atom.equals(new DefaultAtom(atom)));

        Predicate otherPred = new Predicate("otherPred", 3);
        Term[] otherTerms = new Term[3];
        otherTerms[0] = new Term("Y", Term.Type.VARIABLE);
        otherTerms[1] = new Term("b", Term.Type.CONSTANT);
        otherTerms[2] = new Term("b", Term.Type.CONSTANT);

        Atom other = new DefaultAtom(otherPred, Arrays.asList(terms));
        Assert.assertFalse("Atom equals an other atom with other predicate",
                atom.equals(other));

        other = new DefaultAtom(predicate, Arrays.asList(otherTerms));
        Assert.assertFalse("Atom equals an other atom with other terms",
                atom.equals(other));

        other = new DefaultAtom(atom);
        other.setPredicate(otherPred);
        Assert.assertFalse("Atom equals a copy with modified predicate",
                predicate.equals(other));

        other = new DefaultAtom(atom);
        other.setTerm(2, terms[0]);
        Assert.assertFalse("Atom equals a copy with modified terms",
                atom.equals(other));

    }

}
