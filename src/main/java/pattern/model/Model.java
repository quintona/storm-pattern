/*
 * Copyright (c) 2007-2013 Concurrent, Inc. All Rights Reserved.
 *
 * Project and contact information: http://www.concurrentinc.com/
 */

package pattern.model;

import java.io.Serializable;

import pattern.PatternException;
import pattern.Schema;
import storm.trident.tuple.TridentTuple;

public abstract class Model implements Serializable {
	public Schema schema = null;

	/**
	 * Prepare to classify with this model. Called immediately before the
	 * enclosing Operation instance is put into play processing Tuples.
	 */
	public abstract void prepare();

	/**
	 * Classify an input tuple, returning the predicted label.
	 * 
	 * @param values
	 *            tuple values
	 * @param fields
	 *            tuple fields
	 * @return String
	 * @throws PatternException
	 */
	public abstract String classifyTuple(TridentTuple values)
			throws PatternException;
}
