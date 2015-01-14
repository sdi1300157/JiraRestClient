package de.micromata.jira.rest.junit;

import de.micromata.jira.rest.core.domain.JqlSearchResult;
import de.micromata.jira.rest.core.domain.filter.FilterBean;
import de.micromata.jira.rest.core.jql.*;
import de.micromata.jira.rest.core.util.RestException;
import junit.framework.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * User: Christian Schulze
 * Email: c.schulze@micromata.de
 * Date: 11.08.2014
 */
public class TestSearchClient extends BaseTest {

    @Test
    public void testSearchIssues() throws RestException, IOException, ExecutionException, InterruptedException {
        JqlSearchBean jsb = new JqlSearchBean();
        JqlBuilder builder = new JqlBuilder();
        String jql = builder.addCondition(EField.PROJECT, EOperator.EQUALS, "DEMO")
                .and().addCondition(EField.STATUS, EOperator.EQUALS, STATUS_OPEN)
                .orderBy(SortOrder.ASC, EField.CREATED);
        jsb.setJql(jql);
        jsb.addField(EField.ISSUE_KEY, EField.STATUS, EField.DUE, EField.SUMMARY, EField.ISSUE_TYPE, EField.PRIORITY, EField.UPDATED, EField.TRANSITIONS);
        jsb.addExpand(EField.TRANSITIONS);
        Future<JqlSearchResult> future = jiraRestClient.getSearchClient().searchIssues(jsb);
        JqlSearchResult jqlSearchResult = future.get();
        Assert.assertNotNull(jqlSearchResult);
        Assert.assertEquals(6, jqlSearchResult.getTotal());
        Assert.assertEquals(6, jqlSearchResult.getIssues().size());


    }

    @Test
    public void testSearchIssueWithMultipleValues() throws IOException, RestException, ExecutionException, InterruptedException {
        JqlSearchBean jsb = new JqlSearchBean();
        JqlBuilder builder = new JqlBuilder();
        String jql = builder.addCondition(EField.PROJECT, EOperator.EQUALS, "DEMO")
                .and().addCondition(EField.STATUS, EOperator.IN, STATUS_OPEN, STATUS_IN_PROGRESS)
                .orderBy(SortOrder.ASC, EField.CREATED);
        jsb.setJql(jql);
        jsb.addField(EField.ISSUE_KEY, EField.STATUS, EField.DUE, EField.SUMMARY, EField.ISSUE_TYPE, EField.PRIORITY, EField.UPDATED, EField.TRANSITIONS);
        jsb.addExpand(EField.TRANSITIONS);
        Future<JqlSearchResult> future = jiraRestClient.getSearchClient().searchIssues(jsb);
        JqlSearchResult jqlSearchResult = future.get();
        Assert.assertNotNull(jqlSearchResult);
        Assert.assertEquals(6, jqlSearchResult.getTotal());
        Assert.assertEquals(6, jqlSearchResult.getIssues().size());

    }


    @Test
    public void testCreateFilter() {
        FilterBean filter = new FilterBean();
        filter.setName("Filter_".concat(new Date().toString()));
        filter.setDescription("foobar");
        filter.setFavourite(Boolean.TRUE);
        filter.setJql("project = DEMO");
        final Future<FilterBean> future = jiraRestClient.getSearchClient().createSearchFilter(filter);
        final FilterBean filterBean;
        try {
            filterBean = future.get();
            Assert.assertNotNull(filterBean);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
            final Throwable cause = e.getCause();
            cause.printStackTrace();
        }

    }

    @Test
    public void testGetFilterForLoggedInUser() throws ExecutionException, InterruptedException {
        final Future<List<FilterBean>> future = jiraRestClient.getSearchClient().getFavoriteFilter();
        while(future.isDone() == false);
        final List<FilterBean> filterBeans = future.get();
        Assert.assertNotNull(filterBeans);
        Assert.assertFalse(filterBeans.isEmpty());
    }
}
