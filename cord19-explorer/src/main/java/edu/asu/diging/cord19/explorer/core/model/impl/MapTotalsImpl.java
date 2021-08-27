package edu.asu.diging.cord19.explorer.core.model.impl;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import edu.asu.diging.cord19.explorer.core.model.MapTotals;

@Entity
public class MapTotalsImpl implements MapTotals {

    @Id
    @Column(name = "U_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int highCount;

    private int lowCount;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long u_id) {
        id = u_id;
    }

    @Override
    public int getHighCount() {
        return highCount;
    }

    @Override
    public void setHighCount(int i) {
        this.highCount = i;
    }

    @Override
    public int getLowCount() {
        return lowCount;
    }

    @Override
    public void setLowCount(int lowCount) {
        this.lowCount = lowCount;
    }

}