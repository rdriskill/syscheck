# syscheck

## What does it do?
Checks that database and web resources are available. If one or more are not up, a notification is sent. 
 
## How do I run it?
Below is an example script to run the app.
```
cd ~/git/syscheck && mvn clean package
cd ~/syscheck && java -jar ~/git/syscheck/target/syscheck.jar ~/syscheck/checkpoints.json
```

It expects a JSON file containing resources to check. Below is an example of the JSON format.
```
[{
    "name": "Database 01",
    "type": "DATABASE",
    "url": "jdbc:mysql://db01.example.com/db",
    "user": "dbuser",
    "password": "dbpass",
    "enabled": "true"
},{
    "name": "Web 01",
    "type": "WEB",
    "url": "https://web01.example.com/app/serverCheck",
    "enabled": "true"
}]
```