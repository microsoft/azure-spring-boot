package com.microsoft.azure.autoconfigure.aad;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by yaweiw on 8/15/2017.
 */
public class UserGroupTest {
    private final static UserGroup group1 = new UserGroup("12345", "test");
    @Test
    public void getDisplayName() throws Exception {
        Assert.assertTrue(group1.getDisplayName().equals("test"));
    }

    @Test
    public void getObjectID() throws Exception {
        Assert.assertTrue(group1.getObjectID().equals("12345"));
    }

    @Test
    public void equals() throws Exception {
        UserGroup group2 = new UserGroup("12345", "test");
        Assert.assertTrue(group1.equals(group2));
    }

    @Test
    public void hashCodeTest() {
        UserGroup group2 = new UserGroup("12345", "test");
        Assert.assertTrue(group1.hashCode() == group2.hashCode());
    }
}