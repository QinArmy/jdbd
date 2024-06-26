# Reactive Java Database Driver SPI

[![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/https/s01.oss.sonatype.org/io.jdbd/jdbd-spi.svg)](https://s01.oss.sonatype.org/content/repositories/snapshots/io/jdbd/jdbd-spi/)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.jdbd/jdbd-spi/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.jdbd/jdbd-spi)
[![Apache 2.0](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)
[![Java support](https://img.shields.io/badge/Java-21+-green?logo=java&logoColor=white)](https://openjdk.java.net/)

The Reactive Java Database Driver (JDBD) project brings reactive(non-blocking) programming APIs to relational databases.

# Design Philosophy

1. support standard
2. more dialectal
3. more practical
4. more efficient

#### the persistence frameworks that use jdbd-spi :

1. [Army](https://github.com/QinArmy/army)

### How to start ?

#### Maven

for example: use jdbd-mysql

```xml
<dependency>
    <groupId>io.jdbd.mysql</groupId>
    <artifactId>jdbd-mysql</artifactId>
    <version>0.11.9</version>
</dependency>
```

#### Java code

```java
public class HowToStartTests {

    private static DatabaseSessionFactory sessionFactory;

    @BeforeClass
    public void createSessionFactory() {
        final String url;
        final Map<String, Object> map = new HashMap<>();
        url = "jdbd:mysql://localhost:3306/army_test?factoryWorkerCount=30";
        map.put(Driver.USER, "army_w");
        map.put(Driver.PASSWORD, "army123");
        sessionFactory = Driver.findDriver(url).forDeveloper(url, map);
    }

    @Test
    public void howToStart() {
        final String sql;
        sql = "INSERT INTO mysql_types(my_boolean,my_bigint,my_datetime,my_datetime6,my_var_char200) VALUES (?,?,?,?,?)";
        final ResultStates resultStates;

        resultStates = Mono.from(sessionFactory.localSession())
                .flatMap(session -> session.bindStatement(sql)
                        .bind(0, JdbdType.BOOLEAN, true)
                        .bind(1, JdbdType.BIGINT, null)
                        .bind(2, JdbdType.TIMESTAMP, LocalDateTime.now())
                        .bind(3, JdbdType.TIMESTAMP_WITH_TIMEZONE, OffsetDateTime.now(ZoneOffset.UTC))
                        .bind(4, JdbdType.VARCHAR, "中国 QinArmy's jdbd \n \\ \t \" \032 \b \r '''  \\' ")
                        .executeUpdate(Mono::from)
                )
                .block();

        Assert.assertNotNull(resultStates);
        Assert.assertEquals(resultStates.affectedRows(), 1);
    }


    @Test
    public void localTransaction() {
        final String sql;
        sql = "INSERT INTO mysql_types(my_boolean,my_bigint,my_datetime,my_datetime6,my_var_char200) VALUES (?,?,?,?,?)";
        Mono.from(sessionFactory.localSession())

                .flatMap(session -> Mono.from(session.startTransaction(TransactionOption.option(Isolation.REPEATABLE_READ, false)))      // start new transaction
                        .doOnSuccess(s -> {
                            Assert.assertTrue(s.inTransaction()); // session in  transaction block
                            Assert.assertTrue(session.inTransaction()); // session in  transaction block
                        })
                        .flatMap(t -> session.bindStatement(sql)
                                .bind(0, JdbdType.BOOLEAN, true)
                                .bind(1, JdbdType.BIGINT, null)
                                .bind(2, JdbdType.TIMESTAMP, LocalDateTime.now())
                                .bind(3, JdbdType.TIMESTAMP_WITH_TIMEZONE, OffsetDateTime.now(ZoneOffset.UTC))
                                .bind(4, JdbdType.VARCHAR, "中国 QinArmy's jdbd \n \\ \t \" \032 \b \r '''  \\' ")
                                .executeUpdate(Mono::from))
                        .then(Mono.defer(() -> Mono.from(session.commit())))    // commit transaction
                        .then(Mono.from(session.close())) // close session if no error, driver don't send message to database server before subscribing
                        .onErrorResume(error -> Mono.from(session.close())  // close session when occur error
                                .then(Mono.error(error))
                        )

                )
                .block();
    }

    @Test
    public void localTransactionAndCommitChain() {
        final String sql;
        sql = "INSERT INTO mysql_types(my_boolean,my_bigint,my_datetime,my_datetime6,my_var_char200) VALUES (?,?,?,?,?)";
        Mono.from(sessionFactory.localSession())

                .flatMap(session -> Mono.from(session.startTransaction(TransactionOption.option(Isolation.REPEATABLE_READ, false)))      // start new transaction
                        .doOnSuccess(s -> {
                            Assert.assertTrue(s.inTransaction()); // session in  transaction block
                            Assert.assertTrue(session.inTransaction()); // session in  transaction block
                        })
                        .flatMap(t -> session.bindStatement(sql)
                                .bind(0, JdbdType.BOOLEAN, true)
                                .bind(1, JdbdType.BIGINT, null)
                                .bind(2, JdbdType.TIMESTAMP, LocalDateTime.now())
                                .bind(3, JdbdType.TIMESTAMP_WITH_TIMEZONE, OffsetDateTime.now(ZoneOffset.UTC))
                                .bind(4, JdbdType.VARCHAR, "中国 QinArmy's jdbd \n \\ \t \" \032 \b \r '''  \\' ")
                                .executeUpdate(Mono::from))
                        .then(Mono.defer(() -> Mono.from(session.commit(Option.singleFunc(Option.CHAIN, Boolean.TRUE)))))    // commit chain transaction
                        .doOnSuccess(o -> {
                            Assert.assertTrue(o.isPresent());
                            Assert.assertTrue(o.get().inTransaction()); // session in new transaction block
                        }).then(Mono.defer(() -> Mono.from(session.commit())))
                        .then(Mono.from(session.close())) // close session if no error, driver don't send message to database server before subscribing
                        .onErrorResume(error -> Mono.from(session.close())  // close session when occur error
                                .then(Mono.error(error))
                        )
                )
                .block();
    }

}
```

### two kind DatabaseSession

* io.jdbd.session.LocalDatabaseSession support local transaction
* io.jdbd.session.RmDatabaseSession support XA transaction

### four kind io.jdbd.statement.Statement

* io.jdbd.statement.StaticStatement static statement,no parameter placeholder
* io.jdbd.statement.BindStatement client-prepare and server-prepare adaptor
* io.jdbd.statement.PreparedStatement server-prepare
* io.jdbd.statement.MultiStatement multi-statement

### currently exists tow implementation of jdbd spi

* [jdbd-mysql](https://github.com/QinArmy/jdbd-mysql "jdbd-mysql")
* [jdbd-postgre](https://github.com/QinArmy/jdbd-postgre "jdbd-postgre")

## License

This project is released under version 2.0 of the [Apache License][l].

[l]: https://www.apache.org/licenses/LICENSE-2.0
