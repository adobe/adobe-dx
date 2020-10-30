<%--
  ADOBE CONFIDENTIAL

  Copyright 2016 Adobe Systems Incorporated
  All Rights Reserved.

  NOTICE:  All information contained herein is, and remains
  the property of Adobe Systems Incorporated and its suppliers,
  if any.  The intellectual and technical concepts contained
  herein are proprietary to Adobe Systems Incorporated and its
  suppliers and may be covered by U.S. and Foreign Patents,
  patents in process, and are protected by trade secret or copyright law.
  Dissemination of this information or reproduction of this material
  is strictly forbidden unless prior written permission is obtained
  from Adobe Systems Incorporated.
--%><%
%><%@include file="/libs/granite/ui/global.jsp" %><%
%><%@page session="false"
          import="com.adobe.granite.ui.components.AttrBuilder,
                  com.adobe.granite.ui.components.Config,
                  com.adobe.granite.ui.components.ComponentHelper.Options,
                  com.adobe.granite.ui.components.Field,
                  com.adobe.granite.ui.components.Tag,
                  java.util.Iterator,
                  org.apache.commons.lang3.StringUtils" %><%--###
Slider
======

.. granite:servercomponent:: /apps/granite/ui/components/coral/foundation/form/colorfield

   A field that allows user to enter a long value based on sliding a toggle.

   It extends :granite:servercomponent:`Field </libs/granite/ui/components/coral/foundation/form/field>` component.

###--%><%

    Config cfg = cmp.getConfig();
    Tag tag = cmp.consumeTag();
    AttrBuilder attrs = tag.getAttrs();
    cmp.populateCommonAttrs(attrs);

    String text = i18n.getVar(cfg.get("text", String.class));

    ValueMap vm = (ValueMap) request.getAttribute(Field.class.getName());

    String fieldLabel = cfg.get("fieldLabel", String.class);
    String fieldDesc = cfg.get("fieldDescription", String.class);
    String labelledBy = null;

    if (StringUtils.isNoneBlank(fieldLabel, fieldDesc)) {
        labelledBy = vm.get("labelId", String.class) + " " + vm.get("descriptionId", String.class);
    } else if (fieldLabel != null) {
        labelledBy = vm.get("labelId", String.class);
    } else if (fieldDesc != null) {
        labelledBy = vm.get("descriptionId", String.class);
    }

    if (StringUtils.isNotBlank(labelledBy)) {
        attrs.add("labelledby", labelledBy);
    }

    attrs.add("value", vm.get("value", Integer.class));

    // Setup vals for support fields
    String name = cfg.get("name", String.class);
    String value = vm.get("value", "");
    String defaultValue = cfg.get("value", "");
    String disabledText = cfg.get("disabledText", "Off");
    String buttonSuffix = cfg.get("buttonSuffix", "");

    attrs.add("name", cfg.get("name", String.class));
    attrs.addDisabled(cfg.get("disabled", false));
    attrs.addBoolean("required", cfg.get("required", false));
    attrs.add("variant", cfg.get("variant", "default"));

    attrs.add("step", cfg.get("step", Double.class));
    attrs.add("min", cfg.get("min", Double.class));
    attrs.add("max", cfg.get("max", Double.class));

    String validation = StringUtils.join(cfg.get("validation", new String[0]), " ");
    attrs.add("data-foundation-validation", validation);
    attrs.add("data-validation", validation); // Compatibility

%><div class="dx-Form-fieldwrapper dx-Form-fieldwrapper--slider">
    <button type="button" class="dx-SliderTooltip" data-disabled-text="<%=xssAPI.encodeForHTMLAttr(disabledText)%>" data-button-suffix="<%=xssAPI.encodeForHTMLAttr(buttonSuffix)%>"><%=xssAPI.encodeForHTMLAttr(disabledText)%></button>
    <coral-slider <%= attrs.build() %> data-default-value="<%=xssAPI.encodeForHTMLAttr(defaultValue)%>" filled></coral-slider>
    <input class="dx-SliderHiddenField" type="hidden" name="<%=xssAPI.encodeForHTMLAttr(name)%>" value="<%=xssAPI.encodeForHTMLAttr(value)%>"/>
</div>
