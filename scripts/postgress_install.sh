#!/bin/sh

export LANGUAGE=en_US.UTF-8
export LANG=en_US.UTF-8
export LC_ALL=en_US.UTF-8
locale-gen en_US.UTF-8
sudo dpkg-reconfigure locales

sudo apt-get update
#install java
sudo apt-get install default-jdk

#install postgress
sudo apt-get install postgresql-client
sudo apt-get install postgresql postgresql-contrib
sudo apt-get install pgadmin3
sudo -u postgres psql postgres
sudo -u postgres createdb messaging
sudo -u postgres createuser --superuser $USER
psql messaging < messaging/db/messaging.sql