# Reactive Java Database Driver SPI

The Reactive Java Database Driver (JDBD) project brings reactive(non-blocking) programming APIs to relational
databases.[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.jdbd/jdbd-spi/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.jdbd/jdbd-spi)

# Design Philosophy

1. support standard
2. more dialectal
3. more practical
4. more efficient

### How to start ?

#### Maven

for example: use jdbd-mysql

```xml
<dependency>
    <groupId>io.jdbd.mysql</groupId>
    <artifactId>jdbd-mysql</artifactId>
    <version>0.8.0</version>
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
