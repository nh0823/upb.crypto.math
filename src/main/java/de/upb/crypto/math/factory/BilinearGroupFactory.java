package de.upb.crypto.math.factory;

import de.upb.crypto.math.lazy.LazyBilinearGroup;
import de.upb.crypto.math.pairings.bn.BarretoNaehrigProvider;
import de.upb.crypto.math.pairings.debug.DebugBilinearGroupProvider;
import de.upb.crypto.math.pairings.supersingular.SupersingularProvider;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Factory for {@link BilinearGroup}s that allows setting certain configuration parameters
 * ({@link BilinearGroupRequirement}). It then picks a bilinear group fitting this configuration.
 *
 * <p>
 * Usage of this factory:
 * </p>
 * <ol>
 * <li>Create an object and set the desired security parameter using {@link #BilinearGroupFactory(int)}</li>
 * <li>
 * Set a configuration for the factory by setting {@link BilinearGroupRequirement} using {@link #setRequirements}.
 * </li>
 * <li>
 * Optionally: Register {@link BilinearGroupProvider} using {@link #registerProvider(List)}.
 * Default providers are {@link SupersingularProvider} for Type 1 groups and {@link BarretoNaehrigProvider} for Type
 * 3 groups.
 * </li>
 * <li>
 * Optionally: Set whether the returned bilinear group should be a (non-secure) debug group or an (optimized)
 * lazy group using {@link #setDebugMode(boolean)} and {@link #setLazyGroups(boolean)}, respectively.
 * </li>
 * <li>Create the bilinear group fulfilling the defined requirements by {@link #createBilinearGroup()}.</li>
 * </ol>
 */
public class BilinearGroupFactory {
    /**
     * Security parameter
     */
    protected int securityParameter;
    protected boolean lazygroup = false;
    private BilinearGroupRequirement requirements;
    private List<? extends BilinearGroupProvider> registeredProvider = Arrays.asList(new SupersingularProvider(),
            new BarretoNaehrigProvider());
    private boolean debugMode;

    /**
     * Constructs a factory
     *
     * @param securityParameter The security parameter of the resulting groups, i.e., the complexity of DLOG in G1,
     *                          G2, GT roughly in
     *                          terms of equivalent-security symmetric encryption key length (cf. http://www
     *                          .keylength.com/)
     */
    public BilinearGroupFactory(int securityParameter) {
        this.securityParameter = securityParameter;
    }

    public void setRequirements(BilinearGroupRequirement requirements) {
        this.requirements = requirements;
    }

    /**
     * Configures a prime order bilinear group with the given {@code type}.
     */
    public void setRequirements(BilinearGroup.Type type) {
        this.setRequirements(new BilinearGroupRequirement(type));
    }

    /**
     * Configures a composite order bilinear group with the given {@code type} and a group order of
     * {@code cardinalityNumPrimeFactors} prime factors.
     */
    public void setRequirements(BilinearGroup.Type type, int cardinalityNumPrimeFactors) {
        this.setRequirements(new BilinearGroupRequirement(type, cardinalityNumPrimeFactors));
    }

    /**
     * Configures a composite order bilinear group with the given {@code type}, a group order of
     * {@code cardinalityNumPrimeFactors} prime factors and the given requirements to hashing.
     */
    public void setRequirements(BilinearGroup.Type type, boolean hashIntoG1Needed, boolean hashIntoG2Needed,
                                boolean hashIntoGTNeeded, int cardinalityNumPrimeFactors) {
        this.setRequirements(new BilinearGroupRequirement(type, hashIntoG1Needed, hashIntoG2Needed, hashIntoGTNeeded,
                cardinalityNumPrimeFactors));
    }

    /**
     * Configures a prime order bilinear group with the given {@code type} and the given requirements to hashing.
     */
    public void setRequirements(BilinearGroup.Type type, boolean hashIntoG1Needed, boolean hashIntoG2Needed,
                                boolean hashIntoGTNeeded) {
        this.setRequirements(new BilinearGroupRequirement(type, hashIntoG1Needed, hashIntoG2Needed, hashIntoGTNeeded));
    }

    public void registerProvider(List<? extends BilinearGroupProvider> bilinearGroupProvider) {
        this.registeredProvider = bilinearGroupProvider;
    }

    /**
     * @param securityParameter The security parameter of the resulting groups, i.e., the complexity of DLOG in G1,
     *                          G2, GT roughly in
     *                          terms of equivalent-security symmetric encryption key length (cf. http://www
     *                          .keylength.com/)
     */
    public void setSecurityParameter(int securityParameter) {
        this.securityParameter = securityParameter;
    }

    /**
     * If set to true, the factory is going to output a lazy version of the pairing and its groups. In that case,
     * computation of values is deferred until needed and the lazy group will try to optimize computation.
     */
    public void setLazyGroups(boolean lazygroups) {
        this.lazygroup = lazygroups;
    }

    /**
     * If set to true, a special bilinear group for non-secure pairings Zn x Zn -> Zn is returned. In this case, n =
     * [largest prime <= securityParameter]
     */
    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }

    /**
     * Creates a bilinear group according to the defined requirements and registered provider.
     * <p>
     * see {@link #setRequirements} and {@link #registerProvider(List)}
     */
    public BilinearGroup createBilinearGroup() {
        if (requirements == null) {
            throw new IllegalArgumentException(
                    "Please set appropriate requirements for the factory " + "using setRequirements!");
        }
        BilinearGroup.Type type = requirements.getType();
        int primeFactors = requirements.getCardinalityNumPrimeFactors();

        if (debugMode) {
            // DLOG is trivial in (Zn,+), use for debug only!
            DebugBilinearGroupProvider fac = new DebugBilinearGroupProvider();
            return fac.provideBilinearGroup(securityParameter, type, primeFactors);
        }

        // filter registered bilinear group provider for the suitable ones
        List<? extends BilinearGroupProvider> suitableProvider = registeredProvider.stream().filter(
                provider -> provider.checkRequirements(securityParameter, requirements)).collect(Collectors.toList());

        if (suitableProvider.isEmpty()) {
            throw new UnsupportedOperationException("Unable to create a group with the given constraints");
        }

        BilinearGroup result = suitableProvider.get(0).provideBilinearGroup(securityParameter, requirements);

        return lazygroup ? new LazyBilinearGroup(result) : result;
    }
}
