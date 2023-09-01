package io.jdbd.type;


import org.reactivestreams.Publisher;

import java.util.function.Function;


public interface Blob extends PublisherParameter {


    Publisher<byte[]> value();

    /**
     * <p>
     * This method is equivalent to following :
     * <pre>
     *         <code><br/>
     *             // blob is a instance of {@link Blob}
     *              R flux  = function.apply(blob.value()) ;
     *
     *              // for example ,if use Project reactor , reactor.core.publisher.Flux
     *              blob.value(Flux::from)
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
    <F extends Publisher<byte[]>> F value(Function<Publisher<byte[]>, F> fluxFunc);

    static Blob from(Publisher<byte[]> source) {
        return JdbdTypes.blobParam(source);
    }

}
