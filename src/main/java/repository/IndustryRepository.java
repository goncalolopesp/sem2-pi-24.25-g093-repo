package repository;

import domain.Industry;

import java.util.ArrayList;
import java.util.List;

public class IndustryRepository {
    private static IndustryRepository instance;
    private List<Industry> industries;

    public IndustryRepository() {
        industries = new ArrayList<>();
    }

    public static IndustryRepository getInstance() {
        if (instance == null) {
            instance = new IndustryRepository();
        }
        return instance;
    }

    public void addIndustry(Industry industry) {
        industries.add(industry);
    }

    public List<Industry> getIndustries() {
        return List.copyOf(industries);
    }

    public void clearIndustries() {
        industries.clear();
    }
}
