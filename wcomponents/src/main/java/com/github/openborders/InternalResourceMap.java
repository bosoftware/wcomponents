package com.github.openborders; 

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.Adler32;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.github.openborders.util.SystemException;

/** 
 * <p>The ImageResourceMap map keeps a reference to all internal resources within an application.
 * It is used by the WContentHelperServlet to efficiently serve binary data without having to go
 * through normal WComponent processing.</p>
 * 
 * Two things stop this map from consuming excessive amounts of memory:
 * <ol>
 * <li>InternalResources are files which are present in the classpath. There will be a finite set.</li>
 * <li>The InternalResource implementation does not hold the file data, just a reference to the file.</li>
 * </ol>
 * 
 * @author Yiannis Paschalidis
 * @since 1.0.0
 */
public class InternalResourceMap
{
    /** The logger instance for this class. */
    private static final Log log = LogFactory.getLog(InternalResourceMap.class);
    
    /** The map of internal resources by resource path. */
    private static final Map<String, InternalResource> resources = new HashMap<String, InternalResource>();
    
    /** 
     * A map of internal resources cache keys by path.
     * This is used to bust the browser cache when a resource changes after a new deployment of the application.
     */
    private static final Map<String, String> resourceCacheKeys = new HashMap<String, String>();
    
    /** Hide utility class constructor. */
    private InternalResourceMap()
    {
    }
    
    /**
     * Adds a resource to the resource map.
     * @param resource the resource.
     */
    public static void registerResource(final InternalResource resource)
    {
        String resourceName = resource.getResourceName();
        
        if (!resources.containsKey(resourceName))
        {
            resources.put(resourceName, resource);
            resourceCacheKeys.put(resourceName, computeHash(resource));
        }
    }
    
    /**
     * Retrieves a resource from the resource map.
     * @param path the path to the resource.
     * @return the resource path.
     */
    public static InternalResource getResource(final String path)
    {
        return resources.get(path);
    }
    
    /**
     * Retrieves the cache key for a resource path.
     * @param path the path to the resource.
     * @return the resource cache key.
     */
    public static String getResourceCacheKey(final String path)
    {
        return resourceCacheKeys.get(path);
    }
    
    /**
     * Computes a simple hash of the resource contents. 
     * @param resource the resource to hash.
     * @return a hash of the resource contents.
     */
    public static String computeHash(final InternalResource resource)
    {
        InputStream stream = null;
        
        try
        {
            stream = resource.getStream();
            
            if (stream == null)
            {
                return null;
            }
            
            // Compute CRC-32 checksum
            // TODO: Is a 1 in 2^32 chance of a cache bust fail good enough?
            Checksum checksumEngine = new Adler32();
            checksumEngine = new CRC32();
            byte[] buf = new byte[1024];

            for (int read = stream.read(buf); read != -1; read = stream.read(buf))
            {
                checksumEngine.update(buf, 0, read);
            }

            return Long.toHexString(checksumEngine.getValue());
        }
        catch (Exception e)
        {
            throw new SystemException("Error calculating resource hash", e);
        }
        finally
        {
            if (stream != null)
            {
                try
                {
                    stream.close();
                }
                catch (IOException e)
                {
                    log.error("Error closing stream", e);
                }
            }
        }
    }
}