package helidon.config.ldap;

import io.helidon.config.ConfigException;
import io.helidon.config.internal.ConfigUtils;
import io.helidon.config.spi.AbstractConfigSource;
import io.helidon.config.spi.ConfigNode;
import ldaptive.LdapException;
import ldaptive.LdapTree;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;

/**
 *
 */
public class LdapConfigSource extends AbstractConfigSource<Instant> {

    private final LdapTree client;
    private final String baseDn;
    private final String attrName;

    LdapConfigSource(final LdapConfigSourceBuilder builder) {
        this(builder, new LdapTree.Default(new LdapEndpoint(builder.uri(), builder.bindDn(), builder.password())));
    }

    public LdapConfigSource(final LdapConfigSourceBuilder builder, final LdapTree ldapTree) {
        super(builder);
        this.client = ldapTree;
        baseDn = builder.baseDn();
        attrName = builder.attrName();
    }

    public static LdapConfigSourceBuilder builder() {
        return new LdapConfigSourceBuilder();
    }

    @Override
    protected Optional<Instant> dataStamp() {
        return Optional.of(Instant.now());
    }

    @Override
    protected Data<ConfigNode.ObjectNode, Instant> loadData() {
        return new Data<>(Optional.of(ConfigUtils.mapToObjectNode(getMap(baseDn, attrName), true)), dataStamp());
    }

    private Map<String, String> getMap(final String baseDn, final String attrName) {
        try {
            return client.toMap(baseDn, attrName);
        } catch (LdapException e) {
            throw new ConfigException("cannot read ldap tree", e);
        }
    }
}
