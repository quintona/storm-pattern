/*
 * Copyright (c) 2007-2013 Concurrent, Inc. All Rights Reserved.
 *
 * Project and contact information: http://www.concurrentinc.com/
 */

package pattern;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.operation.TridentOperationContext;
import storm.trident.tuple.TridentTuple;
import backtype.storm.tuple.Values;

public class ClassifierSplitFunction extends BaseFunction {
	/** Field LOG */
	private static final Logger LOG = LoggerFactory
			.getLogger(ClassifierSplitFunction.class);

	public Map<String, Classifier> classifierMap;
	public String splitField;
	Map<String, String> pmmlMap;

	public ClassifierSplitFunction(String splitField,
			Map<String, String> pmmlMap) {
		this.pmmlMap = pmmlMap;
		this.splitField = splitField;

	}

	@Override
	public void prepare(Map conf, TridentOperationContext context) {
		classifierMap = new HashMap<String, Classifier>();

		for (Map.Entry<String, String> entry : pmmlMap.entrySet()) {
			String splitId = entry.getKey();
			String pmmlPath = entry.getValue();
			Classifier classifier = new Classifier(pmmlPath);
			classifierMap.put(splitId, classifier);
			classifier.prepare();
		}
	}

	@Override
	public void execute(TridentTuple tuple, TridentCollector collector) {
		String splitId = tuple.getStringByField(splitField);
		Classifier classifier = classifierMap.get(splitId);

		if (classifier != null) {
			String label = classifier.classifyTuple(tuple);
			collector.emit(new Values(label));
		} else {
			String message = String.format(
					"unknown experimental split ID [ %s ]", splitId);
			LOG.error(message);
			throw new PatternException(message);
		}
	}
}
