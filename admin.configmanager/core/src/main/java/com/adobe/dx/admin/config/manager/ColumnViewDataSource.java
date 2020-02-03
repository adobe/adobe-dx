package com.adobe.dx.admin.config.manager;

import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

@ProviderType
public interface ColumnViewDataSource {
    public List<ColumnViewItem> getItems();
}