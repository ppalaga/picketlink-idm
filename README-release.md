How to release Picketlink IDM 1.4.X
===================================

**1)** Ensure you have JDK 1.6 and Maven 3.1 in PATH

```
$mvn -version
Apache Maven 3.1.1 (0728685237757ffbf44136acec0402957f723d9a; 2013-09-17 17:22:22+0200)
Maven home: /home/mposolda/software/apache-maven-3.1.1
Java version: 1.6.0_32, vendor: Sun Microsystems Inc.
```

**2)** Ensure you're on branch 1.4

```
git checkout 1.4
```

**3)** Run:

```
mvn release:prepare -Pall
```

Answer to 3 questions at startup (assuming you are releasing tag 1.4.6.Final. Replace with the correct numbers for your release):
```
1.4.6.Final
1.4.6.Final
1.4.7.Final-SNAPSHOT
```

**4)** Push the new tag and changes to both origin and upstream repositories:

```
git push origin 1.4
git push origin 1.4.6.Final
git push upstream 1.4
git push upstream 1.4.6.Final
```

**5)** Run:

```
mvn release:perform -Pdistro
```

**6)** Go to [https://repository.jboss.org/nexus/index.html#stagingRepositories](https://repository.jboss.org/nexus/index.html#stagingRepositories) 
You log in then see a list of artifacts, find yours, check the checkbox, and then click "Close" at the top. Then find the artifacts again and click "Release" on top.

According my experience, it took some time until artifacts appear in public repository under https://repository.jboss.org/nexus/content/groups/public/org/picketlink/idm/picketlink-idm-api/1.4.6.Final/

However when going to [https://repository.jboss.org/nexus/#nexus-search;quick~org.picketlink.idm](https://repository.jboss.org/nexus/#nexus-search;quick~org.picketlink.idm) showing some artifact like `picketlink-idm-api` and then click on `JBoss Releases` repository, you should see them immediatelly. Picketlink IDM dependency in gatein can be also updated immediatelly to 1.4.6.Final.

