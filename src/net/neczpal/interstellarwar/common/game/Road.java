package net.neczpal.interstellarwar.common.game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Road implements Serializable {
    private static final long serialVersionUID = 1683452581122892189L;

    private RoadKey mKey;
    private Planet mFrom, mTo;
    private List<SpaceShip> mSpaceShips;

    /**
     * Erstellt ein Weg zwischen zwei Planet
     *
     * @param from der eine Planet
     * @param to   der andere Planet
     */
    public Road (RoadKey key, Planet from, Planet to) {
        this.mKey = key;
        this.mFrom = from;
        this.mTo = to;
        this.mSpaceShips = new ArrayList<> ();
    }

    public RoadKey getKey () {
        return mKey;
    }

    // GETTERS

    public void addSpaceship (SpaceShip spaceShip) {
        if (!mSpaceShips.contains (spaceShip))
            mSpaceShips.add (spaceShip);
    }

    public void removeSpaceships (SpaceShip spaceShips) {
        mSpaceShips.remove (spaceShips);
    }

    public void removeSpaceships (List<SpaceShip> spaceShips) {
        mSpaceShips.removeAll (spaceShips);
    }

    public List<SpaceShip> getSpaceShips () {
        return mSpaceShips;
    }

    public Planet getFrom () {
        return mFrom;
    }

    public Planet getTo () {
        return mTo;
    }

    @Override
    public boolean equals (Object o) {
        if (this == o) return true;
        if (o == null || getClass () != o.getClass ()) return false;
        Road road = (Road) o;
        return ((Objects.equals (mFrom, road.mFrom) && Objects.equals (mTo, road.mTo)) ||
                Objects.equals (mFrom, road.mTo) && Objects.equals (mTo, road.mFrom));
    }

    static class RoadKey {
        public int from;
        public int to;

        public RoadKey (int from, int to) {
            this.from = from;
            this.to = to;
        }

        @Override
        public boolean equals (Object o) {
            if (this == o) return true;
            if (o == null || getClass () != o.getClass ()) return false;
            RoadKey roadKey = (RoadKey) o;
            return (from == roadKey.from &&
                    to == roadKey.to) ||
                    (from == roadKey.to &&
                            to == roadKey.from);
        }

        @Override
        public int hashCode () {
            return Objects.hash (from, to) + Objects.hash (to, from);
        }
    }

    @Override
    public int hashCode () {
        return Objects.hash (mFrom, mTo);
    }
}
