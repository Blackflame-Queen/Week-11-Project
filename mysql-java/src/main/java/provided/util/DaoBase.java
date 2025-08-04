package provided.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

// Helpful utilities for DAO operations can be found here
public abstract class DaoBase {
  /**
   * Starts a transaction, letting me decide when to commit
   * @param conn Database connection
   * @throws SQLException If starting fails
   */
  protected void startTransaction(Connection conn) throws SQLException {
    conn.setAutoCommit(false);
  }

  /**
   * Commits changes to the database
   * @param conn Connection to commit
   * @throws SQLException If committing fails
   */
  protected void commitTransaction(Connection conn) throws SQLException {
    conn.commit();
  }

  /**
   * Undoes all transaction changes
   * @param conn Connection to roll back
   * @throws SQLException If rolling back fails
   */
  protected void rollbackTransaction(Connection conn) throws SQLException {
    conn.rollback();
  }

  /**
   * Sets a parameter, gracefully handling nulls
   * @param stmt Prepared statement
   * @param parameterIndex Position (starts with 1 over the usual 0)
   * @param value Value to set, possibly null
   * @param classType Java type of the parameter
   * @throws SQLException If setting fails
   */
  protected void setParameter(PreparedStatement stmt, int parameterIndex, Object value,
      Class<?> classType) throws SQLException {
    int sqlType = convertJavaClassToSqlType(classType);

    if(Objects.isNull(value)) {
      stmt.setNull(parameterIndex, sqlType);
    }
    else {
      switch(sqlType) {
        case Types.DECIMAL:
          stmt.setBigDecimal(parameterIndex, (BigDecimal)value);
          break;

        case Types.DOUBLE:
          stmt.setDouble(parameterIndex, (Double)value);
          break;

        case Types.INTEGER:
          stmt.setInt(parameterIndex, (Integer)value);
          break;

        case Types.OTHER:
          stmt.setObject(parameterIndex, value);
          break;

        case Types.VARCHAR:
          stmt.setString(parameterIndex, (String)value);
          break;

        default:
          throw new DaoException("Unknown parameter type: " + classType);
      }
    }
  }

  /**
   * Maps Java class to SQL type
   * @param classType Java class to convert
   * @return Corresponding java.sql.Types value
   */
  private int convertJavaClassToSqlType(Class<?> classType) {
    if(Integer.class.equals(classType)) {
      return Types.INTEGER;
    }

    if(String.class.equals(classType)) {
      return Types.VARCHAR;
    }

    if(Double.class.equals(classType)) {
      return Types.DOUBLE;
    }

    if(BigDecimal.class.equals(classType)) {
      return Types.DECIMAL;
    }

    if(LocalTime.class.equals(classType)) {
      return Types.OTHER;
    }

    throw new DaoException("Unsupported class type: " + classType.getName());
  }

  /**
   * Gets next sequence number for child rows
   * @param conn Database connection
   * @param id Parent entity ID
   * @param tableName Child row table
   * @param idName Parent ID column name
   * @return Next sequence number
   * @throws SQLException If querying fails
   */
  protected Integer getNextSequenceNumber(Connection conn, Integer id, String tableName,
      String idName) throws SQLException {
    String sql = "SELECT COUNT(*) FROM " + tableName + " WHERE " + idName + " = ?";

    try(PreparedStatement stmt = conn.prepareStatement(sql)) {
      setParameter(stmt, 1, id, Integer.class);

      try(ResultSet rs = stmt.executeQuery()) {
        if(rs.next()) {
          return rs.getInt(1) + 1;
        }

        return 1;
      }
    }
  }

  /**
   * Fetches last inserted row’s primary key
   * @param conn Database connection
   * @param table Table to query
   * @return Last inserted ID
   * @throws SQLException If retrieval fails
   */
  protected Integer getLastInsertId(Connection conn, String table) throws SQLException {
    String sql = String.format("SELECT LAST_INSERT_ID() FROM %s", table);

    try(Statement stmt = conn.createStatement()) {
      try(ResultSet rs = stmt.executeQuery(sql)) {
        if(rs.next()) {
          return rs.getInt(1);
        }

        throw new SQLException("Unable to retrieve the primary key value. No result set!");
      }
    }
  }

  /**
   * Builds an object from result set using reflection
   * @param <T> Object type to create
   * @param rs Result set (on correct row)
   * @param classType Class to instantiate
   * @return Populated object
   */
  protected <T> T extract(ResultSet rs, Class<T> classType) {
    try {
      Constructor<T> con = classType.getConstructor();
      T obj = con.newInstance();

      for(Field field : classType.getDeclaredFields()) {
        String colName = camelCaseToSnakeCase(field.getName());
        Class<?> fieldType = field.getType();

        field.setAccessible(true);
        Object fieldValue = null;

        try {
          fieldValue = rs.getObject(colName);
        }
        catch(SQLException e) {
          // Field not in result set; leave as is
        }

        if(Objects.nonNull(fieldValue)) {
          if(fieldValue instanceof Time && fieldType.equals(LocalTime.class)) {
            fieldValue = ((Time)fieldValue).toLocalTime();
          }
          else if(fieldValue instanceof Timestamp && fieldType.equals(LocalDateTime.class)) {
            fieldValue = ((Timestamp)fieldValue).toLocalDateTime();
          }

          field.set(obj, fieldValue);
        }
      }

      return obj;

    }
    catch(Exception e) {
      throw new DaoException("Unable to create object of type " + classType.getName(), e);
    }
  }

  private String camelCaseToSnakeCase(String identifier) {
    StringBuilder nameBuilder = new StringBuilder();

    for(char ch : identifier.toCharArray()) {
      if(Character.isUpperCase(ch)) {
        nameBuilder.append('_').append(Character.toLowerCase(ch));
      }
      else {
        nameBuilder.append(ch);
      }
    }

    return nameBuilder.toString();
  }

  static class DaoException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public DaoException(String message, Throwable cause) {
      super(message, cause);
    }

    public DaoException(String message) {
      super(message);
    }
  }
}