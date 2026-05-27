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
package org.xwiki.contrib.guidedtour.internal;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;
import javax.inject.Provider;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.xwiki.contrib.guidedtour.api.dtos.StepDTO;
import org.xwiki.contrib.guidedtour.api.enums.Placement;
import org.xwiki.contrib.guidedtour.api.enums.TourProperty;
import org.xwiki.contrib.guidedtour.api.exceptions.DuplicatedIdException;
import org.xwiki.contrib.guidedtour.api.exceptions.InvalidIdException;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.model.reference.DocumentReferenceResolver;
import org.xwiki.test.junit5.mockito.ComponentTest;
import org.xwiki.test.junit5.mockito.InjectMockComponents;
import org.xwiki.test.junit5.mockito.MockComponent;

import com.xpn.xwiki.XWiki;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.BaseObject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.xwiki.contrib.guidedtour.internal.util.GuidedTourConstants.STEP_CLASS;

@ComponentTest
class StepsManagerTest
{
    private final StepDTO stepDTO = new StepDTO();

    @InjectMockComponents
    private StepsManager stepsManager;

    @MockComponent
    private Provider<XWikiContext> wikiContextProvider;

    @MockComponent
    @Named("current")
    private DocumentReferenceResolver<String> documentReferenceResolver;

    @Mock
    private XWikiContext xwikiContext;

    @Mock
    private XWiki xwiki;

    @Mock
    private BaseObject stepObject1;

    @Mock
    private BaseObject stepObject2;

    @Mock
    private BaseObject stepObject3;

    @Mock
    private XWikiDocument taskDocument;

    @Mock
    private DocumentReference tourReference;

    @Mock
    private DocumentReference taskReference;

    @BeforeEach
    void setUp() throws XWikiException
    {
        when(wikiContextProvider.get()).thenReturn(xwikiContext);
        when(xwikiContext.getWiki()).thenReturn(xwiki);
        when(documentReferenceResolver.resolve("testTour")).thenReturn(tourReference);
        when(documentReferenceResolver.resolve("testTask", tourReference)).thenReturn(taskReference);
        when(xwiki.exists(taskReference, xwikiContext)).thenReturn(true);
        when(xwiki.exists(tourReference, xwikiContext)).thenReturn(true);
        when(xwiki.getDocument(taskReference, xwikiContext)).thenReturn(taskDocument);

        List<BaseObject> steps = new ArrayList<>();
        steps.add(stepObject1);
        steps.add(stepObject2);
        steps.add(null);
        steps.add(stepObject3);
        when(taskDocument.getXObjects(STEP_CLASS)).thenReturn(steps);
        when(taskDocument.newXObject(STEP_CLASS, xwikiContext)).thenReturn(stepObject1);

        when(stepObject1.getIntValue(TourProperty.ORDER.getBaseKey())).thenReturn(1);
        when(stepObject2.getIntValue(TourProperty.ORDER.getBaseKey())).thenReturn(2);
        when(stepObject3.getIntValue(TourProperty.ORDER.getBaseKey())).thenReturn(3);

        when(stepObject1.getStringValue(TourProperty.CONTENT.getBaseKey())).thenReturn("content for step 1");
        when(stepObject2.getStringValue(TourProperty.CONTENT.getBaseKey())).thenReturn("content for step 2");
        when(stepObject3.getStringValue(TourProperty.CONTENT.getBaseKey())).thenReturn("content for step 3");

        when(stepObject1.getStringValue(TourProperty.ELEMENT.getBaseKey())).thenReturn("element1");
        when(stepObject2.getStringValue(TourProperty.ELEMENT.getBaseKey())).thenReturn("element2");
        when(stepObject3.getStringValue(TourProperty.ELEMENT.getBaseKey())).thenReturn("element3");

        when(stepObject1.getStringValue(TourProperty.PLACEMENT.getBaseKey())).thenReturn("BOTTOM_START");
        when(stepObject2.getStringValue(TourProperty.PLACEMENT.getBaseKey())).thenReturn("TOP_END");
        when(stepObject3.getStringValue(TourProperty.PLACEMENT.getBaseKey())).thenReturn("LEFT_CENTER");

        when(stepObject1.getIntValue(TourProperty.BACKDROP.getBaseKey())).thenReturn(1);
        when(stepObject2.getIntValue(TourProperty.BACKDROP.getBaseKey())).thenReturn(1);
        when(stepObject3.getIntValue(TourProperty.BACKDROP.getBaseKey())).thenReturn(0);

        when(stepObject1.getIntValue(TourProperty.REFLEX.getBaseKey())).thenReturn(0);
        when(stepObject2.getIntValue(TourProperty.REFLEX.getBaseKey())).thenReturn(1);
        when(stepObject3.getIntValue(TourProperty.REFLEX.getBaseKey())).thenReturn(0);

        when(stepObject1.getStringValue(TourProperty.TARGET_PAGE.getBaseKey())).thenReturn("tp1");
        when(stepObject2.getStringValue(TourProperty.TARGET_PAGE.getBaseKey())).thenReturn("tp2");
        when(stepObject3.getStringValue(TourProperty.TARGET_PAGE.getBaseKey())).thenReturn("");

        when(stepObject1.getStringValue(TourProperty.TARGET_ACTION.getBaseKey())).thenReturn("");
        when(stepObject2.getStringValue(TourProperty.TARGET_ACTION.getBaseKey())).thenReturn("");
        when(stepObject3.getStringValue(TourProperty.TARGET_ACTION.getBaseKey())).thenReturn("view");

        when(stepObject1.getStringValue(TourProperty.QUERY_PARAMETERS.getBaseKey())).thenReturn("param=value");
        when(stepObject2.getStringValue(TourProperty.QUERY_PARAMETERS.getBaseKey())).thenReturn("");
        when(stepObject3.getStringValue(TourProperty.QUERY_PARAMETERS.getBaseKey())).thenReturn("");

        stepDTO.setContent("content for step 4");
        stepDTO.setElement("element4");
        stepDTO.setPlacement("RIGHT_END");
        stepDTO.setBackdrop(true);
        stepDTO.setReflex(false);
        stepDTO.setTargetPage("tp4");
        stepDTO.setTargetAction("edit");
        stepDTO.setQueryParameters("param=value");
        stepDTO.setOrder(3);
    }

    @Test
    void getSteps() throws XWikiException, InvalidIdException
    {
        List<StepDTO> steps = stepsManager.getAllSteps("testTour", "testTask");
        assertEquals(3, steps.size());
        assertEquals("content for step 1", steps.get(0).getContent());
        assertEquals("element2", steps.get(1).getElement());
        assertEquals(Placement.LEFT_CENTER, steps.get(2).getPlacement());
    }

    @Test
    void getStepsInvalidTour()
    {
        InvalidIdException exception = assertThrows(InvalidIdException.class, () -> {
            stepsManager.getAllSteps("testTourInv", "testTask");
        });

        assertEquals(String.format("Tour with the given id [%s] does not exists.", "testTourInv"),
            exception.getMessage());
    }

    @Test
    void getStepsInvalidTask()
    {
        InvalidIdException exception = assertThrows(InvalidIdException.class, () -> {
            stepsManager.getAllSteps("testTour", "testTaskInv");
        });

        assertEquals(String.format("Task with the given id [%s] does not exists.", "testTaskInv"),
            exception.getMessage());
    }

    @Test
    void createStep() throws XWikiException, DuplicatedIdException, InvalidIdException
    {
        stepDTO.setOrder(-1);
        stepsManager.createStep("testTour", "testTask", stepDTO);
        verify(stepObject1, times(1)).set(TourProperty.PLACEMENT.getBaseKey(), Placement.RIGHT_END, xwikiContext);
        verify(stepObject1, times(1)).set(TourProperty.BACKDROP.getBaseKey(), 1, xwikiContext);
        verify(stepObject1, times(1)).set(TourProperty.REFLEX.getBaseKey(), 0, xwikiContext);
        verify(stepObject1, times(1)).set(TourProperty.ORDER.getBaseKey(), 4, xwikiContext);
    }

    @Test
    void createStepDuplicate()
    {
        DuplicatedIdException exception = assertThrows(DuplicatedIdException.class, () -> {
            stepsManager.createStep("testTour", "testTask", stepDTO);
        });

        assertEquals("A step with the given order [3] already exists.", exception.getMessage());
    }

    @Test
    void updateStepSameOrder() throws XWikiException, InvalidIdException
    {
        stepsManager.updateStep("testTour", "testTask", 3, stepDTO);
        verify(stepObject3, times(1)).set(TourProperty.PLACEMENT.getBaseKey(), Placement.RIGHT_END, xwikiContext);
        verify(stepObject3, times(1)).set(TourProperty.BACKDROP.getBaseKey(), 1, xwikiContext);
        verify(stepObject3, times(1)).set(TourProperty.REFLEX.getBaseKey(), 0, xwikiContext);
    }

    @Test
    void updateStepDifferentOrder() throws XWikiException, InvalidIdException
    {
        stepDTO.setOrder(1);

        stepsManager.updateStep("testTour", "testTask", 3, stepDTO);
        verify(stepObject3, times(1)).set(TourProperty.PLACEMENT.getBaseKey(), Placement.RIGHT_END, xwikiContext);
        verify(stepObject3, times(1)).set(TourProperty.BACKDROP.getBaseKey(), 1, xwikiContext);
        verify(stepObject3, times(1)).set(TourProperty.REFLEX.getBaseKey(), 0, xwikiContext);
        verify(stepObject3, times(1)).set(TourProperty.ORDER.getBaseKey(), 1, xwikiContext);
        verify(stepObject1, times(1)).set(TourProperty.ORDER.getBaseKey(), 2, xwikiContext);
        verify(stepObject2, times(1)).set(TourProperty.ORDER.getBaseKey(), 3, xwikiContext);
    }

    @Test
    void updateStepInvalidOrder()
    {
        InvalidIdException exception = assertThrows(InvalidIdException.class, () -> {
            stepsManager.updateStep("testTour", "testTask", 5, stepDTO);
        });

        assertEquals("No step was found on the given order position [5].", exception.getMessage());
    }

    @Test
    void deleteStep() throws XWikiException, InvalidIdException
    {
        when(stepObject2.getOwnerDocument()).thenReturn(taskDocument);

        stepsManager.deleteStep("testTour", "testTask", 2);
        verify(stepObject3, times(1)).set(TourProperty.ORDER.getBaseKey(), 2, xwikiContext);
        verify(stepObject1, times(0)).set(eq(TourProperty.ORDER.getBaseKey()), any(), any(XWikiContext.class));
        verify(xwiki, times(1)).saveDocument(taskDocument, "Removed step 2.", xwikiContext);
    }
}
