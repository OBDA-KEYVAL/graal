# Graal KeyVal

## How to build ? ##

* install [git](http://www.git-scm.com/)
* clone the repository
~~~
  git clone git@github.com:graphik-team/graal.git
~~~

* build the project
~~~
mvn package
~~~

## How to generate Javadoc? ##

~~~
mvn javadoc:javadoc
mvn javadoc:aggregate
~~~

## How to check code with code analyzer? ##

~~~
mvn pmd:check
mvn findbugs:check
~~~

## How to build graal when you don't want to get all stuff maven brings?

~~~
./prepare_ant.sh
ant
~~~
