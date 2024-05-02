# Truck App
I tried to build apis for Truck  App



<p align="center">
  <img src="https://www.underworldcode.org/content/images/size/w600/2020/08/Moby-logo-1.png" width="200">

</p>




## Quick start

### Clone the repository

```
git clone git@github.com:ZuhriddinShamsiddinov/truck-app.git
```

### Build the project

```
cd getting-started-with-spring-boot
docker-compose build
```

The project includes a ``web`` service, running the Java code, and a ``db`` service, running a Postgres database.
See the ``docker-compose.yml`` file for details.

### Run the project

```
docker-compose up
````

Containers for both services will be launched. The project can be reached at http://localhost:8090.

Hot-reloading is enabled (i.e. changes to the Java code in the project will cause the application to restart so that they
can be used.)


## How to

### Run the local project on a different port

The container runs a Tomcat server listening on port 8080. The ``docker-compose.yml`` file is set up to
expose this port to the Docker host at port 8000, but you are free to change it as you wish - edit the ``ports`` directive:

```
services:
  web:
    [...]
    ports: 
      - 8000:8080
```
