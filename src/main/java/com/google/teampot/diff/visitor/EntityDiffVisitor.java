package com.google.teampot.diff.visitor;

import java.util.List;

import com.google.teampot.model.EntityDiff;

import de.danielbechler.diff.node.DiffNode;
import de.danielbechler.diff.node.Visit;

public class EntityDiffVisitor implements DiffNode.Visitor {
	private final Object working;
	private final Object base;
	
	private List<EntityDiff> entityDiffs;

	public EntityDiffVisitor(final Object working, final Object base, List<EntityDiff> entityDiffs) {
		this.base = base;
		this.working = working;
		this.entityDiffs = entityDiffs;
	}

	public void node(final DiffNode node, final Visit visit) {
		if (filter(node)) {
			EntityDiff entityDiff = new EntityDiff();
			entityDiff.setPath(node.getPath().toString());
			entityDiff.setOldValue(node.canonicalGet(base));
			entityDiff.setNewValue(node.canonicalGet(working));
			this.entityDiffs.add(entityDiff);
		}
	}

	protected boolean filter(final DiffNode node) {
		return (node.isRootNode() && !node.hasChanges()) || (node.hasChanges() && !node.hasChildren());
	}
	

}
