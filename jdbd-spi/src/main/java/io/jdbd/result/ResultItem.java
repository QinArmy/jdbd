package io.jdbd.result;

import io.jdbd.JdbdException;

/**
 * <p>
 * This interface representing one item of result (update/query result).
 * <ul>
 *     <li>The update result always is represented by just one {@link ResultStates} instance.</li>
 *     <li>The query result is represented by following sequence :
 *         <ol>
 *             <li>one {@link ResultRowMeta} instance.</li>
 *             <li>0-N {@link ResultRow} instance.</li>
 *             <li>one {@link ResultStates} instance.</li>
 *         </ol>
 *         in  {@link OrderedFlux}.
 *     </li>
 * </ul>
 * This interface is base interface of following :
 *     <ul>
 *         <li>{@link ResultRowMeta}</li>
 *         <li>{@link ResultRow}</li>
 *         <li>{@link ResultStates}</li>
 *         <li>{@link CurrentRow}</li>
 *     </ul>
 * </p>
 *
 * @since 1.0
 */
public interface ResultItem {


    /**
     * @return the number of this Query/Update result, based one. The first value is 1 .
     */
    int getResultNo();


    static boolean isRowOrStatesItem(ResultItem item) {
        return !(item instanceof ResultRowMeta);
    }

    static boolean isRowItem(ResultItem item) {
        return item instanceof ResultRow;
    }

    static boolean isStatesItem(ResultItem item) {
        return item instanceof ResultStates;
    }

    static boolean isUpdateStatesItem(ResultItem item) {
        return item instanceof ResultStates && !((ResultStates) item).hasColumn();
    }

    static boolean isQueryStatesItem(ResultItem item) {
        return item instanceof ResultStates && ((ResultStates) item).hasColumn();
    }


    /**
     * <p>
     * This interface just is base interface of following :
     *     <ul>
     *         <li>{@link ResultRowMeta}</li>
     *         <li>{@link DataRow}</li>
     *     </ul>
     * </p>
     *
     * @since 1.0
     */
    interface ResultAccessSpec {

        /**
         * Returns the number of columns
         *
         * @return the number of columns
         */
        int getColumnCount();

        /**
         * @param indexBasedZero index based zero,the first value is 0 .
         * @return the suggested column title              .
         * @throws JdbdException if a database access error occurs
         */
        String getColumnLabel(int indexBasedZero) throws JdbdException;


        /**
         * <p>
         * Get column index , if columnLabel duplication ,then return last index that have same columnLabel.
         * </p>
         *
         * @param columnLabel column label
         * @return index based 0,the first column is 0, the second is 1, ..
         * @throws JdbdException if a database access error occurs
         */
        int getColumnIndex(String columnLabel) throws JdbdException;


    }


}
