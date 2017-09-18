/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.data.documentdb.repository;

import com.microsoft.azure.spring.data.documentdb.domain.Address;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ContactRepositoryConfig.class)
public class AddressRepositoryIT {

    private static final Address TEST_ADDRESS1_PARTITION1 = new Address("111", "redmond", "111st avenue");
    private static final Address TEST_ADDRESS2_PARTITION1 = new Address("222", "redmond", "98th street");
    private static final Address TEST_ADDRESS1_PARTITION2 = new Address("333", "bellevue", "103rd street");

    @Autowired
    AddressRepository repository;

    @Before
    public void setup() {
        repository.save(TEST_ADDRESS1_PARTITION1);
        repository.save(TEST_ADDRESS1_PARTITION2);
        repository.save(TEST_ADDRESS2_PARTITION1);
    }

    @After
    public void cleanup() {
        repository.deleteAll();
    }

    @Test
    public void testFindAll() {
        // findAll cross partition
        final List<Address> result = repository.findAll();

        assertThat(result.size()).isEqualTo(3);

        // findAll per partition
        final List<Address> partition1 = repository.findAll(TEST_ADDRESS1_PARTITION1.getCity());
        assertThat(partition1.size()).isEqualTo(2);

        final List<Address> partition2 = repository.findAll(TEST_ADDRESS1_PARTITION2.getCity());
        assertThat(partition2.size()).isEqualTo(1);
    }

    @Test
    public void testCountAndDeleteByID() {
        long count = repository.count();
        assertThat(count).isEqualTo(3);

        repository.delete(TEST_ADDRESS1_PARTITION1.getPostalCode(), TEST_ADDRESS1_PARTITION1.getCity());

        final List<Address> result = repository.findAll();

        assertThat(result.size()).isEqualTo(2);

        count = repository.count();
        assertThat(count).isEqualTo(2);
    }

    @Test
    public void testCountAndDeleteEntity() {
        repository.delete(TEST_ADDRESS1_PARTITION1, TEST_ADDRESS1_PARTITION1.getCity());

        final List<Address> result = repository.findAll();

        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    public void testUpdateEntity() {
        final Address updatedAddress = new Address(TEST_ADDRESS1_PARTITION1.getPostalCode(),
                TEST_ADDRESS1_PARTITION1.getCity(), "new street");

        repository.update(updatedAddress, updatedAddress.getCity());

        final Address address = repository.findOne(updatedAddress.getPostalCode(), updatedAddress.getCity());

        assertThat(address.getStreet()).isEqualTo(updatedAddress.getStreet());
        assertThat(address.getPostalCode()).isEqualTo(updatedAddress.getPostalCode());
    }

}
