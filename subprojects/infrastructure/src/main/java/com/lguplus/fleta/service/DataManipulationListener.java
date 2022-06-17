package com.lguplus.fleta.service;

import java.util.Map;

public interface DataManipulationListener {

    void onInsert(Map<String, String> data);
    void onUpdate(Map<String, String> data, Map<String, String> before);
    void onDelete(Map<String, String> before);
}
