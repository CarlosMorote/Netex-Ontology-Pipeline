# NeTEx - Linked data - Pipeline


[![Generic badge](https://img.shields.io/badge/Status-Developing-yellow)](https://shields.io/)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/oeg-upm/Mapeathor/blob/master/LICENSE)

---

## Overview

As part of the development of the master thesis for the master in data science for 
the Universidad Politécnica de Madrid, together with the Ontology Engineering Group,
we have implemented a proof of concept of a converter between two formats to
represent data related to public transport. These formats will be NeTEx in
its Norwegian format and an ontology designed by the OEG. 
The project developed in Java seeks to satisfy this need while facilitating 
the incremental implementation in order to integrate it natively in 
Open Trip Planner.

 - Ontology definition [here](https://oeg-upm.github.io/mobility/index.html#)
 - Transmodel (NeTEx) definition [here](https://www.transmodel-cen.eu/model/index.htm)

## Requirements 

Before even compile and run the code the follow requirements mus be fulfilled
 - Get [Java 11](https://www.oracle.com/es/java/technologies/javase/jdk11-archive-downloads.html) installed
 - Get [Maven](https://maven.apache.org) installed
 - Data to cast in its origin format (NeTEx or Turtle)
   - **Netex** compressed in a zip. `ouput.netex.zip`
   - **Turtle** as a _ttl_ exetension single file. `linked.ttl.zip`

## Run the code

To generate a runnable jar file we must use _maven_. 
From terminal use the following command to generate an executable jar.

      mvn clean package

<br>

In order to run the code we have to use an IDE, such as IntelliJ, or use a terminal if the jar was previously generated.
Nonetheless a set of parameters must be provided so the program knows in which
direction execute the pipeline and where the files are located. However, the option
`-h` is available to further details.

The Main class that must be invoked is called `Main`. The first parameter is the
direction of the Pipeline (`-f`, `--flow`). It is a optional parameter but the default 
value is _"NeTEx &rarr; Turtle"_ Posible values: `N-T` and `T-N`. The other two parameters are mandatory and are the
loaction of the input and output folders/files. **Without spaces**

Get _help_ with:

    java -jar <complied_file.jar> -h

Example from Turtle to NeTEx:
    
    java -jar <compiled_file.jar> --flow T-N "./linked.ttl" "./output/"

Example from NeTEx to Turtle:
    
    java -jar <compiled_file.jar> --flow N-T "./output.netex.zip" "./output/linked.ttl"

---

## Authors

 - [Morote García, Carlos](https://github.com/CarlosMorote)