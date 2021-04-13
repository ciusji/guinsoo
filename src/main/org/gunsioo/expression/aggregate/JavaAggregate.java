/*
 * Copyright 2004-2021 Gunsioo Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (https://github.com/ciusji/guinsoo/blob/master/LICENSE.txt).
 * Initial Developer: Gunsioo Group
 */
package org.gunsioo.expression.aggregate;

import java.sql.SQLException;
import org.gunsioo.api.Aggregate;
import org.gunsioo.command.query.Select;
import org.gunsioo.engine.SessionLocal;
import org.gunsioo.expression.Expression;
import org.gunsioo.expression.ExpressionVisitor;
import org.gunsioo.expression.aggregate.AggregateDataCollecting.NullCollectionMode;
import org.gunsioo.jdbc.JdbcConnection;
import org.gunsioo.message.DbException;
import org.gunsioo.schema.UserAggregate;
import org.gunsioo.util.ParserUtil;
import org.gunsioo.value.TypeInfo;
import org.gunsioo.value.Value;
import org.gunsioo.value.ValueBoolean;
import org.gunsioo.value.ValueNull;
import org.gunsioo.value.ValueRow;
import org.gunsioo.value.ValueToObjectConverter;

/**
 * This class wraps a user-defined aggregate.
 */
public class JavaAggregate extends AbstractAggregate {

    private final UserAggregate userAggregate;
    private int[] argTypes;
    private int dataType;
    private JdbcConnection userConnection;

    public JavaAggregate(UserAggregate userAggregate, Expression[] args, Select select, boolean distinct) {
        super(select, args, distinct);
        this.userAggregate = userAggregate;
    }

    @Override
    public int getCost() {
        int cost = 5;
        for (Expression e : args) {
            cost += e.getCost();
        }
        if (filterCondition != null) {
            cost += filterCondition.getCost();
        }
        return cost;
    }

    @Override
    public StringBuilder getUnenclosedSQL(StringBuilder builder, int sqlFlags) {
        ParserUtil.quoteIdentifier(builder, userAggregate.getName(), sqlFlags).append('(');
        writeExpressions(builder, args, sqlFlags).append(')');
        return appendTailConditions(builder, sqlFlags, false);
    }

    @Override
    public boolean isEverything(ExpressionVisitor visitor) {
        if (!super.isEverything(visitor)) {
            return false;
        }
        switch (visitor.getType()) {
        case ExpressionVisitor.DETERMINISTIC:
            // TODO optimization: some functions are deterministic, but we don't
            // know (no setting for that)
        case ExpressionVisitor.OPTIMIZABLE_AGGREGATE:
            // user defined aggregate functions can not be optimized
            return false;
        case ExpressionVisitor.GET_DEPENDENCIES:
            visitor.addDependency(userAggregate);
            break;
        default:
        }
        for (Expression e : args) {
            if (e != null && !e.isEverything(visitor)) {
                return false;
            }
        }
        return filterCondition == null || filterCondition.isEverything(visitor);
    }

    @Override
    public Expression optimize(SessionLocal session) {
        super.optimize(session);
        userConnection = session.createConnection(false);
        int len = args.length;
        argTypes = new int[len];
        for (int i = 0; i < len; i++) {
            int type = args[i].getType().getValueType();
            argTypes[i] = type;
        }
        try {
            Aggregate aggregate = getInstance();
            dataType = aggregate.getInternalType(argTypes);
            type = TypeInfo.getTypeInfo(dataType);
        } catch (SQLException e) {
            throw DbException.convert(e);
        }
        return this;
    }

    private Aggregate getInstance() {
        Aggregate agg = userAggregate.getInstance();
        try {
            agg.init(userConnection);
        } catch (SQLException ex) {
            throw DbException.convert(ex);
        }
        return agg;
    }

    @Override
    public Value getAggregatedValue(SessionLocal session, Object aggregateData) {
        try {
            Aggregate agg;
            if (distinct) {
                agg = getInstance();
                AggregateDataCollecting data = (AggregateDataCollecting) aggregateData;
                if (data != null) {
                    for (Value value : data.values) {
                        if (args.length == 1) {
                            agg.add(ValueToObjectConverter.valueToDefaultObject(value, userConnection, false));
                        } else {
                            Value[] values = ((ValueRow) value).getList();
                            Object[] argValues = new Object[args.length];
                            for (int i = 0, len = args.length; i < len; i++) {
                                argValues[i] = ValueToObjectConverter.valueToDefaultObject(values[i], userConnection,
                                        false);
                            }
                            agg.add(argValues);
                        }
                    }
                }
            } else {
                agg = (Aggregate) aggregateData;
                if (agg == null) {
                    agg = getInstance();
                }
            }
            Object obj = agg.getResult();
            if (obj == null) {
                return ValueNull.INSTANCE;
            }
            return ValueToObjectConverter.objectToValue(session, obj, dataType);
        } catch (SQLException e) {
            throw DbException.convert(e);
        }
    }

    @Override
    protected void updateAggregate(SessionLocal session, Object aggregateData) {
        updateData(session, aggregateData, null);
    }

    private void updateData(SessionLocal session, Object aggregateData, Value[] remembered) {
        try {
            if (distinct) {
                AggregateDataCollecting data = (AggregateDataCollecting) aggregateData;
                Value[] argValues = new Value[args.length];
                Value arg = null;
                for (int i = 0, len = args.length; i < len; i++) {
                    arg = remembered == null ? args[i].getValue(session) : remembered[i];
                    argValues[i] = arg;
                }
                data.add(session, args.length == 1 ? arg : ValueRow.get(argValues));
            } else {
                Aggregate agg = (Aggregate) aggregateData;
                Object[] argValues = new Object[args.length];
                Object arg = null;
                for (int i = 0, len = args.length; i < len; i++) {
                    Value v = remembered == null ? args[i].getValue(session) : remembered[i];
                    arg = ValueToObjectConverter.valueToDefaultObject(v, userConnection, false);
                    argValues[i] = arg;
                }
                agg.add(args.length == 1 ? arg : argValues);
            }
        } catch (SQLException e) {
            throw DbException.convert(e);
        }
    }

    @Override
    protected void updateGroupAggregates(SessionLocal session, int stage) {
        super.updateGroupAggregates(session, stage);
        for (Expression expr : args) {
            expr.updateAggregate(session, stage);
        }
    }

    @Override
    protected int getNumExpressions() {
        int n = args.length;
        if (filterCondition != null) {
            n++;
        }
        return n;
    }

    @Override
    protected void rememberExpressions(SessionLocal session, Value[] array) {
        int length = args.length;
        for (int i = 0; i < length; i++) {
            array[i] = args[i].getValue(session);
        }
        if (filterCondition != null) {
            array[length] = ValueBoolean.get(filterCondition.getBooleanValue(session));
        }
    }

    @Override
    protected void updateFromExpressions(SessionLocal session, Object aggregateData, Value[] array) {
        if (filterCondition == null || array[getNumExpressions() - 1].getBoolean()) {
            updateData(session, aggregateData, array);
        }
    }

    @Override
    protected Object createAggregateData() {
        return distinct ? new AggregateDataCollecting(true, false, NullCollectionMode.IGNORED) : getInstance();
    }

}
