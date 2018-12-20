package org.lorislab.p6.service;

import lombok.extern.slf4j.Slf4j;
import org.lorislab.p6.flow.model.ProcessFlow;
import org.lorislab.p6.config.ConfigService;
import org.lorislab.p6.jpa.model.ProcessDefinition;
import org.lorislab.p6.jpa.service.ProcessDefinitionService;
import org.lorislab.p6.model.RuntimeProcess;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.PostConstruct;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.Message;
import javax.jms.MessageListener;
import java.nio.charset.StandardCharsets;

@Slf4j
@MessageDriven(
        activationConfig = {
                @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "queue/" + ConfigService.QUEUE_CMD),
                @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")
        }
)
public class CommandService implements MessageListener {

    private Yaml yaml;

    @EJB
    private ProcessDefinitionService processDefinitionService;

    @EJB
    private RuntimeProcessService runtimeProcessService;

    @PostConstruct
    public void init() {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        yaml = new Yaml(options);
    }

    @Override
    public void onMessage(Message message) {
        String cmd = null;
        try {
            cmd = message.getStringProperty(ConfigService.MSG_CMD);
            switch (cmd) {
                case ConfigService.CMD_DEPLOY:
                    deploy(message);
                    break;
                case ConfigService.CMD_START_PROCESS:
                    startProcess(message);
                    break;
                case ConfigService.CMD_SEND_EVENT:
                    sendEvent(message);
                    break;
                case ConfigService.CMD_SEND_MESSAGE:
                    sendMessage(message);
                    break;
                default:
                    log.error("Not supported command '{}' in the command message.", cmd);
            }
        } catch (Exception ex) {
            log.error("Error execute the command " + cmd, ex);
        }
    }

    private void deploy(Message message) throws Exception {
        String guid = message.getStringProperty(ConfigService.MSG_PROCESS_DEF_GUID);
        ProcessDefinition def = processDefinitionService.loadByGuid(guid);
        String data = new String(def.getContent().getData(), StandardCharsets.UTF_8);
        ProcessFlow flow = yaml.loadAs(data, ProcessFlow.class);
        def.setContent(null);
        RuntimeProcess process = new RuntimeProcess(def, flow);
        runtimeProcessService.addRuntimeProcess(process);
    }

    private void sendEvent(Message message) throws Exception {
        log.error("###################### NOT SUPPORTED: sendEvent ###################");
    }

    private void sendMessage(Message message) throws Exception {
        log.error("###################### NOT SUPPORTED: sendMessage ###################");
    }

    private void startProcess(Message message) throws Exception {
        String processId = message.getStringProperty(ConfigService.MSG_PROCESS_ID);
        RuntimeProcess process = runtimeProcessService.getRuntimeProcess(processId);
        if (process != null) {
            log.error("###################### NOT SUPPORTED: startProcess ###################");

        } else {
            log.error("No runtime process found for the process id: {}", processId);
        }
    }
}
