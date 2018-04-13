
from __future__ import print_function

from pyspark import SparkContext
from pyspark.mllib.util import MLUtils
from pyspark.mllib.linalg import Vectors
from pyspark.mllib.classification import LogisticRegressionWithLBFGS, LogisticRegressionModel
from pyspark.mllib.classification import LogisticRegressionWithSGD, LogisticRegressionModel
from pyspark.mllib.regression import LabeledPoint
from pyspark.mllib.linalg import SparseVector

if __name__ == "__main__":
	sc = SparkContext(appName="PythonLRExample")
	
	#data = sc.textFile("/home/mkasara/project/file.txt")
	examples = MLUtils.loadLibSVMFile(sc, "file.txt")

	# Build the model
	model = LogisticRegressionWithLBFGS.train(examples)
	#lrm = LogisticRegressionWithSGD.train(sc.parallelize(examples), iterations=10)






