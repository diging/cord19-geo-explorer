package edu.asu.diging.cord19.explorer.core.model.impl;

import edu.asu.diging.cord19.explorer.core.model.RefEntry;

public class RefEntryImpl implements RefEntry {

    private String text;
    private String type;
    private String latex;
    private String html;

    /*
     * (non-Javadoc)
     * 
     * @see edu.asu.diging.cord19.explorer.core.model.impl.RefEntry#getText()
     */
    @Override
    public String getText() {
        return text;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.asu.diging.cord19.explorer.core.model.impl.RefEntry#setText(java.lang.
     * String)
     */
    @Override
    public void setText(String text) {
        this.text = text;
    }

    /*
     * (non-Javadoc)
     * 
     * @see edu.asu.diging.cord19.explorer.core.model.impl.RefEntry#getType()
     */
    @Override
    public String getType() {
        return type;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.asu.diging.cord19.explorer.core.model.impl.RefEntry#setType(java.lang.
     * String)
     */
    @Override
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String getLatex() {
        return latex;
    }

    @Override
    public void setLatex(String latex) {
        this.latex = latex;
    }

    @Override
    public String getHtml() {
        return html;
    }

    @Override
    public void setHtml(String html) {
        this.html = html;
    }

}
