package edu.asu.diging.cord19.explorer.core.model.impl;

import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import edu.asu.diging.cord19.explorer.core.model.LocationMatch;
import edu.asu.diging.cord19.explorer.core.model.Metadata;
import edu.asu.diging.cord19.explorer.core.model.Publication;

@JsonIgnoreProperties(ignoreUnknown=true)
public class PublicationImpl implements Publication {
	
	@Id 
	private ObjectId id;
	private String cordId;
	private String sha;
	@JsonProperty("paper_id")
	private String paperId;
	private Metadata metadata;
	private String doi;
	private String pmcid;
	private String pubmedId;
	private String license;
	private String sourceX;
	private String publishTime;
	private int publishYear;
	private String journal;
	private String msAcademicPaperId;
	private boolean hasPdfParse;
	private boolean hasPmcXmlParse;
	private String fulltextFile;
	private String url;
	private String whoCovidence;
	
	@JsonProperty("abstract")
	private List<ParagraphImpl> abstracts;
	@JsonProperty("body_text")
	private List<ParagraphImpl> bodyText;
	@JsonProperty("bib_entries")
	private Map<String, BibEntryImpl> bibEntries;
	@JsonProperty("ref_entries")
	private Map<String, RefEntryImpl> refEntries;
	@JsonProperty("back_matter")
	private List<ParagraphImpl> backMatter;
	
	
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.Publication#getCordId()
	 */
	@Override
	public String getCordId() {
		return cordId;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.Publication#setCordId(java.lang.String)
	 */
	@Override
	public void setCordId(String cordId) {
		this.cordId = cordId;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.Publication#getSha()
	 */
	@Override
	public String getSha() {
		return sha;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.Publication#setSha(java.lang.String)
	 */
	@Override
	public void setSha(String sha) {
		this.sha = sha;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.Publication#getPaperId()
	 */
	@Override
	public String getPaperId() {
		return paperId;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.Publication#setPaperId(java.lang.String)
	 */
	@Override
	public void setPaperId(String paperId) {
		this.paperId = paperId;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.Publication#getMetadata()
	 */
	@Override
	public Metadata getMetadata() {
		return metadata;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.Publication#setMetadata(edu.asu.diging.cord19.explorer.core.model.impl.MetadataImpl)
	 */
	@Override
	public void setMetadata(Metadata metadata) {
		this.metadata = metadata;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.Publication#getDoi()
	 */
	@Override
	public String getDoi() {
		return doi;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.Publication#setDoi(java.lang.String)
	 */
	@Override
	public void setDoi(String doi) {
		this.doi = doi;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.Publication#getPmcid()
	 */
	@Override
	public String getPmcid() {
		return pmcid;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.Publication#setPmcid(java.lang.String)
	 */
	@Override
	public void setPmcid(String pmcid) {
		this.pmcid = pmcid;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.Publication#getPubmedId()
	 */
	@Override
	public String getPubmedId() {
		return pubmedId;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.Publication#setPubmedId(java.lang.String)
	 */
	@Override
	public void setPubmedId(String pubmedId) {
		this.pubmedId = pubmedId;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.Publication#getLicense()
	 */
	@Override
	public String getLicense() {
		return license;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.Publication#setLicense(java.lang.String)
	 */
	@Override
	public void setLicense(String license) {
		this.license = license;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.Publication#getSourceX()
	 */
	@Override
	public String getSourceX() {
		return sourceX;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.Publication#setSourceX(java.lang.String)
	 */
	@Override
	public void setSourceX(String sourceX) {
		this.sourceX = sourceX;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.Publication#getPublishTime()
	 */
	@Override
	public String getPublishTime() {
		return publishTime;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.Publication#setPublishTime(java.lang.String)
	 */
	@Override
	public void setPublishTime(String publishTime) {
		this.publishTime = publishTime;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.Publication#getPublishYear()
	 */
	@Override
	public int getPublishYear() {
		return publishYear;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.Publication#setPublishYear(int)
	 */
	@Override
	public void setPublishYear(int publishYear) {
		this.publishYear = publishYear;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.Publication#getJournal()
	 */
	@Override
	public String getJournal() {
		return journal;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.Publication#setJournal(java.lang.String)
	 */
	@Override
	public void setJournal(String journal) {
		this.journal = journal;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.Publication#getMsAcademicPaperId()
	 */
	@Override
	public String getMsAcademicPaperId() {
		return msAcademicPaperId;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.Publication#setMsAcademicPaperId(java.lang.String)
	 */
	@Override
	public void setMsAcademicPaperId(String msAcademicPaperId) {
		this.msAcademicPaperId = msAcademicPaperId;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.Publication#isHasPdfParse()
	 */
	@Override
	public boolean isHasPdfParse() {
		return hasPdfParse;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.Publication#setHasPdfParse(boolean)
	 */
	@Override
	public void setHasPdfParse(boolean hasPdfParse) {
		this.hasPdfParse = hasPdfParse;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.Publication#isHasPmcXmlParse()
	 */
	@Override
	public boolean isHasPmcXmlParse() {
		return hasPmcXmlParse;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.Publication#setHasPmcXmlParse(boolean)
	 */
	@Override
	public void setHasPmcXmlParse(boolean hasPmcXmlParse) {
		this.hasPmcXmlParse = hasPmcXmlParse;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.Publication#getFulltextFile()
	 */
	@Override
	public String getFulltextFile() {
		return fulltextFile;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.Publication#setFulltextFile(java.lang.String)
	 */
	@Override
	public void setFulltextFile(String fulltextFile) {
		this.fulltextFile = fulltextFile;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.Publication#getUrl()
	 */
	@Override
	public String getUrl() {
		return url;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.Publication#setUrl(java.lang.String)
	 */
	@Override
	public void setUrl(String url) {
		this.url = url;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.Publication#getWhoCovidence()
	 */
	@Override
	public String getWhoCovidence() {
		return whoCovidence;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.Publication#setWhoCovidence(java.lang.String)
	 */
	@Override
	public void setWhoCovidence(String whoCovidence) {
		this.whoCovidence = whoCovidence;
	}

	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.Metadata#getAbstracts()
	 */
	@Override
	public List<ParagraphImpl> getAbstracts() {
		return abstracts;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.Metadata#setAbstracts(java.util.List)
	 */
	@Override
	public void setAbstracts(List<ParagraphImpl> abstracts) {
		this.abstracts = abstracts;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.Metadata#getBodyText()
	 */
	@Override
	public List<ParagraphImpl> getBodyText() {
		return bodyText;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.Metadata#setBodyText(java.util.List)
	 */
	@Override
	public void setBodyText(List<ParagraphImpl> bodyText) {
		this.bodyText = bodyText;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.Metadata#getBibEntries()
	 */
	@Override
	public Map<String, BibEntryImpl> getBibEntries() {
		return bibEntries;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.Metadata#setBibEntries(java.util.Map)
	 */
	@Override
	public void setBibEntries(Map<String, BibEntryImpl> bibEntries) {
		this.bibEntries = bibEntries;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.Metadata#getRefEntries()
	 */
	@Override
	public Map<String, RefEntryImpl> getRefEntries() {
		return refEntries;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.Metadata#setRefEntries(java.util.Map)
	 */
	@Override
	public void setRefEntries(Map<String, RefEntryImpl> refEntries) {
		this.refEntries = refEntries;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.Metadata#getBackMatter()
	 */
	@Override
	public List<ParagraphImpl> getBackMatter() {
		return backMatter;
	}
	/* (non-Javadoc)
	 * @see edu.asu.diging.cord19.explorer.core.model.impl.Metadata#setBackMatter(java.util.List)
	 */
	@Override
	public void setBackMatter(List<ParagraphImpl> backMatter) {
		this.backMatter = backMatter;
	}
	
}
