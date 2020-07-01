package edu.asu.diging.cord19.explorer.core.service.worker.impl;

import com.opencsv.bean.CsvBindByName;

public class MetadataEntry {
    @CsvBindByName(column = "cord_uid")
    private String cord_uid;

    @CsvBindByName(column = "sha")
    private String sha;
    @CsvBindByName(column = "source_x")
    private String sourceX;
    @CsvBindByName(column = "title")
    private String title;
    @CsvBindByName(column = "doi")
    private String doi;
    @CsvBindByName(column = "pmcid")
    private String pmcid;
    @CsvBindByName(column = "pubmed_id")
    private String pubmed_id;
    @CsvBindByName(column = "license")
    private String license;
    @CsvBindByName(column = "publish_time")
    private String publishTime;
    @CsvBindByName(column = "journal")
    private String journal;
    @CsvBindByName(column = "mag_id")
    private String MsAcademicPaperId;
    @CsvBindByName(column = "who_covidence_id")
    private String whoCov;
    @CsvBindByName(column = "pdf_json_files")
    private String pdfJsonFiles;
    @CsvBindByName(column = "pmc_json_files")
    private String pmcJsonFiles;
    @CsvBindByName(column = "full_text_file")
    private String fullTextFile;
    @CsvBindByName(column = "url")
    private String url;
    @CsvBindByName(column = "arxiv_id")
    private String arxivId;
    @CsvBindByName(column = "s2_id")
    private String s2Id;

    public MetadataEntry() {
    }

    public String getCord_uid() {
        return cord_uid;
    }

    public void setCord_uid(String cord_uid) {
        this.cord_uid = cord_uid;
    }

    public String getSha() {
        return sha;
    }

    public void setSha(String sha) {
        this.sha = sha;
    }

    public String getSourceX() {
        return sourceX;
    }

    public void setSourceX(String sourceX) {
        this.sourceX = sourceX;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getPubmed_id() {
        return pubmed_id;
    }

    public void setPubmed_id(String pubmed_id) {
        this.pubmed_id = pubmed_id;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    public String getJournal() {
        return journal;
    }

    public void setJournal(String journal) {
        this.journal = journal;
    }

    public String getMsAcademicPaperId() {
        return MsAcademicPaperId;
    }

    public void setMsAcademicPaperId(String msAcademicPaperId) {
        MsAcademicPaperId = msAcademicPaperId;
    }

    public String getWhoCov() {
        return whoCov;
    }

    public void setWhoCov(String whoCov) {
        this.whoCov = whoCov;
    }

    public String getPdfJsonFiles() {
        return pdfJsonFiles;
    }

    public void setPdfJsonFiles(String pdfJsonFiles) {
        this.pdfJsonFiles = pdfJsonFiles;
    }

    public String getPmcJsonFiles() {
        return pmcJsonFiles;
    }

    public void setPmcJsonFiles(String pmcJsonFiles) {
        this.pmcJsonFiles = pmcJsonFiles;
    }

    public String getFullTextFile() {
        return fullTextFile;
    }

    public void setFullTextFile(String fullTextFile) {
        this.fullTextFile = fullTextFile;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getArxivId() {
        return arxivId;
    }

    public void setArxivId(String arxivId) {
        this.arxivId = arxivId;
    }

    public String getS2Id() {
        return s2Id;
    }

    public void setS2Id(String s2Id) {
        this.s2Id = s2Id;
    }
}