# VibeCheck
VibeCheck - glazbena socijalna mreza

## Git pravila
### Grane
pull request > develop > test > prod

### Keywords za commit
* feat:     new feature
* fix:      bug fix
* docs:     documentation changes
* style:    formatting, missing semicolons, etc (no logic change)
* refactor: code restructure (no feature/fix)
* test:     adding or updating tests
* chore:    build process, dependencies, tooling
* ci:       CI/CD configuration changes

- slobodno dodati nova

### Primjer Commita
keyword: kratki opis
Ovo je razlog zašto se napravilo X Y.


### Jira connections:
Link your development information to Jira work items
To link branches, commits, and pull requests to Jira, your team must include Jira keys in their development actions.

* Find the key for the Jira work item you want to link to, for example "JRA-123". You can find the key in several places in Jira:
    * On the board, keys appear at the bottom of a card.
    * On the work item's details, keys appear in the navigation at the top of the page.

* Check out a new branch in your repo, using the key in the branch name. For example, git checkout -b JRA-123-{branch-name}.

* When committing changes to your branch, use the key in your commit message to link those commits to the development panel in your Jira work item. For example, git commit -m "JRA-123 {summary of commit}".

* When you create a pull request, use the key in the pull request title.

After you push your branch, you'll see development information in your Jira work item. 

## Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/4.0.3/maven-plugin)
* [Create an OCI image](https://docs.spring.io/spring-boot/4.0.3/maven-plugin/build-image.html)
* [Spring Web](https://docs.spring.io/spring-boot/4.0.3/reference/web/servlet.html)
* [Spring Boot DevTools](https://docs.spring.io/spring-boot/4.0.3/reference/using/devtools.html)
* [SpringDoc OpenAPI](https://springdoc.org/)
* [Spring Security](https://docs.spring.io/spring-boot/4.0.3/reference/web/spring-security.html)
* [Spring Data JPA](https://docs.spring.io/spring-boot/4.0.3/reference/data/sql.html#data.sql.jpa-and-spring-data)
* [Spring Boot Actuator](https://docs.spring.io/spring-boot/4.0.3/reference/actuator/index.html)
* [HTTP Client](https://docs.spring.io/spring-boot/4.0.3/reference/io/rest-client.html#io.rest-client.restclient)
* [Liquibase Migration](https://docs.spring.io/spring-boot/4.0.3/how-to/data-initialization.html#howto.data-initialization.migration-tool.liquibase)

## Guides
The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)
* [SpringDoc OpenAPI](https://github.com/springdoc/springdoc-openapi-demos/)
* [Securing a Web Application](https://spring.io/guides/gs/securing-web/)
* [Spring Boot and OAuth2](https://spring.io/guides/tutorials/spring-boot-oauth2/)
* [Authenticating a User with LDAP](https://spring.io/guides/gs/authenticating-ldap/)
* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)
* [Building a RESTful Web Service with Spring Boot Actuator](https://spring.io/guides/gs/actuator-service/)