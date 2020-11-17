package edu.asu.diging.cord19.explorer.core.service.worker.impl;

import java.util.List;
import java.util.Map;

import edu.asu.diging.cord19.explorer.core.model.PublicationType;
import edu.asu.diging.cord19.explorer.core.model.impl.CategoryImpl;

public class ExportedMetadataEntry {

    private String cordId;
    private String sha;
    private String paperId;
    private String title;
    private String authors;
    private String doi;
    private String pmcid;
    private String pubmedId;
    private String arxivId;
    private String license;
    private String sourceX;
    private String publishTime;
    private int publishYear;
    private String journal;
    private String msAcademicPaperId;
    private String fulltextFile;
    private String url;
    private String documentUrl;
    private String documentType;
    private String whoCovidence;
    
    private String database;

    private String categories;
    private String primaryCategory;
    private String comment;
    private String abstractText;
    
    private String publicationType;
    private String funder;
    private String volume;
    private String issue;
    private String pages;
    
    private String meshTerms;
    private int timesCited;
    private int recentCitations;
    
    private String extraData;
    
    public String getCordId() {
        return cordId;
    }
    public void setCordId(String cordId) {
        this.cordId = cordId;
    }
    public String getSha() {
        return sha;
    }
    public void setSha(String sha) {
        this.sha = sha;
    }
    public String getPaperId() {
        return paperId;
    }
    public void setPaperId(String paperId) {
        this.paperId = paperId;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getAuthors() {
        return authors;
    }
    public void setAuthors(String authors) {
        this.authors = authors;
    }
    public String getDoi() {
        return doi;
    }
    public void setDoi(String doi) {
        this.doi = doi;
    }
    public String getPmcid() {
        return pmcid;
    }
    public void setPmcid(String pmcid) {
        this.pmcid = pmcid;
    }
    public String getPubmedId() {
        return pubmedId;
    }
    public void setPubmedId(String pubmedId) {
        this.pubmedId = pubmedId;
    }
    public String getArxivId() {
        return arxivId;
    }
    public void setArxivId(String arxivId) {
        this.arxivId = arxivId;
    }
    public String getLicense() {
        return license;
    }
    public void setLicense(String license) {
        this.license = license;
    }
    public String getSourceX() {
        return sourceX;
    }
    public void setSourceX(String sourceX) {
        this.sourceX = sourceX;
    }
    public String getPublishTime() {
        return publishTime;
    }
    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }
    public int getPublishYear() {
        return publishYear;
    }
    public void setPublishYear(int publishYear) {
        this.publishYear = publishYear;
    }
    public String getJournal() {
        return journal;
    }
    public void setJournal(String journal) {
        this.journal = journal;
    }
    public String getMsAcademicPaperId() {
        return msAcademicPaperId;
    }
    public void setMsAcademicPaperId(String msAcademicPaperId) {
        this.msAcademicPaperId = msAcademicPaperId;
    }
    public String getFulltextFile() {
        return fulltextFile;
    }
    public void setFulltextFile(String fulltextFile) {
        this.fulltextFile = fulltextFile;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getDocumentUrl() {
        return documentUrl;
    }
    public void setDocumentUrl(String documentUrl) {
        this.documentUrl = documentUrl;
    }
    public String getDocumentType() {
        return documentType;
    }
    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }
    public String getWhoCovidence() {
        return whoCovidence;
    }
    public void setWhoCovidence(String whoCovidence) {
        this.whoCovidence = whoCovidence;
    }
    public String getDatabase() {
        return database;
    }
    public void setDatabase(String database) {
        this.database = database;
    }
    public String getCategories() {
        return categories;
    }
    public void setCategories(String categories) {
        this.categories = categories;
    }
    public String getPrimaryCategory() {
        return primaryCategory;
    }
    public void setPrimaryCategory(String primaryCategory) {
        this.primaryCategory = primaryCategory;
    }
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
    public String getAbstractText() {
        return abstractText;
    }
    public void setAbstractText(String abstractText) {
        this.abstractText = abstractText;
    }
    public String getPublicationType() {
        return publicationType;
    }
    public void setPublicationType(String publicationType) {
        this.publicationType = publicationType;
    }
    public String getFunder() {
        return funder;
    }
    public void setFunder(String funder) {
        this.funder = funder;
    }
    public String getVolume() {
        return volume;
    }
    public void setVolume(String volume) {
        this.volume = volume;
    }
    public String getIssue() {
        return issue;
    }
    public void setIssue(String issue) {
        this.issue = issue;
    }
    public String getPages() {
        return pages;
    }
    public void setPages(String pages) {
        this.pages = pages;
    }
    public String getMeshTerms() {
        return meshTerms;
    }
    public void setMeshTerms(String meshTerms) {
        this.meshTerms = meshTerms;
    }
    public int getTimesCited() {
        return timesCited;
    }
    public void setTimesCited(int timesCited) {
        this.timesCited = timesCited;
    }
    public int getRecentCitations() {
        return recentCitations;
    }
    public void setRecentCitations(int recentCitations) {
        this.recentCitations = recentCitations;
    }
    public String getExtraData() {
        return extraData;
    }
    public void setExtraData(String extraData) {
        this.extraData = extraData;
    }
    
    
}
