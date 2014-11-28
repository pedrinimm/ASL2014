#!/bin/sh

#DB
scp -i Documents/Master\ ETH/Amazon/DBkey.pem -rd Documents/workspace/messaging/dist/jar/server-Messaging.jar ubuntu@ec2-54-93-197-44.eu-central-1.compute.amazonaws.com:/home/ubuntu
scp -i Documents/Master\ ETH/Amazon/DBkey.pem -rd Documents/workspace/messaging/scripts ubuntu@ec2-54-93-197-44.eu-central-1.compute.amazonaws.com:/home/ubuntu


#Middle
#md1
scp -i Documents/Master\ ETH/Amazon/DBkey.pem -rd Documents/workspace/messaging/dist/jar/server-Messaging.jar ubuntu@ec2-54-93-131-45.eu-central-1.compute.amazonaws.com:/home/ubuntu/
scp -i Documents/Master\ ETH/Amazon/DBkey.pem -rd Documents/workspace/messaging/scripts ubuntu@ec2-54-93-131-45.eu-central-1.compute.amazonaws.com:/home/ubuntu/

#md2
scp -i Documents/Master\ ETH/Amazon/DBkey.pem -rd Documents/workspace/messaging/dist/jar/server-Messaging.jar ubuntu@ec2-54-93-196-124.eu-central-1.compute.amazonaws.com:/home/ubuntu/
scp -i Documents/Master\ ETH/Amazon/DBkey.pem -rd Documents/workspace/messaging/scripts ubuntu@ec2-54-93-196-124.eu-central-1.compute.amazonaws.com:/home/ubuntu/

#Client
#cl1
scp -i Documents/Master\ ETH/Amazon/DBkey.pem -rd Documents/workspace/messaging/dist/jar/client-Messaging.jar ubuntu@ec2-54-93-196-220.eu-central-1.compute.amazonaws.com:/home/ubuntu/
scp -i Documents/Master\ ETH/Amazon/DBkey.pem -rd Documents/workspace/messaging/scripts ubuntu@ec2-54-93-196-220.eu-central-1.compute.amazonaws.com:/home/ubuntu/

#cl2
scp -i Documents/Master\ ETH/Amazon/DBkey.pem -rd Documents/workspace/messaging/dist/jar/client-Messaging.jar ubuntu@ec2-54-93-197-37.eu-central-1.compute.amazonaws.com:/home/ubuntu/
scp -i Documents/Master\ ETH/Amazon/DBkey.pem -rd Documents/workspace/messaging/scripts ubuntu@ec2-54-93-197-37.eu-central-1.compute.amazonaws.com:/home/ubuntu/


