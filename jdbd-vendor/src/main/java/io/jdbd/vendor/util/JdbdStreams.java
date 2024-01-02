/*
 * Copyright 2023-2043 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.jdbd.vendor.util;
import io.jdbd.lang.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public abstract class JdbdStreams {

    protected JdbdStreams() {
        throw new UnsupportedOperationException();
    }

    private static final Logger LOG = LoggerFactory.getLogger(JdbdStreams.class);


    public static Charset fileEncodingOrUtf8() {
        String fileEncoding = System.getProperty("file.encoding");
        return JdbdStrings.hasText(fileEncoding) ? Charset.forName(fileEncoding) : StandardCharsets.UTF_8;
    }

    public static String readAsString(final Path path) throws IOException {
        return readAsString(path, fileEncodingOrUtf8());
    }

    public static String readAsString(final Path path, final Charset charset) throws IOException {

        try (FileChannel in = FileChannel.open(path, StandardOpenOption.READ)) {
            final long fileSize = in.size();
            if (fileSize > (1 << 30)) {
                throw new IOException(String.format("file[%s] too large,can't as String instance.", fileSize));
            }
            final byte[] bufferArray = new byte[Math.min((int) fileSize, 1024)];
            final ByteBuffer buffer = ByteBuffer.wrap(bufferArray);

            StringBuilder builder = new StringBuilder((int) fileSize);
            CharBuffer charBuffer;
            while (in.read(buffer) > 0) {
                buffer.flip();
                charBuffer = charset.decode(buffer);
                builder.append(charBuffer.toString());
                buffer.clear();
            }
            return builder.toString();
        } catch (IOException e) {
            throw e;
        } catch (Throwable e) {
            throw new IOException(e.getMessage(), e);
        }


    }

    /**
     * @return file size before truncate.
     */
    public static long truncateIfExists(final Path path, final long newSize) throws IOException {
        if (newSize < 0) {
            throw new IllegalArgumentException("newSize must be non-negative");
        }
        if (Files.notExists(path, LinkOption.NOFOLLOW_LINKS)) {
            return 0L;
        }
        if (!Files.isWritable(path)) {
            throw new IOException(String.format("File[%s] isn't writable.", path));
        }
        final long fileSize;
        try (FileChannel in = FileChannel.open(path, StandardOpenOption.WRITE, LinkOption.NOFOLLOW_LINKS)) {
            fileSize = in.size();
            in.truncate(newSize);
        }
        return fileSize;
    }


    public static void copyFromChannel(final Path targetPath, final long needBytes, final FileChannel in
            , final boolean deleteTargetIfError) throws IOException {
        doCopyFromChanel(targetPath, needBytes, in, deleteTargetIfError, null);
    }


    /**
     * @param targetPath if error ,delete tempPath. if not exists then create .
     * @return md5 bytes
     */
    public static byte[] copyFromChannelWithMd5(final Path targetPath, final long needBytes, final FileChannel in
            , final boolean deleteTargetIfError) throws IOException {

        byte[] md5Bytes;
        md5Bytes = doCopyFromChanel(targetPath, needBytes, in, deleteTargetIfError, createMd5Digest());
        assert md5Bytes != null;
        return md5Bytes;
    }

    /*################################## blow private method ##################################*/

    @Nullable
    protected static byte[] doCopyFromChanel(final Path targetPath, final long needBytes, final FileChannel in
            , final boolean deleteTargetIfError, @Nullable final MessageDigest digest) throws IOException {
        final long startTime = System.currentTimeMillis();
        final boolean infoEnabled = LOG.isInfoEnabled();

        Throwable error = null;
        try (FileChannel out = FileChannel.open(targetPath, StandardOpenOption.WRITE, StandardOpenOption.CREATE)) {

            final byte[] bufferArray = new byte[2048];
            final int multiple = 0xFFFF_F;
            final ByteBuffer buffer = ByteBuffer.wrap(bufferArray);
            long readBytes = 0L;

            for (int i = 0, readLength; readBytes < needBytes; ) {
                readLength = (int) Math.min(bufferArray.length, needBytes - readBytes);
                if (readLength < bufferArray.length) {
                    buffer.limit(readLength);
                }
                if (in.read(buffer) != readLength) {
                    throw new IOException(String.format("Channel size < %s", needBytes));
                }
                buffer.flip();
                out.write(buffer);
                if (digest != null) {
                    digest.update(bufferArray, 0, readLength);
                }
                readBytes += readLength;
                // clear buffer for next.
                buffer.clear();
                i++;
                if (infoEnabled && (i & multiple) == 0) {
                    // file too big, print log avoid no process.
                    LOG.info("copy large Geometry process {}%.", (readBytes / (double) needBytes) * 100);
                    i = 0;
                }
            }
            if (infoEnabled && needBytes > multiple) {
                LOG.info("copy large Geometry process 100% ,cost {}ms", System.currentTimeMillis() - startTime);
            }

        } catch (Throwable e) {
            error = e;
        }
        if (error != null) {
            if (deleteTargetIfError) {
                Files.deleteIfExists(targetPath);
            }
            if (error instanceof IOException) {
                throw (IOException) error;
            } else {
                throw new IOException(error.getMessage(), error);
            }
        }
        return digest == null ? null : digest.digest();
    }

    public static MessageDigest createMd5Digest() {
        return createDigest("MD5");
    }


    protected static MessageDigest createDigest(String algorithm) throws IllegalArgumentException {
        try {
            return MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalArgumentException(
                    String.format("Could not find MessageDigest with algorithm '%s'", algorithm), ex);
        }
    }


}
