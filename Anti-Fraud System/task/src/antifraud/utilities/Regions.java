package antifraud.utilities;

import java.util.HashMap;
import java.util.Map;

public class Regions {

    private final Map<String, String> regions = new HashMap<>();
    public Map<String, String> getRegions() {
        regions.put("EAP", "East Asia and Pacific");
        regions.put("ECA", "Europe and Central Asia");
        regions.put("HIC", "High-Income countries");
        regions.put("LAC", "Latin America and the Caribbean");
        regions.put("MENA", "The Middle East and North Africa");
        regions.put("SA", "South Asia");
        regions.put("SSA", "Sub-Saharan Africa");
        return regions;
    }
}
