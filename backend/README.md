# Backend Apis


|GET&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;| /services&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;| Get list of services available <br/>
|POST&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;| /newservice &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;| Create new service. Example - {
   "name":"linkedin",
   "url":"https://www.linkedin.com"
}<br/>
|DELETET&nbsp;| /delete/:name &nbsp;&nbsp;| Delete service name in path variable<br/>
|GET&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;| /listen &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;| Get status list of service urls<br/>
|GET&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;| /batchUpdate &nbsp;&nbsp;&nbsp;| Update service url status in DB<br/>


# ENV Variables
SERVER_PORT=8888
DB_HOST=localhost
DB_PORT=3306
DB_DATABASE=simpleservicepoller
DB_USER=root
DB_PASSWORD=secret

## Dockerized MySQL
* User: root
* Password: secret
* Database: simpleservicepoller
Note: for easy access we are using the root user.
Contains a volume for permanent storage of data. On system restart the data is available again.
```
To delete volume: docker volume rm services-data-local
```

## Useful commands to start the application:
Start docker database then build the project. Execute from backend directory:
```
1. docker-compose -f docker/mysql/mysql.yml up -d
```
```
2. mvn clean install
```
```
3. docker-compose -f docker/mysql/mysql.yml down
```
