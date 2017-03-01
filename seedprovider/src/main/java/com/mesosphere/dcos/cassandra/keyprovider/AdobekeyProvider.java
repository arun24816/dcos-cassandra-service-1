package com.mesosphere.dcos.cassandra.keyprovider;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.Key;
import java.security.KeyStore;

import org.apache.cassandra.config.TransparentDataEncryptionOptions;
import org.apache.cassandra.security.KeyProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdobekeyProvider implements KeyProvider
{
    private static final Logger logger = LoggerFactory.getLogger(AdobekeyProvider.class);
    static final String PROP_KEYSTORE = "keystore";
    static final String PROP_KEYSTORE_PW = "keystore_password";
    static final String PROP_KEYSTORE_TYPE = "store_type";
    static final String PROP_KEY_PW = "key_password";
    private final KeyStore store;
    private final boolean isJceks;
    private final TransparentDataEncryptionOptions options;
    public AdobekeyProvider(TransparentDataEncryptionOptions options)
    {
    	logger.info("arun: key adobekeyprovider");
        this.options = options;
        logger.info("initializing keystore from file {}", options.get(PROP_KEYSTORE));
        try
        {
        	
        	URL url = new URL("https://s3-us-west-1.amazonaws.com/cassandra-arun-bucket/adobe.keystore");
        	InputStream is = url.openStream();
            store = KeyStore.getInstance(options.get(PROP_KEYSTORE_TYPE));
            store.load(is, options.get(PROP_KEYSTORE_PW).toCharArray());
            isJceks = store.getType().equalsIgnoreCase("jceks");
        }
        catch (Exception e)
        {
            throw new RuntimeException("couldn't load keystore", e);
        }
    }
    public Key getSecretKey(String keyAlias) throws IOException
    {
        // there's a lovely behavior with jceks files that all aliases are lower-cased
        if (isJceks)
            keyAlias = keyAlias.toLowerCase();
        Key key;
        try
        {
            String password = options.get(PROP_KEY_PW);
            if (password == null || password.isEmpty())
                password = options.get(PROP_KEYSTORE_PW);
            key = store.getKey(keyAlias, password.toCharArray());
        }
        catch (Exception e)
        {
            throw new IOException("unable to load key from keystore");
        }
        if (key == null)
            throw new IOException(String.format("key %s was not found in keystore", keyAlias));
        return key;
    }
}
