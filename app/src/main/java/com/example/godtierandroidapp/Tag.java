package com.example.godtierandroidapp;

import java.io.Serializable;

public class Tag implements Serializable {
    private String name;

    @Override
    public boolean equals(Object other) {
        if (other.getClass() != getClass()) {
            return false;
        }

        Tag otherTag = (Tag) other;
        return otherTag.getName().equals(getName());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Tag(String name) {
        this.name = name;
    }
}
