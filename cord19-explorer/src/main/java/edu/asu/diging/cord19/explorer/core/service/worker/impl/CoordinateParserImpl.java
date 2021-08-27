package edu.asu.diging.cord19.explorer.core.service.worker.impl;

import java.util.ArrayList;
import java.util.List;

import edu.asu.diging.cord19.explorer.core.model.CoordType;
import edu.asu.diging.cord19.explorer.core.model.impl.CleanedCoordinatesImpl;
import edu.asu.diging.cord19.explorer.core.service.worker.CoordinateParser;


public class CoordinateParserImpl implements CoordinateParser {
    
    private CleanedCoordinatesImpl createCleanCoords(List<Double> formattedCoords) {
        CleanedCoordinatesImpl cleanedCoords = new CleanedCoordinatesImpl();
        cleanedCoords.setType(CoordType.Point);
        cleanedCoords.setCoordinates(formattedCoords);
        return cleanedCoords;
    }
    
    private double processLength3Coords(List<String> coords) {
        float degrees = Float.parseFloat(coords.get(0));
        float minutes = Float.parseFloat(coords.get(1));
        float seconds = Float.parseFloat(coords.get(2));
        double decimalDegrees = 0;
        if (degrees == 0) {
            decimalDegrees = ((minutes / 60.0) + (seconds / 3600.0));
        } else {
            decimalDegrees = Math.signum(degrees) * (Math.abs(degrees) + (minutes / 60.0) + (seconds / 3600.0));
        }
        return decimalDegrees;
    }

    private double processLength2Coords(List<String> coords) {
        float degrees = Float.parseFloat(coords.get(0));
        float minutes = Float.parseFloat(coords.get(1));
        double decimalDegrees = Math.signum(degrees) * (Math.abs(degrees) + (minutes / 60.0));
        return decimalDegrees;
    }
    
    private Double processCoordinates(List<String> coords, int sign) {
        if (coords.size() == 3) {
           return processLength3Coords(coords) * sign;
        } else if (coords.size() == 2) {
            return processLength2Coords(coords) * sign;
        } else {
            return Double.parseDouble(coords.get(0)) * sign;
        }
    }
    
    private  List<Double> iterateCoordString(String[] splitted) {
        // split on pipes then use Ordinals to gather Degree, Mins, Secs then compute
        // Decimal Degrees
        List<String> coords = new ArrayList<String>();
        List<Double> formattedCoords = new ArrayList<Double>();
        for (String s : splitted) {
            if (s.matches("-?\\d+(\\.\\d+)?")) {
                coords.add(s);
            } else if (coords.size() > 0 && coords.size() < 4) {
                if (s.toLowerCase().equals("n") || s.toLowerCase().equals("e")) {
                    Double processedCoords = processCoordinates(coords, 1);
                    formattedCoords.add(processedCoords);
                    coords.clear();
                }
            } else if (coords.size() > 0 && coords.size() < 4) { 
                if (s.toLowerCase().equals("s") || s.toLowerCase().equals("w")) {
                    Double processedCoords = processCoordinates(coords, -1);
                    formattedCoords.add(processedCoords);
                    coords.clear();
                }
            }
        }
        return formattedCoords;
    }

    /* (non-Javadoc)
     * @see edu.asu.diging.cord19.explorer.core.service.worker.impl.CoordinateParser#parse(java.lang.String)
     */
    @Override
    public CleanedCoordinatesImpl parse(String coordinates) {
        String[] splitted = coordinates.split("\\|");
        return createCleanCoords(iterateCoordString(splitted));
    }
    
}
