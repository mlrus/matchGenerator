# Match Generator

Generate keymatches from a file of labels and text. The generated results provide the keymatch definitions for ``trieMATCHER``. The precomputed naïve Bayes probabilities will be aggregated by the matcher. 

## Sample Input
```
...
book://guten/item10023,the day of days an extravaganza
book://guten/item10024,the day of the beast
book://guten/item10025,the day of the confederacy; a chronicle of the embattled south
book://guten/item10026,the day of the dog
book://guten/item10028,the days before yesterday
book://guten/item10034,the death of balder
book://guten/item10035,the death of lord nelson
book://guten/item10036,the death of the lion
book://guten/item10037,the death of wallenstein
book://guten/item10054,the descent of man
book://guten/item10055,the descent of man and other stories
...
```

## Sample Output
```
...
#0.600,death,KeywordMatch,book://guten/item10034,the death of balder
#0.600,death,KeywordMatch,book://guten/item10036,the death of the lion
#0.600,death,KeywordMatch,book://guten/item10037,the death of wallenstein
#0.583,day,KeywordMatch,book://guten/item10024,the day of the beast
#0.583,day,KeywordMatch,book://guten/item10026,the day of the dog
#0.550,descent,KeywordMatch,book://guten/item10054,the descent of man
#0.517,of man,PhraseMatch,book://guten/item10054,the descent of man
...
```

## Usage
```
java -cp src/lib/kGen.jar -DshowAsGened=false -DshowZeros=false -Dexplain=false bayesMatchGenerator/BayesMatches src/INPUTS/input.2col /tmp/bayesMatches.output.km > /tmp/output.log 2> /tmp/trace.log 
```
