# REST Webservice

## Description

The server part is a shell script that uses standard streams to receive/send
data, so a super server (such as inetd) is required.

## inetd installation and configuration

Debian/Ubuntu instrutions:

Install *inetutils-inetd*:

    $ sudo apt-get install inetutils-inetd

Open configuration file with your preferred editor:

    $ sudo nano /etc/inetd.conf

Add this line at the end of the file:

    3333 stream tcp nowait your_user_name path_to/server.sh

Start service:

    $ sudo service inetutils-inetd start

That's it!
