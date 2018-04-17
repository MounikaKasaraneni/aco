from pyspark import SparkContext
from pyspark.mllib.classification import SVMWithSGD, SVMModel
from pyspark.mllib.regression import LabeledPoint
from pyspark.mllib.util import MLUtils
sc = SparkContext(appName="SVM")

# Split the data into training and test sets (30% held out for testing)

def parsePoint(line):
    values = [float(x) for x in line.split(',')]
    return LabeledPoint(values[0], values[1:])

data = sc.textFile("train_data.csv")
parsedData = data.map(parsePoint)
testdata = sc.textFile("test_data.csv")
testData1 = testdata.map(parsePoint)
# Build the model
model = SVMWithSGD.train(parsedData, iterations=100)
predictions = model.predict(testData1.map(lambda x: x.features))
labelsAndPredictions = testData1.map(lambda lp: lp.label).zip(predictions)
testErr = labelsAndPredictions.filter(lambda lp: lp[0] == lp[1]).count() / float(testData1.count())
print('Accuracy = %f '%testErr)
