package edu.asu.diging.cord19.explorer.core.service.worker;

import edu.asu.diging.cord19.explorer.core.model.impl.CleanedCoordinatesImpl;

public interface CoordinateParser {

    CleanedCoordinatesImpl parse(String coordinates);

}