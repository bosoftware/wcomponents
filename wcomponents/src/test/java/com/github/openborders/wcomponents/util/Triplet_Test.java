package com.github.openborders.wcomponents.util; 

import junit.framework.Assert;

import org.junit.Test;


/** 
 * Triplet_Test - JUnit tests for {@link Triplet}.
 * 
 * @author Yiannis Paschalidis.
 * @since 1.0.0
 */
public class Triplet_Test
{
    @Test
    public void testNoArgsConstructor()
    {
        Triplet<String, Integer, Boolean> triplet = new Triplet<String, Integer, Boolean>();
        
        Assert.assertNull("First should be null", triplet.getFirst());
        Assert.assertNull("Second should be null", triplet.getSecond());
        Assert.assertNull("Third should be null", triplet.getThird());
    }
    
    @Test
    public void testConstructor()
    {
        Triplet<String, Integer, Boolean> triplet = new Triplet<String, Integer, Boolean>("testConstructor.value1", 1, true);
        String value1 = triplet.getFirst();
        Integer value2 = triplet.getSecond();
        boolean value3 = triplet.getThird();
        
        Assert.assertEquals("Incorrect value1", "testConstructor.value1", value1);
        Assert.assertEquals("Incorrect value2", Integer.valueOf(1), value2);
        Assert.assertEquals("Incorrect value3", true, value3);
    }    
    
    @Test
    public void testSetFirst()
    {
        Triplet<String, Integer, Boolean> triplet = new Triplet<String, Integer, Boolean>();
        
        triplet.setFirst("testSetFirst.first");
        Assert.assertEquals("Incorrect first", "testSetFirst.first", triplet.getFirst());
        Assert.assertNull("Second should be null", triplet.getSecond());
        Assert.assertNull("Third should be null", triplet.getThird());
    }
    
    @Test
    public void testSetSecond()
    {
        Triplet<String, Integer, Boolean> triplet = new Triplet<String, Integer, Boolean>();
        
        triplet.setSecond(1);
        Assert.assertNull("First should be null", triplet.getFirst());
        Assert.assertEquals("Incorrect second", new Integer(1), triplet.getSecond());
        Assert.assertNull("Third should be null", triplet.getThird());
    }
    
    @Test
    public void testSetThird()
    {
        Triplet<String, Integer, Boolean> triplet = new Triplet<String, Integer, Boolean>();
        
        triplet.setThird(true);
        Assert.assertNull("First should be null", triplet.getFirst());
        Assert.assertNull("Second should be null", triplet.getSecond());
        Assert.assertEquals("Incorrect third", Boolean.TRUE, triplet.getThird());
    }
    
    @Test
    public void testEquals()
    {
        Triplet<Object, Object, Object> trip1 = new Triplet<Object, Object, Object>("1", new Long(12345), null);
        Triplet<Object, Object, Object> trip2 = new Triplet<Object, Object, Object>("1", new Long(12345), null);
        Triplet<Object, Object, Object> trip3 = new Triplet<Object, Object, Object>("1", null, new Long(12345));
        
        Assert.assertTrue("Triplet should equal itself", trip1.equals(trip1));
        Assert.assertTrue("Triplet should equal an equivalent triplet", trip1.equals(trip2));
        
        Assert.assertFalse("Triplet should not equal null", trip1.equals(null));
        Assert.assertFalse("Triplet should not equal another class", trip1.equals(""));
        Assert.assertFalse("Triplet should equal a different triplet", trip1.equals(trip3));
    }
    
    @Test
    public void testHashCode()
    {
        Triplet<Object, Object, Object> trip1 = new Triplet<Object, Object, Object>("1", new Long(12345), null);
        Triplet<Object, Object, Object> trip2 = new Triplet<Object, Object, Object>("1", new Long(12345), null);
        
        Assert.assertEquals("Triplet should have same hash as an equivalent triplet", trip1.hashCode(), trip2.hashCode());
    }
}