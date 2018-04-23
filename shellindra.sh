#!/bin/bash
echo ---------------------------
echo Indra Shell v. 0.1.2
echo ---------------------------
echo

CONFIG="config"
PREPROCESS="pp"
INDEX="index"
LOAD="load"
TEST="test"

COMMAND=$1

if [ "$COMMAND" == "$CONFIG" ]; then
   mvn clean package
fi

if [ "$COMMAND" == "$PREPROCESS" ]; then
   java -cp indra-preprocessing/target/indra-preprocessing-1.1.0-rc2-jar-with-dependencies.jar org.lambda3.indra.pp.IndraPreProcessorCommandLine pp "${@:2}"
fi

if [ "$COMMAND" == "$INDEX" ]; then
   java -cp indra-index/target/indra-index-1.1.0-rc2-jar-with-dependencies.jar org.lambda3.indra.indexer.IndraIndexerCommandLine index "${@:2}"
fi

if [ "$COMMAND" == "$LOAD" ]; then
   java -cp indra-index/target/indra-index-1.1.0-rc2-jar-with-dependencies.jar org.lambda3.indra.loader.LoaderCommandLine load "${@:2}"
fi

if [ "$COMMAND" == "$TEST" ]; then
   java -cp indra-index/target/indra-index-1.1.0-rc2-jar-with-dependencies.jar org.lambda3.indra.loader.LoaderCommandLine test "${@:2}"
fi


