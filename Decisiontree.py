from pyspark import SparkContext
# $example on$
from pyspark.mllib.tree import DecisionTree, DecisionTreeModel
from pyspark.mllib.util import MLUtils

sc = SparkContext(appName="PythonDecisionTreeRegressionExample")

# Split the data into training and test sets (30% held out for testing)
trainingData = MLUtils.loadLibSVMFile(sc,"train_data1.txt")
testData = MLUtils.loadLibSVMFile(sc,"test_data1.txt")
model = DecisionTree.trainClassifier(trainingData, numClasses=12999, categoricalFeaturesInfo={},
                                     impurity='entropy', maxDepth=5, maxBins=32)

# Evaluate model on test instances and compute test error
predictions = model.predict(testData.map(lambda x: x.features))
labelsAndPredictions = testData.map(lambda lp: lp.label).zip(predictions)
testErr = labelsAndPredictions.filter(
    lambda lp: lp[0] == lp[1]).count() / float(testData.count())
print('Accuracy = %f '%testErr)


