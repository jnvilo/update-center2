package org.jvnet.hudson.update_center.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.jvnet.hudson.update_center.Signer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.GeneralSecurityException;

/**
 * Support generation of JSON output with included checksum + signatures block for the same JSON output.
 */
public abstract class WithSignature {
    private JsonSignature signature;

    @JSONField
    public JsonSignature getSignature() {
        return signature;
    }

    /**
     * Generate JSON checksums and add a signature block to the JSON written to the specified {@link Writer}.
     *
     * This will run JSON generation twice: Once without the signature block to compute checksums, and a second time to
     * include the signature block and write it to the output file.
     *
     * Because of this, it is important that (with the exception of {@link #getSignature()} all getters etc. of subtypes
     * and any types reachable through the object graph for JSON generation return the same content on subsequent calls.
     *
     * Additionally, implementations of this class, and all types reachable via fields and getters used during JSON
     * generation should employ some sort of caching to prevent expensive computations from being invoked twice.
     *
     * @param writer the writer to write to
     * @throws IOException when any IO error occurs
     */
    public void writeWithSignature(Writer writer, Signer signer) throws IOException, GeneralSecurityException {
        signature = null;

        final String unsignedJson = JSON.toJSONString(this, SerializerFeature.DisableCircularReferenceDetect);
        signature = signer.sign(unsignedJson);

        JSON.writeJSONString(writer, this, SerializerFeature.DisableCircularReferenceDetect);
        writer.flush();
    }

    /**
     * Convenience wrapper for {@link #writeWithSignature(Writer, Signer)} writing to a file.
     *
     */
    public void writeWithSignature(File outputFile, Signer signer) throws IOException, GeneralSecurityException {
        try (OutputStream os = Files.newOutputStream(outputFile.toPath()); OutputStreamWriter writer = new OutputStreamWriter(os, StandardCharsets.UTF_8)) {
            writeWithSignature(writer, signer);
        }
    }

    /**
     * Like {@link #writeWithSignature(File, Signer)} but the output is returned as a String.
     * @param signer the signer
     * @return the JSON output
     */
    public String encodeWithSignature(Signer signer)  throws IOException, GeneralSecurityException {
        StringWriter writer = new StringWriter();
        writeWithSignature(writer, signer);
        return writer.getBuffer().toString();
    }
}