package edu.asu.diging.cord19.explorer.core.service.worker.impl;

import java.util.Arrays;

import org.springframework.stereotype.Component;

import edu.asu.diging.cord19.explorer.core.model.Affiliation;
import edu.asu.diging.cord19.explorer.core.model.Publication;
import edu.asu.diging.cord19.explorer.core.model.impl.CategoryImpl;
import edu.asu.diging.cord19.explorer.core.model.impl.ParagraphImpl;
import edu.asu.diging.cord19.explorer.core.model.impl.PersonImpl;
import edu.asu.diging.cord19.explorer.core.service.worker.ExportedMetadataFactory;

@Component
public class ExportedMetadataFactoryImpl implements ExportedMetadataFactory {

    /* (non-Javadoc)
     * @see edu.asu.diging.cord19.explorer.core.service.impl.IExportedMetadataFactory#createEntry(edu.asu.diging.cord19.explorer.core.model.Publication)
     */
    @Override
    public ExportedMetadataEntry createEntry(Publication pub) {
        ExportedMetadataEntry mdEntry = new ExportedMetadataEntry();

        mdEntry.setArxivId(pub.getArxivId());
        mdEntry.setComment(pub.getComment());
        mdEntry.setCordId(pub.getCordId());
        mdEntry.setDatabase(pub.getDatabase());
        mdEntry.setDocumentType(pub.getDocumentType());
        mdEntry.setDocumentUrl(pub.getDocumentUrl());
        mdEntry.setDoi(pub.getDoi());
        mdEntry.setFulltextFile(pub.getFulltextFile());
        mdEntry.setJournal(pub.getJournal());
        mdEntry.setLicense(pub.getLicense());
        mdEntry.setMsAcademicPaperId(pub.getMsAcademicPaperId());
        mdEntry.setPaperId(pub.getPaperId());
        mdEntry.setPmcid(pub.getPmcid());
        mdEntry.setPublishTime(pub.getPublishTime());
        mdEntry.setPublishYear(pub.getPublishYear());
        mdEntry.setPubmedId(pub.getPubmedId());
        mdEntry.setSha(pub.getSha());
        mdEntry.setSourceX(pub.getSourceX());
        if (pub.getMetadata() != null) {
            mdEntry.setTitle(pub.getMetadata().getTitle());
        }
        mdEntry.setUrl(pub.getUrl());
        mdEntry.setWhoCovidence(pub.getWhoCovidence());

        setAuthors(pub, mdEntry);
        setAbstract(pub, mdEntry);
        setCategories(pub, mdEntry);
        
        if (pub.getPrimaryCategory() != null) {
            mdEntry.setPrimaryCategory(pub.getPrimaryCategory().getTerm() + " (" + pub.getPrimaryCategory().getScheme() + ")");
        }
        return mdEntry;
    }

    private void setCategories(Publication pub, ExportedMetadataEntry mdEntry) {
        StringBuffer catSb = new StringBuffer();
        if (pub.getCategories() != null) {
            for (CategoryImpl cat : pub.getCategories()) {
                if (pub.getCategories().indexOf(cat) > 0) {
                    catSb.append(", ");
                }
                catSb.append(cat.getTerm());
                catSb.append(" (" + cat.getScheme() + ")");
            }
        }
        mdEntry.setCategories(catSb.toString());
    }

    private void setAbstract(Publication pub, ExportedMetadataEntry mdEntry) {
        StringBuffer abstractText = new StringBuffer();
        if (pub.getAbstracts() != null) {
            for (ParagraphImpl para : pub.getAbstracts()) {
                abstractText.append(para.getText() + "\n");
            }
        }
        
        mdEntry.setAbstractText(abstractText.toString());
    }

    private void setAuthors(Publication pub, ExportedMetadataEntry mdEntry) {
        StringBuffer authors = new StringBuffer();
        if (pub.getMetadata() != null && pub.getMetadata().getAuthors() != null) {
            for (PersonImpl p : pub.getMetadata().getAuthors()) {
                if (pub.getMetadata().getAuthors().indexOf(p) > 0) {
                    authors.append(" // ");
                }
                if (p.getName() != null && !p.getName().trim().isEmpty()) {
                    authors.append(p.getName());
                } else {
                    authors.append(String.join(" ",
                            Arrays.asList(new String[] { p.getFirst(), String.join(" ", p.getMiddle()), p.getLast() })));
                }
                
                authors.append(" [");
                if (p.getEmail() != null && !p.getEmail().trim().isEmpty()) {
                    authors.append(p.getEmail());
                }
                authors.append("] {");
                
                Affiliation aff = p.getAffiliation();
                if (aff != null) {
                    authors.append("Laboratory: " + aff.getLaboratory() + ", ");
                    authors.append("Institution: " + aff.getInstitution() + ", ");
                    authors.append("Settlement: " + aff.getLocationSettlement() + ", ");
                    authors.append("Region: " + aff.getLocationRegion() + ", ");
                    authors.append("Country: " + aff.getLocationCountry() + ", ");
                }
                
                authors.append("}");
            }
        }
        mdEntry.setAuthors(authors.toString());
    }
}
