package edu.asu.diging.cord19.explorer.core.model.impl;

import edu.asu.diging.cord19.explorer.core.model.Category;

public class CategoryImpl implements Category {

    private String term;
    private String label;
    private String scheme;
    
    public CategoryImpl() {}
    
    public CategoryImpl(String term, String scheme, String label) {
        this.term = term;
        this.label = label;
        this.scheme = scheme;
    }
    
    /* (non-Javadoc)
     * @see edu.asu.diging.cord19.explorer.core.model.impl.Category#getTerm()
     */
    @Override
    public String getTerm() {
        return term;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.cord19.explorer.core.model.impl.Category#setTerm(java.lang.String)
     */
    @Override
    public void setTerm(String term) {
        this.term = term;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.cord19.explorer.core.model.impl.Category#getLabel()
     */
    @Override
    public String getLabel() {
        return label;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.cord19.explorer.core.model.impl.Category#setLabel(java.lang.String)
     */
    @Override
    public void setLabel(String label) {
        this.label = label;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.cord19.explorer.core.model.impl.Category#getScheme()
     */
    @Override
    public String getScheme() {
        return scheme;
    }
    /* (non-Javadoc)
     * @see edu.asu.diging.cord19.explorer.core.model.impl.Category#setScheme(java.lang.String)
     */
    @Override
    public void setScheme(String scheme) {
        this.scheme = scheme;
    }
    
}
