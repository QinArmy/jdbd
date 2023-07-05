package io.jdbd;

public interface DriverVersion extends VersionSpec {

    /**
     * <p>  return driver name follow below:
     *     <ul>
     *         <li>If developer of implementation is database vendor,then should be database product name(eg:MySQL,DB2)</li>
     *         <li>Else ,then should be driver class name(eg:io.jdbd.mysql.Driver) </li>
     *     </ul>
     * </p>
     */
    String getName();




}
