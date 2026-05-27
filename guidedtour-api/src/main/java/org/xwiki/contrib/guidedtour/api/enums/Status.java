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
 * Enum representing the status of a task.
 *
 * @version $Id$
 * @since 1.0
 */
@Unstable
public enum Status
{
    /**
     * To do status: the task is not yet started and needs to be completed by the user.
     */
    TODO,
    /**
     * Skipped status: the task has been skipped by the user and will not be completed.
     */
    SKIPPED,
    /**
     * Done status: the task has been completed by the user.
     */
    DONE;

    /**
     * Get the Status from a string value.
     *
     * @param text the string value to convert
     * @return the corresponding Status, or to do if the string does not match any status
     */
    public static Status fromString(String text)
    {
        for (Status s : Status.values()) {
            if (s.name().equalsIgnoreCase(text)) {
                return s;
            }
        }
        return TODO;
    }
}
