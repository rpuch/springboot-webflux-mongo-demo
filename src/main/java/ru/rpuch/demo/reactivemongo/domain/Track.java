package ru.rpuch.demo.reactivemongo.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * @author rpuch
 */
@Document
public class Track {
    @Id
    private String id;
    @Indexed(unique = true)
    private String name;
    private String description;
    private Length length;
    @DBRef
    private List<Vehicle> vehicles = new ArrayList<>();

    public Track() {
    }

    public Track(String name, String description, Length length, List<Vehicle> vehicles) {
        this.name = name;
        this.description = description;
        this.length = length;
        this.vehicles = vehicles;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Length getLength() {
        return length;
    }

    public void setLength(Length length) {
        this.length = length;
    }

    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    public void setVehicles(List<Vehicle> vehicles) {
        this.vehicles = vehicles;
    }
}
