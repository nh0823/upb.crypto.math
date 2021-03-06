package de.upb.crypto.math.interfaces.structures;

import de.upb.crypto.math.serialization.ObjectRepresentation;
import de.upb.crypto.math.serialization.Representation;
import de.upb.crypto.math.serialization.StringRepresentation;
import de.upb.crypto.math.serialization.util.RepresentationToJavaObjectHelper;

/**
 * Common base class for ring subgroups (additive/unit groups)
 */
public abstract class RingGroup implements Group {
    protected final Ring ring;

    /**
     * Construct a RingGroup
     *
     * @param ring the ring to wrap
     */
    public RingGroup(Ring ring) {
        this.ring = ring;
    }

    public RingGroup(Representation repr) {
        ObjectRepresentation r = (ObjectRepresentation) repr;
        ring = (Ring) RepresentationToJavaObjectHelper.getInstance().getObject(((StringRepresentation) r.get("ringTypeName")).get(), r.get("ringRepresentation"));
    }

    @Override
    public boolean equals(Object obj) {
        return obj.getClass().equals(this.getClass()) && ((RingGroup) obj).ring.equals(this.ring);
    }

    @Override
    public int hashCode() {
        return ring.hashCode();
    }

    @Override
    public Representation getRepresentation() {
        ObjectRepresentation result = new ObjectRepresentation();
        result.put("ringTypeName", new StringRepresentation(ring.getRepresentedTypeName()));
        result.put("ringRepresentation", ring.getRepresentation());
        return result;
    }

    public Ring getRing() {
        return ring;
    }

    /**
     * Common base class of ring subgroup elements
     */
    public abstract class RingGroupElement implements GroupElement {
        protected final RingElement element;

        public RingGroupElement(RingElement e) {
            element = e;
        }

        @Override
        public boolean equals(Object obj) {
            return //element.equals(obj) || //this is deliberately not in because the equals()-induced relation would not be symmetric anymore with it.
                    obj instanceof RingGroupElement && element.equals(((RingGroupElement) obj).element);
        }

        @Override
        public int hashCode() {
            return element.hashCode();
        }

        /**
         * Projects the element of this group back to its ring
         *
         * @return the same element, interpreted as a ring element
         */
        public RingElement projectToRing() {
            return element;
        }

        @Override
        public Representation getRepresentation() {
            return element.getRepresentation();
        }

        @Override
        public String toString() {
            return element.toString();
        }


    }

    @Override
    public String toString() {
        return ring.toString();
    }
}
