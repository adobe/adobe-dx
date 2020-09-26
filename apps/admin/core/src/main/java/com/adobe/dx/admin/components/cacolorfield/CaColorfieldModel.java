package com.adobe.dx.admin.components.cacolorfield;

import com.adobe.dx.admin.datasource.internal.ContextAwareDatasource;
import com.adobe.granite.ui.components.Config;
import com.adobe.granite.ui.components.ds.DataSource;
import com.day.cq.wcm.api.components.ComponentContext;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

@Model(adaptables = { SlingHttpServletRequest.class },
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class CaColorfieldModel {

    @Self
    private SlingHttpServletRequest request;

    @SlingObject
    private ResourceResolver resourceResolver;

    private Config cfg;

    private List<Resource> items;

    private Map<String, Object> attrs;

    private String name;
    private String contentValue;
    private String configValue;
    private String fieldLabel;
    private String fieldDesc;
    private String placeholder;
    private boolean required;
    private String labelId;
    private String descriptionId;

    @PostConstruct
    private void init() {
        Resource dialogComponent = request.getResource();

        // Setup basics
        cfg = new Config(dialogComponent);
        String uuid = UUID.randomUUID().toString();

        // Wire up the DataSource
        request.adaptTo(ContextAwareDatasource.class);
        items = getDataSourceItems();

        // Get individual component props
        name = cfg.get("name", String.class);
        String propName = name.replace("./", "");
        
        // Setup content & config values
        ValueMap contentVm = getContentValueMap();
        contentValue = contentVm.get(propName, String.class);
        configValue = setupConfigValue();
        
        required = cfg.get("required", false);
        String requiredAsterisk = required ? " *" : "";
        fieldLabel = cfg.get("fieldLabel", String.class);

        fieldLabel = fieldLabel + requiredAsterisk;
        fieldDesc = cfg.get("fieldDescription", String.class);
        
        labelId = "label_" + uuid;
        descriptionId = "description_" + uuid;

        // Build attributes to lessen manual HTL work
        attrs = new HashMap<String, Object>();
        attrs.put("placeholder", cfg.get("emptyText", String.class));
        attrs.put("disabled", cfg.get("disabled", false));
        attrs.put("required", required);

        String labeledBy = labelId + " " + descriptionId;
        attrs.put("labelledby", labeledBy);

        attrs.put("aria-labelledby", labeledBy);

        attrs.put("variant", cfg.get("variant", "default"));

        attrs.put("autogeneratecolors", cfg.get("autogenerateColors", "off"));

        boolean showSwatches = cfg.get("showSwatches", true);
        attrs.put("showswatches", showSwatches ? "on" : "off");

        boolean showProperties = cfg.get("showProperties", true);
        attrs.put("showproperties", showProperties ? "on" : "off");

        boolean showDefaultColors = cfg.get("showDefaultColors", true);
        attrs.put("showdefaultcolors", showDefaultColors ? "on" : "off");

        String validation = StringUtils.join(cfg.get("validation", new String[0]), " ");
        attrs.put("validation", validation);
        attrs.put("data-foundation-validation", validation);
    }

    private ValueMap getContentValueMap() {
        String resourcePath = request.getRequestPathInfo().getSuffix();
        Resource resource = resourceResolver.getResource(resourcePath);
        return resource.getValueMap();
    }

    private List<Resource> getDataSourceItems() {
        DataSource ds = (DataSource) request.getAttribute(DataSource.class.getName());
        if (ds != null) {
            Iterator<Resource> is = ds.iterator();
            if (is != null) {
                return IteratorUtils.toList(is);
            }
        }
        return Collections.emptyList();
    }

    private String setupConfigValue() {
        if (contentValue != null && items.size() > 0) {
            Resource configRes = items.stream().filter(item -> {
                return item.getValueMap().get("value", String.class).equals(contentValue);
            }).findFirst().orElse(null);
            if (configRes != null) {
                return configRes.getValueMap().get("initialValue", String.class);
            }
            return contentValue;
        }
        return cfg.get("value", String.class);
    }

    public List<Resource> getItems() {
        return items;
    }
    
    public String getName() {
        return name;
    }

    public String getContentValue() {
        return contentValue;
    }

    public String getConfigValue() {
        return configValue;
    }

    public String getFieldLabel() {
        return fieldLabel;
    }

    public String getFieldDesc() {
        return fieldDesc;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public Map<String, Object> getBulkAttributes() {
        return attrs;
    }
}
