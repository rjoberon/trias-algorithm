trias-algorithm
===============

An algorithm for computing triadic concepts which fulfill minimal support constraints. 

Trias was first described in 
[this paper](http://www.bibsonomy.org/bibtex/2797d40e05a48f4343d7695dac87b5870/jaeschke), more details can be found in the paper 

[Discovering Shared Conceptualizations in Folksonomies](http://www.bibsonomy.org/bibtex/218e8babe208fae2c0342438617b0ec31/jaeschke).
Robert Jäschke, Andreas Hotho, Christoph Schmitz, Bernhard Ganter, and Gerd Stumme. Web Semantics: Science, Services and Agents on the World Wide Web 6(1):38-53 (February 2008)

Other papers related to Trias can be found on BibSonomy tagged with [trias](http://www.bibsonomy.org/user/jaeschke/trias).


compiling
---------

You need git, Java and Maven2 to download and compile the code. Then
you can do:

```shell
git clone git://github.com/rjoberon/bibsonomy-tools.git
cd bibsonomy-tools
mvn install 
```
You can find the compiled JAR file in the `target` folder. 

running/developing
------------------

Have a look at the wiki pages on [usage](wiki/Usage) and [implementation](wiki/Implementation).

licensing 
--------- 

Please see the file [LICENSE.txt](https://github.com/rjoberon/trias-algorithm/blob/master/LICENSE.txt)

homepage
--------

https://github.com/rjoberon/trias-algorithm
