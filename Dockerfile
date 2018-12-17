FROM jboss/wildfly:15.0.0.Final-1 as build

# Download the liquibase
ADD https://github.com/liquibase/liquibase/releases/download/liquibase-parent-3.6.2/liquibase-3.6.2-bin.tar.gz /opt/liquibase/

USER root

# Install the liquibase
RUN mkdir /opt/database
RUN cd /opt/liquibase/ \
	&& tar -xzf liquibase-*-bin.tar.gz \
	&& chown -R jboss:root /opt/liquibase/ \
    && cp /opt/liquibase/sdk/lib-sdk/slf4j-api-*.jar /opt/liquibase/lib \
    && rm -rf /opt/liquibase/sdk

# Install the postgresql driver to the liquibase
COPY src/docker/wildfly/modules/org/postgresql/main/postgresql-*.jar /opt/liquibase/lib/

# Switch the wildfly configuration to the standalone-full-ha.xml
RUN cd /opt/jboss/wildfly/standalone/configuration/ \
    && rm standalone.xml \
    && cp standalone-full-ha.xml standalone.xml

# Reconfigure the wildfly
COPY src/docker/wildfly/config.cli /tmp/config.cli
RUN /opt/jboss/wildfly/bin/jboss-cli.sh --file=/tmp/config.cli

FROM jboss/wildfly:15.0.0.Final-1

# Copy configuration from the build image
COPY --chown=jboss:root --from=build /opt/jboss/wildfly/standalone/configuration/standalone.xml /opt/jboss/wildfly/standalone/configuration/standalone.xml
COPY --chown=jboss:root --from=build /opt/liquibase /opt/liquibase

# Add the database configuration
COPY src/docker/db /opt/assembly/db

# Add the wildfly modules
COPY src/docker/wildfly/modules /opt/jboss/wildfly/modules

# Deploy the application
ADD target/*.war /opt/jboss/wildfly/standalone/deployments/

# Update the database and start the server
CMD /opt/liquibase/liquibase --changeLogFile=/opt/assembly/db/update.xml --url=${DB_URL} --username=${DB_USER} --password=${DB_PWD} update && \
    /opt/jboss/wildfly/bin/standalone.sh -b 0.0.0.0 -Djboss.bind.address=$(hostname -i) -Djboss.bind.address.private=$(hostname -i)
