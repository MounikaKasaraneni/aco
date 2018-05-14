s=0
k=1

while (( $s < 5));                                    
do
j=1
hdfs dfs -rm -r /rramine/input/inputFile.txt
hdfs dfs -put inputFile.txt /rramine/input/
rm -rf aco.jar
javac acomr.java
jar -cf aco.jar acomr*.class
hdfs dfs -rm -r /rramine/o1
hadoop jar aco.jar acomr /rramine/input /rramine/o1
rm -r part-r-00000
hdfs dfs -copyToLocal /rramine/o1/part-r-00000 .
while IFS= read -r line; do
if(($j > 1520)); then
  python3 columnsfetch.py ${line// /,}
  python3 libsvm.py
  sed '$11603' format.txt
  spark-submit decisionpy.py 
  fi
  j=$((j+k))

done < part-r-00000
  echo "*********** Iteration " $s >> accuracyoutputgraphs.txt
javac -classpath ".:/home/rramine/BigDataProject" accuracy.java 
java -cp ".:/home/rramine/BigDataProject" accuracy 
s=$((s+k))
rm -r /home/rramine/BigDataProject/accuracyoutput.txt
echo $s
done




