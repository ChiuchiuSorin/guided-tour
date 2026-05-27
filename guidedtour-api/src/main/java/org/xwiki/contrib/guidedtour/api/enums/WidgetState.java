/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.xwiki.contrib.guidedtour.api.enums;

import org.xwiki.stability.Unstable;

/**
 * Enum representing the state of a widget.
 *
 * @version $Id$
 * @since 1.0
 */
@Unstable
public enum WidgetState
{
    /**
     * Hidden state: the widget is hidden and not visible to the user.
     */
    HIDDEN,
    /**
     * Open state: the widget is visible and can be interacted with by the user.
     */
    OPEN,
    /**
     * Collapsed state: the widget is collapsed, but visible to the user. It can be expanded to show its content.
     */
    COLLAPSED;

    /**
     * Get the WidgetState from a string value.
     *
     * @param text the string value to convert
     * @return the corresponding WidgetState, or OPEN if the string does not match any state
     */
    public static WidgetState fromString(String text)
    {
        for (WidgetState s : WidgetState.values()) {
            if (s.name().equalsIgnoreCase(text)) {
                return s;
            }
        }
        return OPEN;
    }
}
