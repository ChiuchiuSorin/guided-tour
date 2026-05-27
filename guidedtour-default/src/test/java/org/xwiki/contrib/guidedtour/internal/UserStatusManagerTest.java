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

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.xwiki.contrib.guidedtour.api.dtos.UserTourStatusDTO;
import org.xwiki.contrib.guidedtour.api.enums.Status;
import org.xwiki.contrib.guidedtour.api.enums.WidgetState;
import org.xwiki.contrib.guidedtour.api.exceptions.DuplicatedIdException;
import org.xwiki.contrib.guidedtour.api.exceptions.InvalidIdException;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.test.junit5.mockito.ComponentTest;
import org.xwiki.test.junit5.mockito.InjectMockComponents;
import org.xwiki.test.junit5.mockito.MockComponent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xpn.xwiki.XWiki;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.BaseObject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.xwiki.contrib.guidedtour.internal.util.GuidedTourConstants.USER_TOUR_CLASS;

@ComponentTest
class UserStatusManagerTest
{
    private static final String TASKS_STATUS_KEY = "tasksStatus";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @InjectMockComponents
    private UserStatusManager userStatusManager;

    @MockComponent
    private XWikiContext wikiContext;

    @Mock
    private XWiki xwiki;

    @Mock
    private XWikiDocument userDocument;

    @Mock
    private BaseObject statusObject;

    @Mock
    private DocumentReference userReference;

    @BeforeEach
    void setup() throws XWikiException
    {
        when(wikiContext.getWiki()).thenReturn(xwiki);
        when(wikiContext.getUserReference()).thenReturn(userReference);
        when(xwiki.getDocument(userReference, wikiContext)).thenReturn(userDocument);
        when(userDocument.getXObject(USER_TOUR_CLASS)).thenReturn(statusObject);
        when(statusObject.getOwnerDocument()).thenReturn(userDocument);
    }

    @Test
    void getUserToursStatus() throws XWikiException, JsonProcessingException, InvalidIdException
    {
        Map<String, Status> tasksStatus = new HashMap<>();
        tasksStatus.put("task1", Status.DONE);
        String tasksStatusJson = objectMapper.writeValueAsString(tasksStatus);

        when(statusObject.getStringValue(TASKS_STATUS_KEY)).thenReturn(tasksStatusJson);
        when(statusObject.getStringValue("widgetState")).thenReturn("OPEN");
        when(statusObject.getIntValue("callToAction")).thenReturn(1);

        UserTourStatusDTO result = userStatusManager.getUserToursStatus();

        assertEquals(Status.DONE, result.getTasksStatus().get("task1"));
        assertEquals(WidgetState.OPEN, result.getWidgetState());
        assertTrue(result.isCallToAction());
    }

    @Test
    void createUserTourStatus() throws XWikiException, DuplicatedIdException
    {
        when(userDocument.getXObject(USER_TOUR_CLASS)).thenReturn(null);
        userStatusManager.createUserTourStatus();

        verify(userDocument, times(1)).newXObject(USER_TOUR_CLASS, wikiContext);
        verify(xwiki, times(1)).saveDocument(userDocument, "Added guided tour user status object.", wikiContext);
    }

    @Test
    void createUserTourStatusDuplicate()
    {
        DuplicatedIdException exception = assertThrows(DuplicatedIdException.class, () -> {
            userStatusManager.createUserTourStatus();
        });

        assertEquals(String.format("User tour status already exists for user [%s]", userReference),
            exception.getMessage());
    }

    @Test
    void updateUserTourStatus() throws XWikiException, JsonProcessingException, InvalidIdException
    {
        Map<String, Status> tasksStatus = new HashMap<>();
        tasksStatus.put("task1", Status.DONE);
        UserTourStatusDTO userTourStatusDTO = new UserTourStatusDTO();
        userTourStatusDTO.setTasksStatus(tasksStatus);
        userTourStatusDTO.setWidgetState("HIDDEN");
        userTourStatusDTO.setCallToAction(false);

        userStatusManager.updateUserTourStatus(userTourStatusDTO);
        verify(statusObject, times(1)).setLargeStringValue(TASKS_STATUS_KEY,
            objectMapper.writeValueAsString(tasksStatus));
        verify(statusObject, times(1)).setStringValue("widgetState", "HIDDEN");
        verify(statusObject, times(1)).setIntValue("callToAction", 0);
        verify(xwiki, times(1)).saveDocument(userDocument, "Updated guided tour user status.", wikiContext);
    }

    @Test
    void updateUserTourStatusInvalidId()
    {
        when(userDocument.getXObject(USER_TOUR_CLASS)).thenReturn(null);
        InvalidIdException exception = assertThrows(InvalidIdException.class, () -> {
            userStatusManager.updateUserTourStatus(new UserTourStatusDTO());
        });
        assertEquals(String.format("User tour status not found for user [%s].", userReference), exception.getMessage());
    }
}
