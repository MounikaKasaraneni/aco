#****** importing libraries that the pyspark need to run*******
from pyspark import SparkContext
from pyspark.ml.classification import NaiveBayes
from pyspark.ml.evaluation import MulticlassClassificationEvaluator
from pyspark.mllib.util import MLUtils
from pyspark.mllib.linalg import Vectors
from pyspark.mllib.regression import LabeledPoint
import numpy as np
from pyspark.sql import SparkSession
from pyspark import SparkContext
from pyspark.ml.linalg import Vectors, VectorUDT

#**** Intializing the spark context*****
sc = SparkContext(appName="PythonNaiveBayesExample")
# ******Reading the files and spliting them using comma******* 
data = sc.textFile( "train_data.csv").map(lambda line: line.split(",")).filter(lambda line: len(line)<=760).collect()
label=sc.textFile( "train_labels.csv").map(lambda line: line.split(",")).filter(lambda line: len(line)<=1).map(lambda line: float(line[0])).collect()
test= sc.textFile( "test_data.csv").map(lambda line: line.split(",")).collect()    
testlabel= sc.textFile("test_labels.csv").map(lambda line: line.split(",")).collect()   
#********Converting the data into RDD********
data_rdd=sc.parallelize(data)
labels_rdd=sc.parallelize(label)
labeled_points_rdd= data_rdd.map(lambda seq:(float(seq[-1]),Vectors.dense(seq[:-1])))
#********** Converting the RDD data into DataFrame
spark = SparkSession(sc)
hasattr(labeled_points_rdd, "toDF")
#labeled_points_rdd.toDF().show()
data_labels=spark.createDataFrame(labeled_points_rdd)
df = data_labels.selectExpr("_1 as label", "_2 as features")
print(df.show(5))
#******* Giving the model as Naive Bayes and modelType ="multinomial
nb = NaiveBayes(smoothing=1.0, modelType="multinomial")
model = nb.fit(df)
    
    # Make prediction and test accuracy.
predictionAndLabel = testFloat.map(lambda p: (model.predict(p.features), p.LabelFloat))
accuracy = 1.0 * predictionAndLabel.filter(lambda pl: pl[0] == pl[1]).count() / test.count()
print('model accuracy {}'.format(accuracy))
