from __future__ import print_function

# $example on$
from pyspark.ml.classification import LogisticRegression
# $example off$
from pyspark.sql import SparkSession

if __name__ == "__main__":
    spark = SparkSession \
        .builder \
        .appName("MulticlassLogisticRegressionWithElasticNet") \
        .getOrCreate()

    # $example on$
    # Load training data
    training = spark \
        .read \
        .format("libsvm") \
        .load("format.txt")

    lr = LogisticRegression(maxIter=2, regParam=0.1, elasticNetParam=0.1)

    # Fit the model
    lrModel = lr.fit(training)
    trainingSummary = lrModel.summary

    accuracy = trainingSummary.accuracy
    
    print("Accuracy: %s\n"
          % (accuracy*100))
    # $example off$

    file=open("accuracyoutput.txt",'a')
    file1=open("accuracyoutputgraphs.txt",'a')
    file.write(str(accuracy*100))
    file.write("\n")
    file1.write(str(accuracy*100))
    file1.write("\n")
    spark.stop()