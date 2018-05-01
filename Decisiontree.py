#importing the packages that are required
from pyspark import SparkContext
from pyspark.mllib.tree import DecisionTree, DecisionTreeModel
from pyspark.mllib.util import MLUtils

#Creating the spark context
sc = SparkContext(appName="PythonDecisionTreeRegressionExample")

# training the train data in in libsvm format
trainingData = MLUtils.loadLibSVMFile(sc,"train_data1.txt")
# loading the test data in libsvm format
testData = MLUtils.loadLibSVMFile(sc,"test_data1.txt")
#training the model for Decision trees
model = DecisionTree.trainClassifier(trainingData, numClasses=12999, categoricalFeaturesInfo={},
                                     impurity='entropy', maxDepth=5, maxBins=32)

# Evaluate model on test data and compute Accuracy
predictions = model.predict(testData.map(lambda x: x.features))
labelsAndPredictions = testData.map(lambda lp: lp.label).zip(predictions)
testErr = labelsAndPredictions.filter(
    lambda lp: lp[0] == lp[1]).count() / float(testData.count())
print('Accuracy = %f '%testErr)


