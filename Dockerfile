FROM lorislab/liquibase:3.6.2-1 as liquibase

FROM jboss/wildfly:15.0.0.Final-1 as build

# Switch the wildfly configuration to the standalone-full-ha.xml
RUN cd /opt/jboss/wildfly/standalone/configuration/ \
    && rm standalone.xml \
    && cp standalone-full-ha.xml standalone.xml

# Reconfigure the wildfly
COPY src/main/docker/ /tmp/docker
RUN /opt/jboss/wildfly/bin/jboss-cli.sh --file=/tmp/docker/wildfly/config.cli

FROM jboss/wildfly:15.0.0.Final-1

# Install the liquibase
COPY --chown=jboss:root --from=liquibase /opt/liquibase /opt/liquibase

# Install the postgresql driver to the liquibase
COPY --chown=jboss:root --from=build /tmp/docker/wildfly/modules/org/postgresql/main/postgresql-42.2.5.jar /opt/liquibase/lib/

# Copy configuration from the build image
COPY --chown=jboss:root --from=build /opt/jboss/wildfly/standalone/configuration/standalone.xml /opt/jboss/wildfly/standalone/configuration/standalone.xml

# Add the database configuration
COPY --chown=jboss:root --from=build /tmp/docker/db /opt/assembly/db

# Add the wildfly modules
COPY --chown=jboss:root --from=build /tmp/docker/wildfly/modules /opt/jboss/wildfly/modules

# Deploy the application
ADD target/*.war /opt/jboss/wildfly/standalone/deployments/

# Update the database and start the server
CMD /opt/liquibase/liquibase --changeLogFile=/opt/assembly/db/update.xml --url=${DB_URL} --username=${DB_USER} --password=${DB_PWD} update && \
    /opt/jboss/wildfly/bin/standalone.sh -b 0.0.0.0 -Djboss.bind.address=$(hostname -i) -Djboss.bind.address.private=$(hostname -i)
