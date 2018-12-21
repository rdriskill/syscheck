 ## Running
```
cd ~/git/syscheck
mvn clean package
cp ~/syscheck/checkpoints.json ~/git/syscheck/target
cd ~/syscheck
java -jar ~/git/syscheck/target/syscheck.jar ~/git/syscheck/target/checkpoints.json
```