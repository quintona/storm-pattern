#Storm.pattern

This project is based on the cascading.pattern project. The pattern sub-project for http://Cascading.org/ which uses flows as
containers for machine learning models, importing [PMML](http://en.wikipedia.org/wiki/Predictive_Model_Markup_Language) model descriptions from _R_, _SAS_, _Weka_, _RapidMiner_, _KNIME_,
_SQL Server_, etc.

All Credit to Chris and Paco for the excellent work!

Current support for PMML includes:

 * [Random Forest](http://en.wikipedia.org/wiki/Random_forest) in [PMML 4.0+](http://www.dmg.org/v4-0-1/MultipleModels.html) exported from [R/Rattle](http://cran.r-project.org/web/packages/rattle/index.html)
 * [Linear Regression](http://en.wikipedia.org/wiki/Linear_regression) in [PMML 1.1+](http://www.dmg.org/v1-1/generalregression.html)
 * [Hierarchical Clustering](http://en.wikipedia.org/wiki/Hierarchical_clustering) and [K-Means Clustering](http://en.wikipedia.org/wiki/K-means_clustering) in [PMML 2.0+](http://www.dmg.org/v2-0/ClusteringModel.html)
 * [Logistic Regression](http://en.wikipedia.org/wiki/Logistic_regression) in [PMML 4.0.1+](http://www.dmg.org/v4-0-1/Regression.html)


Use in Storm Topology
---------------------

First include the clojars repo in your POM (or project.clj or sbt or wherever):

	<repositories>
		<repository>
			<id>clojars.org</id>
			<url>http://clojars.org/repo</url>
		</repository>
	</repositories>
	
And then add the dependency:

	<dependency>
  		<groupId>com.github.quintona</groupId>
  		<artifactId>storm-pattern</artifactId>
  		<version>0.0.2-SNAPSHOT</version>
	</dependency>

I have created a very simple trident topology to illustrate the usage, it is available from [here.](https://github.com/quintona/pattern-demo-topology). At a high level, this is all that is required:

	topology.newStream("valueStream", spout)
				.each(new Fields(fields), new ClassifierFunction(pmml_file),
						new Fields("prediction"))
				.each(new Fields("prediction"), new PrintlnFunction(),
						new Fields());
						
You simply need to create the Classifier function and pass in the model. 

## Build Instructions (if you are extending storm-pattern)
------------------
To build and then run its unit tests:

    mvn clean install

The following scripts generate a baseline (model+data) for the _Random
Forest_ algorithm. This baseline includes a reference data set -- 
1000 independent variables, 500 rows of simulated ecommerce orders --
plus a predictive model in PMML:

    ./src/py/gen_orders.py 500 1000 > orders.tsv
    R --vanilla < ./src/r/rf_pmml.R > model.log

This will generate `huge.rf.xml` as the PMML export for a Random
Forest classifier plus `huge.tsv` as a baseline data set for
regression testing.

Example Models
--------------
Check the `src/r/rattle_pmml.R` script for examples of predictive
models which are created in R, then exported using _Rattle_.
These examples use the popular
[Iris](http://en.wikipedia.org/wiki/Iris_flower_data_set) data set.

 * random forest (rf)
 * linear regression (lm)
 * hierarchical clustering (hclust)
 * k-means clustering (kmeans)
 * logistic regression (glm)
 * multinomial model (multinom)
 * single hidden-layer neural network (nnet)
 * support vector machine (ksvm)
 * recursive partition classification tree (rpart)
 * association rules

To execute the R script:

    R --vanilla < src/r/rattle_pmml.R

It is possible to extend PMML support for other kinds of modeling in R
and other analytics platforms.  Contact the developers to discuss on
the [cascading-user](https://groups.google.com/forum/?fromgroups#!forum/cascading-user)
email forum.


PMML Resources
--------------
 * [Data Mining Group](http://www.dmg.org/) XML standards and supported vendors
 * [PMML In Action](http://www.amazon.com/dp/1470003244) book 
 * [PMML validator](http://www.zementis.com/pmml_tools.htm)
