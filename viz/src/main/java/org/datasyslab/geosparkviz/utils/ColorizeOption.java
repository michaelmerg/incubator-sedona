/*
 * FILE: ColorizeOption
 * Copyright (c) 2015 - 2018 GeoSpark Development Team
 *
 * MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */
package org.datasyslab.geosparkviz.utils;

import java.io.Serializable;

// TODO: Auto-generated Javadoc

/**
 * The Enum ColorizeOption.
 */
public enum ColorizeOption
        implements Serializable
{

    /**
     * The earthobservation.
     */
    EARTHOBSERVATION("EARTHOBSERVATION"),

    /**
     * The spatialaggregation.
     */
    SPATIALAGGREGATION("spatialaggregation"),

    /**
     * The normal.
     */
    NORMAL("normal");

    /**
     * The type name.
     */
    private String typeName = "normal";

    /**
     * Instantiates a new colorize option.
     *
     * @param typeName the type name
     */
    private ColorizeOption(String typeName)
    {
        this.setTypeName(typeName);
    }

    /**
     * Gets the colorize option.
     *
     * @param str the str
     * @return the colorize option
     */
    public static ColorizeOption getColorizeOption(String str)
    {
        for (ColorizeOption me : ColorizeOption.values()) {
            if (me.name().equalsIgnoreCase(str)) { return me; }
        }
        return null;
    }

    /**
     * Gets the type name.
     *
     * @return the type name
     */
    public String getTypeName()
    {
        return typeName;
    }

    /**
     * Sets the type name.
     *
     * @param typeName the new type name
     */
    public void setTypeName(String typeName)
    {
        this.typeName = typeName;
    }
}
