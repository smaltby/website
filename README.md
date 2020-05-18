# seanmaltby.com

This is the source code for my personal website. It largely serves as a place
to showcase interesting projects I've worked on.

## Running

JDK 11 or greater is required to build the server.

A PostgreSQL database is required to run the server.

The following environment variables need to be set for the application run.

| environment variable   | description                              |
| ---------------------- | ---------------------------------        |
| SPRING_DATASOURCE_URL  | JDBC url pointing to the database        |
| SPRING_PROFILES_ACTIVE | active profile, either 'local' or 'prod' |
| ADMIN_PASSWORD         | the admin password                       |

`./gradlew bootJar` will generate an executable jar file with an embedded Tomcat server
that can be run with `./website-<version>.jar`

Alternatively, `./gradlew bootWar` will generate a war file which can be
deployed to any servlet container (such as Tomcat).