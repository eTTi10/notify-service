package com.lguplus.fleta.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ManipulationDataDispatcherTest {

    @Mock
    DataManipulationListener dataManipulationListener;

    ManipulationDataDispatcher manipulationDataDispatcher = new ManipulationDataDispatcher();

    @Test
    void test_Insert() {
        assertDoesNotThrow(() -> manipulationDataDispatcher.dispatch(dataManipulationListener,
                "{\"payload\":{\"after\":{}}}"));
    }

    @Test
    void test_Update() {
        assertDoesNotThrow(() -> manipulationDataDispatcher.dispatch(dataManipulationListener,
                "{\"payload\":{\"after\":{},\"before\":{}}}"));
    }

    @Test
    void test_Delete() {
        assertDoesNotThrow(() -> manipulationDataDispatcher.dispatch(dataManipulationListener,
                "{\"payload\":{\"before\":{}}}"));
    }

    @Test
    void test_No_Payload() {
        assertThrows(IllegalArgumentException.class, () ->
                manipulationDataDispatcher.dispatch(dataManipulationListener,
                        "{}"));
    }

    @Test
    void test_Invalid() {
        assertThrows(IllegalArgumentException.class, () ->
                manipulationDataDispatcher.dispatch(dataManipulationListener,
                        "{\"payload\":{}}"));
    }

    @Test
    void test_JsonError() {
        assertThrows(IllegalArgumentException.class, () ->
                manipulationDataDispatcher.dispatch(dataManipulationListener,
                        "{\"payload\":{}"));
    }
}
