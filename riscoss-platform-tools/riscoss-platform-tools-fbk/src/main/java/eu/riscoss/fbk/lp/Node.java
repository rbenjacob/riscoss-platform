package eu.riscoss.fbk.lp;


import java.security.InvalidParameterException;
import java.util.Iterator;
import java.util.LinkedList;

public class Node {
	private Label satLabel;      // node label for the satisfaction
	private Label denLabel;      // node label for the negation
	private Label oldSatLabel;   // doubled values for safe propagation
	private Label oldDenLabel;   // doubled values for safe propagation
	private LinkedList<Relation> parenthood;  // list of  rels. where this node is parent
	private LinkedList<Relation> childhood;   // list of rels. where this node is child
	
	
	public Node(Label aSatLabel, Label aDenLabel) {
		initSatLabel(aSatLabel);
		initDenLabel(aDenLabel);
		parenthood = new LinkedList<Relation>();
		childhood  = new LinkedList<Relation>();
	};
	
	
	boolean hasChanged() {
		if (getSatLabel().isEqualTo(getOldSatLabel()) &&
				getDenLabel().isEqualTo(getOldDenLabel()))
			return false;
		else
			return true;
	}
	
	void syncLabels() {
		syncSatLabel();
		syncDenLabel();
	}
	
	public Label getSatLabel() {
		return satLabel;
	}
	
	Label getOldSatLabel() {
		return oldSatLabel;
	}
	
	public Label getDenLabel() {
		return denLabel;
	}
	
	Label getOldDenLabel() {
		return oldDenLabel;
	}
	
	private void checkLabelConsistency(Label newLabel, Label oldLabel)
			throws InvalidParameterException {
		if (oldLabel == null) return;
		if (newLabel.getClass() != oldLabel.getClass())
			throw new InvalidParameterException
			("Passed label type" + newLabel.getClass().getName()
					+ " does not match current label type"
					+ oldLabel.getClass().getName());
	}
	
	public void setSatLabel(Label aLabel) {
		checkLabelConsistency(aLabel,getSatLabel());
		// stores the current label in the old label field
		oldSatLabel = satLabel;
		// writes in the current label the passed one
		satLabel = aLabel;
	}
	
	private void initSatLabel(Label aLabel) {
		setSatLabel(aLabel);
		setSatLabel(aLabel);
	}
	
	private void syncSatLabel() {
		setSatLabel(getSatLabel());
	}
	
	void setDenLabel(Label aLabel) {
		checkLabelConsistency(aLabel, getDenLabel());
		// stores the current label in the old label field
		oldDenLabel = denLabel;
		// writes in the current label the passed one
		denLabel = aLabel;
	}
	
	private void initDenLabel(Label aLabel) {
		setDenLabel(aLabel);
		setDenLabel(aLabel);
	}
	
	private void syncDenLabel() {
		setDenLabel(getDenLabel());
	}
	
	// adds a relation where this node is parent
	void addToParenthood(Relation aRelation) {
		parenthood.add(aRelation);
	}
	
	// removes a relation where this node is parent
	boolean removeFromParenthood(Relation aRelation) {
		return parenthood.remove(aRelation);
	}
	
	// adds a relation where this node is a child
	void addToChildhood(Relation aRelation) {
		childhood.add(aRelation);
	}
	
	// removes a relation where this node is a child
	boolean removeFromChildhood(Relation aRelation) {
		return childhood.remove(aRelation);
	}
	
	
	LinkedList<Relation> getParenthood() {
		return parenthood;
	}
	
	Iterator<Relation> getFirstInParenthood() {
		return parenthood.iterator();
	}
	
	Iterator<Relation> getFirstInChildhood() {
		return childhood.iterator();
	}
}
