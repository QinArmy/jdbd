package io.jdbd;

import io.jdbd.session.OptionSpec;

/**
 * <p>
 * This interface is base interface of following
 *     <ul>
 *         <li>{@link DriverVersion}</li>
 *         <li>{@link io.jdbd.session.ServerVersion}</li>
 *     </ul>
 * <br/>
 *
 * @since 1.0
 */
public interface VersionSpec extends OptionSpec {

    /**
     * Retrieves the driver's major version number. Initially this should be 1.
     *
     * @return this driver's major version number
     */
    int getMajor();

    /**
     * Gets the driver's minor version number. Initially this should be 0.
     *
     * @return this driver's minor version number
     */
    int getMinor();


    String getVersion();


    int getSubMinor();

    boolean meetsMinimum(int major, int minor, int subMinor);

}
