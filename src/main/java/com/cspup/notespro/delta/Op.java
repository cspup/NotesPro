package com.cspup.notespro.delta;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Objects;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;

@JsonInclude(value = NON_EMPTY)
@JsonDeserialize(using = OpDeserializer.class)
public class Op {

  // 0 length white space
  // placeholder for embedded in diff (no implementation so far)
  static final String EMBED = String.valueOf((char) 0x200b);

  @JsonProperty() private Object insert;
  @JsonProperty() private Integer delete;
  @JsonProperty() private Integer retain;
  @JsonProperty() private AttributeMap attributes;


  public Integer getRetain() {
    return retain;
  }

  public static Op insert(Object arg) {
    if (arg instanceof String) {
      return Op.insert(arg, null);
    }
    var newOp = new Op();
    newOp.insert = arg;
    return newOp;
  }

  public static Op insert(Object arg, AttributeMap attributes) {
    Op newOp = new Op();
    if (attributes != null && attributes.size() > 0) {
      if (arg instanceof String) {
        newOp.attributes = attributes;
      } else {
        throw new IllegalArgumentException("Cannot insert object with attributes");
      }
    }
    newOp.insert = arg;
    return newOp;
  }

  public static Op retain(int length) {
    return Op.retain(length, null);
  }

  public static Op retain(int length, AttributeMap attributes) {
    if (length <= 0) throw new IllegalArgumentException("Length should be greater than 0");
    Op newOp = new Op();
    if (attributes != null && attributes.size() > 0) newOp.attributes = attributes;
    newOp.retain = length;
    return newOp;
  }

  public static Op delete(int length) {
    if (length <= 0) throw new IllegalArgumentException("Length should be greater than 0");
    Op newOp = new Op();
    newOp.delete = length;
    return newOp;
  }

  static Op retainUntilEnd() {
    return Op.retain(Integer.MAX_VALUE);
  }

  @JsonIgnore
  public boolean isDelete() {
    return type().equals(Type.DELETE);
  }

  @JsonIgnore
  public boolean isInsert() {
    return type().equals(Type.INSERT);
  }

  @JsonIgnore
  public boolean isTextInsert() {
    return isInsert() && insert instanceof String;
  }

  @JsonIgnore
  public boolean isRetain() {
    return type().equals(Type.RETAIN);
  }

  public Type type() {
    if (insert != null) return Type.INSERT;
    if (delete != null) return Type.DELETE;
    if (retain != null) return Type.RETAIN;
    throw new IllegalStateException("Op has no insert, delete or retain");
  }

  public Op copy() {
    switch (type()) {
      case RETAIN:
        return Op.retain(retain, attributes != null ? attributes.copy() : null);
      case DELETE:
        return Op.delete(delete);
      case INSERT:
        return Op.insert(insert, attributes != null ? attributes.copy() : null);
      default:
        throw new IllegalStateException("Op has no insert, delete or retain");
    }
  }

  public int length() {
    if (type().equals(Type.DELETE)) return delete;
    if (type().equals(Type.RETAIN)) return retain;
    return insert instanceof String ? ((String) insert).length() : 1;
  }

  public AttributeMap attributes() {
    if (type().equals(Type.DELETE)) return null;
    return attributes != null ? attributes.copy() : null;
  }

  public Object arg() {
    if (Type.INSERT.equals(type())) return insert;
    throw new UnsupportedOperationException("Only insert op has an argument");
  }

  public String argAsString() {
    assert (insert instanceof String);
    return (String) insert;
  }

  public boolean hasAttributes() {
    if (isDelete()) return false;
    return attributes != null;
  }

  @Override
  public int hashCode() {
    return Objects.hash(insert, delete, retain, attributes);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Op op = (Op) o;
    return Objects.equals(insert, op.insert)
        && Objects.equals(delete, op.delete)
        && Objects.equals(retain, op.retain)
        && Objects.equals(attributes, op.attributes);
  }

  @Override
  public String toString() {
    ObjectMapper mapper = new ObjectMapper();
    ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
    try {
      return writer.writeValueAsString(this);
    } catch (JsonProcessingException e) {
      return "Error while generating json:\n" + e.getMessage();
    }
  }

  public enum Type {
    INSERT,
    DELETE,
    RETAIN
  }
}
