package alchemy.object;

import java.io.Serializable;
import java.util.Objects;

public class Effect implements IEffect, Serializable {
    private int id;
    private String title;
    private String description;

    public Effect(int id, String title,String description) {
        this.id = id;
        this.title = title;
        this.description = description;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Effect)) return false;
        Effect other = (Effect) obj;
        return id == other.id &&
                Objects.equals(title, other.title) &&
                Objects.equals(description, other.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description);
    }
}

