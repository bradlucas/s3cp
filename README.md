# s3cp
======

s3cp - Amazon S3 command line copy (cp) program

S3cp works like the Unix cp program allowing you to put files into S3 or to retrieve them from the command line.

## Installation

### To build s3cp

To build s3cp run Ant.


    $ ant

The dist folder will contain the compiled application name s3cp-cmdline-VERSION.jar. This jar is built with 'one-jar' so you can run it with the following command.

    $ java -jar s3cp-cmdline-VERSION.jar -h

### Amazon Keys

Before you can interact with your S3 account you need to setup a file with your access and secret key.

Create a directory called .s3cp under your home directory.

Inside the directory create a file called s3cp.properties

In the s3cp.properties file create the following entries:

    s3.accessKey=YOUR-VALUE-HERE
    s3.secretKey=YOUR-VALUE-HERE

### Alias

There is a script called s3cp.sh in ~/secp/src/scripts that you setup with an alias.

Edit the following with the correct path and put in your .bashrc file

     alias s3cp=$HOME/work/s3cp/src/scripts/s3cp.sh

With that, s3cp will run from the command line through the alias


## Usage

s3cp uses a URI syntax to reference the objects in your S3 account. For example, if you have a bucket called test-files with an object tmp/test.sh you'd be able to get it from S3 with the following command.

    $ s3cp s3://test-files/tmp/test.sh test.sh

### Copy to S3 [PUT]

    $ s3cp local-file s3://bucket/object[/]

If object has a trailing slash it will be assumed to mean a directory and the local-file's filename will be appended to object.

### Copy from S3 [GET]

    $ s3cp s3://bucket/object [local-file]

If local-file is not present a filename from object will be used in the current directory.


## Feedback

If you try s3cp and have a suggestion please let me know.

   brad@beaconhill.com
