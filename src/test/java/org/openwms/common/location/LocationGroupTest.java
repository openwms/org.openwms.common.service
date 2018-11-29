/*
 * Copyright 2018 Heiko Scherrer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openwms.common.location;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openwms.common.StateChangeException;
import org.openwms.common.location.api.LocationGroupState;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * A LocationGroupTest.
 *
 * @author <a href="mailto:scherrer@openwms.org">Heiko Scherrer</a>
 */
public class LocationGroupTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    public
    @Test
    void testConstructionWithNull() {
        thrown.expect(IllegalArgumentException.class);
        new LocationGroup(null);
    }

    public
    @Test
    void testConstructionWithEmpty() {
        thrown.expect(IllegalArgumentException.class);
        new LocationGroup("");
    }

    public
    @Test
    void testDefaultValues() {
        LocationGroup lg = new LocationGroup("Error zone");
        assertThat(lg.getName()).isEqualTo("Error zone");
        assertThat(lg.isLocationGroupCountingActive()).isTrue();
        assertThat(lg.getNoLocations()).isEqualTo(0);
        assertThat(lg.getGroupStateIn()).isEqualTo(LocationGroupState.AVAILABLE);
        assertThat(lg.isInfeedAllowed()).isTrue();
        assertThat(lg.isInfeedBlocked()).isFalse();
        assertThat(lg.getGroupStateOut()).isEqualTo(LocationGroupState.AVAILABLE);
        assertThat(lg.isOutfeedAllowed()).isTrue();
        assertThat(lg.isOutfeedBlocked()).isFalse();
        assertThat(lg.getMaxFillLevel()).isEqualTo(0);
        assertThat(lg.getLocationGroups()).hasSize(0);
        assertThat(lg.getLocations()).hasSize(0);
    }

    public
    @Test
    void testAddLocationGroupWithNull() {
        LocationGroup parent = new LocationGroup("Warehouse");
        thrown.expect(IllegalArgumentException.class);
        parent.addLocationGroup(null);
    }

    public
    @Test
    void testAddLocationGroupToParent() {
        LocationGroup parent = new LocationGroup("Warehouse");
        LocationGroup lg = new LocationGroup("Error zone");
        parent.addLocationGroup(lg);
        assertThat(parent.getLocationGroups()).hasSize(1);
        assertThat(lg.getParent()).isEqualTo(parent);
        assertThat(lg.getGroupStateIn()).isEqualTo(parent.getGroupStateIn());
        assertThat(lg.getGroupStateOut()).isEqualTo(parent.getGroupStateOut());
    }

    public
    @Test
    void testAddLocationGroupToChild() {
        LocationGroup parent = new LocationGroup("Warehouse");
        LocationGroup lg = new LocationGroup("Error zone");
        lg.changeGroupStateIn(LocationGroupState.NOT_AVAILABLE);
        LocationGroup child = new LocationGroup("Picking");

        // test ...
        parent.addLocationGroup(child);
        assertThat(child.getParent()).isEqualTo(parent);
        assertThat(child.getGroupStateIn()).isEqualTo(LocationGroupState.AVAILABLE);

        lg.addLocationGroup(child);
        assertThat(child.getParent()).isEqualTo(lg);
        assertThat(child.getGroupStateIn()).isEqualTo(LocationGroupState.NOT_AVAILABLE);
    }

    public
    @Test
    void testAddLocationGroupWithStateChange() {
        LocationGroup parent = new LocationGroup("Warehouse");
        parent.changeGroupStateIn(LocationGroupState.NOT_AVAILABLE);
        parent.changeGroupStateOut(LocationGroupState.NOT_AVAILABLE, parent);
        LocationGroup lg = new LocationGroup("Error zone");
        parent.addLocationGroup(lg);
        assertThat(parent.getLocationGroups()).hasSize(1);
        assertThat(lg.getParent()).isEqualTo(parent);
        assertThat(lg.getGroupStateIn()).isEqualTo(parent.getGroupStateIn()).isEqualTo(LocationGroupState.NOT_AVAILABLE);
        assertThat(lg.getGroupStateOut()).isEqualTo(parent.getGroupStateOut()).isEqualTo(LocationGroupState.NOT_AVAILABLE);
    }

    public
    @Test
    void testStateChangeAllowed() {
        LocationGroup parent = new LocationGroup("Warehouse");
        LocationGroup lg = new LocationGroup("Error zone");
        parent.addLocationGroup(lg);

        parent.changeGroupStateIn(LocationGroupState.NOT_AVAILABLE);
        assertThat(lg.getGroupStateIn()).isEqualTo(parent.getGroupStateIn()).isEqualTo(LocationGroupState.NOT_AVAILABLE);
        parent.changeGroupStateOut(LocationGroupState.NOT_AVAILABLE, parent);
        assertThat(lg.getGroupStateOut()).isEqualTo(parent.getGroupStateOut()).isEqualTo(LocationGroupState.NOT_AVAILABLE);

        parent.changeGroupStateIn(LocationGroupState.AVAILABLE);
        assertThat(lg.getGroupStateIn()).isEqualTo(parent.getGroupStateIn()).isEqualTo(LocationGroupState.AVAILABLE);
        parent.changeGroupStateOut(LocationGroupState.AVAILABLE, parent);
        assertThat(lg.getGroupStateOut()).isEqualTo(parent.getGroupStateOut()).isEqualTo(LocationGroupState.AVAILABLE);
    }

    public
    @Test
    void testStateChangeNotAllowed() {
        LocationGroup parent = new LocationGroup("Warehouse");
        LocationGroup lg = new LocationGroup("Error zone");
        parent.addLocationGroup(lg);
        parent.changeGroupStateIn(LocationGroupState.NOT_AVAILABLE);
        parent.changeGroupStateOut(LocationGroupState.NOT_AVAILABLE, parent);

        thrown.expect(StateChangeException.class);
        lg.changeGroupStateIn(LocationGroupState.AVAILABLE);
    }

    public
    @Test
    void testAddLocationWithNull() {
        LocationGroup lg = new LocationGroup("Error zone");
        thrown.expect(IllegalArgumentException.class);
        lg.addLocation(null);
    }

    public
    @Test
    void testAddLocation() {
        LocationGroup lg = new LocationGroup("Error zone");
        Location loc = Location.create(new LocationPK("area", "aisle", "x", "y", "z"));
        assertThat(loc.belongsNotToLocationGroup()).isTrue();
        lg.addLocation(loc);
        assertThat(loc.belongsToLocationGroup()).isTrue();
        assertThat(lg.getLocations()).hasSize(1);
        assertThat(lg.hasLocations()).isTrue();
    }

    public
    @Test
    void testRemoveLocationWithNull() {
        LocationGroup lg = new LocationGroup("Error zone");
        thrown.expect(IllegalArgumentException.class);
        lg.removeLocation(null);
    }

    public
    @Test
    void testRemoveLocation() {
        LocationGroup lg = new LocationGroup("Error zone");
        Location loc = Location.create(new LocationPK("area", "aisle", "x", "y", "z"));
        lg.addLocation(loc);
        assertThat(loc.belongsToLocationGroup()).isTrue();
        assertThat(lg.getLocations()).hasSize(1);
        assertThat(lg.hasLocations()).isTrue();

        // test...
        lg.removeLocation(loc);
        assertThat(loc.belongsNotToLocationGroup()).isTrue();
        assertThat(lg.getLocations()).hasSize(0);
        assertThat(lg.hasLocations()).isFalse();
    }

    public
    @Test
    void testEqualityLight() {
        LocationGroup lg1 = new LocationGroup("Error zone");
        LocationGroup lg2 = new LocationGroup("Error zone");
        LocationGroup lg3 = new LocationGroup("Picking");
        assertThat(lg1).isEqualTo(lg2);
        assertThat(lg2).isEqualTo(lg1);
        assertThat(lg1).isNotEqualTo(lg3);
    }

    public
    @Test
    void testHash() {
        Set<LocationGroup> groups = new HashSet();
        groups.add(new LocationGroup("Error zone"));
        assertThat(groups).hasSize(1);
        groups.add(new LocationGroup("Error zone"));
        assertThat(groups).hasSize(1);
        groups.add(new LocationGroup("Picking"));
        assertThat(groups).hasSize(2);
    }
}
