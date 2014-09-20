HTTP server with CGI support for UNIX-like systems
==================================================

Server supports processing several requests at the same time using threads (up to 10 by default).

CGI support is very basic. When the CGI mode is turned on, the server checks if the requested file is executable (it could be any file) and executes it if it is.

There is also inetd mode which delegates all request processing to a script.

Disclamer
---------

This project is nothing more but programming practice. The server should not be used in production environments. That having been said, I cannot be held responsible for anything if you do choose to use it.

Configuration file
------------------

Program uses the file http-server.conf in its working directory for its configuration. File format is simple: lines of 'parameter=value'.

Parameters:

* mode - working mode, http or inetd.
* cgi_enabled - if CGI mode is enabled (has effect only in HTTP mode).
* port - port that the server uses.
* document_root - folder with files that the server uses.
* inetd_service - path to the script that is used to process requests (in inetd mode).

Usage
-----

Compile:

    $ make

Create and edit the configuration file (look above for instructions):

    $ vim http-server.conf

Launch the server. The configuration file should be in current working directory.

    $ bin/http-server

The server should be up and running.
