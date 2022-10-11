# LocalStack Demonstration using the Vaadin Flow implementation of the Spring PetClinic

This repository contains a simple demonstration of using [LocalStack](https://localstack.cloud/) for testing against
emulated AWS services.

This is a modified version of
the [Vaadin Flow Spring PetClinic](https://github.com/spring-petclinic/spring-petclinic-vaadin-flow). When a new Pet is
added, a very simple SQS message is enqueued. This isn't necessarily a "production-ready implementation", but it does
provide a "realistic" way of interacting with an AWS SQS queue that can then be tested against LocalStack.

Two versions of testing against LocalStack are presented:

* One using the [fabric8.io docker-maven-plugin](https://dmp.fabric8.io/) to launch a LocalStack container
* One using [TestContainers](https://www.testcontainers.org/) to launch a LocalStack container

Each presents interesting tradeoffs. For instance, the docker-maven-plugin approach allows a single container to be
launched and used for all test classes, while the TestContainers approach launches a new container for each class. One
is managed via XML, the other Java code. And dozens more comparisons could be made. It's important to note that you
wouldn't normally have both approaches in the same project - try and experience both, but stick with just one in your
own projects.

To find the relevant classes and files, search for "SQS" and "LocalStack" within the project. Alternatively, view commit https://github.com/christopher-nash/localstack-spring-petclinic-demo/commit/b4f650a0dea5355552e08ab5b302ec2a4c3f222a
which contains all modified classes and files demonstrating LocalStack.

Additional information about the [Spring PetClinic](https://spring-petclinic.github.io/) project is available at their
[GitHub organization](https://github.com/spring-petclinic).
