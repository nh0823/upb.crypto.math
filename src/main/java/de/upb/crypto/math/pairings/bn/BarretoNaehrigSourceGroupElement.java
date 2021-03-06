package de.upb.crypto.math.pairings.bn;

import de.upb.crypto.math.interfaces.structures.FieldElement;
import de.upb.crypto.math.pairings.generic.PairingSourceGroupElement;

import java.math.BigInteger;

public abstract class BarretoNaehrigSourceGroupElement extends PairingSourceGroupElement {

    public BarretoNaehrigSourceGroupElement(BarretoNaehrigSourceGroup curve, FieldElement x, FieldElement y) {
        super(curve, x, y);
    }

    public BarretoNaehrigSourceGroupElement(BarretoNaehrigSourceGroup curve) {
        super(curve);
    }


    @Override
    public BarretoNaehrigSourceGroupElement pow(BigInteger e) {
        return (BarretoNaehrigSourceGroupElement) super.pow(e);
    }

    /**
     * Point compression.
     * <p>
     * Compress point (x,y) by mapping x to an integer i in {0,1,2} such that this.getStructure().mapToPoint(y,this.compress(x,y)).equals(this). Hence (y,i) is a compression of (x,y) of approximately half size.
     *
     * @return compression of x
     */
    public int compressX() {
        /*
         * search for correct x-coordiante wrt. to this.getStructure().getFieldOfDefinition().getCubeRoot()
         */
        // TODO, more efficient way to injective mapping of primitive cube root into the integers
        for (int i = 0; i < 3; i++) {
            if (((BarretoNaehrigSourceGroup) this.getStructure()).mapToPoint(this.getY(), i).equals(this)) {
                return i;
            }
        }
        throw new RuntimeException("Not able to compress point");
    }
}
