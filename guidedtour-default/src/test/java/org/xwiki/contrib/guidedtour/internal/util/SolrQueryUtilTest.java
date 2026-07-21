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
package org.xwiki.contrib.guidedtour.internal.util;

import java.util.List;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.xwiki.query.Query;
import org.xwiki.query.QueryManager;
import org.xwiki.query.internal.DefaultQuery;
import org.xwiki.test.junit5.mockito.ComponentTest;
import org.xwiki.test.junit5.mockito.InjectMockComponents;
import org.xwiki.test.junit5.mockito.MockComponent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ComponentTest
public class SolrQueryUtilTest
{
    private final SolrDocumentList result = new SolrDocumentList();

    @InjectMockComponents
    private SolrQueryUtil solrQueryUtil;

    @MockComponent
    private QueryManager queryManager;

    @Mock
    private DefaultQuery query;

    @Mock
    private QueryResponse queryResponse;

    @Test
    void executeQuery() throws Exception
    {
        String queryString = "queryString";
        String filterQuery = "filterQuery";
        List<String> filteredLines = List.of("test", "reference", "wiki", "spaces", "name");

        when(this.queryManager.createQuery(queryString, "solr")).thenReturn(this.query);
        when(this.query.bindValue("fq", filterQuery)).thenReturn(this.query);
        when(this.query.bindValue("fl", filteredLines)).thenReturn(this.query);
        when(this.query.bindValue("group", true)).thenReturn(this.query);
        when(this.query.bindValue("group.field", "fullname")).thenReturn(this.query);
        when(this.query.bindValue("group.main", true)).thenReturn(this.query);
        when(this.query.execute()).thenReturn(List.of(this.queryResponse));
        when(this.queryResponse.getResults()).thenReturn(this.result);

        SolrDocumentList solrDocumentList = this.solrQueryUtil.executeQuery(queryString, filterQuery, List.of("test"));
        assertEquals(this.result, solrDocumentList);
    }
}
