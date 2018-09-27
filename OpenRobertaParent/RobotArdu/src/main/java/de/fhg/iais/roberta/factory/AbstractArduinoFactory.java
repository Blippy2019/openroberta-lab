package de.fhg.iais.roberta.factory;

import java.util.ArrayList;
import java.util.Properties;

import org.apache.commons.lang3.SystemUtils;

import de.fhg.iais.roberta.codegen.ArduinoCompilerWorkflow;
import de.fhg.iais.roberta.codegen.ICompilerWorkflow;
import de.fhg.iais.roberta.components.Configuration;
import de.fhg.iais.roberta.components.arduino.ArduinoConfiguration;
import de.fhg.iais.roberta.factory.AbstractRobotFactory;
import de.fhg.iais.roberta.inter.mode.action.IActorPort;
import de.fhg.iais.roberta.inter.mode.action.IShowPicture;
import de.fhg.iais.roberta.inter.mode.sensor.ISensorPort;
import de.fhg.iais.roberta.mode.action.ActorPort;
import de.fhg.iais.roberta.mode.sensor.SensorPort;
import de.fhg.iais.roberta.syntax.Phrase;
import de.fhg.iais.roberta.util.RobertaProperties;
import de.fhg.iais.roberta.util.Util1;
import de.fhg.iais.roberta.visitor.codegen.ArduinoCppVisitor;
import de.fhg.iais.roberta.visitor.validate.AbstractBrickValidatorVisitor;
import de.fhg.iais.roberta.visitor.validate.AbstractSimValidatorVisitor;
import de.fhg.iais.roberta.visitor.validate.ArduinoBrickValidatorVisitor;

public abstract class AbstractArduinoFactory extends AbstractRobotFactory {
    private final ArduinoCompilerWorkflow compilerWorkflow;
    private final Properties arduinoProperties;
    private final String name;

    public AbstractArduinoFactory(RobertaProperties robertaProperties, String propertyName) {
        super(robertaProperties);
        this.arduinoProperties = Util1.loadProperties("classpath:" + propertyName);
        this.name = this.arduinoProperties.getProperty("robot.name");
        String os = "linux";
        if ( SystemUtils.IS_OS_WINDOWS ) {
            os = "windows";
        }
        this.compilerWorkflow =
            new ArduinoCompilerWorkflow(
                robertaProperties.getTempDirForUserProjects(),
                robertaProperties.getStringProperty("robot.plugin." + this.name + ".compiler.resources.dir"),
                robertaProperties.getStringProperty("robot.plugin." + this.name + ".compiler." + os + ".dir"),
                this.name);
        addBlockTypesFromProperties(propertyName, this.arduinoProperties);
    }

    public SensorPort getSensorName(String port) {
        return new SensorPort(port, port);
    }

    public ActorPort getActorName(String port) {
        return new ActorPort(port, port);
    }

    @Override
    public ISensorPort getSensorPort(String port) {
        return getSensorName(port);
    }

    @Override
    public IActorPort getActorPort(String port) {
        return getActorName(port);
    }

    @Override
    public IShowPicture getShowPicture(String picture) {
        return null;
    }

    @Override
    public ICompilerWorkflow getRobotCompilerWorkflow() {
        return this.compilerWorkflow;
    }

    @Override
    public ICompilerWorkflow getSimCompilerWorkflow() {
        return null;
    }

    @Override
    public String getFileExtension() {
        return "ino";
    }

    @Override
    public String getProgramToolboxBeginner() {
        return this.arduinoProperties.getProperty("robot.program.toolbox.beginner");
    }

    @Override
    public String getProgramToolboxExpert() {
        return this.arduinoProperties.getProperty("robot.program.toolbox.expert");
    }

    @Override
    public String getProgramDefault() {
        return this.arduinoProperties.getProperty("robot.program.default");
    }

    @Override
    public String getConfigurationToolbox() {
        return this.arduinoProperties.getProperty("robot.configuration.toolbox");
    }

    @Override
    public String getConfigurationDefault() {
        return this.arduinoProperties.getProperty("robot.configuration.default");
    }

    @Override
    public String getRealName() {
        return this.arduinoProperties.getProperty("robot.real.name");
    }

    @Override
    public Boolean hasSim() {
        return this.arduinoProperties.getProperty("robot.sim").equals("true") ? true : false;
    }

    @Override
    public String getInfo() {
        return this.arduinoProperties.getProperty("robot.info") != null ? this.arduinoProperties.getProperty("robot.info") : "#";
    }

    @Override
    public Boolean isBeta() {
        return this.arduinoProperties.getProperty("robot.beta") != null ? true : false;
    }

    @Override
    public String getConnectionType() {
        return this.arduinoProperties.getProperty("robot.connection");
    }

    @Override
    public String getVendorId() {
        return this.arduinoProperties.getProperty("robot.vendor");
    }

    @Override
    public AbstractSimValidatorVisitor getSimProgramCheckVisitor(Configuration brickConfiguration) {
        return null;
    }

    @Override
    public AbstractBrickValidatorVisitor getRobotProgramCheckVisitor(Configuration brickConfiguration) {
        return new ArduinoBrickValidatorVisitor(brickConfiguration);
    }

    @Override
    public Boolean hasConfiguration() {
        return Boolean.parseBoolean(this.arduinoProperties.getProperty("robot.configuration"));
    }

    @Override
    public String getGroup() {
        return this.robertaProperties.getStringProperty("robot.plugin." + this.name + ".group") != null
            ? this.robertaProperties.getStringProperty("robot.plugin." + this.name + ".group")
            : this.name;
    }

    @Override
    public String generateCode(Configuration brickConfiguration, ArrayList<ArrayList<Phrase<Void>>> phrasesSet, boolean withWrapping) {
        return ArduinoCppVisitor.generate((ArduinoConfiguration) brickConfiguration, phrasesSet, withWrapping);
    }

    @Override
    public String getCommandline() {
        return this.arduinoProperties.getProperty("robot.connection.commandLine");
    }

    @Override
    public String getSignature() {
        return this.arduinoProperties.getProperty("robot.connection.signature");
    }
}