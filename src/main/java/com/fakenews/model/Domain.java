package com.fakenews.model;

public class Domain {
    public String domain;
    public int real;
    public int unverifiable;
    public int fake;
    public boolean blocked;

    public Domain(String domain, int real, int unverifiable, int fake) {
        this.domain = domain;
        this.real = real;
        this.unverifiable = unverifiable;
        this.fake = fake;
        this.blocked = false;
    }
}
