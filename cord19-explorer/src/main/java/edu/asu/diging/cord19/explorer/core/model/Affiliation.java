package edu.asu.diging.cord19.explorer.core.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import edu.asu.diging.cord19.explorer.core.model.impl.AffiliationImpl;

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

}