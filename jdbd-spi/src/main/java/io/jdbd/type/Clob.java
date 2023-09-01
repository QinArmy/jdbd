package io.jdbd.type;

import org.reactivestreams.Publisher;

import java.util.function.Function;

/**
 * @see Text
 * @since 1.0
 */
public interface Clob extends PublisherParameter {


    Publisher<CharSequence> value();


    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // clob is a instance of {@link Clob}
     *              R flux  = function.apply(clob.value()) ;
     *
     *              // for example ,if use Project reactor , reactor.core.publisher.Flux
     *              clob.value(Flux::from)
     *                 .collectList()
     *
     *         </code>
     *     </pre>
     * </p>
     *
     * @param fluxFunc convertor function of Publisher ,for example : {@code reactor.core.publisher.Flux#from(org.reactivestreams.Publisher)}
     * @param <F>      F representing Flux that emit 0-N element or {@link Throwable}.
     * @return non-null Flux that emit just one element or {@link Throwable}.
     * @throws NullPointerException throw when
     *                              <ul>
     *                                  <li>fluxFunc is null</li>
     *                                  <li>fluxFunc return null</li>
     *                              </ul>
     */
    <F extends Publisher<CharSequence>> F value(Function<Publisher<CharSequence>, F> fluxFunc);

    static <T extends CharSequence> Clob from(Publisher<T> source) {
        return JdbdTypes.clobParam(source);
    }

}
