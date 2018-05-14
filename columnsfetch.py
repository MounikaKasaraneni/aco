import sys
input=sys.argv[1]
inputwords=input.split(",")
writeout= open("revised.csv",'w')
with open("test_data.csv",'r') as file:
      for line in file:
        filerows=line.split(",")
        zero=filerows[int(0)]
        writeout.write(zero)
        writeout.write(",")
        for i in range (0,len(inputwords)-1):
            j=inputwords[i]
            x=float(j)
            num=filerows[int(x)+1]
            writeout.write(num)
            if(i!=(len(inputwords)-2)):
             writeout.write(",")
        writeout.write("\n")
			 
       







