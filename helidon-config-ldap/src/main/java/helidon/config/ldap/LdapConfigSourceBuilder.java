package helidon.config.ldap;

import io.helidon.config.Config;
import io.helidon.config.spi.AbstractParsableConfigSource;
import io.helidon.config.spi.ConfigSource;

import java.util.function.Consumer;

/**
 * LdapConfigSource builder.
 * <p>
 * Creates a {@link LdapConfigSource} while allowing the application to the following properties:
 * <ul>
 * <li>{@code uri} - an uri to the ldap server</li>
 * <li>{@code bindDn} - distinguished name of user</li>
 * <li>{@code password} - password of bindDn</li>
 * <li>{@code baseDn} - root of configuration tree</li>
 * <li>{@code attrName} - name of ldap attribute containing config value</li>
 * </ul>
 * <p>
 */
@SuppressWarnings({"PMD.AvoidFieldNameMatchingMethodName", "PMD.DefaultPackage"})
public class LdapConfigSourceBuilder
    extends AbstractParsableConfigSource.Builder<LdapConfigSourceBuilder, String> {

    private String uri;
    private String password;
    private String bindDn;
    private String baseDN;
    private String attrName;

    LdapConfigSourceBuilder() {
        super(String.class);
    }

    public LdapConfigSourceBuilder config(final Config metaConfig) {
        readMeta(metaConfig, "uri", this::uri);
        readMeta(metaConfig, "password", this::password);
        readMeta(metaConfig, "bindDn", this::bindDn);
        readMeta(metaConfig, "baseDn", this::baseDn);
        readMeta(metaConfig, "attrName", this::attrName);
        return this;
    }

    private void readMeta(final Config metaConfig, final String attrName, final Consumer<String> consumer) {
        metaConfig.get(attrName).asString().ifPresent(consumer);
    }

    @Override
    public ConfigSource build() {
        return new LdapConfigSource(this);
    }

    LdapConfigSourceBuilder uri(final String uri) {
        this.uri = uri;
        return this;
    }

    String uri() {
        return uri;
    }


    String bindDn() {
        return bindDn;
    }

    public LdapConfigSourceBuilder bindDn(final String bindDn) {
        this.bindDn = bindDn;
        return this;
    }

    String baseDn() {
        return baseDN;
    }

    public LdapConfigSourceBuilder baseDn(final String baseDN) {
        this.baseDN = baseDN;
        return this;
    }

    String attrName() {
        return attrName;
    }

    public LdapConfigSourceBuilder attrName(final String attrName) {
        this.attrName = attrName;
        return this;
    }

    String password() {
        return password;
    }

    LdapConfigSourceBuilder password(final String password) {
        this.password = password;
        return this;
    }
}
