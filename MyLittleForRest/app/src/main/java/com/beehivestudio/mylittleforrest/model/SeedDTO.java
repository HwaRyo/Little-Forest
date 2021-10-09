package com.beehivestudio.mylittleforrest.model;

public class SeedDTO {
    String seed_species;
    String seed_name;
    String seed_exp;

    public SeedDTO(String seed_species, String seed_name, String seed_exp){
        this.seed_species = seed_species;
        this.seed_name = seed_name;
        this.seed_exp = seed_exp;
    }
}
