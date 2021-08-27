package edu.asu.diging.cord19.explorer.core.model;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import edu.asu.diging.cord19.explorer.core.model.impl.AffiliationImpl;
import edu.asu.diging.cord19.explorer.core.model.impl.WikipediaArticleImpl;
import edu.asu.diging.cord19.explorer.core.model.impl.WikipediaSelectionStatus;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, defaultImpl = AffiliationImpl.class)
public interface Affiliation {

    String getLaboratory();

    void setLaboratory(String laboratory);

    String getInstitution();

    void setInstitution(String institution);

    String getLocationSettlement();

    void setLocationSettlement(String locationSettlement);

    String getLocationRegion();

    void setLocationRegion(String locationRegion);

    String getLocationCountry();

    void setLocationCountry(String locationCountry);

    Map<String, Object> getOther();

    void setOther(Map<String, Object> other);

    void setWikiarticles(List<WikipediaArticleImpl> wikiarticles);

    List<WikipediaArticleImpl> getWikiarticles();

    void setSelectedWikiarticle(WikipediaArticleImpl selectedWikiarticle);

    WikipediaArticleImpl getSelectedWikiarticle();

    void setSelectionCheckedOn(String selectionCheckedOn);

    String getSelectionCheckedOn();

    void setSelectionStatus(WikipediaSelectionStatus selectionStatus);

    WikipediaSelectionStatus getSelectionStatus();

}