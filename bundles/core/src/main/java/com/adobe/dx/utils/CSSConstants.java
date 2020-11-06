/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2020 Adobe
 ~
 ~ Licensed under the Apache License, Version 2.0 (the "License");
 ~ you may not use this file except in compliance with the License.
 ~ You may obtain a copy of the License at
 ~
 ~     http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~ Unless required by applicable law or agreed to in writing, software
 ~ distributed under the License is distributed on an "AS IS" BASIS,
 ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ~ See the License for the specific language governing permissions and
 ~ limitations under the License.
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
package com.adobe.dx.utils;

public class CSSConstants {
    private CSSConstants() {
    }
    public static final String CLASS_DELIMITER = "-";
    public static final String DECLARATION_DELIMITER = ";";
    public static final String RULE_DELIMITER = "\n";
    public static final String DECLARATION = ": ";
    public static final String SPACE = " ";
    public static final String PX = "px";
    public static final String PERCENT = "%";
    public static final String PX_SPACE = PX + SPACE;
    public static final String DEL_SPACE = DECLARATION_DELIMITER + SPACE;
    public static final String PN_TOP = "Top";
    public static final String PN_RIGHT = "Right";
    public static final String PN_LEFT = "Left";
    public static final String PN_BOTTOM = "Bottom";
    public static final String TOP = CLASS_DELIMITER + "top";
    public static final String RIGHT = CLASS_DELIMITER + "right";
    public static final String LEFT = CLASS_DELIMITER + "left";
    public static final String BOTTOM = CLASS_DELIMITER + "bottom";

}
