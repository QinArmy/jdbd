package io.jdbd.statement;

import io.jdbd.meta.DataType;
import io.jdbd.result.ResultStates;

/**
 * <p>
 * This interface representing OUT (<strong>NOTE</strong>: is OUT not INOUT) parameter of stored procedure/function.
 * You create instance of {@link OutParameter} by {@link OutParameter#out(String)}.
 *<br/>
 * <p>
 * OUT parameter is usually supported by following statement :
 *     <ul>
 *         <li>{@link PreparedStatement}</li>
 *         <li>{@link BindStatement}</li>
 *     </ul>
 *<br/>
 * <p>
 *     <strong>NOTE</strong>: If procedure produce multi-result in single statement ,then out parameter usually present in last result,
 *     see {@link ResultStates#hasMoreResult()} is false.
 *<br/>
 * <p>
 * For example :
 * <pre>
 *     <code><br/>
 *       // PostgreSQL procedure
 *       CREATE  PROCEDURE my_test_procedure( IN my_input INT, OUT my_out INT, INOUT my_inout INT)
 *           LANGUAGE plpgsql
 *       AS $$
 *
 *       BEGIN
 *           my_out = my_input + 1;
 *           my_inout = my_inout + 8888;
 *       END;
 *       $$;
 *        <br/>
 *        LocalDatabaseSession session;
 *        BindStatement stmt = session.bindStatement("CALL my_test_procedure( ? , ? , ?)");
 *        stmt.bind(0,JdbdType.INTEGER,1);
 *        stmt.bind(1,JdbdType.INTEGER,OutParameter.out("my_out")); //  must be non-null
 *        stmt.bind(2,JdbdType.INTEGER,6666);
 *
 *        Flux.from(stmt.executeQuery())
 *              .map(this::handleOutParameter)
 *
 *       private Map&lt;String, Integer> handleOutParameter(final ResultRow row) {
 *           Map&lt;String, Integer> map = new HashMap&lt;>(4);
 *           map.put(row.getColumnLabel(0), row.get(0, Integer.class));
 *           map.put(row.getColumnLabel(1), row.get(1, Integer.class));
 *           return map;
 *       }
 *     </code>
 * </pre>
 *<br/>
 *
 * <p>
 * <strong>NOTE</strong>: this interface can use in function..
 * For example :
 * <pre>
 *     <code><br/>
 *       // PostgreSQL function (not procedure)
 *       CREATE  FUNCTION my_test_function( IN my_input INT, OUT my_out INT, INOUT my_inout INT)
 *           LANGUAGE plpgsql
 *       AS $$
 *
 *       BEGIN
 *           my_out = my_input + 1;
 *           my_inout = my_inout + 8888;
 *       END;
 *       $$;
 *        <br/>
 *        LocalDatabaseSession session;
 *        BindStatement stmt = session.bindStatement("SELECT t.* FROM my_test_function(? , ? , ?) AS t");
 *        stmt.bind(0,JdbdType.INTEGER,1);
 *        stmt.bind(1,JdbdType.INTEGER,OutParameter.out("my_out")); //  must be non-null {@link String}.
 *        stmt.bind(2,JdbdType.INTEGER,6666);
 *
 *        Flux.from(stmt.executeQuery())
 *              .map(this::handleOutParameter)
 *
 *       private Map&lt;String, Integer> handleOutParameter(final ResultRow row) {
 *           Map&lt;String, Integer> map = new HashMap&lt;>(4);
 *           map.put(row.getColumnLabel(0), row.get(0, Integer.class));
 *           map.put(row.getColumnLabel(1), row.get(1, Integer.class));
 *           return map;
 *       }
 *     </code>
 * </pre>
 *<br/>
 *
 * @see ParametrizedStatement#bind(int, DataType, Object)
 * @since 1.0
 */
public interface OutParameter extends Parameter {

    String name();


    /**
     * override {@link Object#hashCode()}
     */
    @Override
    int hashCode();

    /**
     * override {@link Object#equals(Object)}
     */
    @Override
    boolean equals(Object obj);

    /**
     * override {@link Object#toString()}
     *
     * @return out parameter info, contain {@link System#identityHashCode(Object)},
     */
    @Override
    String toString();

    /**
     * @param name OUT parameter name,empty is allowed by some database , for example : MySQL ,PostgreSQL,because these database don't need.
     * @throws NullPointerException throw when name is null .
     */
    static OutParameter out(String name) {
        return JdbdParameters.outParam(name);
    }


}
