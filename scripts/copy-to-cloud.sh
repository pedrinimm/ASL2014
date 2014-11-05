#!/bin/sh

#DB
scp -i Documents/Master\ ETH/Amazon/DBkey.pem -rd Documents/workspace/messaging/ ubuntu@ec2-54-93-189-104.eu-central-1.compute.amazonaws.com:/home/ubuntu/

#Middle
scp -i Documents/Master\ ETH/Amazon/DBkey.pem -rd Documents/workspace/messaging/ ubuntu@ec2-54-93-189-135.eu-central-1.compute.amazonaws.com:/home/ubuntu/

#Client
scp -i Documents/Master\ ETH/Amazon/DBkey.pem -rd Documents/workspace/messaging/ ubuntu@ec2-54-93-190-9.eu-central-1.compute.amazonaws.com:/home/ubuntu/


#!/bin/sh

#DB
#scp -i Documents/Master\ ETH/Amazon/DBkey.pem -rd Documents/workspace/messaging/ ubuntu@ec2-54-93-189-104.eu-central-1.compute.amazonaws.com:/home/ubuntu/

#Middle
#scp -i Documents/Master\ ETH/Amazon/DBkey.pem -rd Documents/workspace/messaging/ ubuntu@ec2-54-93-189-135.eu-central-1.compute.amazonaws.com:/home/ubuntu/

#Client
#scp -i Documents/Master\ ETH/Amazon/DBkey.pem -rd Documents/workspace/messaging/ ubuntu@ec2-54-93-190-9.eu-central-1.compute.amazonaws.com:/home/ubuntu/
