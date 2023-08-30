1. 发送 sql 包时 若 长参数 出异常，怎么处理,如:MySQL ClientPrepared 发送 命令包.
2. 对于 detectCustomCollations 属性,如果定制的字符集是未知的那么将无法转换成 java 字符庥.
3. 一个 SQLType 可以被映射成多种JAVA类型,如:MySQL 的 TIME 可以 映射成 LocalTime 和 Duration.

How to clear channel?

1. 把 ByteBuf 给 Task 之前 记录 packet header index.
2. 出错时 reset 到 之前记录的 index
3. 交给之类 清空 channel.

任务执行器 在任务正常结束 时要检查 ByteBuf 是否已 读取完毕,若没有读取完毕,则下一个任务会读取到 脏数据.

4. 为什么一定要坚持在创建 packet 的过程中使用 List<ByteBuf> 表示 multi packet?
    1. 因为 byte[] 数组的长度最大为 (1<< 30)(超出此长度将抛出异常),但 MySQL最大的包是 (1<< 30)
       ,这就使用是无法发送最大的包,因为每个单包必须有 header.
    2. 因为 可以避免不必要的复制.

5. 为什么要放弃 Geometric type 的定义
    1. jdbd 本身只驱动,把 驱动层的数据结构返回到上层应用是不明智的 就像也很少有人把 java.sql.Blob java.sql.Clob
       java.sql.SQLXML 返回到上层应用一样.
    2. Geometric type 理论上可以是 byte[] 也可以是 大到 几 T 的数据. hashCode 和 equals 方法的实现是困难的也是没有必要的,因为
       驱动的本质只是封装 和数据库的通信细节.

6. 为什么 statement 的 bind 方法不支持 浮点类型( float 和 double) 与其它数字类型相互转换?
    1. jdbd 的理念是 转换不能损失精度,而将浮点型是不精确的,它不能做到这一点.
    2. ResultRow 的 get 也不支持浮点型与其它数字类型的相互转换

7. 为什么 ResultRow 的 get 方法不支持浮点型( float 和 double) 与其它数字类型相互转换?
    1. jdbd 的理念是 转换不能损失精度,而将浮点型是不精确的,它不能做到这一点.
    2. statement 的 bind 也不支持浮点型与其它数字类型的相互转换
8. 为什么 return Publisher&lt; ResultStates> 而不是 Publisher&lt; ? extends ResultStates> ?
    * 后者虽然方便了 driver 开发者,但 application 开发者在调用 reactor.core.publisher.Mono.concatWith() 等方法是不方便
      在有冲突时应优先方便 application 开发者.

9. MySQL null_bit_set 在 execute 和 row为什么 不同?
    * sfsdf?

10. 为什么 text protocol 不支持 Publisher?
    * 因为 如果参数出错已发出的部分不可撒回.

11. 为什么 io.jdbd.session.Option 一定要加上 javaType ?
    * 在运行时获取 type 信息
    * 由于 option 太多,在不断增加的过程中 name 可能重复,但 java type 可能不同,若不加 javaType 则可能造成 bug.

12. 为什么 不支持 JDBC escape call syntax {call storedobject(?)} ？
    * 用中国人的话讲,那不是道.
    * 用老外的话讲,stupid.

13. 为什么要把 DatabaseSession 方法上 的 Option Map 重构成 Function?
    * map 能遍历,需要对未知的和不支持的 Option 做处理,这增加了编程工作
    * map 能修改,若不想被修改则需要再创建一个Map 对象
    * 这样一来 driver 开发者只需关心相应的 Option 即可.可以看作是责任的免除.

14. 为什么 jdbd 删除 auto reconnect ?
    * 在 reconnect 之后,事务信息并不能迁移,若支持可能造成糟糕的数据问题.
    * 保持 session 的 immutable 将使实现更简单.
    

    
