#********Importing the pyspark statement
from pyspark import SparkContext
from pyspark.ml import Pipeline
from pyspark.ml.classification import DecisionTreeClassifier
from pyspark.ml.feature import StringIndexer, VectorIndexer
from pyspark.ml.evaluation import MulticlassClassificationEvaluator
from pyspark.sql import SparkSession
from pyspark import SparkContext
from pyspark.ml.linalg import Vectors, VectorUDT
from pyspark.mllib.evaluation import MultilabelMetrics
#********* Intializing the spark context
sc = SparkContext(appName="DecisionTreeClassifier")
#*********** Reading the whole file and spliting the data by ,
data = sc.textFile( "train_data.csv").map(lambda line: line.split(",")).filter(lambda line: len(line)<=760).collect()
label=sc.textFile( "train_labels.csv").map(lambda line: line.split(",")).filter(lambda line: len(line)<=1).map(lambda line: float(line[0])).collect()
test= sc.textFile( "test_data.csv").map(lambda line: line.split(",")).filter(lambda line: len(line)<=760).collect()
testlabel= sc.textFile("test_labels.csv").map(lambda line: line.split(",")).filter(lambda line: len(line)<=1).map(lambda line: float(line[0])).collect()   
#****************** Converting the data into RDD format
data_rdd=sc.parallelize(data)
test_rdd=sc.parallelize(test)
testlabels_rdd= test_rdd.map(lambda seq:(float(seq[-1]),Vectors.dense((seq[:-2]))))
labeled_points_rdd= data_rdd.map(lambda seq:(float(seq[-1]),Vectors.dense((seq[:-2]))))
#******** creating the spark session to convert RDD into Dataframe
spark = SparkSession(sc)
hasattr(testlabels_rdd, "toDF")
hasattr(labeled_points_rdd, "toDF")
test_labels=spark.createDataFrame(testlabels_rdd)
data_labels=spark.createDataFrame(labeled_points_rdd)
data = data_labels.selectExpr("_1 as label", "_2 as features")
test_labels = test_labels.selectExpr("_1 as label", "_2 as features")
print(data.show(5))
print(test_labels.show(5))

# Assinging the training and testing models
trainingData = data
testingData = test_labels
print("fitting data")
labelIndexer = StringIndexer(inputCol="label", outputCol="indexedLabel").fit(data)
featureIndexer =VectorIndexer(inputCol="features", outputCol="indexedFeatures").fit(data)
# **************Train a DecisionTree model.
dt = DecisionTreeClassifier(labelCol="indexedLabel", featuresCol="indexedFeatures")
# ****************Chain indexers and tree in a Pipeline
pipeline = Pipeline(stages=[labelIndexer, featureIndexer, dt])
print("train")
# **************Train model.  This also runs the indexers.
model = pipeline.fit(trainingData)
print("testing")
# ***********Make predictions.
predictions = model.transform(testingData)
#predictionsIndexer = StringIndexer(inputCol="prediction", outputCol="prediction1").fit(predictions).transform(predictions)
# Select example rows to display.
predictions.select("label","prediction","features").show(5)
evaluator = MulticlassClassificationEvaluator(labelCol="indexedLabel", predictionCol="label", metricName="accuracy")
# Select (prediction, true label) and compute test error
accuracy = evaluator.evaluate(predictions)
print("Accuracy = %g " % (accuracy))
