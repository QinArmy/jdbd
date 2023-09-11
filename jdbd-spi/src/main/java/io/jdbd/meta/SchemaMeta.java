package io.jdbd.meta;


import io.jdbd.session.OptionSpec;

import java.util.function.Function;

/**
 * <p>
 * This interface representing database schema meta data.
 * </p>
 *
 * @see DatabaseMetaData#tablesOfSchema(SchemaMeta, Function)
 * @since 1.0
 */
public interface SchemaMeta extends OptionSpec {

    /**
     * @return the {@link DatabaseMetaData} that create this instance .
     */
    DatabaseMetaData databaseMetadata();

    /**
     * @return database catalog or pseudo-catalog (eg: 'def')
     * @see #isPseudoCatalog()
     */
    String catalog();

    /**
     * @return database schema or pseudo-schema (eg: 'def')
     * @see #isPseudoSchema()
     */
    String schema();

    /**
     * @return true : {@link #catalog()} is pseudo-catalog
     */
    boolean isPseudoCatalog();

    /**
     * @return true : {@link #schema()} is pseudo-schema
     */
    boolean isPseudoSchema();

    /**
     * override {@link Object#toString()}
     *
     * @return schema info, contain : <ol>
     * <li>implementation class name</li>
     * <li>{@link #catalog()}</li>
     * <li>{@link #schema()}</li>
     * <li>{@link #isPseudoCatalog()}</li>
     * <li>{@link #isPseudoSchema()}</li>
     * <li>{@link System#identityHashCode(Object)}</li>
     * </ol>
     */
    @Override
    String toString();


}
