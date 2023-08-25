package io.jdbd.vendor.util;

import io.jdbd.Driver;
import io.jdbd.DriverVersion;
import io.jdbd.JdbdException;
import io.jdbd.session.Option;

import java.util.Objects;

public final class DefaultDriverVersion implements DriverVersion {


    public static DefaultDriverVersion from(final String name, final Class<? extends Driver> driverClass) {
        // https://maven.apache.org/shared/maven-archiver/
        final String version = driverClass.getPackage().getImplementationVersion();
        final DefaultDriverVersion driverVersion;
        if (version == null) {
            // here, driverClass run in  module test environment.
            driverVersion = new DefaultDriverVersion(name, "0.0.0", 0, 0, 0);
        } else {
            try {
                final int major, minor, subMinor;
                final int pointIndex1, pointIndex2, hyphenIndex;

                pointIndex1 = version.indexOf('.');
                major = Integer.parseInt(version.substring(0, pointIndex1));

                pointIndex2 = version.indexOf('.', pointIndex1 + 1);

                minor = Integer.parseInt(version.substring(pointIndex1 + 1, pointIndex2));

                hyphenIndex = version.indexOf('-', pointIndex2 + 1);

                subMinor = Integer.parseInt(version.substring(pointIndex2 + 1, hyphenIndex));
                driverVersion = new DefaultDriverVersion(name, version, major, minor, subMinor);
            } catch (RuntimeException e) {
                throw new JdbdException(String.format("unknown version format %s", version), e);
            }
        }
        return driverVersion;
    }

    private final String name;

    private final String version;

    private final int major;

    private final int minor;

    private final int subMinor;


    private DefaultDriverVersion(String name, String version, int major, int minor, int subMinor) {
        this.name = name;
        this.version = version;
        this.major = major;
        this.minor = minor;
        this.subMinor = subMinor;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getMajor() {
        return this.major;
    }

    @Override
    public int getMinor() {
        return this.minor;
    }

    @Override
    public String getVersion() {
        return this.version;
    }

    @Override
    public int getSubMinor() {
        return this.subMinor;
    }

    @Override
    public boolean meetsMinimum(int major, int minor, int subMinor) {
        //TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T valueOf(Option<T> option) {
        // always null
        return null;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name, this.version, this.major, this.minor, this.subMinor);
    }

    @Override
    public boolean equals(final Object obj) {
        final boolean match;
        if (obj == this) {
            match = true;
        } else if (obj instanceof DefaultDriverVersion) {
            final DefaultDriverVersion o = (DefaultDriverVersion) obj;
            match = o.name.equals(this.name)
                    && o.version.equals(this.version)
                    && o.major == this.major
                    && o.minor == this.minor
                    && o.subMinor == this.subMinor;
        } else {
            match = false;
        }
        return match;
    }

    @Override
    public String toString() {
        return String.format("%s[ name : %s , version : %s , hash : %s]",
                getClass().getName(),
                this.name,
                this.version,
                System.identityHashCode(this)
        );
    }


}
