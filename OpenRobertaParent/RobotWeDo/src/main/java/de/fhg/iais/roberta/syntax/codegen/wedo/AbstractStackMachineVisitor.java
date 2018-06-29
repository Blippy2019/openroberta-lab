package de.fhg.iais.roberta.syntax.codegen.wedo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONObject;

import de.fhg.iais.roberta.components.Configuration;
import de.fhg.iais.roberta.mode.action.DriveDirection;
import de.fhg.iais.roberta.mode.action.TurnDirection;
import de.fhg.iais.roberta.mode.sensor.TimerSensorMode;
import de.fhg.iais.roberta.syntax.MotorDuration;
import de.fhg.iais.roberta.syntax.Phrase;
import de.fhg.iais.roberta.syntax.action.communication.BluetoothCheckConnectAction;
import de.fhg.iais.roberta.syntax.action.communication.BluetoothConnectAction;
import de.fhg.iais.roberta.syntax.action.communication.BluetoothReceiveAction;
import de.fhg.iais.roberta.syntax.action.communication.BluetoothSendAction;
import de.fhg.iais.roberta.syntax.action.communication.BluetoothWaitForConnectionAction;
import de.fhg.iais.roberta.syntax.action.sound.PlayNoteAction;
import de.fhg.iais.roberta.syntax.action.sound.ToneAction;
import de.fhg.iais.roberta.syntax.lang.blocksequence.ActivityTask;
import de.fhg.iais.roberta.syntax.lang.blocksequence.Location;
import de.fhg.iais.roberta.syntax.lang.blocksequence.MainTask;
import de.fhg.iais.roberta.syntax.lang.blocksequence.StartActivityTask;
import de.fhg.iais.roberta.syntax.lang.expr.ActionExpr;
import de.fhg.iais.roberta.syntax.lang.expr.Binary;
import de.fhg.iais.roberta.syntax.lang.expr.BoolConst;
import de.fhg.iais.roberta.syntax.lang.expr.ColorConst;
import de.fhg.iais.roberta.syntax.lang.expr.ConnectConst;
import de.fhg.iais.roberta.syntax.lang.expr.EmptyExpr;
import de.fhg.iais.roberta.syntax.lang.expr.EmptyList;
import de.fhg.iais.roberta.syntax.lang.expr.Expr;
import de.fhg.iais.roberta.syntax.lang.expr.ExprList;
import de.fhg.iais.roberta.syntax.lang.expr.FunctionExpr;
import de.fhg.iais.roberta.syntax.lang.expr.ListCreate;
import de.fhg.iais.roberta.syntax.lang.expr.MathConst;
import de.fhg.iais.roberta.syntax.lang.expr.MethodExpr;
import de.fhg.iais.roberta.syntax.lang.expr.NullConst;
import de.fhg.iais.roberta.syntax.lang.expr.NumConst;
import de.fhg.iais.roberta.syntax.lang.expr.RgbColor;
import de.fhg.iais.roberta.syntax.lang.expr.SensorExpr;
import de.fhg.iais.roberta.syntax.lang.expr.ShadowExpr;
import de.fhg.iais.roberta.syntax.lang.expr.StmtExpr;
import de.fhg.iais.roberta.syntax.lang.expr.StringConst;
import de.fhg.iais.roberta.syntax.lang.expr.Unary;
import de.fhg.iais.roberta.syntax.lang.expr.Var;
import de.fhg.iais.roberta.syntax.lang.expr.VarDeclaration;
import de.fhg.iais.roberta.syntax.lang.functions.FunctionNames;
import de.fhg.iais.roberta.syntax.lang.functions.GetSubFunct;
import de.fhg.iais.roberta.syntax.lang.functions.IndexOfFunct;
import de.fhg.iais.roberta.syntax.lang.functions.LengthOfIsEmptyFunct;
import de.fhg.iais.roberta.syntax.lang.functions.ListGetIndex;
import de.fhg.iais.roberta.syntax.lang.functions.ListRepeat;
import de.fhg.iais.roberta.syntax.lang.functions.ListSetIndex;
import de.fhg.iais.roberta.syntax.lang.functions.MathConstrainFunct;
import de.fhg.iais.roberta.syntax.lang.functions.MathNumPropFunct;
import de.fhg.iais.roberta.syntax.lang.functions.MathOnListFunct;
import de.fhg.iais.roberta.syntax.lang.functions.MathPowerFunct;
import de.fhg.iais.roberta.syntax.lang.functions.MathRandomFloatFunct;
import de.fhg.iais.roberta.syntax.lang.functions.MathRandomIntFunct;
import de.fhg.iais.roberta.syntax.lang.functions.MathSingleFunct;
import de.fhg.iais.roberta.syntax.lang.functions.TextJoinFunct;
import de.fhg.iais.roberta.syntax.lang.functions.TextPrintFunct;
import de.fhg.iais.roberta.syntax.lang.methods.MethodCall;
import de.fhg.iais.roberta.syntax.lang.methods.MethodIfReturn;
import de.fhg.iais.roberta.syntax.lang.methods.MethodReturn;
import de.fhg.iais.roberta.syntax.lang.methods.MethodVoid;
import de.fhg.iais.roberta.syntax.lang.stmt.ActionStmt;
import de.fhg.iais.roberta.syntax.lang.stmt.AssignStmt;
import de.fhg.iais.roberta.syntax.lang.stmt.ExprStmt;
import de.fhg.iais.roberta.syntax.lang.stmt.FunctionStmt;
import de.fhg.iais.roberta.syntax.lang.stmt.IfStmt;
import de.fhg.iais.roberta.syntax.lang.stmt.MethodStmt;
import de.fhg.iais.roberta.syntax.lang.stmt.RepeatStmt;
import de.fhg.iais.roberta.syntax.lang.stmt.SensorStmt;
import de.fhg.iais.roberta.syntax.lang.stmt.StmtFlowCon;
import de.fhg.iais.roberta.syntax.lang.stmt.StmtFlowCon.Flow;
import de.fhg.iais.roberta.syntax.lang.stmt.StmtList;
import de.fhg.iais.roberta.syntax.lang.stmt.StmtTextComment;
import de.fhg.iais.roberta.syntax.lang.stmt.WaitStmt;
import de.fhg.iais.roberta.syntax.lang.stmt.WaitTimeStmt;
import de.fhg.iais.roberta.syntax.sensor.generic.GetSampleSensor;
import de.fhg.iais.roberta.syntax.sensor.generic.TimerSensor;
import de.fhg.iais.roberta.typecheck.BlocklyType;
import de.fhg.iais.roberta.util.dbc.DbcException;
import de.fhg.iais.roberta.visitor.actor.AstActorCommunicationVisitor;
import de.fhg.iais.roberta.visitor.actor.AstActorDisplayVisitor;
import de.fhg.iais.roberta.visitor.actor.AstActorLightVisitor;
import de.fhg.iais.roberta.visitor.actor.AstActorMotorVisitor;
import de.fhg.iais.roberta.visitor.actor.AstActorSoundVisitor;
import de.fhg.iais.roberta.visitor.lang.AstLanguageVisitor;
import de.fhg.iais.roberta.visitor.sensor.AstSensorsVisitor;

public abstract class AbstractStackMachineVisitor<V> implements AstLanguageVisitor<V>, AstSensorsVisitor<V>, AstActorCommunicationVisitor<V>,
    AstActorDisplayVisitor<V>, AstActorMotorVisitor<V>, AstActorLightVisitor<V>, AstActorSoundVisitor<V> {
    protected int loopsCounter = 0;
    protected int currentLoop = 0;
    protected int stmtsNumber = 0;
    protected int methodsNumber = 0;
    private final ArrayList<Boolean> inStmt = new ArrayList<>();

    protected List<JSONObject> opArray = new ArrayList<>();
    protected final List<List<JSONObject>> opArrayStack = new ArrayList<>();
    protected final Configuration brickConfiguration;

    protected AbstractStackMachineVisitor(Configuration brickConfiguration) {
        this.brickConfiguration = brickConfiguration;
    }

    @Override
    public V visitNumConst(NumConst<V> numConst) {
        JSONObject o = mk(C.EXPR).put(C.EXPR, numConst.getKind().getName()).put(C.VALUE, numConst.getValue());
        return app(o);
    }

    @Override
    public V visitMathConst(MathConst<V> mathConst) {
        JSONObject o = mk(C.EXPR).put(C.EXPR, mathConst.getMathConst() + "')");
        return app(o);
    }

    @Override
    public V visitBoolConst(BoolConst<V> boolConst) {
        JSONObject o = mk(C.EXPR).put(C.EXPR, boolConst.getKind().getName()).put(C.VALUE, boolConst.isValue());
        return app(o);
    }

    @Override
    public V visitStringConst(StringConst<V> stringConst) {
        JSONObject o = mk(C.EXPR).put(C.EXPR, stringConst.getKind().getName());
        o.put(C.VALUE, StringEscapeUtils.escapeEcmaScript(stringConst.getValue().replaceAll("[<>\\$]", "")));
        return app(o);
    }

    @Override
    public V visitNullConst(NullConst<V> nullConst) {
        JSONObject o = mk(C.EXPR).put(C.EXPR, "C." + nullConst.getKind().getName());
        return app(o);
    }

    @Override
    public V visitColorConst(ColorConst<V> colorConst) {
        JSONObject o = mk(C.EXPR).put(C.EXPR, colorConst.getKind().getName()).put(C.VALUE, colorConst.getValue());
        return app(o);
    }

    @Override
    public V visitRgbColor(RgbColor<V> rgbColor) {
        rgbColor.getR().visit(this);
        rgbColor.getG().visit(this);
        rgbColor.getB().visit(this);
        JSONObject o = mk(C.RGB_COLOR_CONST);
        return app(o);
    }

    @Override
    public V visitShadowExpr(ShadowExpr<V> shadowExpr) {
        if ( shadowExpr.getBlock() != null ) {
            shadowExpr.getBlock().visit(this);
        } else {
            shadowExpr.getShadow().visit(this);
        }
        return null;
    }

    @Override
    public V visitVar(Var<V> var) {
        JSONObject o = mk(C.EXPR).put(C.EXPR, C.VAR).put(C.NAME, var.getValue());
        return app(o);
    }

    @Override
    public V visitVarDeclaration(VarDeclaration<V> var) {
        if ( var.getValue().getKind().hasName("EXPR_LIST") ) {
            ExprList<V> list = (ExprList<V>) var.getValue();
            if ( list.get().size() == 2 ) {
                list.get().get(1).visit(this);
            } else {
                list.get().get(0).visit(this);
            }
        } else {
            var.getValue().visit(this);
        }
        JSONObject o = mk(C.VAR_DECLARATION).put(C.TYPE, var.getTypeVar()).put(C.NAME, var.getName());
        return app(o);
    }

    @Override
    public V visitUnary(Unary<V> unary) {
        unary.getExpr().visit(this);
        JSONObject o = mk(C.EXPR).put(C.EXPR, C.UNARY).put(C.OP, unary.getOp());
        return app(o);
    }

    @Override
    public V visitBinary(Binary<V> binary) {
        binary.getLeft().visit(this);
        binary.getRight().visit(this);
        JSONObject o;
        // FIXME: The math change should be removed from the binary expression since it is a statement
        switch ( binary.getOp() ) {
            case MATH_CHANGE:
                o = mk(C.MATH_CHANGE);
                break;
            case TEXT_APPEND:
                o = mk(C.TEXT_APPEND);
                break;
            default:
                o = mk(C.EXPR).put(C.EXPR, C.BINARY).put(C.OP, binary.getOp());
                break;
        }
        return app(o);
    }

    @Override
    public V visitToneAction(ToneAction<V> toneAction) {
        toneAction.getFrequency().visit(this);
        toneAction.getDuration().visit(this);
        JSONObject o = mk(C.TONE_ACTION);
        return app(o);
    }

    @Override
    public V visitPlayNoteAction(PlayNoteAction<V> playNoteAction) {
        JSONObject frequency = mk(C.EXPR).put(C.EXPR, C.NUM_CONST).put(C.VALUE, playNoteAction.getFrequency());
        app(frequency);
        JSONObject duration = mk(C.EXPR).put(C.EXPR, C.NUM_CONST).put(C.VALUE, playNoteAction.getDuration());
        app(duration);
        JSONObject o = mk(C.TONE_ACTION);
        return app(o);
    }

    @Override
    public V visitMathPowerFunct(MathPowerFunct<V> mathPowerFunct) {
        mathPowerFunct.getParam().get(0).visit(this);
        mathPowerFunct.getParam().get(1).visit(this);
        JSONObject o = mk(C.EXPR).put(C.EXPR, C.BINARY).put(C.OP, mathPowerFunct.getFunctName());
        return app(o);
    }

    @Override
    public V visitActionExpr(ActionExpr<V> actionExpr) {
        actionExpr.getAction().visit(this);
        return null;
    }

    @Override
    public V visitSensorExpr(SensorExpr<V> sensorExpr) {
        sensorExpr.getSens().visit(this);
        return null;
    }

    @Override
    public V visitMethodExpr(MethodExpr<V> methodExpr) {
        methodExpr.getMethod().visit(this);
        return null;
    }

    @Override
    public V visitEmptyList(EmptyList<V> emptyList) {
        throw new DbcException("Operation not supported");
    }

    @Override
    public V visitEmptyExpr(EmptyExpr<V> emptyExpr) {
        JSONObject o;
        switch ( emptyExpr.getDefVal() ) {
            case STRING:
                o = mk(C.EXPR).put(C.EXPR, C.STRING_CONST).put(C.VALUE, "");
                break;
            case BOOLEAN:
                o = mk(C.EXPR).put(C.EXPR, C.BOOL_CONST).put(C.VALUE, "true");
                break;
            case NUMBER_INT:
            case NUMBER:
                o = mk(C.EXPR).put(C.EXPR, C.NUM_CONST).put(C.VALUE, 0);
                break;
            case COLOR:
                o = mk(C.EXPR).put(C.EXPR, C.LED_COLOR_CONST).put(C.VALUE, C.Colors.GREEN);
                break;
            case NULL:
                o = mk(C.EXPR).put(C.EXPR, C.NULL_CONST);
                break;
            default:
                throw new DbcException("Operation not supported");
        }
        return app(o);
    }

    @Override
    public V visitExprList(ExprList<V> exprList) {
        for ( Expr<V> expr : exprList.get() ) {
            if ( !expr.getKind().hasName("EMPTY_EXPR") ) {
                expr.visit(this);
            }
        }
        return null;
    }

    @Override
    public V visitStmtExpr(StmtExpr<V> stmtExpr) {
        stmtExpr.getStmt().visit(this);
        return null;
    }

    @Override
    public V visitActionStmt(ActionStmt<V> actionStmt) {
        actionStmt.getAction().visit(this);
        return null;
    }

    @Override
    public V visitAssignStmt(AssignStmt<V> assignStmt) {
        assignStmt.getExpr().visit(this);
        JSONObject o = mk(C.ASSIGN_STMT).put(C.NAME, assignStmt.getName().getValue());
        return app(o);
    }

    @Override
    public V visitExprStmt(ExprStmt<V> exprStmt) {
        exprStmt.getExpr().visit(this);
        return null;
    }

    //TODO
    @Override
    public V visitIfStmt(IfStmt<V> ifStmt) {
        if ( ifStmt.isTernary() ) {
            throw new DbcException("Operation not supported");
        } else {
            appendIfStmtConditions(ifStmt);
            appendThenStmts(ifStmt);
            appendElseStmt(ifStmt);
            JSONObject o = mk(C.IF_STMT);
            return app(o);
        }
    }

    @Override
    public V visitRepeatStmt(RepeatStmt<V> repeatStmt) {
        increaseLoopCounter(repeatStmt);
        pushOpArray();
        repeatStmt.getExpr().visit(this);
        JSONObject ifBreak = mk(C.FLOW_CONTROL).put(C.LOOP_NUMBER, this.currentLoop).put(C.IF_RETURN, true).put(C.BREAK, true);
        this.opArray.add(ifBreak);
        repeatStmt.getList().visit(this);
        List<JSONObject> whileBody = popOpArray();
        JSONObject o = mk(C.REPEAT_STMT).put(C.LOOP_NUMBER, this.loopsCounter).put(C.MODE, repeatStmt.getMode()).put(C.STMT_LIST, whileBody);
        exitLoop(repeatStmt);
        return app(o);
    }

    private void increaseLoopCounter(RepeatStmt<V> repeatStmt) {
        if ( repeatStmt.getMode() != RepeatStmt.Mode.WAIT ) {
            this.loopsCounter++;
            this.currentLoop = this.loopsCounter;
        }
    }

    private void exitLoop(RepeatStmt<V> repeatStmt) {
        if ( repeatStmt.getMode() != RepeatStmt.Mode.WAIT ) {
            this.currentLoop--;
        }
    }

    @Override
    public V visitSensorStmt(SensorStmt<V> sensorStmt) {
        sensorStmt.getSensor().visit(this);
        return null;
    }

    @Override
    public V visitStmtFlowCon(StmtFlowCon<V> stmtFlowCon) {
        JSONObject o = mk(C.FLOW_CONTROL).put(C.LOOP_NUMBER, this.currentLoop).put(C.IF_RETURN, false).put(C.BREAK, stmtFlowCon.getFlow() == Flow.BREAK);
        return app(o);
    }

    @Override
    public V visitStmtList(StmtList<V> stmtList) {
        if ( stmtList.get().size() == 0 ) {
            return null;
        }
        for ( int i = 0; i < stmtList.get().size(); i++ ) {
            stmtList.get().get(i).visit(this);
        }
        return null;
    }

    @Override
    public V visitTimerSensor(TimerSensor<V> timerSensor) {
        JSONObject o;
        switch ( (TimerSensorMode) timerSensor.getMode() ) {
            case DEFAULT:
            case VALUE:
                o = mk(C.GET_SAMPLE).put(C.GET_SAMPLE, C.TIMER).put(C.NAME, "timer" + timerSensor.getPort().getOraName());
                break;
            case RESET:
                o = mk(C.TIMER_SENSOR_RESET).put(C.NAME, "timer" + timerSensor.getPort().getOraName());
                break;
            default:
                throw new DbcException("Invalid Time Mode!");
        }
        return app(o);
    }

    @Override
    public V visitGetSampleSensor(GetSampleSensor<V> sensorGetSample) {
        sensorGetSample.getSensor().visit(this);
        return null;
    }

    @Override
    public V visitMainTask(MainTask<V> mainTask) {
        mainTask.getVariables().visit(this);
        if ( mainTask.getDebug().equals("TRUE") ) {
            JSONObject o = mk(C.CREATE_DEBUG_ACTION);
            return app(o);
        }
        return null;
    }

    @Override
    public V visitActivityTask(ActivityTask<V> activityTask) {
        throw new DbcException("Operation not supported");
    }

    @Override
    public V visitStartActivityTask(StartActivityTask<V> startActivityTask) {
        throw new DbcException("Operation not supported");
    }

    @Override
    public V visitWaitStmt(WaitStmt<V> waitStmt) {
        addInStmt();
        visitStmtList(waitStmt.getStatements());
        removeInStmt();
        JSONObject o = mk(C.WAIT_STMT);
        return app(o);
    }

    @Override
    public V visitWaitTimeStmt(WaitTimeStmt<V> waitTimeStmt) {
        waitTimeStmt.getTime().visit(this);
        JSONObject o = mk(C.WAIT_TIME_STMT);
        return app(o);
    }

    @Override
    public V visitLocation(Location<V> location) {
        throw new DbcException("Operation not supported");
    }

    @Override
    public V visitTextPrintFunct(TextPrintFunct<V> textPrintFunct) {
        return null;
    }

    @Override
    public V visitStmtTextComment(StmtTextComment<V> textComment) {
        JSONObject o = mk(C.NOOP_STMT);
        return app(o);
    }

    @Override
    public V visitFunctionStmt(FunctionStmt<V> functionStmt) {
        functionStmt.getFunction().visit(this);
        return null;
    }

    @Override
    public V visitFunctionExpr(FunctionExpr<V> functionExpr) {
        functionExpr.getFunction().visit(this);
        return null;
    }

    @Override
    public V visitGetSubFunct(GetSubFunct<V> getSubFunct) {
        throw new DbcException("Operation not supported");
    }

    @Override
    public V visitIndexOfFunct(IndexOfFunct<V> indexOfFunct) {
        throw new DbcException("Operation not supported");
    }

    @Override
    public V visitLengthOfIsEmptyFunct(LengthOfIsEmptyFunct<V> lengthOfIsEmptyFunct) {
        throw new DbcException("Operation not supported");
    }

    @Override
    public V visitListCreate(ListCreate<V> listCreate) {
        throw new DbcException("Operation not supported");
    }

    @Override
    public V visitListSetIndex(ListSetIndex<V> listSetIndex) {
        throw new DbcException("Operation not supported");
    }

    @Override
    public V visitListGetIndex(ListGetIndex<V> listGetIndex) {
        throw new DbcException("Operation not supported");
    }

    @Override
    public V visitListRepeat(ListRepeat<V> listRepeat) {
        throw new DbcException("Operation not supported");
    }

    @Override
    public V visitMathConstrainFunct(MathConstrainFunct<V> mathConstrainFunct) {
        mathConstrainFunct.getParam().get(0).visit(this);
        mathConstrainFunct.getParam().get(1).visit(this);
        mathConstrainFunct.getParam().get(2).visit(this);
        JSONObject o = mk(C.MATH_CONSTRAIN_FUNCTION);
        return app(o);
    }

    @Override
    public V visitMathNumPropFunct(MathNumPropFunct<V> mathNumPropFunct) {
        mathNumPropFunct.getParam().get(0).visit(this);
        if ( mathNumPropFunct.getFunctName() == FunctionNames.DIVISIBLE_BY ) {
            mathNumPropFunct.getParam().get(1).visit(this);
        }
        JSONObject o = mk(C.MATH_PROP_FUNCT).put(C.NAME, mathNumPropFunct.getFunctName());
        return app(o);
    }

    @Override
    public V visitMathOnListFunct(MathOnListFunct<V> mathOnListFunct) {
        throw new DbcException("Operation not supported");
    }

    @Override
    public V visitMathRandomFloatFunct(MathRandomFloatFunct<V> mathRandomFloatFunct) {
        JSONObject o = mk(C.RANDOM_DOUBLE);
        return app(o);
    }

    @Override
    public V visitMathRandomIntFunct(MathRandomIntFunct<V> mathRandomIntFunct) {
        mathRandomIntFunct.getParam().get(0).visit(this);
        mathRandomIntFunct.getParam().get(1).visit(this);
        JSONObject o = mk(C.RANDOM_INT);
        return app(o);
    }

    @Override
    public V visitMathSingleFunct(MathSingleFunct<V> mathSingleFunct) {
        mathSingleFunct.getParam().get(0).visit(this);
        JSONObject o = mk(C.SINGLE_FUNCTION).put(C.NAME, mathSingleFunct.getFunctName());
        return app(o);
    }

    @Override
    public V visitTextJoinFunct(TextJoinFunct<V> textJoinFunct) {
        textJoinFunct.getParam().visit(this);
        JSONObject o = mk(C.TEXT_JOIN);
        return app(o);
    }

    @Override
    public V visitMethodVoid(MethodVoid<V> methodVoid) {
        addInStmt();
        pushOpArray();
        methodVoid.getParameters().visit(this);
        methodVoid.getBody().visit(this);
        removeInStmt();
        increaseMethods();
        List<JSONObject> methodBody = popOpArray();
        JSONObject o = mk(C.METHOD_VOID).put(C.NAME, methodVoid.getMethodName()).put(C.STATEMENTS, methodBody);
        return app(o);
    }

    @Override
    public V visitMethodReturn(MethodReturn<V> methodReturn) {
        addInStmt();
        pushOpArray();
        methodReturn.getParameters().visit(this);
        methodReturn.getBody().visit(this);
        methodReturn.getReturnValue().visit(this);
        removeInStmt();
        increaseMethods();
        List<JSONObject> methodBody = popOpArray();
        JSONObject o = mk(C.METHOD_RETURN).put(C.TYPE, methodReturn.getReturnType()).put(C.NAME, methodReturn.getMethodName()).put(C.STATEMENTS, methodBody);
        return app(o);
    }

    @Override
    public V visitMethodIfReturn(MethodIfReturn<V> methodIfReturn) {
        methodIfReturn.getCondition().visit(this);
        methodIfReturn.getReturnValue().visit(this);
        JSONObject o = mk(C.IF_RETURN).put(C.TYPE, methodIfReturn.getReturnType().toString());
        return app(o);
    }

    @Override
    public V visitMethodStmt(MethodStmt<V> methodStmt) {
        methodStmt.getMethod().visit(this);
        return null;
    }

    @Override
    public V visitMethodCall(MethodCall<V> methodCall) {
        List<Expr<V>> parametersNames = methodCall.getParameters().get();
        List<Expr<V>> parametersValues = methodCall.getParametersValues().get();
        parametersValues.stream().forEach(p -> p.visit(this));
        pushOpArray();
        parametersNames.stream().forEach(p -> p.visit(this));
        List<String> names = this.opArray.stream().map(d -> d.getString(C.NAME)).collect(Collectors.toList());
        popOpArray();
        JSONObject o =
            mk(methodCall.getReturnType() == BlocklyType.VOID ? C.METHOD_CALL_VOID : C.METHOD_CALL_RETURN)
                .put(C.NAME, methodCall.getMethodName())
                .put(C.NAMES, names);
        return app(o);
    }

    @Override
    public V visitBluetoothReceiveAction(BluetoothReceiveAction<V> bluetoothReceiveAction) {
        throw new DbcException("Operation not supported");
    }

    @Override
    public V visitBluetoothConnectAction(BluetoothConnectAction<V> bluetoothConnectAction) {
        throw new DbcException("Operation not supported");
    }

    @Override
    public V visitBluetoothSendAction(BluetoothSendAction<V> bluetoothSendAction) {
        throw new DbcException("Operation not supported");
    }

    @Override
    public V visitBluetoothWaitForConnectionAction(BluetoothWaitForConnectionAction<V> bluetoothWaitForConnection) {
        throw new DbcException("Operation not supported");
    }

    @Override
    public V visitConnectConst(ConnectConst<V> connectConst) {
        throw new DbcException("Operation not supported");
    }

    @Override
    public V visitBluetoothCheckConnectAction(BluetoothCheckConnectAction<V> bluetoothCheckConnectAction) {
        throw new DbcException("Operation not supported");
    }

    protected void increaseStmt() {
        this.stmtsNumber++;
    }

    protected void increaseMethods() {
        this.methodsNumber++;
    }

    /**
     * @return the inStmt
     */
    protected boolean isInStmt() {
        if ( this.inStmt.size() == 0 ) {
            return false;
        }
        return this.inStmt.get(this.inStmt.size() - 1);
    }

    /**
     * @param inStmt the inStmt to set
     */
    protected void addInStmt() {
        this.inStmt.add(true);
    }

    protected void removeInStmt() {
        if ( !this.inStmt.isEmpty() ) {
            this.inStmt.remove(this.inStmt.size() - 1);
        }
    }

    protected void appendIfStmtConditions(IfStmt<V> ifStmt) {
        int exprSize = ifStmt.getExpr().size();
        for ( int i = 0; i < exprSize; i++ ) {
            ifStmt.getExpr().get(i).visit(this);
        }
    }

    protected void appendElseStmt(IfStmt<V> ifStmt) {
        if ( !ifStmt.getElseList().get().isEmpty() ) {
            addInStmt();
            ifStmt.getElseList().visit(this);
            removeInStmt();
        }
    }

    protected void appendThenStmts(IfStmt<V> ifStmt) {
        int thenListSize = ifStmt.getThenList().size();
        for ( int i = 0; i < thenListSize; i++ ) {
            addInStmt();
            ifStmt.getThenList().get(i).visit(this);
            removeInStmt();
        }
    }

    protected void appendDuration(MotorDuration<V> duration) {
        if ( duration != null ) {
            duration.getValue().visit(this);
        }
    }

    protected DriveDirection getDriveDirection(boolean isReverse) {
        return isReverse ? DriveDirection.BACKWARD : DriveDirection.FOREWARD;
    }

    protected TurnDirection getTurnDirection(boolean isReverse) {
        return isReverse ? TurnDirection.RIGHT : TurnDirection.LEFT;
    }

    protected void generateCodeFromPhrases(ArrayList<ArrayList<Phrase<V>>> phrasesSet) {
        for ( ArrayList<Phrase<V>> phrases : phrasesSet ) {
            for ( Phrase<V> phrase : phrases ) {
                phrase.visit(this);
            }
        }
    }

    protected JSONObject mk(String opCode) {
        return new JSONObject().put(C.OPCODE, opCode);
    }

    protected V app(JSONObject o) {
        this.opArray.add(o);
        return null;
    }

    protected void pushOpArray() {
        this.opArrayStack.add(this.opArray);
        this.opArray = new ArrayList<>();
    }

    protected List<JSONObject> popOpArray() {
        List<JSONObject> opArray = this.opArray;
        this.opArray = this.opArrayStack.remove(this.opArrayStack.size() - 1);
        return opArray;
    }
}
