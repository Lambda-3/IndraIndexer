#!/bin/bash
echo ---------------------------
echo Indra Packer v. 0.2.0
echo ---------------------------
echo

deploy=$1
dump=$2

for MODEL in w2v lsa esa glove
do
  for LANG in ko en de it pt es fr zh ja fa ar sv nl el ru
  do
    for NAME in wiki-2018
    do
    	cd $deploy	    
	if [ -e $MODEL/$LANG/$NAME ]; then
		if [[ $1 != esa* ]] ; then
			FULL="$MODEL-$LANG-$NAME.annoy.tar.gz"
		else
			FULL="$MODEL-$LANG-$NAME.lucene.tar.gz"
		fi

		echo "packing $FULL"
		tar -czf "$dump$FULL" $MODEL/$LANG/$NAME

		cd $dump
		md5sum "$FULL" > "$FULL.md5"
		md5sum -c "$FULL.md5"
	fi
    done
  done
done
