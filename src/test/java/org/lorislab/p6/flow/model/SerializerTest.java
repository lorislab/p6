package org.lorislab.p6.flow.model;

import org.junit.Test;
import org.lorislab.p6.flow.model.event.StartEvent;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.introspector.BeanAccess;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.introspector.PropertyUtils;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import javax.json.bind.annotation.JsonbPropertyOrder;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class SerializerTest {

    @Test
    public void serilizeExampleTest() {
        ProcessFlow p = new ProcessFlow();
        p.setProcessId("org.lorislab.p6.example.Test1");
        p.setProcessVersion("1.0.0");

        StartEvent s = p.createStartEvent("start");
            ServiceTask n1 = p.createServiceTask("service1", s);
            GatewayNode g1 = p.createGatewayNode("gateway1", n1);
                ServiceTask n3 = p.createServiceTask("service3", g1);
                ServiceTask n4 = p.createServiceTask("service4", g1);
            GatewayNode g2 = p.createGatewayNode("gateway2", n3, n4);
        p.createEndEvent("end", g2);


        Representer repr = new Representer() {
            @Override
            protected NodeTuple representJavaBeanProperty(Object javaBean, Property property, Object propertyValue, Tag customTag) {
                if (propertyValue == null) {
                    return null;
                } else {
                    return super.representJavaBeanProperty(javaBean, property, propertyValue, customTag);
                }
            }
        };
        repr.setPropertyUtils(new PropertyUtils() {
            @Override
            protected Set<Property> createPropertySet(Class<? extends Object> type, BeanAccess bAccess) {
                JsonbPropertyOrder order = type.getAnnotation(JsonbPropertyOrder.class);
                if (order != null) {
                    Map<String, Property> data = getPropertiesMap(type, BeanAccess.FIELD);
                    Set<Property> result = new LinkedHashSet<>();
                    String[] tmp = order.value();
                    for (String item : tmp) {
                        result.add(data.get(item));
                    }
                    return result;
                }
                return super.createPropertySet(type, bAccess);
            }

        });
//        Constructor con = new Constructor(new TypeDescription(ProcessFlow.class), null);


        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Yaml yaml = new Yaml(repr, options);

        String tmp = yaml.dump(p);
        System.out.println(tmp);

        ProcessFlow pp = yaml.loadAs(tmp, ProcessFlow.class);

    }
}
