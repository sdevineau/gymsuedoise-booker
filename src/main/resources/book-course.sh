#!/bin/bash
currentDir="$( cd "$( dirname "$0" )" && pwd )"
echo "Working in  the directory $currentDir"
cd $currentDir
executable="./gymsuedoise-booker.jar"
if [ -f $executable ]
then
	java -jar $executable 	
else
	echo "$executable is not found in the current directory. Please run the script in the same directory than $executable."
fi

