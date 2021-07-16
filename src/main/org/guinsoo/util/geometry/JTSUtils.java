/*
 * Copyright 2021 Guinsoo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Guinsoo Group
 */
package org.guinsoo.util.geometry;

import java.io.ByteArrayOutputStream;

import org.guinsoo.message.DbException;
import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.geom.impl.CoordinateArraySequenceFactory;
import org.locationtech.jts.geom.impl.PackedCoordinateSequenceFactory;

/**
 * Utilities for Geometry data type from JTS library.
 */
public final class JTSUtils {

    /**
     * Converter output target that creates a JTS Geometry.
     */
    public static final class GeometryTarget extends GeometryUtils.Target {

        private final int dimensionSystem;

        private GeometryFactory factory;

        private int type;

        private CoordinateSequence coordinates;

        private CoordinateSequence[] innerCoordinates;

        private int innerOffset;

        private Geometry[] subgeometries;

        /**
         * Creates a new instance of JTS Geometry target.
         *
         * @param dimensionSystem
         *            dimension system to use
         */
        public GeometryTarget(int dimensionSystem) {
            this.dimensionSystem = dimensionSystem;
        }

        private GeometryTarget(int dimensionSystem, GeometryFactory factory) {
            this.dimensionSystem = dimensionSystem;
            this.factory = factory;
        }

        @Override
        protected void init(int srid) {
            factory = new GeometryFactory(new PrecisionModel(), srid,
                    (dimensionSystem & GeometryUtils.DIMENSION_SYSTEM_XYM) != 0 ? PackedCoordinateSequenceFactory.DOUBLE_FACTORY
                            : CoordinateArraySequenceFactory.instance());
        }

        @Override
        protected void startPoint() {
            type = GeometryUtils.POINT;
            initCoordinates(1);
            innerOffset = -1;
        }

        @Override
        protected void startLineString(int numPoints) {
            type = GeometryUtils.LINE_STRING;
            initCoordinates(numPoints);
            innerOffset = -1;
        }

        @Override
        protected void startPolygon(int numInner, int numPoints) {
            type = GeometryUtils.POLYGON;
            initCoordinates(numPoints);
            innerCoordinates = new CoordinateSequence[numInner];
            innerOffset = -1;
        }

        @Override
        protected void startPolygonInner(int numInner) {
            innerCoordinates[++innerOffset] = createCoordinates(numInner);
        }

        @Override
        protected void startCollection(int type, int numItems) {
            this.type = type;
            switch (type) {
            case GeometryUtils.MULTI_POINT:
                subgeometries = new Point[numItems];
                break;
            case GeometryUtils.MULTI_LINE_STRING:
                subgeometries = new LineString[numItems];
                break;
            case GeometryUtils.MULTI_POLYGON:
                subgeometries = new Polygon[numItems];
                break;
            case GeometryUtils.GEOMETRY_COLLECTION:
                subgeometries = new Geometry[numItems];
                break;
            default:
                throw new IllegalArgumentException();
            }
        }

        @Override
        protected GeometryUtils.Target startCollectionItem(int index, int total) {
            return new GeometryTarget(dimensionSystem, factory);
        }

        @Override
        protected void endCollectionItem(GeometryUtils.Target target, int type, int index, int total) {
            subgeometries[index] = ((GeometryTarget) target).getGeometry();
        }

        private void initCoordinates(int numPoints) {
            coordinates = createCoordinates(numPoints);
        }

        private CoordinateSequence createCoordinates(int numPoints) {
            int d, m;
            switch (dimensionSystem) {
            case GeometryUtils.DIMENSION_SYSTEM_XY:
                d = 2;
                m = 0;
                break;
            case GeometryUtils.DIMENSION_SYSTEM_XYZ:
                d = 3;
                m = 0;
                break;
            case GeometryUtils.DIMENSION_SYSTEM_XYM:
                d = 3;
                m = 1;
                break;
            case GeometryUtils.DIMENSION_SYSTEM_XYZM:
                d = 4;
                m = 1;
                break;
            default:
                throw DbException.getInternalError();
            }
            return factory.getCoordinateSequenceFactory().create(numPoints, d, m);
        }

        @Override
        protected void addCoordinate(double x, double y, double z, double m, int index, int total) {
            if (type == GeometryUtils.POINT && Double.isNaN(x) && Double.isNaN(y) && Double.isNaN(z) && Double.isNaN(m)) {
                this.coordinates = createCoordinates(0);
                return;
            }
            CoordinateSequence coordinates = innerOffset < 0 ? this.coordinates : innerCoordinates[innerOffset];
            coordinates.setOrdinate(index, GeometryUtils.X, GeometryUtils.checkFinite(x));
            coordinates.setOrdinate(index, GeometryUtils.Y, GeometryUtils.checkFinite(y));
            switch (dimensionSystem) {
            case GeometryUtils.DIMENSION_SYSTEM_XYZM:
                coordinates.setOrdinate(index, GeometryUtils.M, GeometryUtils.checkFinite(m));
                //$FALL-THROUGH$
            case GeometryUtils.DIMENSION_SYSTEM_XYZ:
                coordinates.setOrdinate(index, GeometryUtils.Z, GeometryUtils.checkFinite(z));
                break;
            case GeometryUtils.DIMENSION_SYSTEM_XYM:
                coordinates.setOrdinate(index, 2, GeometryUtils.checkFinite(m));
            }
        }

        Geometry getGeometry() {
            switch (type) {
            case GeometryUtils.POINT:
                return new Point(coordinates, factory);
            case GeometryUtils.LINE_STRING:
                return new LineString(coordinates, factory);
            case GeometryUtils.POLYGON: {
                LinearRing shell = new LinearRing(coordinates, factory);
                int innerCount = innerCoordinates.length;
                LinearRing[] holes = new LinearRing[innerCount];
                for (int i = 0; i < innerCount; i++) {
                    holes[i] = new LinearRing(innerCoordinates[i], factory);
                }
                return new Polygon(shell, holes, factory);
            }
            case GeometryUtils.MULTI_POINT:
                return new MultiPoint((Point[]) subgeometries, factory);
            case GeometryUtils.MULTI_LINE_STRING:
                return new MultiLineString((LineString[]) subgeometries, factory);
            case GeometryUtils.MULTI_POLYGON:
                return new MultiPolygon((Polygon[]) subgeometries, factory);
            case GeometryUtils.GEOMETRY_COLLECTION:
                return new GeometryCollection(subgeometries, factory);
            default:
                throw new IllegalStateException();
            }
        }

    }

    /**
     * Converts EWKB to a JTS geometry object.
     *
     * @param ewkb
     *            source EWKB
     * @return JTS geometry object
     */
    public static Geometry ewkb2geometry(byte[] ewkb) {
        // Determine dimension system first
        GeometryUtils.DimensionSystemTarget dimensionTarget = new GeometryUtils.DimensionSystemTarget();
        EWKBUtils.parseEWKB(ewkb, dimensionTarget);
        // Generate a Geometry
        return ewkb2geometry(ewkb, dimensionTarget.getDimensionSystem());
    }

    /**
     * Converts EWKB to a JTS geometry object.
     *
     * @param ewkb
     *            source EWKB
     * @param dimensionSystem
     *            dimension system
     * @return JTS geometry object
     */
    public static Geometry ewkb2geometry(byte[] ewkb, int dimensionSystem) {
        GeometryTarget target = new GeometryTarget(dimensionSystem);
        EWKBUtils.parseEWKB(ewkb, target);
        return target.getGeometry();
    }

    /**
     * Converts Geometry to EWKB.
     *
     * @param geometry
     *            source geometry
     * @return EWKB representation
     */
    public static byte[] geometry2ewkb(Geometry geometry) {
        // Determine dimension system first
        GeometryUtils.DimensionSystemTarget dimensionTarget = new GeometryUtils.DimensionSystemTarget();
        parseGeometry(geometry, dimensionTarget);
        // Write an EWKB
        return geometry2ewkb(geometry, dimensionTarget.getDimensionSystem());
    }

    /**
     * Converts Geometry to EWKB.
     *
     * @param geometry
     *            source geometry
     * @param dimensionSystem
     *            dimension system
     * @return EWKB representation
     */
    public static byte[] geometry2ewkb(Geometry geometry, int dimensionSystem) {
        // Write an EWKB
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        EWKBUtils.EWKBTarget target = new EWKBUtils.EWKBTarget(output, dimensionSystem);
        parseGeometry(geometry, target);
        return output.toByteArray();
    }

    /**
     * Parses a JTS Geometry object.
     *
     * @param geometry
     *            geometry to parse
     * @param target
     *            output target
     */
    public static void parseGeometry(Geometry geometry, GeometryUtils.Target target) {
        parseGeometry(geometry, target, 0);
    }

    /**
     * Parses a JTS Geometry object.
     *
     * @param geometry
     *            geometry to parse
     * @param target
     *            output target
     * @param parentType
     *            type of parent geometry collection, or 0 for the root geometry
     */
    private static void parseGeometry(Geometry geometry, GeometryUtils.Target target, int parentType) {
        if (parentType == 0) {
            target.init(geometry.getSRID());
        }
        if (geometry instanceof Point) {
            if (parentType != 0 && parentType != GeometryUtils.MULTI_POINT && parentType != GeometryUtils.GEOMETRY_COLLECTION) {
                throw new IllegalArgumentException();
            }
            target.startPoint();
            Point p = (Point) geometry;
            if (p.isEmpty()) {
                target.addCoordinate(Double.NaN, Double.NaN, Double.NaN, Double.NaN, 0, 1);
            } else {
                CoordinateSequence sequence = p.getCoordinateSequence();
                addCoordinate(sequence, target, 0, 1);
            }
            target.endObject(GeometryUtils.POINT);
        } else if (geometry instanceof LineString) {
            if (parentType != 0 && parentType != GeometryUtils.MULTI_LINE_STRING && parentType != GeometryUtils.GEOMETRY_COLLECTION) {
                throw new IllegalArgumentException();
            }
            LineString ls = (LineString) geometry;
            CoordinateSequence cs = ls.getCoordinateSequence();
            int numPoints = cs.size();
            if (numPoints < 0 || numPoints == 1) {
                throw new IllegalArgumentException();
            }
            target.startLineString(numPoints);
            for (int i = 0; i < numPoints; i++) {
                addCoordinate(cs, target, i, numPoints);
            }
            target.endObject(GeometryUtils.LINE_STRING);
        } else if (geometry instanceof Polygon) {
            if (parentType != 0 && parentType != GeometryUtils.MULTI_POLYGON && parentType != GeometryUtils.GEOMETRY_COLLECTION) {
                throw new IllegalArgumentException();
            }
            Polygon p = (Polygon) geometry;
            int numInner = p.getNumInteriorRing();
            if (numInner < 0) {
                throw new IllegalArgumentException();
            }
            CoordinateSequence cs = p.getExteriorRing().getCoordinateSequence();
            int size = cs.size();
            // Size may be 0 (EMPTY) or 4+
            if (size < 0 || size >= 1 && size <= 3) {
                throw new IllegalArgumentException();
            }
            if (size == 0 && numInner > 0) {
                throw new IllegalArgumentException();
            }
            target.startPolygon(numInner, size);
            if (size > 0) {
                addRing(cs, target, size);
                for (int i = 0; i < numInner; i++) {
                    cs = p.getInteriorRingN(i).getCoordinateSequence();
                    size = cs.size();
                    // Size may be 0 (EMPTY) or 4+
                    if (size < 0 || size >= 1 && size <= 3) {
                        throw new IllegalArgumentException();
                    }
                    target.startPolygonInner(size);
                    addRing(cs, target, size);
                }
                target.endNonEmptyPolygon();
            }
            target.endObject(GeometryUtils.POLYGON);
        } else if (geometry instanceof GeometryCollection) {
            if (parentType != 0 && parentType != GeometryUtils.GEOMETRY_COLLECTION) {
                throw new IllegalArgumentException();
            }
            GeometryCollection gc = (GeometryCollection) geometry;
            int type;
            if (gc instanceof MultiPoint) {
                type = GeometryUtils.MULTI_POINT;
            } else if (gc instanceof MultiLineString) {
                type = GeometryUtils.MULTI_LINE_STRING;
            } else if (gc instanceof MultiPolygon) {
                type = GeometryUtils.MULTI_POLYGON;
            } else {
                type = GeometryUtils.GEOMETRY_COLLECTION;
            }
            int numItems = gc.getNumGeometries();
            if (numItems < 0) {
                throw new IllegalArgumentException();
            }
            target.startCollection(type, numItems);
            for (int i = 0; i < numItems; i++) {
                GeometryUtils.Target innerTarget = target.startCollectionItem(i, numItems);
                parseGeometry(gc.getGeometryN(i), innerTarget, type);
                target.endCollectionItem(innerTarget, type, i, numItems);
            }
            target.endObject(type);
        } else {
            throw new IllegalArgumentException();
        }
    }

    private static void addRing(CoordinateSequence sequence, GeometryUtils.Target target, int size) {
        // 0 or 4+ are valid
        if (size >= 4) {
            double startX = GeometryUtils.toCanonicalDouble(sequence.getX(0)), startY = GeometryUtils.toCanonicalDouble(sequence.getY(0));
            addCoordinate(sequence, target, 0, size, startX, startY);
            for (int i = 1; i < size - 1; i++) {
                addCoordinate(sequence, target, i, size);
            }
            double endX = GeometryUtils.toCanonicalDouble(sequence.getX(size - 1)), //
                    endY = GeometryUtils.toCanonicalDouble(sequence.getY(size - 1));
            /*
             * TODO OGC 06-103r4 determines points as equal if they have the
             * same X and Y coordinates. Should we check Z and M here too?
             */
            if (startX != endX || startY != endY) {
                throw new IllegalArgumentException();
            }
            addCoordinate(sequence, target, size - 1, size, endX, endY);
        }
    }

    private static void addCoordinate(CoordinateSequence sequence, GeometryUtils.Target target, int index, int total) {
        addCoordinate(sequence, target, index, total, GeometryUtils.toCanonicalDouble(sequence.getX(index)),
                GeometryUtils.toCanonicalDouble(sequence.getY(index)));
    }

    private static void addCoordinate(CoordinateSequence sequence, GeometryUtils.Target target, int index, int total, double x,
                                      double y) {
        double z = GeometryUtils.toCanonicalDouble(sequence.getZ(index));
        double m = GeometryUtils.toCanonicalDouble(sequence.getM(index));
        target.addCoordinate(x, y, z, m, index, total);
    }

    private JTSUtils() {
    }

}
