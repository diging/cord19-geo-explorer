package edu.asu.diging.cord19.explorer.core.service.worker.impl;

public class CsvParseResult {

    private int processed;
    private int updated;
    private int added;
    private int skipped;
    
    public CsvParseResult() {}
    
    public CsvParseResult(int processed, int updated, int added, int skipped) {
        super();
        this.processed = processed;
        this.updated = updated;
        this.added = added;
        this.skipped = skipped;
    }
    
    public int getProcessed() {
        return processed;
    }
    public void setProcessed(int processed) {
        this.processed = processed;
    }
    public int getUpdated() {
        return updated;
    }
    public void setUpdated(int updated) {
        this.updated = updated;
    }
    public int getAdded() {
        return added;
    }
    public void setAdded(int added) {
        this.added = added;
    }
    public int getSkipped() {
        return skipped;
    }
    public void setSkipped(int skipped) {
        this.skipped = skipped;
    }
    
    
}
