/*
 * Copyright (c) 2007-2013 Concurrent, Inc. All Rights Reserved.
 *
 * Project and contact information: http://www.concurrentinc.com/
 */

package pattern;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pattern.model.MiningModel;
import pattern.model.Model;
import pattern.model.clust.ClusteringModel;
import pattern.model.glm.GeneralizedRegressionModel;
import pattern.model.lm.RegressionModel;
import pattern.model.tree.TreeModel;
import storm.trident.tuple.TridentTuple;

public class Classifier implements Serializable {
	/** Field LOG */
	private static final Logger LOG = LoggerFactory.getLogger(Classifier.class);

	public Model model;

	/**
	 * Construct a Classifier by parsing the PMML file, verifying the model
	 * type, and building an appropriate Model.
	 * 
	 * @param pmmlUri
	 *            XML source for the PMML description
	 * @throws PatternException
	 */
	public Classifier(String pmmlUri) throws PatternException {
		try {
			PMML pmml = new PMML(getSourceReader(pmmlUri));

			if (PMML.Models.MINING.equals(pmml.model_type))
				model = new MiningModel(pmml);
			else if (PMML.Models.TREE.equals(pmml.model_type))
				model = new TreeModel(pmml);
			else if (PMML.Models.REGRESSION.equals(pmml.model_type))
				model = new RegressionModel(pmml);
			else if (PMML.Models.CLUSTERING.equals(pmml.model_type))
				model = new ClusteringModel(pmml);
			else if (PMML.Models.GENERALIZED_REGRESSION.equals(pmml.model_type))
				model = new GeneralizedRegressionModel(pmml);
			else
				throw new PatternException("unsupported model type: "
						+ pmml.model_type.name());
		} catch (IOException exception) {
			LOG.error("could not read PMML file", exception);
			throw new PatternException(" could not read PMML file", exception);
		}
	}

	/**
	 * Construct a Reader by reading the PMML file into a string buffer first.
	 * This default implementation expects a file on the local disk.
	 * 
	 * @param file
	 *            XML source for the PMML description
	 * @return Reader
	 * @throws IOException
	 */
	public Reader getSourceReader(String filePath) throws IOException {
		File file = new File(filePath);
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(file)));
		String line = null;
		StringBuilder stringBuilder = new StringBuilder();
		String ls = System.getProperty("line.separator");
		while ((line = reader.readLine()) != null) {
			stringBuilder.append(line);
			stringBuilder.append(ls);
		}
		reader.close();
		return new StringReader(stringBuilder.toString());
	}

	/**
	 * Prepare to classify with this model. Called immediately before the
	 * enclosing Operation instance is put into play processing Tuples.
	 */
	public void prepare() {
		model.prepare();
	}

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
	public String classifyTuple(TridentTuple values) throws PatternException {
		return model.classifyTuple(values);
	}
}
