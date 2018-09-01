FROM lorislab/wildfly:14.0.0.1 as build

RUN cd /opt/jboss/wildfly/standalone/configuration/ \
    &&  rm standalone.xml \
    && cp standalone-full-ha.xml standalone.xml
 
COPY src/docker/wildfly/modules /opt/jboss/wildfly/modules

COPY src/docker/wildfly/config.cli /tmp/config.cli

RUN /opt/jboss/wildfly/bin/jboss-cli.sh --file=/tmp/config.cli

FROM lorislab/wildfly:14.0.0.1

COPY --chown=jboss:root --from=build /opt/jboss/wildfly/standalone/configuration/standalone.xml /opt/jboss/wildfly/standalone/configuration/standalone.xml

COPY src/docker/db /opt/assembly/db

COPY src/docker/wildfly/modules /opt/jboss/wildfly/modules

COPY src/docker/wildfly/modules/org/postgresql/main/postgresql-*.jar /opt/liquibase/lib/

ADD target/p6.war /opt/jboss/wildfly/standalone/deployments/

#ENTRYPOINT /opt/liquibase/liquibase --changeLogFile=/opt/assembly/db/update.xml --url=${DB_URL} --username=${DB_USER} --password=${DB_PWD} update

#CMD /opt/jboss/wildfly/bin/standalone.sh -b 0.0.0.0

CMD /opt/liquibase/liquibase --changeLogFile=/opt/assembly/db/update.xml --url=${DB_URL} --username=${DB_USER} --password=${DB_PWD} update && \
    /opt/jboss/wildfly/bin/standalone.sh -b 0.0.0.0 -Djboss.bind.address=$(hostname -i) -Djboss.bind.address.private=$(hostname -i)