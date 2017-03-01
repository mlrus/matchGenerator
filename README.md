# Match Generator

Generate keymatches from a file of labels and text. The generated results provide the keymatch definitions for ``trieMATCHER``.  

## Usage
```
java -cp src/lib/kGen.jar -DshowAsGened=false -DshowZeros=false -Dexplain=false bayesMatchGenerator/BayesMatches src/INPUTS/symtwo.2col /tmp/bayesMatches.symtwo.km > /tmp/output.log 2> /tmp/trace.log 
```
