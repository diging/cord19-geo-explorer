package edu.asu.diging.cord19.explorer.core.model;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import edu.asu.diging.cord19.explorer.core.model.impl.BibEntryImpl;
import edu.asu.diging.cord19.explorer.core.model.impl.CategoryImpl;
import edu.asu.diging.cord19.explorer.core.model.impl.ParagraphImpl;
import edu.asu.diging.cord19.explorer.core.model.impl.PublicationImpl;
import edu.asu.diging.cord19.explorer.core.model.impl.RefEntryImpl;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, defaultImpl = PublicationImpl.class)
public interface Publication {
    
    final static String DATABASE_ARXIV = "arxiv";
    final static String DATABASE_DIMENSIONS = "dimensions";

    String getCordId();

    void setCordId(String cordId);

    String getSha();

    void setSha(String sha);

    String getPaperId();

    void setPaperId(String paperId);

    Metadata getMetadata();

    void setMetadata(Metadata metadata);

    String getDoi();

    void setDoi(String doi);

    String getPmcid();

    void setPmcid(String pmcid);

    String getPubmedId();

    void setPubmedId(String pubmedId);

    String getLicense();

    void setLicense(String license);

    String getSourceX();

    void setSourceX(String sourceX);

    String getPublishTime();

    void setPublishTime(String publishTime);

    int getPublishYear();

    void setPublishYear(int publishYear);

    String getJournal();

    void setJournal(String journal);

    String getMsAcademicPaperId();

    void setMsAcademicPaperId(String msAcademicPaperId);

    boolean isHasPdfParse();

    void setHasPdfParse(boolean hasPdfParse);

    boolean isHasPmcXmlParse();

    void setHasPmcXmlParse(boolean hasPmcXmlParse);

    String getFulltextFile();

    void setFulltextFile(String fulltextFile);

    String getUrl();

    void setUrl(String url);

    String getWhoCovidence();

    void setWhoCovidence(String whoCovidence);

    List<ParagraphImpl> getAbstracts();

    void setAbstracts(List<ParagraphImpl> abstracts);

    List<ParagraphImpl> getBodyText();

    void setBodyText(List<ParagraphImpl> bodyText);

    Map<String, BibEntryImpl> getBibEntries();

    void setBibEntries(Map<String, BibEntryImpl> bibEntries);

    Map<String, RefEntryImpl> getRefEntries();

    void setRefEntries(Map<String, RefEntryImpl> refEntries);

    List<ParagraphImpl> getBackMatter();

    void setBackMatter(List<ParagraphImpl> backMatter);

    void setDatabase(String database);

    String getDatabase();

    void setDocumentType(String documentType);

    String getDocumentType();

    void setDocumentUrl(String documentUrl);

    String getDocumentUrl();

    void setCategories(List<CategoryImpl> categories);

    List<CategoryImpl> getCategories();

    void setComment(String comment);

    String getComment();

    void setPrimaryCategory(CategoryImpl primaryCategory);

    CategoryImpl getPrimaryCategory();

    void setArxivId(String arxivId);

    String getArxivId();

    void setPdfJsonFiles(String pdfJsonFiles);

    String getPdfJsonFiles();

    void setPmcJsonFiles(String pmcJsonFiles);

    String getPmcJsonFiles();

    void setDuplicate(boolean duplicate);

    boolean isDuplicate();

    void setExtraData(Map<String, Object> extraData);

    Map<String, Object> getExtraData();

    void setFunder(String funder);

    String getFunder();

    void setMeshTerms(List<String> meshTerms);

    List<String> getMeshTerms();

    void setIssue(String issue);

    String getIssue();

    void setVolume(String volume);

    String getVolume();

    void setPages(String pages);

    String getPages();

    void setPublicationType(PublicationType publicationType);

    PublicationType getPublicationType();

    void setRecentCitations(int recentCitations);

    int getRecentCitations();

    void setTimesCited(int timesCited);

    int getTimesCited();
}