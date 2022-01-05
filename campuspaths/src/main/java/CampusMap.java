/*
 * Copyright (C) 2021 Kevin Zatloukal.  All rights reserved.  Permission is
 * hereby granted to students registered for University of Washington
 * CSE 331 for use solely during Spring Quarter 2021 for purposes of
 * the course.  No other use, copying, distribution, or modification
 * is permitted without prior written consent. Copyrights for
 * third-party components of this work must be honored.  Instructors
 * interested in reusing these course materials should contact the
 * author.
 */

package pathfinder;

import graph.DirGraph;
import pathfinder.datastructures.Path;
import pathfinder.datastructures.Point;
import pathfinder.parser.CampusBuilding;
import pathfinder.parser.CampusPath;
import pathfinder.parser.CampusPathsParser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A CampusMap represents a campus with buildings that may be connected through
 * paths
 */
public class CampusMap implements ModelAPI {

    // AF(this) = a map of campus where 'buildingList' associates the short name
    // of a building to a CampusBuilding object that keep track of its x and y
    // coordinates. The DirGraph represents the relationship between buildings
    // and paths, as the edge label represents the distance of a path between
    // coordinate Points(the nodes).
    //
    // Representation Invariant: 'campus' and 'buildingList' cannot be null.
    // Keys, the short name of buildings, cannot be null and values (CampusBuilding)
    // also cannot be null in 'buildingList'

    /**
     * Whether to check if rep. inv. is violated or not
     */
    private static final boolean DEBUG = false;

    /**
     * This DirGraph have nodes that are Point objects and edge
     * labels that are double
     */
    private final DirGraph<Point, Double> campus;

    /**
     * This Map associates the shortName of a building to their
     * CampusBuilding objects
     */
    private final Map<String, CampusBuilding> buildingList;

    /**
     * Creates a new CampusMap using data from given files
     *
     * @spec.requires both given csv files are well-formatted. Each line in buildingFile
     * must be a comma separated list of shortName,longName,x,y. Each line in pathFile must
     * be a comma separated list of x1,y1,x2,y2,distance.
     * @spec.modifies this
     * @spec.effects creates a new CampusMap with buildings and paths from given file
     * @param buildingFile a csv file that contains all buildings of the campus
     * @param pathFile a csv file that contains all paths in the campus
     */
    public CampusMap(String buildingFile, String pathFile) {
        campus = new DirGraph<>();
        buildingList = new HashMap<>();
        List<CampusBuilding> buildings = CampusPathsParser.parseCampusBuildings(buildingFile);
        // for each building, add its point as node to graph and map the building to its name.
        for (CampusBuilding b : buildings) {
            String shortN = b.getShortName();
            buildingList.put(shortN, b);
            campus.addNode(new Point(b.getX(), b.getY()));
        }
        List<CampusPath> paths = CampusPathsParser.parseCampusPaths(pathFile);
        // adds each path as an edge to DirGraph
        for (CampusPath p : paths) {
            Point start = new Point(p.getX1(), p.getY1());
            Point end = new Point(p.getX2(), p.getY2());
            campus.addEdge(start, end, p.getDistance());
        }
        checkRep();
    }

    @Override
    public boolean shortNameExists(String shortName) {
        checkRep();
        boolean contains = buildingList.containsKey(shortName);
        checkRep();
        return contains;
    }

    @Override
    public String longNameForShort(String shortName) throws IllegalArgumentException {
        if (!shortNameExists(shortName)) {
            throw new IllegalArgumentException();
        }
        checkRep();
        String longName = buildingList.get(shortName).getLongName();
        checkRep();
        return longName;
    }

    /**
     * Returns the corresponding CampusBuilding of the given shortName
     *
     * @param shortName The short name of a building to look up.
     * @return a CampusBuilding object for the given building name
     * @throws IllegalArgumentException if the short name provided does not exist.
     */
    public CampusBuilding buildingForShort(String shortName) throws IllegalArgumentException {
        if (!shortNameExists(shortName)) {
            throw new IllegalArgumentException();
        }
        checkRep();
        CampusBuilding cb = buildingList.get(shortName);
        checkRep();
        return cb;
    }

    @Override
    public Map<String, String> buildingNames() {
        checkRep();
        Map<String, String> allNames = new HashMap<>();
        for (String s : buildingList.keySet()) {
            allNames.put(s, buildingList.get(s).getLongName());
        }
        checkRep();
        return allNames;
    }

    @Override
    public Path<Point> findShortestPath(String startShortName, String endShortName)
                                        throws IllegalArgumentException {
        if (startShortName == null || endShortName == null ||
                !shortNameExists(startShortName) || !shortNameExists(endShortName)) {
            throw new IllegalArgumentException();
        }
        checkRep();
        CampusBuilding b1 = buildingList.get(startShortName);
        Point start = new Point(b1.getX(), b1.getY());
        CampusBuilding b2 = buildingList.get(endShortName);
        Point end = new Point(b2.getX(), b2.getY());
        Path<Point> p = new Path<>(start);
        List<DirGraph<Point, Double>.Edge> shortestP = DirShortPath.shortestPath(campus, start, end);
        // extend Path to the whole route of the shortest path from source to destination
        for (DirGraph<Point, Double>.Edge e : shortestP) {
            p = p.extend(e.getChild(), e.getLabel());
        }
        checkRep();
        return p;
    }

    /**
     * Throws an AssertionError if the representation invariant of CampusMap is violated.
     */
    private void checkRep() {
        assert (campus != null) : "campus cannot be null";
        assert (buildingList != null): "Map of buildings cannot be null";
        assert (!buildingList.containsKey(null)) : "a building name cannot be null";
        if (DEBUG) {
            for (String shortN : buildingList.keySet()) {
                assert (buildingList.get(shortN) != null) : "A campus building object cannot be null";
            }
        }
    }
}
