package org.lorislab.p6.test;

import java.io.File;
import org.eu.ingwar.tools.arquillian.extension.suite.annotations.ArquillianSuiteDeployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import static org.jboss.shrinkwrap.resolver.api.maven.ScopeType.RUNTIME;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenDependencies;
import org.jboss.arquillian.container.test.api.Deployment;
import org.lorislab.p6.rs.JaxrsApplication;


@ArquillianSuiteDeployment
public class DeploymentConfig {

    private static final String WEBAPP_SRC = "src/main/webapp";

    public static WebArchive createArchive() {

        File[] libsCache = Maven.resolver().loadPomFromFile("pom.xml").importCompileAndRuntimeDependencies()
                .addDependency(MavenDependencies.createDependency("io.rest-assured:rest-assured", RUNTIME, false))
                .addDependency(MavenDependencies.createDependency("org.dbunit:dbunit:2.5.4", RUNTIME, false))
                .addDependency(MavenDependencies.createDependency("org.apache.poi:poi:3.14", RUNTIME, false))
                .addDependency(MavenDependencies.createDependency("org.apache.poi:poi-ooxml:3.14", RUNTIME, false))
                .addDependency(MavenDependencies.createDependency("org.apache.poi:poi-ooxml-schemas:3.14", RUNTIME, false))
                .resolve().withTransitivity().asFile();

        WebArchive war = ShrinkWrap.create(WebArchive.class, "p6.war")
                .addClass(JaxrsApplication.class)
                .addAsWebInfResource(new File(WEBAPP_SRC, "WEB-INF/jboss-web.xml"))
                .addAsWebInfResource(new File(WEBAPP_SRC, "WEB-INF/beans.xml"))
                .addAsWebInfResource(new File(WEBAPP_SRC, "WEB-INF/ejb-jar.xml"))
                .addAsLibraries(libsCache)
                .addAsWebInfResource("test-jms.xml")
                .addAsWebInfResource("h2-ds.xml")
                .addAsResource("test-persistence.xml", "META-INF/persistence.xml")
                ;
        //.addAsResource("datasets","datasets");

        return war;
    }

    @Deployment
    public static WebArchive createDeployment() {
        WebArchive war = createArchive().addPackages(true, "org.lorislab.p6");
        return war;
    }
    
}
