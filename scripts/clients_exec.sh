#!/bin/sh


for i in `seq 1 30`; 
do
	java -cp messaging/dist/jar/Messaging.jar controller.Client_E Pedro 1989 54.93.196.124 &
done

