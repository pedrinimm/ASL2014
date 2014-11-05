#!/bin/sh


for i in `seq 1 20`; 
do
	java -cp messaging/dist/jar/Messaging.jar controller.Client_A Pepe 1989 localhost
done

for i in `seq 1 10`; 
do
	java -cp messaging/dist/jar/Messaging.jar controller.Client_B Pepe 1989 localhost
done