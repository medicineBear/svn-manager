package com.isc.svnmanager.config.handler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 通用枚举类型处理器
 * 用于 MyBatis 在数据库 VARCHAR 和 Java Enum 之间进行转换
 * 
 * @param <E> 枚举类型
 */
@MappedJdbcTypes(JdbcType.VARCHAR)
@MappedTypes({
    com.isc.svnmanager.entity.enums.UserRole.class,
    com.isc.svnmanager.entity.enums.OperationType.class,
    com.isc.svnmanager.entity.enums.OperationStatus.class,
    com.isc.svnmanager.entity.enums.GroupStatus.class
})
public class EnumTypeHandler<E extends Enum<E>> extends BaseTypeHandler<E> {
    
    private final Class<E> type;
    
    public EnumTypeHandler(Class<E> type) {
        if (type == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        this.type = type;
    }
    
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.name());
    }
    
    @Override
    public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return value == null ? null : Enum.valueOf(type, value);
    }
    
    @Override
    public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return value == null ? null : Enum.valueOf(type, value);
    }
    
    @Override
    public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return value == null ? null : Enum.valueOf(type, value);
    }
}

