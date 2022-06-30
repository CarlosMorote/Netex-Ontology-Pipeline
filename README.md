# NeTEx - Linked data - Pipeline

### Morote García, Carlos

#### ETSIINF, UPM

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

## Run the code

In order to run the code we have to use an IDE, such as IntelliJ, or use a terminal.
Nonetheless a set of parameters must be provided so the program knows in which
direction execute the pipeline and where the files are located. However, the option
`-h` is available to further details.

The Main class that must be invoked is called `Main`. The first parameter is the
direction of the Pipeline (`-f`, `--flow`). It is a optional parameter but the default 
value is _"NeTEx &rarr; Turtle"_ The other two parameters are mandatory and are the
loaction of the input and output folders/files.
