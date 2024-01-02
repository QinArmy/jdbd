/*
 * Copyright 2023-2043 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.jdbd.vendor.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

@Deprecated
@Test
public class GeometryUtilsSuiteTests {

    private static final Logger LOG = LoggerFactory.getLogger(GeometryUtilsSuiteTests.class);
//
//    /**
//     * @see GeometryUtils#readAndWritePoints(ByteBuffer, GenericGeometries.WkbOUtWrapper, WkbType)
//     */
//    @Test
//    public void readAndWritePoints() {
//        WkbType wkbType;
//        String pointText;
//        ByteBuffer inBuffer;
//        GenericGeometries.WkbOUtWrapper outWrapper;
//        int pointCount;
//
//
//        wkbType = WkbType.LINE_STRING;
//        pointText = String.format("0 0, 1.3 3.4 , %s %s ,0 0)", Double.MAX_VALUE, Double.MIN_VALUE);
//
//        inBuffer = ByteBuffer.wrap(pointText.getBytes(StandardCharsets.US_ASCII));
//        outWrapper = new GenericGeometries.WkbOUtWrapper(1024, true);
//        pointCount = GeometryUtils.readAndWritePoints(inBuffer, outWrapper, wkbType);
//
//        assertEquals(inBuffer.get(), ')', pointText);
//        assertEquals(pointCount, 4, pointText);
//
//
//        wkbType = WkbType.MULTI_POINT;
//        pointText = String.format("  (   0 0),( 1.3 3.4 ), (%s %s)  )", Double.MAX_VALUE, Double.MIN_VALUE);
//
//        inBuffer = ByteBuffer.wrap(pointText.getBytes(StandardCharsets.US_ASCII));
//        outWrapper = new GenericGeometries.WkbOUtWrapper(wkbType.coordinates() * 8 * 3, true);
//        pointCount = GeometryUtils.readAndWritePoints(inBuffer, outWrapper, wkbType);
//
//        assertEquals(inBuffer.get(), ')', pointText);
//        assertEquals(pointCount, 3, pointText);
//
//    }
//
//    /**
//     * @see GeometryUtils#readAndWriteLinearRing(ByteBuffer, GenericGeometries.WkbMemoryWrapper, WkbType)
//     */
//    @Test
//    public void readAndWriteLinearRing() {
//        WkbType wkbType;
//        String linearRingText;
//        ByteBuffer inBuffer;
//        GenericGeometries.WkbMemoryWrapper outWrapper;
//        int linearRingCount;
//
//        wkbType = WkbType.POLYGON;
//        linearRingText = " ( 0 0,1.3 3.4, 5.2 5.7, 0 0) )  ";
//
//        inBuffer = ByteBuffer.wrap(linearRingText.getBytes(StandardCharsets.US_ASCII));
//        outWrapper = new GenericGeometries.WkbMemoryWrapper(1024, true);
//        linearRingCount = GeometryUtils.readAndWriteLinearRing(inBuffer, outWrapper, wkbType);
//
//        assertEquals(inBuffer.get(), ')', linearRingText);
//        assertEquals(linearRingCount, 1, linearRingText);
//
//
//        wkbType = WkbType.POLYGON;
//        linearRingText = String.format(" ( 0 0,1.3 3.4, 5.2 5.7, 0 0) , ( 0 0,1.3 3.4, %s %s, 0 0))  "
//                , Double.MAX_VALUE, Double.MIN_VALUE);
//
//        inBuffer = ByteBuffer.wrap(linearRingText.getBytes(StandardCharsets.US_ASCII));
//        outWrapper = new GenericGeometries.WkbMemoryWrapper(1024, true);
//        linearRingCount = GeometryUtils.readAndWriteLinearRing(inBuffer, outWrapper, wkbType);
//
//        assertEquals(inBuffer.get(), ')', linearRingText);
//        assertEquals(linearRingCount, 2, linearRingText);
//    }
//
//    /**
//     * @see GeometryUtils#pointToWkb(String, boolean)
//     */
//    @Test
//    public void pointToWkb() {
//        LOG.info("pointToWkb test start");
//        String wktText;
//        byte[] wkbArray;
//        Pair<Double, Double> pair;
//
//        wktText = " POINT EMPTY  ";
//        wkbArray = GeometryUtils.pointToWkb(wktText, true);
//        assertEquals(GeometryUtils.readElementCount(wkbArray), 0, wktText);
//        LOG.info("'{}' WKT : {}", wktText, GeometryUtils.pointToWkt(wkbArray));
//
//
//        wktText = " POINT  ( 0 0)  ";
//        wkbArray = GeometryUtils.pointToWkb(wktText, true);
//        pair = GeometryUtils.readPointAsPair(wkbArray, 0);
//        assertEquals(pair.getFirst(), Double.valueOf(0.0D), "x");
//        assertEquals(pair.getSecond(), Double.valueOf(0.0D), "y");
//        LOG.info("'{}' WKT : {}", wktText, GeometryUtils.pointToWkt(wkbArray));
//
//
//        wktText = String.format(" POINT  ( %s %s)  ", Double.MAX_VALUE, Double.MIN_VALUE);
//        wkbArray = GeometryUtils.pointToWkb(wktText, true);
//        pair = GeometryUtils.readPointAsPair(wkbArray, 0);
//        assertEquals(pair.getFirst(), Double.valueOf(Double.MAX_VALUE), "x");
//        assertEquals(pair.getSecond(), Double.valueOf(Double.MIN_VALUE), "y");
//        LOG.info("'{}' WKT : {}", wktText, GeometryUtils.pointToWkt(wkbArray));
//
//        LOG.info("pointToWkb test success");
//    }
//
//    /**
//     * @see GeometryUtils#wkbEquals(byte[], byte[])
//     */
//    @Test(dependsOnMethods = "pointToWkb")
//    public void geometryEquals() {
//        LOG.info("pointEquals test start");
//        String wktText;
//        byte[] wkbArrayOne, wkbArrayTow;
//
//        wktText = "POINT(1.0 2.3)";
//        wkbArrayOne = GeometryUtils.pointToWkb(wktText, true);
//        wkbArrayTow = GeometryUtils.pointToWkb(wktText, false);
//
//        assertTrue(GeometryUtils.wkbEquals(wkbArrayOne, wkbArrayTow), wktText);
//
//        wktText = String.format("POINT(%s %s)", Double.MAX_VALUE, Double.MIN_VALUE);
//        wkbArrayOne = GeometryUtils.pointToWkb(wktText, true);
//        wkbArrayTow = GeometryUtils.pointToWkb(wktText, false);
//
//        assertTrue(GeometryUtils.wkbEquals(wkbArrayOne, wkbArrayTow), wktText);
//
//        LOG.info("pointEquals test success");
//    }
//
//    /**
//     * @see GeometryUtils#lineStringToWkb(String, boolean)
//     * @see GeometryUtils#lineStringToWkt(byte[])
//     */
//    @Test
//    public void lineStringToWkb() {
//        LOG.info("lineStringToWkb test start");
//        String wktText, wktTextTwo;
//        byte[] wkbArrayOne, wkbArrayTow;
//
//        wktText = " LINESTRING EMPTY ";
//        wkbArrayOne = GeometryUtils.lineStringToWkb(wktText, true);
//        wkbArrayTow = GeometryUtils.lineStringToWkb(wktText, false);
//        assertTrue(GeometryUtils.wkbEquals(wkbArrayOne, wkbArrayTow), wktText);
//        wktTextTwo = GeometryUtils.lineStringToWkt(wkbArrayOne);
//        LOG.info("'{}' WKT : {}", wktText, wktTextTwo);
//
//        wkbArrayTow = GeometryUtils.lineStringToWkb(wktTextTwo, true);
//        assertTrue(GeometryUtils.wkbEquals(wkbArrayTow, wkbArrayOne), wktText);
//
//
//        wktText = String.format(" LINESTRING (  0 0, 1.0 3.3 ,   %s %s  )", Double.MAX_VALUE, Double.MIN_VALUE);
//        wkbArrayOne = GeometryUtils.lineStringToWkb(wktText, true);
//        wkbArrayTow = GeometryUtils.lineStringToWkb(wktText, false);
//        assertTrue(GeometryUtils.wkbEquals(wkbArrayOne, wkbArrayTow), wktText);
//
//        wktTextTwo = GeometryUtils.lineStringToWkt(wkbArrayOne);
//        LOG.info("'{}' WKT : {}", wktText, wktTextTwo);
//
//        wkbArrayTow = GeometryUtils.lineStringToWkb(wktTextTwo, true);
//        assertTrue(GeometryUtils.wkbEquals(wkbArrayTow, wkbArrayOne), wktText);
//
//        LOG.info("lineStringToWkb test success");
//    }
//
//    /**
//     * @see GeometryUtils#polygonToWkb(String, boolean)
//     * @see GeometryUtils#polygonToWkt(byte[])
//     */
//    @Test
//    public void polygonToWkb() {
//        LOG.info("polygonToWkb test start");
//        String wktText, wktTextTwo;
//        byte[] wkbArrayOne, wkbArrayTow;
//
//        wktText = "POLYGON EMPTY";
//        wkbArrayOne = GeometryUtils.polygonToWkb(wktText, true);
//        wkbArrayTow = GeometryUtils.polygonToWkb(wktText, false);
//        assertTrue(GeometryUtils.wkbEquals(wkbArrayOne, wkbArrayTow), wktText);
//
//        wktTextTwo = GeometryUtils.polygonToWkt(wkbArrayOne);
//        LOG.info("POLYGON WKT compare:\n{}\n{}", wktText, wktTextTwo);
//        wkbArrayTow = GeometryUtils.polygonToWkb(wktTextTwo, true);
//        assertTrue(GeometryUtils.wkbEquals(wkbArrayTow, wkbArrayOne), wktTextTwo);
//
//
//        wktText = "POLYGON((0 0,0 1,0 3,0 0))";
//        wkbArrayOne = GeometryUtils.polygonToWkb(wktText, true);
//        wkbArrayTow = GeometryUtils.polygonToWkb(wktText, false);
//        assertTrue(GeometryUtils.wkbEquals(wkbArrayOne, wkbArrayTow), wktText);
//
//        wktTextTwo = GeometryUtils.polygonToWkt(wkbArrayOne);
//        LOG.info("POLYGON WKT compare:\n{}\n{}", wktText, wktTextTwo);
//        wkbArrayTow = GeometryUtils.polygonToWkb(wktTextTwo, true);
//        assertTrue(GeometryUtils.wkbEquals(wkbArrayTow, wkbArrayOne), wktTextTwo);
//
//        wktText = String.format("POLYGON((0 0,0 1,0 3,0 0),(3 4,0 1,0 3,%s %s,3 4))"
//                , Double.MAX_VALUE, Double.MIN_VALUE);
//        wkbArrayOne = GeometryUtils.polygonToWkb(wktText, true);
//        wkbArrayTow = GeometryUtils.polygonToWkb(wktText, false);
//        assertTrue(GeometryUtils.wkbEquals(wkbArrayOne, wkbArrayTow), wktText);
//
//        wktTextTwo = GeometryUtils.polygonToWkt(wkbArrayOne);
//        LOG.info("POLYGON WKT compare:\n{}\n{}", wktText, wktTextTwo);
//        wkbArrayTow = GeometryUtils.polygonToWkb(wktTextTwo, true);
//        assertTrue(GeometryUtils.wkbEquals(wkbArrayTow, wkbArrayOne), wktTextTwo);
//
//        LOG.info("polygonToWkb test success");
//    }
//
//    /**
//     * @see GeometryUtils#multiPointToWkb(String, boolean)
//     * @see GeometryUtils#multiPointToWkt(byte[])
//     */
//    @Test
//    public void multiPointToWkb() {
//        LOG.info("multiPointToWkb test start");
//        String wktText, wktTextTwo;
//        byte[] wkbArrayOne, wkbArrayTow;
//
//        wktText = "MULTIPOINT EMPTY";
//        wkbArrayOne = GeometryUtils.multiPointToWkb(wktText, true);
//        wkbArrayTow = GeometryUtils.multiPointToWkb(wktText, false);
//        assertTrue(GeometryUtils.wkbEquals(wkbArrayOne, wkbArrayTow), wktText);
//
//        wktTextTwo = GeometryUtils.multiPointToWkt(wkbArrayOne);
//        LOG.info("MULTIPOINT WKT compare:\n{}\n{}", wktText, wktTextTwo);
//        wkbArrayTow = GeometryUtils.multiPointToWkb(wktTextTwo, true);
//        assertTrue(GeometryUtils.wkbEquals(wkbArrayTow, wkbArrayOne), wktTextTwo);
//
//        wktText = " MULTIPOINT ( ( 0 0 ) , (1 1),(1 3), (0 0))  ";
//        wkbArrayOne = GeometryUtils.multiPointToWkb(wktText, true);
//        wkbArrayTow = GeometryUtils.multiPointToWkb(wktText, false);
//        assertTrue(GeometryUtils.wkbEquals(wkbArrayOne, wkbArrayTow), wktText);
//
//        wktTextTwo = GeometryUtils.multiPointToWkt(wkbArrayOne);
//        LOG.info("MULTIPOINT WKT compare:\n{}\n{}", wktText, wktTextTwo);
//        wkbArrayTow = GeometryUtils.multiPointToWkb(wktTextTwo, true);
//        assertTrue(GeometryUtils.wkbEquals(wkbArrayTow, wkbArrayOne), wktTextTwo);
//
//        LOG.info("multiPointToWkb test success");
//    }
//
//
//    /**
//     * @see GeometryUtils#multiLineStringToWkb(String, boolean)
//     * @see GeometryUtils#multiLineStringToWkt(byte[])
//     */
//    @Test
//    public void multiLineString() {
//        LOG.info("multiLineString test start");
//        String wktText, wktTextTwo;
//        byte[] wkbArrayOne, wkbArrayTow;
//
//        wktText = " MULTILINESTRING EMPTY";
//        wkbArrayOne = GeometryUtils.multiLineStringToWkb(wktText, true);
//        wkbArrayTow = GeometryUtils.multiLineStringToWkb(wktText, false);
//        assertTrue(GeometryUtils.wkbEquals(wkbArrayOne, wkbArrayTow), wktText);
//
//        wktTextTwo = GeometryUtils.multiLineStringToWkt(wkbArrayOne);
//        LOG.info("MULTILINESTRING WKT compare:\n{}\n{}", wktText, wktTextTwo);
//        wkbArrayTow = GeometryUtils.multiLineStringToWkb(wktTextTwo, true);
//        assertTrue(GeometryUtils.wkbEquals(wkbArrayTow, wkbArrayOne), wktTextTwo);
//
//
//        wktText = String.format(" MULTILINESTRING ( EMPTY,(0.0 1.3 ,3 3),(3.4 34.5 ,%s %s),EMPTY )"
//                , Double.MAX_VALUE, Double.MIN_VALUE);
//        wkbArrayOne = GeometryUtils.multiLineStringToWkb(wktText, true);
//        wkbArrayTow = GeometryUtils.multiLineStringToWkb(wktText, false);
//        assertTrue(GeometryUtils.wkbEquals(wkbArrayOne, wkbArrayTow), wktText);
//
//        wktTextTwo = GeometryUtils.multiLineStringToWkt(wkbArrayOne);
//        LOG.info("MULTILINESTRING WKT compare:\n{}\n{}", wktText, wktTextTwo);
//        wkbArrayTow = GeometryUtils.multiLineStringToWkb(wktTextTwo, true);
//        assertTrue(GeometryUtils.wkbEquals(wkbArrayTow, wkbArrayOne), wktTextTwo);
//
//
//        LOG.info("multiLineString test success");
//    }
//
//    /**
//     * @see GeometryUtils#multiPolygonToWkb(String, boolean)
//     * @see GeometryUtils#multiPolygonToWkt(byte[])
//     */
//    @Test
//    public void multiPolygon() {
//        LOG.info("multiPolygon test start");
//        String wktText, wktTextTwo;
//        byte[] wkbArrayOne, wkbArrayTow;
//
//        wktText = "MULTIPOLYGON EMPTY";
//        wkbArrayOne = GeometryUtils.multiPolygonToWkb(wktText, true);
//        wkbArrayTow = GeometryUtils.multiPolygonToWkb(wktText, false);
//        assertTrue(GeometryUtils.wkbEquals(wkbArrayOne, wkbArrayTow), wktText);
//
//        wktTextTwo = GeometryUtils.multiPolygonToWkt(wkbArrayOne);
//        LOG.info("MULTIPOLYGON WKT compare:\n{}\n{}", wktText, wktTextTwo);
//        wkbArrayTow = GeometryUtils.multiPolygonToWkb(wktTextTwo, true);
//        assertTrue(GeometryUtils.wkbEquals(wkbArrayTow, wkbArrayOne), wktTextTwo);
//
//
//        wktText = String.format("MULTIPOLYGON ( EMPTY,((0 0 ,3 4,5 8 , 0 0)) , EMPTY ,((1.3 3.5 ,7 4,5 9 ,%s %s,1.3 3.5)) ) "
//                , Double.MAX_VALUE, Double.MIN_VALUE);
//        wkbArrayOne = GeometryUtils.multiPolygonToWkb(wktText, true);
//        wkbArrayTow = GeometryUtils.multiPolygonToWkb(wktText, false);
//        assertTrue(GeometryUtils.wkbEquals(wkbArrayOne, wkbArrayTow), wktText);
//
//        wktTextTwo = GeometryUtils.multiPolygonToWkt(wkbArrayOne);
//        LOG.info("MULTIPOLYGON WKT compare:\n{}\n{}", wktText, wktTextTwo);
//        wkbArrayTow = GeometryUtils.multiPolygonToWkb(wktTextTwo, true);
//        assertTrue(GeometryUtils.wkbEquals(wkbArrayTow, wkbArrayOne), wktTextTwo);
//
//        LOG.info("multiPolygon test success");
//    }
//
//    @Test
//    public void geometryCollection() {
//        LOG.info("geometryCollection test start");
//        String wktText, wktTextTwo;
//        byte[] wkbArrayOne, wkbArrayTow;
//
//        wktText = "GEOMETRYCOLLECTION EMPTY";
//        wkbArrayOne = GeometryUtils.geometryCollectionToWkb(wktText, true);
//        wkbArrayTow = GeometryUtils.geometryCollectionToWkb(wktText, false);
//        assertTrue(GeometryUtils.wkbEquals(wkbArrayOne, wkbArrayTow), wktText);
//
//        wktTextTwo = GeometryUtils.geometryCollectionToWkt(wkbArrayOne);
//        LOG.info("GEOMETRYCOLLECTION WKT compare:\n{}\n{}", wktText, wktTextTwo);
//        wkbArrayTow = GeometryUtils.geometryCollectionToWkb(wktTextTwo, true);
//        assertTrue(GeometryUtils.wkbEquals(wkbArrayTow, wkbArrayOne), wktTextTwo);
//
//        final String point, lineString, polygon, multiPoint, multiLineString, multiPolygon, geometryCollection;
//
//        point = String.format(" POINT  ( %s %s)  ", Double.MAX_VALUE, Double.MIN_VALUE);
//        lineString = String.format(" LINESTRING (  0 0, 1.0 3.3 ,   %s %s  )", Double.MAX_VALUE, Double.MIN_VALUE);
//        polygon = String.format("POLYGON((0 0,0 1,0 3,0 0),(3 4,0 1,0 3,%s %s,3 4))"
//                , Double.MAX_VALUE, Double.MIN_VALUE);
//        multiPoint = " MULTIPOINT ( ( 0 0 ) , (1 1),(1 3), (0 0))  ";
//        multiLineString = String.format(" MULTILINESTRING ( (0.0 1.3 ,3 3),(3.4 34.5 ,%s %s) )"
//                , Double.MAX_VALUE, Double.MIN_VALUE);
//        multiPolygon = String.format("MULTIPOLYGON ( ((0 0 ,3 4,5 8 , 0 0))  ,((1.3 3.5 ,7 4,5 9 ,%s %s,1.3 3.5)) ) "
//                , Double.MAX_VALUE, Double.MIN_VALUE);
//        geometryCollection = "GEOMETRYCOLLECTION(POINT(0 0))";
//
//
//        wktText = String.format("GEOMETRYCOLLECTION (%s,%s,%s,%s ,%s,%s,%s) "
//                , point
//                , lineString
//                , polygon
//                , multiPoint
//
//                , multiLineString
//                , multiPolygon
//                , geometryCollection
//        );
//        wkbArrayOne = GeometryUtils.geometryCollectionToWkb(wktText, true);
//        wkbArrayTow = GeometryUtils.geometryCollectionToWkb(wktText, false);
//        assertTrue(GeometryUtils.wkbEquals(wkbArrayOne, wkbArrayTow), wktText);
//
//        wktTextTwo = GeometryUtils.geometryCollectionToWkt(wkbArrayOne);
//        LOG.info("GEOMETRYCOLLECTION WKT compare:\n{}\n{}", wktText, wktTextTwo);
//        wkbArrayTow = GeometryUtils.geometryCollectionToWkb(wktTextTwo, true);
//        assertTrue(GeometryUtils.wkbEquals(wkbArrayTow, wkbArrayOne), wktTextTwo);
//
//        LOG.info("geometryCollection test success");
//    }


}
