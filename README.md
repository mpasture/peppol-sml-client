# peppol-sml-client
This project contains the SML client library used by the SMP's to interact with the SML.
It is based on cipa-sml-client-library 2.2.3.
This library is usually only used within SMP servers, to communicate the changes to the central SML.

This project contains 2 main classes for talking to the PEPPOL SML:
  * `ManageServiceMetadataServiceCaller` which is used to change SMP assignments in the SML. This must be called for a new SMP to register it once at the SML.
  * `ManageParticipantIdentifierServiceCaller` which is used to manage the assignment of participants to SMPs. This must be invoked from the SMP server every time a new participant is registered (or an existing one is modified or deleted).
  
Both classes offer the possibility to set an optional custom `SSLSocketFactory` as well as a custom optional `HostnameVerifier`. The implementation of this is in the base class `AbstractSMLClientCaller`.

This project is used by the following projects:
  * [peppol-smp-server-library](https://github.com/phax/peppol-smp-server-library/) - the SMP server library with the shared code use in all SMP server implementations.
  * [peppol-smp-server](https://github.com/phax/peppol-smp-server/) - the SMP server with a database backend
  * [peppol-smp-server-lightweight](https://github.com/phax/peppol-smp-server-lightweight/) - the SMP server with a file-based backend

This project is licensed under EUPL 1.1 or MPL 1.1 - like CIPA e-Delivery.

#Building from source
This project is meant to be build by Maven 3.x.
It requires at least Java 1.6 to be build.
Use `mvn clean install` to build the project locally.

#Maven usage
Add the following to your pom.xml to use this artifact:
```
<dependency>
  <groupId>com.helger</groupId>
  <artifactId>peppol-sml-client</artifactId>
  <version>3.1.1</version>
</dependency>
```

---

On Twitter: <a href="https://twitter.com/philiphelger">Follow @philiphelger</a>
