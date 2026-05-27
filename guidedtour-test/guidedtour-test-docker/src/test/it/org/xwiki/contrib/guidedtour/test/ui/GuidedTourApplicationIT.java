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
package org.xwiki.contrib.guidedtour.test.ui;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.xwiki.contrib.guidedtour.test.po.TourWidget;
import org.xwiki.test.docker.junit5.TestReference;
import org.xwiki.test.docker.junit5.UITest;
import org.xwiki.test.ui.TestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Functional tests for the Tour Application.
 *
 * @version $Id$
 */
@UITest
class GuidedTourApplicationIT
{
    /**
     * The displayed tours should not include inactive tours, or tours which the user has no rights to view.
     */
    @Test
    void tourVisibility(TestUtils testUtils, TestReference testReference)
    {
        testUtils.gotoPage(testReference);
        TourWidget tourWidget = new TourWidget();
        assertEquals(tourWidget.getTourIds(), List.of("xwiki:GuidedTour.PanelTour"));
    }
}
