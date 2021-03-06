/*
 * Copyright (c) 2017-2020 Payara Foundation and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://github.com/payara/Payara/blob/master/LICENSE.txt
 * See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * The Payara Foundation designates this particular file as subject to the "Classpath"
 * exception as provided by the Payara Foundation in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package fish.payara.arquillian.container.payara.managed;

import fish.payara.arquillian.container.payara.CommonPayaraConfiguration;
import static org.jboss.arquillian.container.spi.client.deployment.Validate.notNull;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;

import org.jboss.arquillian.container.spi.ConfigurationException;

import fish.payara.arquillian.container.payara.PayaraVersion;


public class PayaraMicroContainerConfiguration extends CommonPayaraConfiguration {

    private String microJar = getConfigurableVariable("payara.microJar", "MICRO_JAR", null);
    private PayaraVersion microVersion = null;
    private boolean enterprise = false;

    private int startupTimeoutInSeconds = Integer.parseInt(getConfigurableVariable("payara.startupTimeoutInSeconds", "MICRO_STARTUP_TIMEOUT_IN_SECONDS", "180"));

    private boolean clusterEnabled = Boolean.parseBoolean(getConfigurableVariable("payara.clusterEnabled", "MICRO_CLUSTER_ENABLED", "false"));

    private boolean randomHttpPort = Boolean.parseBoolean(getConfigurableVariable("payara.randomHttpPort", "MICRO_RANDOM_HTTP_PORT", "true"));

    private boolean autoBindHttp = Boolean.parseBoolean(getConfigurableVariable("payara.autoBindHttp", "MICRO_AUTOBIND_HTTP", "true"));

    private boolean outputToConsole = Boolean.parseBoolean(getConfigurableVariable("payara.consoleOutput", "MICRO_CONSOLE_OUTPUT", "true"));

    private String cmdOptions = getConfigurableVariable("payara.cmdOptions", "MICRO_CMD_OPTIONS", null);

    private String extraMicroOptions = getConfigurableVariable("payara.extraMicroOptions", "EXTRA_MICRO_OPTIONS", null);

    private boolean microOnClasspath;

    private final String ZIP_ENTRY_FILE_DIR = "MICRO-INF/domain/branding/glassfish-version.properties";

    public PayaraMicroContainerConfiguration() {
        debug = Boolean.parseBoolean(getConfigurableVariable("payara.debug", "MICRO_DEBUG", "false"));
    }


    public String getMicroJar() {
        if (microJar == null) {
            URL microUrl = getClass().getClassLoader().getResource("fish/payara/micro/PayaraMicro.class");
            if (microUrl != null && microUrl.getProtocol().equals("jar") && microUrl.getPath().startsWith("file:")) {
                String path = microUrl.getPath();
                microOnClasspath = true;
                microJar = path.substring(5, path.indexOf('!'));
            }
        }
        return microJar;
    }

    boolean isMicroOnClasspath() {
        return microOnClasspath;
    }

    public File getMicroJarFile() {
        return new File(getMicroJar());
    }

    public PayaraVersion getMicroVersion() {
        return microVersion;
    }

    /**
     * @param microJar the location of the Payara Micro Jar. This is a required field.
     */
    public void setMicroJar(String microJar) {
        this.microJar = microJar;
    }

    public boolean isClusterEnabled() {
        return clusterEnabled;
    }

    /**
     * @param clusterEnabled Enable clustering on the instance.
     * Disabled by default.
     */
    public void setClusterEnabled(boolean clusterEnabled) {
        this.clusterEnabled = clusterEnabled;
    }

    /**
     * @param startupTimeoutInSeconds The maximum time allowed for Payara Micro to startup.
     * After this time has been exceeded the start will be aborted. -1 means an infinite wait. 180 by default.
     */
    public void setStartupTimeoutInSeconds(int startupTimeoutInSeconds) {
        this.startupTimeoutInSeconds = startupTimeoutInSeconds;
    }

    public int getStartupTimeoutInSeconds() {
        return startupTimeoutInSeconds;
    }

    /**
     * @param randomHttpPort Enable/disable using a random port between 8080 and 9080.
     * Enabled by default.
     */
    public void setRandomHttpPort(boolean randomHttpPort) {
        this.randomHttpPort = randomHttpPort;
    }

    public boolean isRandomHttpPort() {
        return randomHttpPort;
    }


    public boolean isAutoBindHttp() {
        return autoBindHttp;
    }

    /**
     * @param autoBindHttp Enable/disable adding the --autoBindHttp option.
     * Enabled by default.
     */
    public void setAutoBindHttp(boolean autoBindHttp) {
        this.autoBindHttp = autoBindHttp;
    }

    public boolean isOutputToConsole() {
        return outputToConsole;
    }

    /**
     * @param outputToConsole Show the output of the admin commands on the console.
     * Enabled by default.
     */
    public void setOutputToConsole(boolean outputToConsole) {
        this.outputToConsole = outputToConsole;
    }

    public String getCmdOptions() {
        return cmdOptions;
    }

    /**
     * @param cmdOptions extra command line options to pass to the Payara Micro instance (between java and -jar).
     */
    public void setCmdOptions(String cmdOptions) {
        this.cmdOptions = cmdOptions;
    }

    public String getExtraMicroOptions() {
        return extraMicroOptions;
    }

    /**
     * @param extraMicroOptions extra command line options to pass to the Payara Micro instance (at the end of the command).
     */
    public void setExtraMicroOptions(String extraMicroOptions) {
        this.extraMicroOptions = extraMicroOptions;
    }

    public boolean isEnterprise() {
        return this.enterprise;
    }

    /**
     * Validates if current configuration is valid, that is if all required properties are set and
     * have correct values
     */
    @Override
    public void validate() throws ConfigurationException {
        notNull(getMicroJar(), "The property payara.microJar must be specified or the MICRO_JAR environment variable must be set");
        if (!getMicroJarFile().isFile()) {
            throw new IllegalArgumentException("Could not locate the Payara Micro Jar file " + getMicroJar());
        }

        try (JarFile microJarFile = new JarFile(getMicroJarFile())) {

            ZipEntry pomProperties = microJarFile.getEntry(ZIP_ENTRY_FILE_DIR);

            Properties microProperties = new Properties();
            microProperties.load(microJarFile.getInputStream(pomProperties));
            this.microVersion = PayaraVersion.buildVersionFromBrandingProperties(microProperties.getProperty("major_version"),
                                                                                 microProperties.getProperty("minor_version"),
                                                                                 microProperties.getProperty("update_version"),
                                                                                 microProperties.getProperty("payara_version"),
                                                                                 microProperties.getProperty("payara_update_version"));
            this.enterprise = microProperties.getProperty("product_name").contains("Enterprise");

        } catch (IOException e) {
            throw new IllegalArgumentException(
                    "Unable to find Payara Micro Version. Please check the file is a valid Payara Micro Jar.", e);
        } catch (NullPointerException e) {
            throw new IllegalArgumentException(
                    "Unable to find Payara Micro Version. Please check the file is a valid Payara Micro Jar.\nProperties File Used: " + ZIP_ENTRY_FILE_DIR, e);
        }
        notNull(getMicroVersion(), "Unable to find Payara Micro Jar version. Please check the file is a valid Payara Micro Jar.");

        // Escape spaces in paths for the cmd options
        if (cmdOptions != null) {
            cmdOptions = escapePaths(cmdOptions);
        }
        if (extraMicroOptions != null) {
            extraMicroOptions = escapePaths(extraMicroOptions);
        }
    }

    private String escapePaths(String input) {
        Pattern pathPattern = Pattern.compile("((?>[\\/\\\\]|\\\\ )[\\w ]+) ([^-])");

        Matcher matcher = pathPattern.matcher(input);
        String output = input;
        while (matcher.find()) {
            output = matcher.replaceAll("$1\\\\ $2");
            matcher = pathPattern.matcher(output);
        }
        return output;
    }

    private static String getConfigurableVariable(String systemPropertyName, String environmentVariableName, String defaultValue) {
        String systemProperty = System.getProperty(systemPropertyName);
        String environmentProperty = System.getenv(environmentVariableName);

        if (systemProperty == null || systemProperty.isEmpty()) {
            if (environmentProperty == null || environmentProperty.isEmpty()) {
                if (defaultValue == null || defaultValue.isEmpty()) {
                    return null;
                }
                return defaultValue;
            }
            return environmentProperty;
        }
        return systemProperty;
    }
}
