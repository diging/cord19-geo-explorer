package edu.asu.diging.cord19.explorer.core.service.worker.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Component;

import edu.asu.diging.cord19.explorer.core.elastic.model.impl.Wikientry;
import edu.asu.diging.cord19.explorer.core.service.worker.ElasticsearchConnector;

@Component
public class ElasticsearchConnectorImpl implements ElasticsearchConnector {

    @Qualifier("elasticsearchTemplate")
    @Autowired
    private ElasticsearchOperations searchTemplate;

    
    /* (non-Javadoc)
     * @see edu.asu.diging.cord19.explorer.core.service.impl.ElasticsearchConnector#followRedirect(edu.asu.diging.cord19.explorer.core.elastic.model.impl.Wikientry)
     */
    @Override
    public Wikientry followRedirect(Wikientry entry) {
        Pattern redirectPattern = Pattern.compile("#([rR][eE][Dd][Ii][Rr][Ee][Cc][Tt]) \\[\\[(.+?)\\]\\]");
        Matcher redirectMatcher = redirectPattern.matcher(entry.getComplete_text());

        if (redirectMatcher.find()) {
            PageRequest redirectPage = PageRequest.of(0, 1);
            String searchTerm = prepareSearchTerm(redirectMatcher.group(2));

            if (searchTerm.trim().isEmpty()) {
                return null;
            }

            BoolQueryBuilder redirectBuilder = QueryBuilders.boolQuery();
            redirectBuilder.must(QueryBuilders.termQuery("title_keyword", searchTerm));

            NativeSearchQueryBuilder redirectQueryBuilder = new NativeSearchQueryBuilder();
            redirectQueryBuilder.withQuery(redirectBuilder);
            redirectQueryBuilder.withPageable(redirectPage);
            NativeSearchQuery redirectQuery = redirectQueryBuilder.build();
            List<Wikientry> redirectEntry = searchTemplate.queryForList(redirectQuery, Wikientry.class);

            if (redirectEntry.size() > 0) {
                return redirectEntry.get(0);
            }
        }

        return null;
    }
    
    /* (non-Javadoc)
     * @see edu.asu.diging.cord19.explorer.core.service.impl.ElasticsearchConnector#searchInTitle(java.lang.String)
     */
    @Override
    public List<Wikientry> searchInTitle(String location) {
        PageRequest page = PageRequest.of(0, 10);

        BoolQueryBuilder builder = QueryBuilders.boolQuery();
        String searchTerm = prepareSearchTerm(location);
        if (searchTerm.trim().isEmpty()) {
            return new ArrayList<>();
        }
        builder.must(QueryBuilders.queryStringQuery("title:" + searchTerm));
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        nativeSearchQueryBuilder.withQuery(builder);
        nativeSearchQueryBuilder.withPageable(page);
        NativeSearchQuery query = nativeSearchQueryBuilder.build();
        List<Wikientry> entries = searchTemplate.queryForList(query, Wikientry.class);
        for (Wikientry entry : entries) {
            String text = entry.getComplete_text();
            Pattern pDesc = Pattern.compile("\\{\\{ *?short description *?\\|(.+?)\\}\\}");
            Matcher mDesc = pDesc.matcher(text);
            if (mDesc.find()) {
                entry.setShortDescription(mDesc.group(1));
                continue;
            }
            
            Pattern pRedir = Pattern.compile("#[Rr][Ee][Dd][Ii][Rr][Ee][Cc][Tt] +?\\[\\[(.+?)\\]\\]");
            Matcher mRedir = pRedir.matcher(text);
            if (mRedir.find()) {
                entry.setRedirectsTo(mRedir.group(1));
            }
        }
        return entries;
    }
    
    @Override
    public Wikientry findById(String id) {
        BoolQueryBuilder builder = QueryBuilders.boolQuery();
        builder.must(QueryBuilders.queryStringQuery("_id:" + id));
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        nativeSearchQueryBuilder.withQuery(builder);
        NativeSearchQuery query = nativeSearchQueryBuilder.build();
        List<Wikientry> entries = searchTemplate.queryForList(query, Wikientry.class);
        if (entries != null && entries.size() > 0) {
            return entries.get(0);
        }
        return null;
    }
    
    private String prepareSearchTerm(String term) {
        term = term.replaceAll("/", " ").replaceAll("\\(", " ").replaceAll("\\)", " ");
        term = term.replaceAll("\\[", " ").replaceAll("\\]", " ").replaceAll(":", " ");
        term = term.replaceAll("\\{", " ").replaceAll("\\}", " ").replaceAll("~", " ").replaceAll("\\*",  " ");
        term = term.replaceAll("\"", "").replaceAll("'", "").replaceAll("\\^", "").replaceAll("\\#", " ");
        term = term.replaceAll("!", "").replaceAll("\\-", " ").replaceAll("\\.", " ");
        term = term.replaceAll("_", " ").replaceAll("\\\\", " ").replaceAll("\\+", " ");
        term = term.replaceAll("\\|",  " ").replaceAll(" OR", " ").replaceAll(" OR ", " ");
        term = term.replaceAll(" AND$", " ").replaceAll("^AND ", " ").replaceAll(" AND ", " ");

        // FIXME: check against US states
        if (term.trim().equals("OR") || term.trim().equals("AND")) {
            return "";
        }
        return term;
    }

}
