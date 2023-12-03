package com.example.godtierandroidapp.tag;

import java.io.Serializable;

/**
 *
 * @author Alex
 * @version 1.0
 * @since 2023-11-06
 */
public class Tag implements Serializable, Comparable<Tag> {
    private String name;

    @Override
    public int compareTo(Tag otherTag){
        return this.name.compareTo(otherTag.getName());
    }

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
